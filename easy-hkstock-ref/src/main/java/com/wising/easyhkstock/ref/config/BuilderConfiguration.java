package com.wising.easyhkstock.ref.config;

import java.net.URI;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BuilderConfiguration {

	private URI enUri, tcUri, scUri;
	private String pageName, tableSelector;


	public URI getEnUri() {
		return enUri;
	}

	public void setEnUri(URI enUri) {
		Objects.requireNonNull(enUri);
		this.enUri = enUri;
	}

	public URI getTcUri() {
		return tcUri;
	}

	public void setTcUri(URI tcUri) {
		Objects.requireNonNull(tcUri);
		this.tcUri = tcUri;
	}

	public URI getScUri() {
		return scUri;
	}

	public void setScUri(URI scUri) {
		Objects.requireNonNull(scUri);
		this.scUri = scUri;
	}

	public String getPageName() {
		return pageName;
	}

	public void setPageName(String pageName) {
		Validate.notBlank(pageName);
		this.pageName = pageName;
	}

	public String getTableSelector() {
		return tableSelector;
	}

	public void setTableSelector(String tableSelector) {
		Validate.notBlank(tableSelector);
		this.tableSelector = tableSelector;
	}

}
