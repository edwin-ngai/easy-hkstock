package com.wising.easyhkstock.ccass.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

public class SnapshotDetail {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	private String id;
	private String stockCode;
	private LocalDate snapshotDate;
	private Map<String, Long> shareholding;

	
	public SnapshotDetail(String stockCode, LocalDate snapshotDate, Map<String, Long> shareholding) {
		
		Validate.notBlank(stockCode);
		Validate.notNull(snapshotDate);
		Validate.notNull(shareholding);
		Validate.isTrue(!shareholding.isEmpty());
		
		this.stockCode = stockCode;
		this.snapshotDate = snapshotDate;
		this.shareholding = shareholding;
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

	public Map<String, Long> getShareholding() {
		return shareholding;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof SnapshotDetail)) {
			return false;
		}
		return Objects.equals(id, ((SnapshotDetail)o).id);
	}

	@Override
    public int hashCode() {
        return Objects.hash(id);
    }
	
	@Override
	public String toString() {
		return "ShareholdingSnapshotSummary [stockCode=" + stockCode + ", snapshotDate=" + snapshotDate
				+ ", shareholding=" + shareholding + "]";
	}
	
	
}
