package com.wising.easyhkstock.ccass.task;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.wising.easyhkstock.ccass.domain.SnapshotDetail;
import com.wising.easyhkstock.ccass.domain.SnapshotSummary;
import com.wising.easyhkstock.common.task.DataBuilder;

public class ParallelSnapshotDataBuilder implements DataBuilder<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> {

	private static final Logger logger = LoggerFactory.getLogger(ParallelSnapshotDataBuilder.class);

	private RestTemplate restTpl;
	private ThreadPoolTaskExecutor executor;
	private ThreadPoolTaskExecutor fetcherPool;
	private ThreadPoolTaskExecutor parserPool;
	private BuilderConfiguration configuration;
	private boolean terminated;

	public ParallelSnapshotDataBuilder(BuilderConfiguration configuration) {
		Objects.requireNonNull(configuration);
		this.configuration = configuration;
		initExecutor();
		initRestTemplate();
	}

	private void initExecutor() {
		executor = new ThreadPoolTaskExecutor();
		executor.setCorePoolSize(configuration.getCorePoolSize());
		executor.setQueueCapacity(configuration.getQueueCapacity());
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.setWaitForTasksToCompleteOnShutdown(true);
		executor.initialize();
		
		fetcherPool = new ThreadPoolTaskExecutor();
		fetcherPool.setCorePoolSize(configuration.getCorePoolSize());
		fetcherPool.setQueueCapacity(configuration.getQueueCapacity());
		fetcherPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		fetcherPool.setWaitForTasksToCompleteOnShutdown(true);
		fetcherPool.initialize();
		
		parserPool = new ThreadPoolTaskExecutor();
		parserPool.setCorePoolSize(configuration.getCorePoolSize());
		parserPool.setQueueCapacity(configuration.getQueueCapacity());
		parserPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		parserPool.setWaitForTasksToCompleteOnShutdown(true);
		parserPool.initialize();
	}

	private void initRestTemplate() {
		
		RequestConfig requestConfig = RequestConfig.custom().setConnectionRequestTimeout(configuration.getRequestTimeout())
				.setConnectTimeout(configuration.getConnectTimeout())
				.setSocketTimeout(configuration.getSocketTimeout()).build();
		CloseableHttpClient httpClient = HttpClientBuilder.create().useSystemProperties().setDefaultRequestConfig(requestConfig)
				.setMaxConnTotal(configuration.getMaxConnTotal()).setMaxConnPerRoute(configuration.getMaxConnTotal()).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		restTpl = new RestTemplate(requestFactory);
		restTpl.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
	}
	
	public void terminate() {
		if (!terminated) {
			if (executor != null) {
				executor.shutdown();
			}
			terminated = true;
		}
	}

	@Override
	public List<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> build() {
		
		if (terminated) {
			throw new IllegalStateException("This builder has been terminated.");
		}
		List<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> result = new ArrayList<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>();
		
		//setup fetcher
		ConcurrentLinkedQueue<FetchingInfo> pendingListOfFetcher = new ConcurrentLinkedQueue<FetchingInfo>();
		LocalDate startDate = configuration.getStartDate();
		LocalDate endDate = configuration.getEndDate();
		List<String> stockList = configuration.getStocks();
		while (startDate.isBefore(endDate)) {
			final LocalDate date = startDate;
			stockList.forEach(stock -> pendingListOfFetcher.offer(new FetchingInfo(stock, date)));
			startDate = startDate.plusDays(1);
		}

//		CountDownLatch fetchingDoneSignal = new CountDownLatch(pendingListOfFetcher.size());
//		AtomicBoolean fetchingDoneIndicator = new AtomicBoolean(false);
//		new Thread(new Coordinator(fetchingDoneSignal, fetchingDoneIndicator)).start();
		
		LinkedBlockingQueue<PageInfo> ResultListOfFetcher = new LinkedBlockingQueue<PageInfo>();
		Fetcher fetcher = new Fetcher(pendingListOfFetcher, ResultListOfFetcher);
		for (int i=0, size=configuration.getFetcherNo(); i<size; i++) {
			executor.submit(fetcher);
		}
//		pendingListOfFetcher.forEach(pendingEntry -> fetcherPool.submit(fetcher));
		
		//setup parser
		
		
		int totalDays = 0;
		while (startDate.isBefore(endDate)) {
			final LocalDate date = startDate;
			List<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> subResult = new ArrayList<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>();
			List<FutureTask<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>> tasks = new ArrayList<FutureTask<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>>(
					stockList.size());
			stockList.forEach(stockCode -> {
				FutureTask<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> task = new FutureTask<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>(
						new Callable<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>() {
							public SimpleImmutableEntry<SnapshotSummary, SnapshotDetail> call() {
								return getData(date, stockCode);
							}
						});
				tasks.add(task);
				executor.submit(task);
			});
			tasks.forEach(task -> {
				try {
					SimpleImmutableEntry<SnapshotSummary, SnapshotDetail> taskResult = task.get();
					if (taskResult != null) {
						subResult.add(taskResult);
					}
				} catch (Exception e) {
					logger.error("Unexpected errors encountered.", e);
					// TODO should send notification or retry
				}
			});
			result.addAll(subResult);
			logger.debug("Completed data of [{}] with [{}] records", startDate, subResult.size());
			totalDays++;
			startDate = startDate.plusDays(1);
		}

		logger.debug("Totally processing {} days of data with [{}] records", totalDays, result.size());
		return Collections.unmodifiableList(result);

	}


	private SimpleImmutableEntry<SnapshotSummary, SnapshotDetail> getData(LocalDate date, String stockCode) {

		String identifier = stockCode + ":" + date;
		SimpleImmutableEntry<SnapshotSummary, SnapshotDetail> result = null;
		String year = String.valueOf(date.getYear());
		String month = String.valueOf(date.getMonthValue());
		if (month.length() == 1) {
			month = "0" + month;
		}
		String day = String.valueOf(date.getDayOfMonth());
		if (day.length() == 1) {
			day = "0" + day;
		}
		HttpEntity<MultiValueMap<String, String>> request = getHttpEntity(year, month, day, stockCode);
		logger.debug("[{}]: Starting to fetch page.", identifier);
		ResponseEntity<String> response = null;
		try {
			response = restTpl.postForEntity(configuration.getUri(), request, String.class);
		}catch (Exception ex) {
			logger.debug("[{}]: Caught exception when fetching page: {}", identifier, ex);
			ex.printStackTrace();
		}finally {
			logger.debug("[{}]: Finished to fetch page.", identifier);
		}
		if (response != null && HttpStatus.OK.equals(response.getStatusCode())) {
			String html = response.getBody();
			SnapshotPage page = new SnapshotPage(html, stockCode, date);
			if (!page.hasError()) {
				String stockCodeOfPage = page.getStockCode();
				// should note use the snapshot date of page, as it may be
				// different with the input date
				// LocalDate snapshotDate = page.getSnapshotDate();
				long totalIssuedShares = page.getTotalIssuedShares();
				long intermediaryShareholding = page.getIntermediaryShareholding();
				short intermediaryNumber = page.getIntermediaryNumber();
				long consentingShareholding = page.getConsentingShareholding();
				short consentingInvestorNumber = page.getConsentingInvestorNumber();
				long nonConsentingShareholding = page.getNonConsentingShareholding();
				short nonConsentingInvestorNumber = page.getNonConsentingInvestorNumber();
				Map<String, Long> details = page.getDetail();
				SnapshotSummary summary = new SnapshotSummary(stockCodeOfPage, date, totalIssuedShares,
						intermediaryNumber, intermediaryShareholding, consentingInvestorNumber, consentingShareholding,
						nonConsentingInvestorNumber, nonConsentingShareholding);
				SnapshotDetail detail = new SnapshotDetail(stockCodeOfPage, date, details);
				result = new SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>(summary, detail);
			} else {
				logger.error("[{}]: Got error when parsing html page.", identifier);
			}
		} else {
			logger.error("[{}]: Got error response: {}. \nOriginal Request is {}", identifier, response, request);
		}

		return result;

	}

	private HttpEntity<MultiValueMap<String, String>> getHttpEntity(String year, String month, String day, String stock) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("__VIEWSTATE", configuration.getViewState());
		body.add("__EVENTVALIDATION", configuration.getEventValidation());
		body.add("btnSearch.x", String.valueOf(configuration.getSearchX()));
		body.add("btnSearch.y", String.valueOf(configuration.getSearchY()));
		body.add("ddlShareholdingDay", day);
		body.add("ddlShareholdingMonth", month);
		body.add("ddlShareholdingYear", year);
		body.add("txtStockCode", stock);
		return new HttpEntity<MultiValueMap<String, String>>(body, headers);
	}

	private static class FetchingInfo {
		String stock;
		LocalDate date;
		
		FetchingInfo(String stock, LocalDate date) {
			this.stock = stock;
			this.date = date;
		}
	}
	
	private static class PageInfo {
		
		String stock, html;
		LocalDate date;
		
		PageInfo(String stock, LocalDate date, String html) {
			this.stock = stock;
			this.date = date;
			this.html = html;
		}
	}
	

	private class Fetcher implements Runnable {

		ConcurrentLinkedQueue<FetchingInfo> pendingList;
		LinkedBlockingQueue<PageInfo> fetchingResult;

		Fetcher(ConcurrentLinkedQueue<FetchingInfo> pendingList, 
				LinkedBlockingQueue<PageInfo> fetchingResult) {
			this.pendingList = pendingList;
			this.fetchingResult = fetchingResult;
		}
		
		@Override
		public void run() {
			
			boolean completed = false;
			while (!completed) {
				FetchingInfo pendingEntry = pendingList.poll();
				if (pendingEntry != null) {
					HttpEntity<MultiValueMap<String, String>> request = getHttpEntity(pendingEntry.stock, pendingEntry.date);
					if (logger.isDebugEnabled()) {
						logger.debug("[{}:{}]: Starting to fetch page.", pendingEntry.stock, pendingEntry.date);
					}
					ResponseEntity<String> response = null;
					try {
						response = restTpl.postForEntity(configuration.getUri(), request, String.class);
						if (response != null && HttpStatus.OK.equals(response.getStatusCode())) {
							String html = response.getBody();
							if (html != null) {
								fetchingResult.offer(new PageInfo(pendingEntry.stock, pendingEntry.date, html));
							}else if (logger.isDebugEnabled()) {
								logger.error("[{}:{}]: Got null body of response.", pendingEntry.stock, pendingEntry.date);
							}
						}else if (logger.isDebugEnabled()) {
							logger.error("[{}:{}]: Got error response:\n{}\nOriginal Request is\n{}", pendingEntry.stock, pendingEntry.date, 
										response, request);
						}
					}catch (RestClientException ex) {
						if (logger.isInfoEnabled()) {
							logger.info("[{}:{}]: Encouter exception when fetching page.Exception message is\n{}", pendingEntry.stock, pendingEntry.date, 
									ex.getMessage());
						}
						if (logger.isDebugEnabled()) {
							logger.debug("Detailed exception is\n", ex);
						}
					}finally {
						if (logger.isDebugEnabled()) {
							logger.debug("[{}:{}]: Finished to fetch page.", pendingEntry.stock, pendingEntry.date);
						}
					}
				}else {
					completed = true;
				}
			}
		}
		
		private HttpEntity<MultiValueMap<String, String>> getHttpEntity(String stock, LocalDate date) {
			
			String year = String.valueOf(date.getYear());
			int intMonth = date.getMonthValue();
			String month = String.valueOf(intMonth);
			if (month.length() == 1) {
				month = "0" + month;
			}
			int intDay = date.getDayOfMonth();
			String day = String.valueOf(intDay);
			if (day.length() == 1) {
				day = "0" + day;
			}

			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
			body.add("__VIEWSTATE", configuration.getViewState());
			body.add("__EVENTVALIDATION", configuration.getEventValidation());
			body.add("btnSearch.x", String.valueOf(configuration.getSearchX()));
			body.add("btnSearch.y", String.valueOf(configuration.getSearchY()));
			body.add("ddlShareholdingDay", day);
			body.add("ddlShareholdingMonth", month);
			body.add("ddlShareholdingYear", year);
			body.add("txtStockCode", stock);
			return new HttpEntity<MultiValueMap<String, String>>(body, headers);
		}
	}
	
	private class Parser implements Runnable {

		LinkedBlockingQueue<PageInfo> pendingList;
		AtomicBoolean fetchingDoneIndicator;
		
		Parser(LinkedBlockingQueue<PageInfo> pendingList, AtomicBoolean fetchingDoneIndicator) {
			this.pendingList = pendingList;
			this.fetchingDoneIndicator = fetchingDoneIndicator;
		}
		@Override
		public void run() {

			try {
				PageInfo pageInfo = null;
				if (fetchingDoneIndicator.get()) {
					pageInfo = pendingList.poll(1000, TimeUnit.MILLISECONDS);
				}else {
					pageInfo = pendingList.take();
				}
				if (pageInfo != null) {
					SnapshotPage page = new SnapshotPage(pageInfo.html, pageInfo.stock, pageInfo.date);
					if (!page.hasError()) {
						String stockCodeOfPage = page.getStockCode();
						// should note use the snapshot date of page, as it may be
						// different with the input date
						// LocalDate snapshotDate = page.getSnapshotDate();
						long totalIssuedShares = page.getTotalIssuedShares();
						long intermediaryShareholding = page.getIntermediaryShareholding();
						short intermediaryNumber = page.getIntermediaryNumber();
						long consentingShareholding = page.getConsentingShareholding();
						short consentingInvestorNumber = page.getConsentingInvestorNumber();
						long nonConsentingShareholding = page.getNonConsentingShareholding();
						short nonConsentingInvestorNumber = page.getNonConsentingInvestorNumber();
						Map<String, Long> details = page.getDetail();
						SnapshotSummary summary = new SnapshotSummary(stockCodeOfPage, pageInfo.date, totalIssuedShares,
								intermediaryNumber, intermediaryShareholding, consentingInvestorNumber, consentingShareholding,
								nonConsentingInvestorNumber, nonConsentingShareholding);
						SnapshotDetail detail = new SnapshotDetail(stockCodeOfPage, pageInfo.date, details);
	//					result = new SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>(summary, detail);
					}
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private class Coordinator implements Runnable {
		
		CountDownLatch fetchingDoneSignal;
		AtomicBoolean fetchingDoneIndicator;
		
		Coordinator(CountDownLatch fetchingDoneSignal, AtomicBoolean fetchingDoneIndicator) {
			this.fetchingDoneSignal = fetchingDoneSignal;
			this.fetchingDoneIndicator = fetchingDoneIndicator;
		}
		
		public void run() {
			try {
				fetchingDoneSignal.await();
				fetchingDoneIndicator.set(true);
			} catch (InterruptedException e) {
				//This should not happen
				if (logger.isDebugEnabled()) {
					logger.debug("Fetching coordinator thread is interrupted unexpected. The cause is:\n{}", e);
				}
			}
		}
	}
}
