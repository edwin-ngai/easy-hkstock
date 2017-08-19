package com.wising.easyhkstock.ref.task.deprecated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.wising.easyhkstock.common.util.Utils;

public class StockListPage {

	private static final Logger logger = LoggerFactory.getLogger(StockListPage.class);
	private String tableSelector = "#form1 > table > tbody";
	private Document doc;
	private Map<String, String> stocks = new HashMap<String, String>();
	private boolean hasError = false;

	
	public StockListPage(String html) {
		
		Validate.notBlank(html);
		doc = Jsoup.parse(html);
		if (doc != null) {
			init();
		}
	}
	
	public Map<String, String> getStocks() {
		return this.stocks;
	}
	
	public boolean hasError() {
		return this.hasError;
	}
	
	private void init() {
		Element table = doc.select(tableSelector).first();
		if (table != null) {
			List<Element> stockElts = table.children();
			if (stockElts.size() > 2) {
				logger.debug("Stock list has {} stocks", stockElts.size()-2);
				for (int i = 2; i < stockElts.size(); i++) {
					Element stockElt = stockElts.get(i);
					List<String> texts = Utils.getElementText(stockElt);
					if (texts.size() < 2) {
						logger.error("The stock row has no necessary columns");
						hasError = true;
					} else {
						String stockCode = texts.get(0);
						String name = texts.get(1);
						if (StringUtils.isEmpty(stockCode) || StringUtils.isEmpty(name)) {
							logger.error("Some column(s) is empty: [{},{}]", stockCode, name);
							hasError = true;
						} else {
							stocks.put(stockCode, name);
						}
					}
				}
				logger.debug("Get {} stocks", stocks.size());
			}else {
				logger.debug("Stock list has no data row.");
				hasError = true;
			}
		}
	}

}
