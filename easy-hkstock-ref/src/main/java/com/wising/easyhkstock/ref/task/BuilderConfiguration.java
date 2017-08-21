package com.wising.easyhkstock.ref.task;

import java.net.URI;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.Validate;
import org.springframework.context.annotation.Configuration;

public class BuilderConfiguration {

	private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	private String enUriBase, tcUriBase, scUriBase, pageName, tableSelector;


	public URI getEnUri() {
		return URI.create(enUriBase+getDateParam());
	}

	public URI getTcUri() {
		return URI.create(tcUriBase+getDateParam());
	}

	public URI getScUri() {
		return URI.create(scUriBase+getDateParam());
	}

	
	public void setEnUriBase(String enUriBase) {
		Validate.notBlank(enUriBase);
		this.enUriBase = enUriBase;
	}

	public void setTcUriBase(String tcUriBase) {
		Validate.notBlank(tcUriBase);
		this.tcUriBase = tcUriBase;
	}

	public void setScUriBase(String scUriBase) {
		Validate.notBlank(scUriBase);
		this.scUriBase = scUriBase;
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

	private String getDateParam() {
		return "&ShareholdingDate="+LocalDate.now().format(formatter);
	}
}
