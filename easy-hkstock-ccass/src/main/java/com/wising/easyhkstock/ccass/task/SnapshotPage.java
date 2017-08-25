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
import org.apache.commons.lang3.Validate;
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
	private long totalIssuedAShares = 0;
	private short intermediaryNumber = 0;
	private long intermediaryShareholding = 0;
	private short consentingInvestorNumber = 0;
	private long consentingShareholding = 0;
	private short nonConsentingInvestorNumber = 0;
	private long nonConsentingShareholding = 0;
	private Map<String, Long> detail = new HashMap<String, Long>();
	private boolean hasError = false;
	private String identifier;
	
	public SnapshotPage(String html, String stockCode, LocalDate snapshotDate) {
		
		Objects.requireNonNull(html);
		Validate.notBlank(stockCode);
		Objects.requireNonNull(snapshotDate);
		this.stockCode = stockCode;
		this.snapshotDate = snapshotDate;
		this.identifier = stockCode + ":" + snapshotDate;
		doc = Jsoup.parse(html);
		if (doc != null) {
			init();
		}else {
			hasError = true;
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

	public long getTotalIssuedAShares() {
		return totalIssuedAShares;
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
		if (!hasError) {
			initSummary();
			initDetail();
		}
	}
	
	private void initBasicInfo() {
		
		Element basicInfoTable = doc.select(basicInfoSelector).first();
		if (basicInfoTable != null) {
			List<String> basicInfo = Utils.getElementText(basicInfoTable);
			if (basicInfo.size() != 11) {
				logger.error("[{}]: The basic info table has no necessary items: [{}]", identifier, basicInfo);
				hasError = true;
			}else {
				String stockCodeOfPage = basicInfo.get(4);
				if (StringUtils.isBlank(stockCodeOfPage)) {
					logger.error("[{}]: The stock code of basic info table is blank.", identifier);
					hasError = true;
				}else if (!stockCodeOfPage.equals(stockCode)) {
					logger.error("[{}]: The stock code [{}] is not expected.", identifier, stockCodeOfPage);
					hasError = true;
				}
			}
		}else {
			logger.error("[{}]: Cannot find basic info table", identifier);
			hasError = true;
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
					logger.error("[{}]: The summary table has no market intermediaries: [{}]", identifier, summaryItems);
				}
				
				int consentingInvestorIndex = summaryItems.indexOf("Consenting Investor Participants");
				if (consentingInvestorIndex!=-1 && consentingInvestorIndex<itemSize-2) {
					consentingShareholding = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(consentingInvestorIndex+1)).longValue();
					consentingInvestorNumber = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(consentingInvestorIndex+2)).shortValue();
				}else {
					logger.debug("[{}]: The summary table of has no consenting investor participants: [{}]", identifier, summaryItems);
				}
				
				int nonConsentingInvestorIndex = summaryItems.indexOf("Non-consenting Investor Participants");
				if (nonConsentingInvestorIndex!=-1 && nonConsentingInvestorIndex<itemSize-2) {
					nonConsentingShareholding = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(nonConsentingInvestorIndex+1)).longValue();
					nonConsentingInvestorNumber = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(nonConsentingInvestorIndex+2)).shortValue();
				}else {
					logger.debug("[{}]: The summary table of has no non-consenting investor participants: [{}]", identifier, summaryItems);
				}
				
				if (intermediaryNumber==0 && consentingInvestorNumber==0 && nonConsentingInvestorNumber==0) {
					logger.error("[{}]: The summary table has no any participants: [{}]", identifier, summaryItems);
					hasError = true;
				}

				int totalIssuedSharesIndex = summaryItems.indexOf("Total number of Issued Shares/Warrants/Units (last updated figure)");
				if (totalIssuedSharesIndex!=-1 && totalIssuedSharesIndex<itemSize-1) {
					totalIssuedShares = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(totalIssuedSharesIndex+1)).longValue();
				}else {
					logger.debug("[{}]: The summary table has no total number of issued shares: [{}]", identifier, summaryItems);
					hasError = true;
				}
//				int totalIssuedASharesIndex = summaryItems.indexOf("Total number of A Shares listed and traded");
//				if (totalIssuedASharesIndex!=-1 && totalIssuedASharesIndex<itemSize-2) {
//					totalIssuedAShares = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(totalIssuedSharesIndex+2)).longValue();
//				}else {
//					logger.debug("[{}]: The summary table has no total number of issued A shares: [{}]", identifier, summaryItems);
//				}
//				if (totalIssuedShares==0 && totalIssuedAShares==0) {
//					logger.error("[{}]: The summary table has no any total issued shares: [{}]", identifier, summaryItems);
//					hasError = true;
//				}
				
			} catch (ParseException e) {
				logger.error("[{}]: The summary table has wrong item(s). Underlying exception is [{}].", identifier, e);
				hasError = true;
			}
		}else {
			logger.error("[{}]: Cannot find summary table", identifier);
			hasError = true;
		}
	}
	
	private void initDetail() {
		
		Element detailTable = doc.select(detailSelector).first();
		if (detailTable != null) {
			List<Element> detailEtls = detailTable.children();
			if (detailEtls.size() > 3) {
				logger.debug("[{}]: Detail table has {} data rows", identifier, detailEtls.size()-3);
				for (int i = 3; i < detailEtls.size(); i++) {
					Element detailElt = detailEtls.get(i);
					List<String> texts = Utils.getElementText(detailElt);
					if (texts.size() < 4) {
						logger.error("[{}]: The detail row has no necessary columns: [{}]", identifier, texts);
						hasError = true;
					} else {
						String participant = texts.get(0);
						if (StringUtils.isBlank(participant)) {
							participant = texts.get(1);
						}
						String shareholding = texts.get(3);
						if (StringUtils.isEmpty(participant) || StringUtils.isEmpty(shareholding)) {
							logger.error("[{}]: Some column(s) is empty: [{},{}]", identifier, participant, shareholding);
							hasError = true;
						} else {
							try {
								detail.put(participant,NumberFormat.getInstance(Locale.US).parse(shareholding).longValue());
							} catch (ParseException e) {
								logger.error("[{}]: Participant shareholding is in wrong format: [{}]", identifier, shareholding);
								hasError = true;
							}
						}
					}
				}
				logger.debug("[{}]: Get {} rows", identifier, detail.size());
			}else {
				logger.error("[{}]: Detail table has no data rows", identifier);
				hasError = true;
			}
		}
	}
}
