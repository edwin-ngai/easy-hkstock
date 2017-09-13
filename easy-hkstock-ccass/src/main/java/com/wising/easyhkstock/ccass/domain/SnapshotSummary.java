package com.wising.easyhkstock.ccass.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

public class SnapshotSummary {

	private static final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyyMMdd");
	
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

	private long totalShareholding;
	private float intermediaryShareholdingPercentage;
	private float consentingShareholdingPercentage;
	private float nonConsentingShareholdingPercentage;
	private boolean outdatedTotalIssuedShares;
	
	public SnapshotSummary(String stockCode, LocalDate snapshotDate, long totalIssuedShares, short intermediaryNumber,
			long intermediaryShareholding, short consentingInvestorNumber, long consentingShareholding,
			short nonConsentingInvestorNumber, long nonConsentingShareholding) {

		Validate.notBlank(stockCode);
		Validate.notNull(snapshotDate);
		Validate.isTrue(totalIssuedShares > -1);
		Validate.isTrue(intermediaryNumber > -1);
		Validate.isTrue(intermediaryShareholding > -1);
		Validate.isTrue(consentingInvestorNumber > -1);
		Validate.isTrue(consentingShareholding > -1);
		Validate.isTrue(nonConsentingInvestorNumber > -1);
		Validate.isTrue(nonConsentingShareholding > -1);
		// TODO: this validation cannot be asserted true, but whats the
		// underlying reason?
		// Validate.isTrue(totalIssuedShares >
		// intermediaryShareholding+consentingShareholding+nonConsentingShareholding);

		this.stockCode = stockCode;
		this.snapshotDate = snapshotDate;
		this.totalIssuedShares = totalIssuedShares;
		this.intermediaryNumber = intermediaryNumber;
		this.intermediaryShareholding = intermediaryShareholding;
		this.consentingInvestorNumber = consentingInvestorNumber;
		this.consentingShareholding = consentingShareholding;
		this.nonConsentingInvestorNumber = nonConsentingInvestorNumber;
		this.nonConsentingShareholding = nonConsentingShareholding;
		
		this.totalShareholding = this.intermediaryShareholding + this.consentingShareholding + this.nonConsentingShareholding;
		if (this.totalShareholding > this.totalIssuedShares) {
			outdatedTotalIssuedShares = true;
			this.intermediaryShareholdingPercentage = (float)(this.intermediaryShareholding * 1D / this.totalShareholding);
			this.consentingShareholdingPercentage = (float)(this.consentingShareholding * 1D / this.totalShareholding);
			this.nonConsentingShareholdingPercentage = (float)(this.nonConsentingShareholding * 1D / this.totalShareholding);
		}else {
			this.intermediaryShareholdingPercentage = (float)(this.intermediaryShareholding * 1D / this.totalIssuedShares);
			this.consentingShareholdingPercentage = (float)(this.consentingShareholding * 1D / this.totalIssuedShares);
			this.nonConsentingShareholdingPercentage = (float)(this.nonConsentingShareholding * 1D / this.totalIssuedShares);
		}
		id = stockCode + snapshotDate.format(dateFormatter);
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
	
	public long getTotalShareholding() {
		return totalShareholding;
	}

	public float getIntermediaryShareholdingPercentage() {
		return intermediaryShareholdingPercentage;
	}

	public float getConsentingShareholdingPercentage() {
		return consentingShareholdingPercentage;
	}

	public float getNonConsentingShareholdingPercentage() {
		return nonConsentingShareholdingPercentage;
	}

	public boolean isOutdatedTotalIssuedShares() {
		return this.outdatedTotalIssuedShares;
	}
	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof SnapshotSummary)) {
			return false;
		}
		return Objects.equals(id, ((SnapshotSummary)o).id);
	}

	@Override
    public int hashCode() {
        return Objects.hash(id);
    }

	@Override
	public String toString() {
		return "SnapshotSummary [stockCode=" + stockCode + ", snapshotDate=" + snapshotDate + ", totalIssuedShares="
				+ totalIssuedShares + ", intermediaryNumber=" + intermediaryNumber + ", intermediaryShareholding="
				+ intermediaryShareholding + ", consentingInvestorNumber=" + consentingInvestorNumber
				+ ", consentingShareholding=" + consentingShareholding + ", nonConsentingInvestorNumber="
				+ nonConsentingInvestorNumber + ", nonConsentingShareholding=" + nonConsentingShareholding
				+ ", totalShareholding=" + totalShareholding + ", intermediaryShareholdingPercentage="
				+ intermediaryShareholdingPercentage + ", consentingShareholdingPercentage="
				+ consentingShareholdingPercentage + ", nonConsentingShareholdingPercentage="
				+ nonConsentingShareholdingPercentage + ", outdatedTotalIssuedShares=" 
				+ outdatedTotalIssuedShares + "]";
	}
}
