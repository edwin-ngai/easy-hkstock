package com.wising.easyhkstock.ccass.task;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.Period;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.CountDownLatch;
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
//	private ThreadPoolTaskExecutor fetcherPool;
//	private ThreadPoolTaskExecutor parserPool;
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
		if (logger.isDebugEnabled()) {
			logger.debug("Finish executor initialization. The core pool size is [{}]", configuration.getCorePoolSize());
		}
//		fetcherPool = new ThreadPoolTaskExecutor();
//		fetcherPool.setCorePoolSize(configuration.getCorePoolSize());
//		fetcherPool.setQueueCapacity(configuration.getQueueCapacity());
//		fetcherPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//		fetcherPool.setWaitForTasksToCompleteOnShutdown(true);
//		fetcherPool.initialize();
//
//		parserPool = new ThreadPoolTaskExecutor();
//		parserPool.setCorePoolSize(configuration.getCorePoolSize());
//		parserPool.setQueueCapacity(configuration.getQueueCapacity());
//		parserPool.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
//		parserPool.setWaitForTasksToCompleteOnShutdown(true);
//		parserPool.initialize();
	}

	private void initRestTemplate() {

		RequestConfig requestConfig = RequestConfig.custom()
				.setConnectionRequestTimeout(configuration.getRequestTimeout())
				.setConnectTimeout(configuration.getConnectTimeout()).setSocketTimeout(configuration.getSocketTimeout())
				.build();
		CloseableHttpClient httpClient = HttpClientBuilder.create().useSystemProperties()
				.setDefaultRequestConfig(requestConfig).setMaxConnTotal(configuration.getMaxConnTotal())
				.setMaxConnPerRoute(configuration.getMaxConnTotal()).build();
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
		restTpl = new RestTemplate(requestFactory);
		restTpl.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		if (logger.isDebugEnabled()) {
			logger.debug("Finish http client initialization. The pool size is [{}]", configuration.getMaxConnTotal());
		}
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
		
		if (logger.isDebugEnabled()) {
			logger.debug("Start to build date");
		}
		// setup fetcher
		ConcurrentLinkedQueue<FetchingInfo> fetcherInput = new ConcurrentLinkedQueue<FetchingInfo>();
		LocalDate startDate = configuration.getStartDate();
		LocalDate endDate = configuration.getEndDate();
		int totalDays = Period.between(startDate, endDate).getDays();
		List<String> stockList = configuration.getStocks();
		while (startDate.isBefore(endDate)) {
			final LocalDate date = startDate;
			stockList.forEach(stock -> fetcherInput.offer(new FetchingInfo(stock, date)));
			startDate = startDate.plusDays(1);
		}
		LinkedBlockingQueue<PageInfo> fetcherOutput = new LinkedBlockingQueue<PageInfo>();
		CountDownLatch fetcherDoneSignal = new CountDownLatch(configuration.getFetcherNo());
		AtomicBoolean fetcherDoneIndicator = new AtomicBoolean(false);
		new Thread(new Monitor("Fetcher", fetcherDoneSignal, fetcherDoneIndicator)).start();
		Fetcher fetcher = new Fetcher(fetcherInput, fetcherOutput, fetcherDoneSignal);
		for (int i = 0, size = configuration.getFetcherNo(); i < size; i++) {
			executor.submit(fetcher);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Finish fetcher setup. Total fetch number is [{}]", configuration.getFetcherNo());
		}
		
		// setup parser
		ConcurrentLinkedQueue<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> parserOutput 
			= new ConcurrentLinkedQueue<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>();
		CountDownLatch parserDoneSignal = new CountDownLatch(configuration.getParserNo());
		Parser parser = new Parser(fetcherOutput, parserOutput, fetcherDoneIndicator, parserDoneSignal);
		for (int i=0, size=configuration.getParserNo(); i<size; i++) {
			executor.submit(parser);
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Finish parser setup. Total parser number is [{}]", configuration.getParserNo());
		}
		
		try {
			parserDoneSignal.await();
//			fetcherDoneSignal.await();
		} catch (InterruptedException e) {
			// This should not happen
			if (logger.isDebugEnabled()) {
				logger.debug("Builder is interrupted unexpected. Exception detail: \n", e);
			}
		}
		if (logger.isDebugEnabled()) {
			logger.debug("Totally processing {} days of data with [{}] records", totalDays, parserOutput.size());
		}

		return Collections.unmodifiableList(new ArrayList<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>(parserOutput));
//		return new ArrayList();
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

		ConcurrentLinkedQueue<FetchingInfo> input;
		LinkedBlockingQueue<PageInfo> output;
		CountDownLatch doneSignal;
		Random random = new Random();

		Fetcher(ConcurrentLinkedQueue<FetchingInfo> input, LinkedBlockingQueue<PageInfo> output,
				CountDownLatch doneSignal) {
			this.input = input;
			this.output = output;
			this.doneSignal = doneSignal;
		}

		@Override
		public void run() {

			if (logger.isDebugEnabled()) {
				logger.debug("Fetcher [{}] starts to run.", Thread.currentThread().getName());
			}
			boolean completed = false;
			while (!completed) {
				FetchingInfo pendingEntry = input.poll();
				if (pendingEntry != null) {
					HttpEntity<MultiValueMap<String, String>> request = getHttpEntity(pendingEntry.stock,
							pendingEntry.date);
					if (logger.isDebugEnabled()) {
						logger.debug("[{}:{}]: Starting to fetch page.", pendingEntry.stock, pendingEntry.date);
					}
					ResponseEntity<String> response = null;
					try {
						response = restTpl.postForEntity(configuration.getUri(), request, String.class);
						if (response != null && HttpStatus.OK.equals(response.getStatusCode())) {
							String html = response.getBody();
							if (html != null) {
								output.offer(new PageInfo(pendingEntry.stock, pendingEntry.date, html));
							} else if (logger.isDebugEnabled()) {
								logger.error("[{}:{}]: Got null body of response.", pendingEntry.stock,
										pendingEntry.date);
							}
						} else if (logger.isDebugEnabled()) {
							logger.error("[{}:{}]: Got error response:\n{}\nOriginal Request is\n{}",
									pendingEntry.stock, pendingEntry.date, response, request);
						}
					} catch (RestClientException ex) {
						if (logger.isInfoEnabled()) {
							logger.info("[{}:{}]: Encouter exception when fetching page.Exception message is\n{}",
									pendingEntry.stock, pendingEntry.date, ex.getMessage());
						}
						if (logger.isDebugEnabled()) {
							logger.debug("Detailed exception is\n", ex);
						}
					} finally {
						if (logger.isDebugEnabled()) {
							logger.debug("[{}:{}]: Finished to fetch page.", pendingEntry.stock, pendingEntry.date);
						}
					}
				} else {
					if (logger.isDebugEnabled()) {
						logger.debug("Fetcher [{}] cannot get fetchin info.", Thread.currentThread().getName());
					}
					completed = true;
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Fetcher [{}] completes.", Thread.currentThread().getName());
			}
			doneSignal.countDown();
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
			int index = random.nextInt(2);
			body.add("__VIEWSTATE", configuration.getTokens().get(index).getViewState());
			body.add("__EVENTVALIDATION", configuration.getTokens().get(index).getEventValidation());
//			body.add("__VIEWSTATE", configuration.getViewState());
//			body.add("__EVENTVALIDATION", configuration.getEventValidation());
			body.add("btnSearch.x", String.valueOf(configuration.getSearchX()));
			body.add("btnSearch.y", String.valueOf(configuration.getSearchY()));
			body.add("ddlShareholdingDay", day);
			body.add("ddlShareholdingMonth", month);
			body.add("ddlShareholdingYear", year);
			body.add("txtStockCode", stock);
			if (logger.isDebugEnabled()) {
				logger.debug("__VIEWSTATE:{}", body.get("__VIEWSTATE"));
				logger.debug("__EVENTVALIDATION:{}", body.get("__EVENTVALIDATION"));
			}
			return new HttpEntity<MultiValueMap<String, String>>(body, headers);
		}
	}

	private class Parser implements Runnable {

		LinkedBlockingQueue<PageInfo> input;
		ConcurrentLinkedQueue<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> output;
		AtomicBoolean fetchingDoneIndicator;
		CountDownLatch doneSignal;

		Parser(LinkedBlockingQueue<PageInfo> input, ConcurrentLinkedQueue<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> output,
				AtomicBoolean fetchingDoneIndicator,
				CountDownLatch doneSignal) {
			this.input = input;
			this.output = output;
			this.fetchingDoneIndicator = fetchingDoneIndicator;
			this.doneSignal = doneSignal;
		}

		@Override
		public void run() {

			if (logger.isDebugEnabled()) {
				logger.debug("Parser [{}] starts to run.", Thread.currentThread().getName());
			}
			boolean completed = false;
			while (!completed) {
				PageInfo pageInfo = null;
				try {
					pageInfo = input.poll(configuration.getParserTimeout(), TimeUnit.MILLISECONDS);
					if (pageInfo == null && fetchingDoneIndicator.get()) {
						pageInfo = input.poll(configuration.getParserTimeout(), TimeUnit.MILLISECONDS);
						if (pageInfo == null) {
							if (logger.isDebugEnabled()) {
								logger.debug("Parser [{}] cannot get page info.", Thread.currentThread().getName());
							}
							completed = true;
						}
					}
				} catch (InterruptedException e) {
					if (logger.isDebugEnabled()) {
						logger.debug("Parser is interrupted unexpected.", e);
					}
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
								intermediaryNumber, intermediaryShareholding, consentingInvestorNumber,
								consentingShareholding, nonConsentingInvestorNumber, nonConsentingShareholding);
						SnapshotDetail detail = new SnapshotDetail(stockCodeOfPage, pageInfo.date, details);
						output.offer(new SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>(summary,detail));
					}
				}
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Parser [{}] completes.", Thread.currentThread().getName());
			}
			doneSignal.countDown();
		}

	}
	
	private class Monitor implements Runnable {
		
		CountDownLatch doneSignal;
		AtomicBoolean doneIndicator;
		String name;
		
		Monitor(String name, CountDownLatch doneSignal, AtomicBoolean doneIndicator) {
			this.name = name;
			this.doneSignal = doneSignal;
			this.doneIndicator = doneIndicator;
		}

		@Override
		public void run() {
			try {
				doneSignal.await();
				doneIndicator.set(true);
			} catch (InterruptedException e) {
				// This should not happen
				if (logger.isDebugEnabled()) {
					logger.debug("Monitor thread [{}] is interrupted unexpected. Exception message is [{}]", name, e.getMessage());
					logger.debug("Exception detail: \n", e);
				}
			}

			
		}
		
	}
}