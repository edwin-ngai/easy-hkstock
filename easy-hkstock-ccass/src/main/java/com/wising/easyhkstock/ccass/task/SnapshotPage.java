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
	private long totalIssuedShares = -1;
	private short intermediaryNumber = -1;
	private long intermediaryShareholding = -1;
	private short nonConsentingInvestorNumber = -1;
	private long nonConsentingShareholding = -1;
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
				logger.error("The basic info table has no necessary items.");
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
			if (summaryItems.size() != 18) {
				logger.error("The summary table has no necessary items");
				hasError = true;
			}else {
				try {
					intermediaryShareholding = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(5)).longValue();
					intermediaryNumber = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(6)).shortValue();
					nonConsentingShareholding = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(9)).longValue();
					nonConsentingInvestorNumber = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(10)).shortValue();
					totalIssuedShares = NumberFormat.getInstance(Locale.US).parse(summaryItems.get(17)).longValue();
				} catch (ParseException e) {
					logger.error("The summary table has wrong item(s)");
					hasError = true;
				}
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
					if (texts.size() < 5) {
						logger.error("The detail row has no necessary columns: [{}]", texts);
						hasError = true;
					} else {
						String participant = texts.get(0);
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
				logger.debug("detail table has no data rows");
				hasError = true;
			}
		}
	}
	
//	private List<String> getElementText(Element elt) {
//		List<String> result = new ArrayList<String>();
//		new NodeTraversor(new NodeVisitor() {
//			public void head(Node node, int depth) {
//				if (node instanceof TextNode) {
//					TextNode textNode = (TextNode) node;
//					String text = org.jsoup.helper.StringUtil.normaliseWhitespace(textNode.getWholeText()).trim();
//					if (!StringUtils.isEmpty(text)) {
//						result.add(text);
//					}
//				}
//			}
//			public void tail(Node node, int depth) {
//			}
//		}).traverse(elt);
//		return result;
//	}
}
