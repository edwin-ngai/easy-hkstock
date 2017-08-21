package com.wising.easyhkstock.ref;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.wising.easyhkstock.common.domain.Participant;
import com.wising.easyhkstock.common.domain.Stock;
import com.wising.easyhkstock.common.task.DataTask;
import com.wising.easyhkstock.common.task.MongoDispatcher;
import com.wising.easyhkstock.ref.config.ApplicationConfiguration;
import com.wising.easyhkstock.ref.task.BuilderHelper;
import com.wising.easyhkstock.ref.task.ParticipantHelper;
import com.wising.easyhkstock.ref.task.ReferenceDataBuilder;
import com.wising.easyhkstock.ref.task.StockHelper;

@EnableAutoConfiguration
@Import(ApplicationConfiguration.class)
public class Application implements CommandLineRunner {

	@Autowired
	private MongoRepository<Stock, String> stockRepository;
	@Autowired
	private MongoRepository<Participant, String> participantRepository;
	@Autowired
	private ApplicationConfiguration configuration;
	
	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
		BuilderHelper<Stock> stockHelper = new StockHelper();
		ReferenceDataBuilder<Stock> stockBuilder = new ReferenceDataBuilder<Stock>(
				configuration.getStock(), stockHelper);
		DataTask<Stock> stockTask = new DataTask<Stock>(stockBuilder);
		stockTask.addDispatcher(new MongoDispatcher<Stock, String>(stockRepository));
		stockTask.run();
		
		BuilderHelper<Participant> participantHelper = new ParticipantHelper();
		ReferenceDataBuilder<Participant> participantBuilder = new ReferenceDataBuilder<Participant>(
				configuration.getParticipant(), participantHelper);
		DataTask<Participant> participantTask = new DataTask<Participant>(participantBuilder);
		participantTask.addDispatcher(new MongoDispatcher<Participant, String>(participantRepository));
		participantTask.run();
		
//		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//		scheduler.setPoolSize(2);
//		scheduler.initialize();
//		scheduler.schedule(stockTask, new CronTrigger("0 0 0 * * *"));
//		scheduler.schedule(participantTask, new CronTrigger("0 15 0 * * *"));
	}
}
