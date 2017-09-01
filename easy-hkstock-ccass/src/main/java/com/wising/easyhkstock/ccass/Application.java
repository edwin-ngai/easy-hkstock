package com.wising.easyhkstock.ccass;


import java.time.LocalDate;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.List;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;

import com.wising.easyhkstock.ccass.config.ApplicationConfiguration;
import com.wising.easyhkstock.ccass.domain.SnapshotDetail;
import com.wising.easyhkstock.ccass.domain.SnapshotSummary;
import com.wising.easyhkstock.ccass.domain.repository.SnapshotDetailRepository;
import com.wising.easyhkstock.ccass.domain.repository.SnapshotSummaryRepository;
import com.wising.easyhkstock.ccass.task.BuilderConfiguration;
import com.wising.easyhkstock.ccass.task.MongoDispatcher;
import com.wising.easyhkstock.ccass.task.SnapshotDataBuilder;
import com.wising.easyhkstock.common.domain.Stock;
import com.wising.easyhkstock.common.repository.StockRepository;
import com.wising.easyhkstock.common.task.DataTask;


@EnableAutoConfiguration
@Import(ApplicationConfiguration.class)
public class Application implements ApplicationRunner {

	private static final Logger logger = LoggerFactory.getLogger(Application.class);
	
	@Autowired
	private SnapshotDetailRepository detailRepository;
	@Autowired
	private SnapshotSummaryRepository summaryRepository;
	@Autowired
	private StockRepository stockRepository;
	@Autowired
	private ApplicationConfiguration configuration;

	public static void main(String[] args) {
		
		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		
		BuilderConfiguration builderConf = configuration.getBuilder();
		if (builderConf.getStocks().isEmpty()) {
			List<String> stocks = stockRepository.findAll().stream().map(Stock::getCode).filter(code->!code.startsWith("9"))
					.collect(Collectors.toList());
			if (stocks!=null) {
				builderConf.setStocks(stocks);
				logger.debug("Will process [{}] stocks", stocks.size());
			}
		}
		
		SnapshotDataBuilder snapshotBuilder = new SnapshotDataBuilder(configuration.getBuilder());
		DataTask<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> snapshotTask
			= new DataTask<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>(snapshotBuilder);
		snapshotTask.addDispatcher(new MongoDispatcher(summaryRepository, detailRepository));
		
		if (args.containsOption("once")) {
			try {
				LocalDate startDate = builderConf.getStartDate();
				LocalDate endDate = builderConf.getEndDate();
				while (startDate.isBefore(endDate) || startDate.equals(endDate)) {
					builderConf.setStartDate(startDate);
					LocalDate newEndDate = startDate.plusDays(1);
					builderConf.setEndDate(newEndDate);
					snapshotTask.run();
					startDate = newEndDate;
				}
			}finally {
				snapshotBuilder.terminate();
			}
		}else {
			ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
			scheduler.setPoolSize(1);
			scheduler.initialize();
			scheduler.schedule(snapshotTask, new CronTrigger("0 0 0 * * *"));
		}
	}

}
