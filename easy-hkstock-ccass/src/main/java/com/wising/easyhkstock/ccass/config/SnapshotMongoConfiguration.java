package com.wising.easyhkstock.ccass.config;

import org.springframework.context.annotation.Configuration;

import com.wising.easyhkstock.common.config.DefaultMongoConfiguration;

@Configuration
public class SnapshotMongoConfiguration extends DefaultMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "snapshot";
	}

}
