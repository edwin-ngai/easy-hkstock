package com.wising.easyhkstock.ref.config;

import java.util.Objects;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.wising.easyhkstock.common.config.DefaultMongoConfiguration;
import com.wising.easyhkstock.ref.task.BuilderConfiguration;

@Configuration
@Import(DefaultMongoConfiguration.class)
@ConfigurationProperties("reference")
public class ApplicationConfiguration {

	private BuilderConfiguration stock;
	private BuilderConfiguration participant;

	public BuilderConfiguration getStock() {
		return stock;
	}

	public void setStock(BuilderConfiguration stock) {
		Objects.requireNonNull(stock);
		this.stock = stock;
	}

	public BuilderConfiguration getParticipant() {
		return participant;
	}

	public void setParticipant(BuilderConfiguration participant) {
		Objects.requireNonNull(participant);
		this.participant = participant;
	}

}
