package com.wising.easyhkstock.ref.task.deprecated;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.wising.easyhkstock.common.util.Utils;

public class CompanyListPage {

	private static final Logger logger = LoggerFactory.getLogger(CompanyListPage.class);
	private String tableSelector = ".table_grey_border";
	private Document doc;
	private Map<String, String> stocks = new HashMap<String, String>();

	
	public CompanyListPage(String html) {
		
		Objects.requireNonNull(html);
		doc = Jsoup.parse(html);
		if (doc != null) {
			init();
		}
	}
	
	public Map<String, String> getStocks() {
		return this.stocks;
	}
	
	private void init() {
		Element table = doc.select(tableSelector).first();
		if (table != null) {
			List<Element> stockElts = table.child(0).children();
			logger.debug("Stock table has {} children", stockElts.size());
			if (stockElts.size() > 1) {
				// start from 2nd node, as the first one is table header
				for (int i = 1; i < stockElts.size(); i++) {
					Element stockElt = stockElts.get(i);
					List<String> texts = Utils.getElementText(stockElt);
					if (texts.size() < 2) {
						// TODO: something wrong here, and how to handle?
						logger.error("The stock row has no necessary columns");
					} else {
						String stockCode = texts.get(0);
						String name = texts.get(1);
						if (StringUtils.isEmpty(stockCode) || StringUtils.isEmpty(name)) {
							// TODO: something wrong here
							logger.error("Some column(s) is empty: [{},{}]", stockCode, name);
						} else {
							stocks.put(stockCode, name);
						}
					}
				}
			}
			logger.debug("Get {} stocks", stocks.size());
		}
	}

}
