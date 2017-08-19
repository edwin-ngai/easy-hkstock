package com.wising.easyhkstock.ccass.task;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.web.client.RestTemplate;

import com.wising.easyhkstock.ccass.domain.SnapshotDetail;
import com.wising.easyhkstock.ccass.domain.SnapshotSummary;
import com.wising.easyhkstock.common.task.DataBuilder;

public class SnapshotDataBuilder implements DataBuilder<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> {

	private static final Logger logger = LoggerFactory.getLogger(SnapshotDataBuilder.class);

	private ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
	private RestTemplate restTpl = new RestTemplate();
	private SnapshotDataHelper helper = new SnapshotDataHelper();

	public SnapshotDataBuilder() {
		executor.setCorePoolSize(20);
		executor.setQueueCapacity(20);
		executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
		executor.initialize();
		restTpl.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
	}

	@Override
	public List<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> build() {

		List<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> result = new ArrayList<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>();
		LocalDate startDate = helper.getStartDate();
		LocalDate today = LocalDate.now();
		List<String> stockList = helper.getStockList();
		while (startDate.isBefore(today)) {
			final LocalDate date = startDate;
			List<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> subResult = new ArrayList<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>();
			List<FutureTask<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>> tasks 
				= new ArrayList<FutureTask<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>>(stockList.size());
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
					subResult.add(task.get());
				} catch (Exception e) {
					logger.error("Unexpected errors encountered.", e);
					//TODO should send notification or retry
				}
			});
			result.addAll(subResult);
			startDate = startDate.plusDays(1);
		}

		return Collections.unmodifiableList(result);

	}

	private SimpleImmutableEntry<SnapshotSummary, SnapshotDetail> getData(LocalDate date,
			String stockCode) {

		SimpleImmutableEntry<SnapshotSummary, SnapshotDetail> result = null;
		String year = String.valueOf(date.getYear());
		String month = String.valueOf(date.getMonthValue());
		if (month.length()==1) {
			month = "0"+month;
		}
		String day = String.valueOf(date.getDayOfMonth());
		ResponseEntity<String> response = restTpl.postForEntity(helper.getURI(),
				helper.getHttpEntity(year, month, day, stockCode), String.class);
		if (response != null && HttpStatus.OK.equals(response.getStatusCode())) {
			String html = response.getBody();
			SnapshotPage page = new SnapshotPage(html);
			if (!page.hasError()) {
				String stockCodeOfPage = page.getStockCode();
				LocalDate snapshotDate = page.getSnapshotDate();
				long totalIssuedShares = page.getTotalIssuedShares();
				long intermediaryShareholding = page.getIntermediaryShareholding();
				short intermediaryNumber = page.getIntermediaryNumber();
				long nonConsentingShareholding = page.getNonConsentingShareholding();
				short nonConsentingInvestorNumber = page.getNonConsentingInvestorNumber();
				Map<String, Long> details = page.getDetail();
				SnapshotSummary summary = new SnapshotSummary(stockCodeOfPage, snapshotDate, totalIssuedShares,
						intermediaryNumber, intermediaryShareholding, nonConsentingInvestorNumber,
						nonConsentingShareholding);
				SnapshotDetail detail = new SnapshotDetail(stockCodeOfPage, snapshotDate, details);
				result = new SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>(summary, detail);
			}
		}

		return result;

	}

}
