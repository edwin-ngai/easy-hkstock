package com.wising.easyhkstock.ccass.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.apache.commons.lang3.Validate;

public class SnapshotSummary {

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
	private String id;
	private String stockCode;
	private LocalDate snapshotDate;
	private long totalIssuedShares;
	private short intermediaryNumber;
	private long intermediaryShareholding;
	private short nonConsentingInvestorNumber;
	private long nonConsentingShareholding;

	
	public SnapshotSummary(String stockCode, LocalDate snapshotDate, long totalIssuedShares,
			short intermediaryNumber, long intermediaryShareholding, short nonConsentingInvestorNumber,
			long nonConsentingShareholding) {
		
		Validate.notBlank(stockCode);
		Validate.notNull(snapshotDate);
		Validate.isTrue(totalIssuedShares > 0);
		Validate.isTrue(intermediaryNumber > 0);
		Validate.isTrue(intermediaryShareholding > 0);
		Validate.isTrue(nonConsentingInvestorNumber > 0);
		Validate.isTrue(nonConsentingShareholding > 0);
		Validate.isTrue(totalIssuedShares > intermediaryShareholding+nonConsentingShareholding);
		
		this.stockCode = stockCode;
		this.snapshotDate = snapshotDate;
		this.totalIssuedShares = totalIssuedShares;
		this.intermediaryNumber = intermediaryNumber;
		this.intermediaryShareholding = intermediaryShareholding;
		this.nonConsentingInvestorNumber = nonConsentingInvestorNumber;
		this.nonConsentingShareholding = nonConsentingShareholding;
		id = stockCode+snapshotDate.format(formatter);
	}
	
	public String getId() {
		return id;
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

	@Override
	public String toString() {
		return "ShareholdingSnapshotSummary [stockCode=" + stockCode + ", snapshotDate=" + snapshotDate
				+ ", totalIssuedShares=" + totalIssuedShares + ", intermediaryNumber=" + intermediaryNumber
				+ ", intermediaryShareholding=" + intermediaryShareholding + ", nonConsentingInvestorNumber="
				+ nonConsentingInvestorNumber + ", nonConsentingShareholding=" + nonConsentingShareholding 
				+ "]";
	}
	
	
}
