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

public class ParticipantListPage {

	private static final Logger logger = LoggerFactory.getLogger(ParticipantListPage.class);
	private String tableSelector = "#form1 > table > tbody";
	private Document doc;
	private Map<String, String> participants = new HashMap<String, String>();
	private boolean hasError = false;

	
	public ParticipantListPage(String html) {
		
		Validate.notBlank(html);
		doc = Jsoup.parse(html);
		if (doc != null) {
			init();
		}
	}
	
	public Map<String, String> getParticipants() {
		return this.participants;
	}
	
	public boolean hasError() {
		return this.hasError;
	}
	
	private void init() {
		Element table = doc.select(tableSelector).first();
		if (table != null) {
			List<Element> participantElts = table.children();
			if (participantElts.size() > 2) {
				logger.debug("Participant list has {} participants", participantElts.size()-2);
				for (int i = 2; i < participantElts.size(); i++) {
					Element participantElt = participantElts.get(i);
					List<String> texts = Utils.getElementText(participantElt);
					if (texts.size() < 2) {
						logger.error("The participant row has no necessary columns");
						hasError = true;
					} else {
						String id = texts.get(0);
						String name = texts.get(1);
						if (StringUtils.isEmpty(id) || StringUtils.isEmpty(name)) {
							logger.error("Some column(s) is empty: [{},{}]", id, name);
							hasError = true;
						} else {
							participants.put(id, name);
						}
					}
				}
				logger.debug("Get {} participants", participants.size());
			}else {
				logger.debug("Participant list has no data row.");
				hasError = true;
			}
		}
	}

}
