package com.wising.easyhkstock.common.config;


import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@Configuration
public class DefaultMongoConfiguration extends AbstractMongoConfiguration {

	@Override
	protected String getDatabaseName() {
		return "test";
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient();
	}
	
	@Bean
	public MappingMongoConverter mappingMongoConverter() throws Exception {
		MappingMongoConverter converter = super.mappingMongoConverter();
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		return converter;
	}

}
