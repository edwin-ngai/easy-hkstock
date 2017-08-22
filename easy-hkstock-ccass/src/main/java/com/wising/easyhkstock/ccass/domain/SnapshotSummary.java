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
	private short consentingInvestorNumber;
	private long consentingShareholding;
	private short nonConsentingInvestorNumber;
	private long nonConsentingShareholding;

	
	public SnapshotSummary(String stockCode, LocalDate snapshotDate, long totalIssuedShares,
			short intermediaryNumber, long intermediaryShareholding, short consentingInvestorNumber,
			long consentingShareholding, short nonConsentingInvestorNumber,
			long nonConsentingShareholding) {
		
		Validate.notBlank(stockCode);
		Validate.notNull(snapshotDate);
		Validate.isTrue(totalIssuedShares > -1);
		Validate.isTrue(intermediaryNumber > -1);
		Validate.isTrue(intermediaryShareholding > -1);
		Validate.isTrue(consentingInvestorNumber > -1);
		Validate.isTrue(consentingShareholding > -1);
		Validate.isTrue(nonConsentingInvestorNumber > -1);
		Validate.isTrue(nonConsentingShareholding > -1);
		//TODO: this validation cannot be asserted true, but whats the underlying reason?
//		Validate.isTrue(totalIssuedShares > intermediaryShareholding+consentingShareholding+nonConsentingShareholding);
		
		this.stockCode = stockCode;
		this.snapshotDate = snapshotDate;
		this.totalIssuedShares = totalIssuedShares;
		this.intermediaryNumber = intermediaryNumber;
		this.intermediaryShareholding = intermediaryShareholding;
		this.consentingInvestorNumber = consentingInvestorNumber;
		this.consentingShareholding = consentingShareholding;
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

	@Override
	public String toString() {
		return "ShareholdingSnapshotSummary [stockCode=" + stockCode + ", snapshotDate=" + snapshotDate
				+ ", totalIssuedShares=" + totalIssuedShares + ", intermediaryNumber=" + intermediaryNumber
				+ ", intermediaryShareholding=" + intermediaryShareholding + ", consentingInvestorNumber="
				+ consentingInvestorNumber + ", consentingShareholding=" + consentingShareholding 
				+ ", nonConsentingInvestorNumber=" + nonConsentingInvestorNumber + ", nonConsentingShareholding=" 
				+ nonConsentingShareholding 
				+ "]";
	}
	
	
}
