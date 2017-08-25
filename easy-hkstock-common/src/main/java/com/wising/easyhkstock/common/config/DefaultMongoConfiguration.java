package com.wising.easyhkstock.common.config;


import org.apache.commons.lang3.Validate;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

@Configuration
@EnableMongoRepositories(basePackages="com.wising.easyhkstock")
@ConfigurationProperties("mongo")
public class DefaultMongoConfiguration extends AbstractMongoConfiguration {

	private String databaseName = "test";
	private String uri = "mongodb://localhost";
	
	@Override
	protected String getDatabaseName() {
		return databaseName;
	}

	public void setUri(String uri) {
		Validate.notBlank(uri);
		this.uri = uri;
	}

	public void setDatabaseName(String databaseName) {
		Validate.notBlank(databaseName);
		this.databaseName = databaseName;
	}

	@Override
	public Mongo mongo() throws Exception {
		return new MongoClient(new MongoClientURI(uri));
	}
	
	@Bean
	public MappingMongoConverter mappingMongoConverter() throws Exception {
		MappingMongoConverter converter = super.mappingMongoConverter();
		converter.setTypeMapper(new DefaultMongoTypeMapper(null));
		converter.setMapKeyDotReplacement("");
		return converter;
	}

}
