package com.wising.easyhkstock.ccass.task;

import java.text.NumberFormat;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
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

import com.wising.easyhkstock.ccass.domain.SnapshotDetail;
import com.wising.easyhkstock.ccass.domain.SnapshotSummary;
import com.wising.easyhkstock.common.util.Utils;

public class SnapshotPage {

	private static final Logger logger = LoggerFactory.getLogger(SnapshotPage.class);
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
//	private String basicInfoSelector = "#Table5 > tbody";
	private String summarySelector = "#pnlResultSummary > table > tbody";
	private String detailSelector = "#participantShareholdingList > tbody";

	private Document doc;
	private String stockCode;
	private LocalDate snapshotDate;

	private SnapshotSummary summary;
	private SnapshotDetail detai;

	public SnapshotPage(String html, String stockCode, LocalDate snapshotDate) {

		Objects.requireNonNull(html);
		Validate.notBlank(stockCode);
		Objects.requireNonNull(snapshotDate);
		this.stockCode = stockCode;
		this.snapshotDate = snapshotDate;
		if (logger.isDebugEnabled()) {
			logger.debug("[{}:{}]: Start to parse html.", stockCode, snapshotDate);
		}
		doc = Jsoup.parse(html);
		if (doc != null) {
			this.summary = initSummary();
			if (this.summary != null) {
				this.detai = initDetail();
			}
		}
	}

	public String getStockCode() {
		return stockCode;
	}

	public LocalDate getSnapshotDate() {
		return snapshotDate;
	}

	public SnapshotSummary getSummary() {
		return summary;
	}

	public SnapshotDetail getDetai() {
		return detai;
	}



	private SnapshotSummary initSummary() {

		if (logger.isDebugEnabled()) {
			logger.debug("[{}:{}]: Starting to init summary info.", stockCode, snapshotDate);
		}
		SnapshotSummary result = null;

		Element summaryTable = doc.select(summarySelector).first();
		if (summaryTable == null) {
			if (logger.isErrorEnabled()) {
				logger.error("[{}:{}]: Cannot find summary table.", stockCode, snapshotDate);
			}
		}else {
			List<String> summaryItems = Utils.getElementText(summaryTable);
			int itemSize = summaryItems.size();
			try {
				long totalIssuedShares = 0;
				short intermediaryNumber = 0;
				long intermediaryShareholding = 0;
				short consentingInvestorNumber = 0;
				long consentingShareholding = 0;
				short nonConsentingInvestorNumber = 0;
				long nonConsentingShareholding = 0;
				boolean hasError = false;

				int intermediariesIndex = summaryItems.indexOf("Market Intermediaries");
				if (intermediariesIndex != -1 && intermediariesIndex < itemSize - 2) {
					intermediaryShareholding = NumberFormat.getInstance(Locale.US)
							.parse(summaryItems.get(intermediariesIndex + 1)).longValue();
					intermediaryNumber = NumberFormat.getInstance(Locale.US)
							.parse(summaryItems.get(intermediariesIndex + 2)).shortValue();
				} else if (logger.isDebugEnabled()) {
					logger.debug("[{}:{}]: Summary table has no market intermediaries: [{}].", stockCode, snapshotDate,
							summaryItems);
				}

				int consentingInvestorIndex = summaryItems.indexOf("Consenting Investor Participants");
				if (consentingInvestorIndex != -1 && consentingInvestorIndex < itemSize - 2) {
					consentingShareholding = NumberFormat.getInstance(Locale.US)
							.parse(summaryItems.get(consentingInvestorIndex + 1)).longValue();
					consentingInvestorNumber = NumberFormat.getInstance(Locale.US)
							.parse(summaryItems.get(consentingInvestorIndex + 2)).shortValue();
				} else if (logger.isDebugEnabled()) {
					logger.debug("[{}:{}]: Summary table has no consenting investor participants: [{}].", stockCode,
							snapshotDate, summaryItems);
				}

				int nonConsentingInvestorIndex = summaryItems.indexOf("Non-consenting Investor Participants");
				if (nonConsentingInvestorIndex != -1 && nonConsentingInvestorIndex < itemSize - 2) {
					nonConsentingShareholding = NumberFormat.getInstance(Locale.US)
							.parse(summaryItems.get(nonConsentingInvestorIndex + 1)).longValue();
					nonConsentingInvestorNumber = NumberFormat.getInstance(Locale.US)
							.parse(summaryItems.get(nonConsentingInvestorIndex + 2)).shortValue();
				} else if (logger.isDebugEnabled()) {
					logger.debug("[{}:{}]: Summary table has no non-consenting investor participants: [{}].", stockCode,
							snapshotDate, summaryItems);
				}

				if (intermediaryNumber == 0 && consentingInvestorNumber == 0 && nonConsentingInvestorNumber == 0) {
					if (logger.isErrorEnabled()) {
						logger.error("[{}:{}]: Summary table has no any participants: [{}].", stockCode, snapshotDate,
								summaryItems);
					}
					hasError = true;
				}

				int totalIssuedSharesIndex = summaryItems
						.indexOf("Total number of Issued Shares/Warrants/Units (last updated figure)");
				if (totalIssuedSharesIndex != -1 && totalIssuedSharesIndex < itemSize - 1) {
					totalIssuedShares = NumberFormat.getInstance(Locale.US)
							.parse(summaryItems.get(totalIssuedSharesIndex + 1)).longValue();
				} else {
					if (logger.isErrorEnabled()) {
						logger.error("[{}:{}]: Summary table has no total number of issued shares: [{}].", stockCode,
								snapshotDate, summaryItems);
					}
					hasError = true;
				}

				if (!hasError) {
					result = new SnapshotSummary(stockCode, snapshotDate, totalIssuedShares, intermediaryNumber,
							intermediaryShareholding, consentingInvestorNumber, consentingShareholding,
							nonConsentingInvestorNumber, nonConsentingShareholding);
				}
			} catch (ParseException e) {
				if (logger.isErrorEnabled()) {
					logger.error("[{}:{}]: Summary table has wrong item(s): [{}]. Underlying exception is [{}].", stockCode,
							snapshotDate, summaryItems, e);
				}
			}
		}
		return result;
	}

	private SnapshotDetail initDetail() {
		
		if (logger.isDebugEnabled()) {
			logger.debug("[{}:{}]: Starting to init detail info.", stockCode, snapshotDate);
		}
		SnapshotDetail result = null;
		
		Element detailTable = doc.select(detailSelector).first();
		if (detailTable == null) {
			if (logger.isErrorEnabled()) {
				logger.error("[{}:{}]: Cannot find detail table.", stockCode, snapshotDate);
			}
		}else {
			List<Element> detailElts = detailTable.children();
			if (detailElts.size() < 4) {
				if (logger.isErrorEnabled()) {
					logger.error("[{}:{}]: Detail table has no data rowse.", stockCode, snapshotDate);
				}
			}else {
				//processing header row
				Element headerElt = detailElts.get(0);
				boolean hasColumnError = false;
				List<String> texts = Utils.getElementText(headerElt);
				//at least "Participant ID, Name of CCASS Participant, (* for Consenting Investor Participants ), Address, Shareholding"
				if (texts.size() < 5) {
					if (logger.isErrorEnabled()) {
						logger.error("[{}:{}]: Detail table has no necessary columns: [{}].", stockCode, snapshotDate, texts);
					}
					hasColumnError = true;
				}
				int shareholdingIndexOfHeader = texts.indexOf("Shareholding");
				if (shareholdingIndexOfHeader == -1) {
					if (logger.isErrorEnabled()) {
						logger.error("[{}:{}]: Detail table has no shareholding columns: [{}].", stockCode, snapshotDate, texts);
					}
					hasColumnError = true;
				}
				
				boolean hasPercentageColumn = true;
				if (shareholdingIndexOfHeader == texts.size()-1) {
					hasPercentageColumn = false;
					if (!this.summary.isOutdatedTotalIssuedShares()){
						if (logger.isErrorEnabled()) {
							logger.error("[{}:{}]: Detail table has no expected percentage columns: [{}].", stockCode, snapshotDate, texts);
						}
						hasColumnError = true;
					}
				}
				
				if (!hasColumnError) {
					//processing data rows
					if (logger.isDebugEnabled()) {
						//the first 3 rows are header rows
						logger.debug("[{}:{}]: Detail table has [{}] data rows.", stockCode, snapshotDate, detailElts.size()-3);
					}

					List<SnapshotDetail.Shareholding> shareholdings = new ArrayList<SnapshotDetail.Shareholding>();
					for (int i=3, size=detailElts.size(); i<size; i++) {
						boolean hasRowError = false;
						Element detailElt = detailElts.get(i);
						texts = Utils.getElementText(detailElt);
						
						//at least should have 3 columns, including participant, address, and shareholding
						if (texts.size() < 3) {
							if (logger.isErrorEnabled()) {
								logger.error("[{}:{}]: Detail row has no necessary data: [{}].", stockCode, snapshotDate, texts);
							}
							hasRowError = true;
						}else {
							//if participant has no id, i.e. "HONG KONG SECURITIES CLEARING CO. LTD." or Individual Investor
							//the first column might be not exist or a blank string
							String participant = texts.get(0);
							if (StringUtils.isBlank(participant)) {
								participant = texts.get(1);
							}
							if (StringUtils.isBlank(participant)) {
								if (logger.isErrorEnabled()) {
									logger.error("[{}:{}]: Detail row has no participant column: [{}].", stockCode, snapshotDate, texts);
								}
								hasRowError = true;
							}
							
							try {
								
								int shareholdingIndexOfRow = texts.size()-2;
								if (!hasPercentageColumn) {
									//if has no percentage column, shareholding should be the last data in the row
									shareholdingIndexOfRow = texts.size()-1;
								}
								Long shareholding = NumberFormat.getInstance(Locale.US).parse(texts.get(shareholdingIndexOfRow)).longValue();
								
								float percentage = 0F;
								if (hasPercentageColumn) {
									percentage = NumberFormat.getPercentInstance(Locale.US).parse(texts.get(texts.size()-1)).floatValue();
								}else {
									//since this method will be called only when this.summary is not null, we can do below calculation
									percentage = (float)(shareholding * 1D / this.summary.getTotalShareholding());
								}
								
								if (!hasRowError) {
									SnapshotDetail.Shareholding shareholdingObj = new SnapshotDetail.Shareholding(participant, shareholding, percentage);
									shareholdings.add(shareholdingObj);
									if (logger.isDebugEnabled()) {
										logger.debug("[{}:{}]: Added shareholding: [{}]", stockCode, snapshotDate, shareholdingObj);
									}
								}
							}catch (ParseException e) {
								if (logger.isErrorEnabled()) {
									logger.error("[{}:{}]: Detail row has wrong shareholding or percentage: [{}].", stockCode, snapshotDate, texts);
								}
							}
						}
					}
					if (logger.isDebugEnabled()) {
						logger.debug("[{}:{}]: Successfully processed [{}] rows of data.", stockCode, snapshotDate, shareholdings.size());
					}
					result = new SnapshotDetail(stockCode, snapshotDate, shareholdings);
				}
			}
		}
		return result;
	}
}
