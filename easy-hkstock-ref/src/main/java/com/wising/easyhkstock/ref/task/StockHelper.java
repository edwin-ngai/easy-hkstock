package com.wising.easyhkstock.ref.task;

import com.wising.easyhkstock.common.domain.Stock;

public class StockHelper extends BuilderHelper<Stock> {

	@Override
	public Stock createData(String code, String enName, String scName, String tcName) {
		
		return new Stock(code, enName, scName, tcName);
	}

}
