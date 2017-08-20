package com.wising.easyhkstock.ccass.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import com.wising.easyhkstock.common.config.DefaultMongoConfiguration;

@Configuration
public class MongoConfiguration extends DefaultMongoConfiguration {

	@Autowired
	private ApplicationProperties applicationProperties;
	
	@Override
	protected String getDatabaseName() {
		return applicationProperties.getDatabase().getDatabaseName();
	}

}
