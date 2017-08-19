package com.wising.easyhkstock.ref.task;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public abstract class ReferenceDataHelper<T> {

	private DateFormat df = new SimpleDateFormat("yyyyMMdd");

	public abstract T createData(String code, String enName, String scName, String tcName);
	
	public abstract URI getEnURI();
	
	public abstract URI getScURI();
	
	public abstract URI getTcURI();
	
	public abstract String getPageName();
	
	public String getTableSelector() {
		return "#form1 > table > tbody";
	}
	
	protected URI getURI(String baseURI) {
		
		String today = df.format(new Date());
		String urlParam = "&ShareholdingDate=" + today;
		return URI.create(baseURI + urlParam);
	}
}
