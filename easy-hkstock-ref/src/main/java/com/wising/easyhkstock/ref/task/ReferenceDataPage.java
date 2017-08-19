package com.wising.easyhkstock.ref.task;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.Validate;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import com.wising.easyhkstock.common.util.Utils;

public class ReferenceDataPage {

	private static final Logger logger = LoggerFactory.getLogger(ReferenceDataPage.class);
	private Map<String, String> data = new HashMap<String, String>();
	private boolean hasError = false;
	private String pageName;
	private String tableSelector;
	private Document doc;

	public ReferenceDataPage(String pageName, String tableSelector, String html) {
		
		Validate.notBlank(pageName);
		Validate.notBlank(tableSelector);
		Validate.notBlank(html);
		this.pageName = pageName;
		this.tableSelector = tableSelector;
		doc = Jsoup.parse(html);
		if (doc != null) {
			init();
		}
	}
	
	public Map<String, String> getData() {
		return this.data;
	}
	
	public boolean hasError() {
		return this.hasError;
	}
	
	private void init() {
		Element table = doc.select(tableSelector).first();
		if (table != null) {
			List<Element> elements = table.children();
			if (elements.size() > 2) {
				logger.debug("[{}] has [{}] rows of data", pageName, elements.size()-2);
				for (int i = 2; i < elements.size(); i++) {
					Element element = elements.get(i);
					List<String> texts = Utils.getElementText(element);
					if (texts.size() < 2) {
						logger.error("The data row [{}] of {} has no necessary columns", texts, pageName);
//						hasError = true;
					} else {
						String code = texts.get(0);
						String name = texts.get(1);
						if (StringUtils.isEmpty(code) || StringUtils.isEmpty(name)) {
							logger.error("Some column(s) of [{}] is empty: [{},{}].", pageName, code, name);
							hasError = true;
						} else {
							data.put(code, name);
						}
					}
				}
				logger.debug("Extracted [{}] rows of data for [{}].", data.size(), pageName);
			}else {
				logger.debug("[{}] has no data row.", pageName);
				hasError = true;
			}
		}
	}

}
