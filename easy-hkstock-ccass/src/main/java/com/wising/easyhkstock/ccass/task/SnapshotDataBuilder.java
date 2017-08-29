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
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

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
import org.springframework.web.client.RestTemplate;

import com.wising.easyhkstock.ccass.domain.SnapshotDetail;
import com.wising.easyhkstock.ccass.domain.SnapshotSummary;
import com.wising.easyhkstock.common.task.DataBuilder;

public class SnapshotDataBuilder implements DataBuilder<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> {

	private static final Logger logger = LoggerFactory.getLogger(SnapshotDataBuilder.class);

	private RestTemplate restTpl;
	private ThreadPoolTaskExecutor executor;
	private BuilderConfiguration configuration;
	private boolean terminated;

	public SnapshotDataBuilder(BuilderConfiguration configuration) {
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
		LocalDate startDate = configuration.getStartDate();
		LocalDate endDate = configuration.getEndDate();
		List<String> stockList = configuration.getStocks();
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

	private HttpEntity<MultiValueMap<String, String>> getHttpEntity(String year, String month, String day,
			String stockCode) {
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
		body.add("txtStockCode", stockCode);
		return new HttpEntity<MultiValueMap<String, String>>(body, headers);
	}

}
