package com.wising.easyhkstock.ref.task;

import java.net.URI;

import com.wising.easyhkstock.common.domain.Stock;

public class StockHelper extends ReferenceDataHelper<Stock> {

	private static final String enUrlBase = "http://www.hkexnews.hk/sdw/search/stocklist.aspx?SortBy=StockCode";
	private static final String scUrlBase = "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/stocklist_c.aspx?SortBy=StockCode";
	private static final String tcUrlBase = "http://www.hkexnews.hk/sdw/search/stocklist_c.aspx?SortBy=StockCode";

	@Override
	public Stock createData(String code, String enName, String scName, String tcName) {
		
		return new Stock(code, enName, scName, tcName);
	}

	@Override
	public URI getEnURI() {

		return getURI(enUrlBase);
	}

	@Override
	public URI getScURI() {
		return getURI(scUrlBase);

	}

	@Override
	public URI getTcURI() {
		return getURI(tcUrlBase);
	}

	@Override
	public String getPageName() {
		return "Stock List";
	}

}
