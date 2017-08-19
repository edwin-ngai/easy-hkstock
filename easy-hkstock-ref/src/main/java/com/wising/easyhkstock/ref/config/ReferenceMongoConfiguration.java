package com.wising.easyhkstock.ref.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.wising.easyhkstock.common.config.DefaultMongoConfiguration;

@Configuration
@EnableMongoRepositories(basePackages="com.wising.easyhkstock")
public class ReferenceMongoConfiguration extends DefaultMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "reference";
	}

}
