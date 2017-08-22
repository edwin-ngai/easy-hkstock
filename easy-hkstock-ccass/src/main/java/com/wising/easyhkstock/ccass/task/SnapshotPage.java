package com.wising.easyhkstock.ccass.task;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.wising.easyhkstock.common.util.Utils;




public class SnapshotPage {

	private static final Logger logger = LoggerFactory.getLogger(SnapshotPage.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	private String basicInfoSelector = "#Table5 > tbody";
	private String summarySelector = "#pnlResultSummary > table > tbody";
	private String detailSelector = "#participantShareholdingList > tbody";

	private Document doc;
	private String stockCode;
	private LocalDate snapshotDate;
	private long totalIssuedShares = 0;
	private short intermediaryNumber = 0;
	private long intermediaryShareholding = 0;
	private short consentingInvestorNumber = 0;
	private long consentingShareholding = 0;
	private short nonConsentingInvestorNumber = 0;
	private long nonConsentingShareholding = 0;
	private Map<String, Long> detail = new HashMap<String, Long>();
	private boolean hasError = false;
	
	public SnapshotPage(String html) {
		
		Objects.requireNonNull(html);
		doc = Jsoup.parse(html);
		if (doc != null) {
			init();
		}
	}
	
	
	public String getStockCode() {
		return stockCode;
	}

	public LocalDate getSnapshotDate() {
		return snapshotDate;
	}

	public long getTotalIssuedShares() {
		return totalIssuedShares;
	}

	public short getIntermediaryNumber() {
		return intermediaryNumber;
	}

	public long getIntermediaryShareholding() {
		return intermediaryShareholding;
	}


	public short getConsentingInvestorNumber() {
		return consentingInvestorNumber;
	}


	public long getConsentingShareholding() {
		return consentingShareholding;
	}


	public short getNonConsentingInvestorNumber() {
		return nonConsentingInvestorNumber;
	}

	public long getNonConsentingShareholding() {
		return nonConsentingShareholding;
	}

	public Map<String, Long> getDetail() {
		return this.detail;
	}

	public boolean hasError() {
		return this.hasError;
	}
	
	private void init() {
		
		initBasicInfo();
		initSummary();
		initDetail();
	}
	
	private void initBasicInfo() {
		
		Element basicInfoTable = doc.select(basicInfoSelector).first();
		if (basicInfoTable != null) {
			List<String> basicInfo = Utils.getElementText(basicInfoTable);
			if (basicInfo.size() != 11) {
				logger.error("The basic info table of [{}:{}] has no necessary items: [{}]", stockCode, snapshotDate, basicInfo);
				hasError = true;
			}else {
				stockCode = basicInfo.get(4);
				String dateStr = basicInfo.get(2);
				if (StringUtils.isBlank(stockCode)) {
					logger.error("The stock code is blank");
					hasError = true;
				}
				if (StringUtils.isEmpty(dateStr)) {
					logger.error("The snapshot date is empty");
					hasError = true;
				}else {
					try {
						snapshotDate = LocalDate.parse(dateStr, formatter);
					} catch (DateTimeParseException e) {
						logger.error("the snapshot date is in wrong format: [{}]", dateStr);
						hasError = true;
					}
				}
			}
		}
	}
	
	private void initSummary() {
		
		Element summaryTable = doc.select(summarySelector).first();
		if (summaryTable != null) {
			List<String> summaryItems = Utils.getElementText(summaryTable);
			int itemSize = summaryItems.size();
			try {
				int intermediariesIndex = summaryItems.indexOf("Market Intermediaries");
				if (intermediariesIndex!=-1 && intermediariesIndex<itemSize-2) {
					intermediaryShareholding = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(intermediariesIndex+1)).longValue();
					intermediaryNumber = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(intermediariesIndex+2)).shortValue();
				}else {
					logger.error("The summary table of [{}:{}] has no necessary items: [{}]", stockCode, snapshotDate, summaryItems);
					hasError = true;
				}
				
				int consentingInvestorIndex = summaryItems.indexOf("Consenting Investor Participants");
				if (consentingInvestorIndex!=-1 && consentingInvestorIndex<itemSize-2) {
					consentingShareholding = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(consentingInvestorIndex+1)).longValue();
					consentingInvestorNumber = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(consentingInvestorIndex+2)).shortValue();
				}
				
				int nonConsentingInvestorIndex = summaryItems.indexOf("Non-consenting Investor Participants");
				if (nonConsentingInvestorIndex!=-1 && nonConsentingInvestorIndex<itemSize-2) {
					nonConsentingShareholding = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(nonConsentingInvestorIndex+1)).longValue();
					nonConsentingInvestorNumber = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(nonConsentingInvestorIndex+2)).shortValue();
				}else {
					logger.error("The summary table of [{}:{}] has no necessary items: [{}]", stockCode, snapshotDate, summaryItems);
					hasError = true;
				}
				
				int totalIssuedSharesIndex = summaryItems.indexOf("Total number of Issued Shares/Warrants/Units (last updated figure)");
				if (totalIssuedSharesIndex!=-1 && totalIssuedSharesIndex<itemSize-1) {
					totalIssuedShares = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(totalIssuedSharesIndex+1)).longValue();
				}else {
					logger.error("The summary table of [{}:{}] has no necessary items: [{}]", stockCode, snapshotDate, summaryItems);
					hasError = true;
				}

			} catch (ParseException e) {
				logger.error("The summary table has wrong item(s)");
				hasError = true;
			}
		}
	}
	
	private void initDetail() {
		
		Element detailTable = doc.select(detailSelector).first();
		if (detailTable != null) {
			List<Element> detailEtls = detailTable.children();
			if (detailEtls.size() > 3) {
				logger.debug("detail table has {} data rows", detailEtls.size()-3);
				for (int i = 3; i < detailEtls.size(); i++) {
					Element detailElt = detailEtls.get(i);
					List<String> texts = Utils.getElementText(detailElt);
					if (texts.size() < 4) {
						logger.error("The detail row of [{}:{}] has no necessary columns: [{}]", stockCode, snapshotDate, texts);
						hasError = true;
					} else {
						String participant = texts.get(0);
						if (StringUtils.isBlank(participant)) {
							participant = texts.get(1);
						}
						String shareholding = texts.get(3);
						if (StringUtils.isEmpty(participant) || StringUtils.isEmpty(shareholding)) {
							logger.error("Some column(s) is empty: [{},{}]", participant, shareholding);
							hasError = true;
						} else {
							try {
								detail.put(participant,NumberFormat.getInstance(Locale.US).parse(shareholding).longValue());
							} catch (ParseException e) {
								logger.error("participant shareholding is in wrong format: [{}]", shareholding);
								hasError = true;
							}
						}
					}
				}
				logger.debug("Get {} rows", detail.size());
			}else {
				logger.error("detail table has no data rows");
				hasError = true;
			}
		}
	}
}
