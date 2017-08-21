package com.wising.easyhkstock.ccass.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.wising.easyhkstock.ccass.task.BuilderConfiguration;
import com.wising.easyhkstock.common.config.DefaultMongoConfiguration;

@Configuration
@Import(DefaultMongoConfiguration.class)

public class ApplicationConfiguration {

	@Bean
	@ConfigurationProperties("snapshot")
	public BuilderConfiguration getBuilder() {
		
		return new BuilderConfiguration();
	}
}
