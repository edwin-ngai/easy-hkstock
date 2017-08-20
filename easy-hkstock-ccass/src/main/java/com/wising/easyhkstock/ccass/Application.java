package com.wising.easyhkstock.ccass;


import java.util.AbstractMap.SimpleImmutableEntry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Import;

import com.wising.easyhkstock.ccass.config.ApplicationProperties;
import com.wising.easyhkstock.ccass.config.MongoConfiguration;
import com.wising.easyhkstock.ccass.domain.SnapshotDetail;
import com.wising.easyhkstock.ccass.domain.SnapshotSummary;
import com.wising.easyhkstock.ccass.domain.repository.SnapshotDetailRepository;
import com.wising.easyhkstock.ccass.domain.repository.SnapshotSummaryRepository;
import com.wising.easyhkstock.ccass.task.MongoDispatcher;
import com.wising.easyhkstock.ccass.task.SnapshotDataBuilder;
import com.wising.easyhkstock.common.task.DataTask;

@EnableAutoConfiguration
@Import(value=MongoConfiguration.class)
@EnableConfigurationProperties(ApplicationProperties.class)
public class Application implements CommandLineRunner {

	@Autowired
	private SnapshotDetailRepository detailRepository;
	@Autowired
	private SnapshotSummaryRepository summaryRepository;

	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		SnapshotDataBuilder snapshotBuilder = new SnapshotDataBuilder();
		DataTask<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>> snapshotTask
			= new DataTask<SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>>(snapshotBuilder);
		snapshotTask.addDispatcher(new MongoDispatcher(summaryRepository, detailRepository));
		snapshotTask.run();
		
		
		
//		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//		scheduler.setPoolSize(2);
//		scheduler.initialize();
//		scheduler.schedule(stockTask, new CronTrigger("0 0 0 * * *"));
//		scheduler.schedule(participantTask, new CronTrigger("0 15 0 * * *"));
	}
}
