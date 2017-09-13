package com.wising.easyhkstock.ccass.domain;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.Validate;

public class SnapshotDetail {
	
	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

	private String id;
	private String stockCode;
	private LocalDate snapshotDate;
	private List<Shareholding> shareholdings;

	
	public SnapshotDetail(String stockCode, LocalDate snapshotDate, List<Shareholding> shareholdings) {
		
		Validate.notBlank(stockCode);
		Validate.notNull(snapshotDate);
		Validate.notNull(shareholdings);
		Validate.isTrue(!shareholdings.isEmpty());
		
		this.stockCode = stockCode;
		this.snapshotDate = snapshotDate;
		this.shareholdings = shareholdings;
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

	public List<Shareholding> getShareholdings() {
		return shareholdings;
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
				+ ", shareholdings=" + shareholdings + "]";
	}
	
	public static class Shareholding {
		
		String shareholder;
		long shareholding;
		float percentage;
		
		public Shareholding(String shareholder, long shareholding, float percentage) {
			this.shareholder = shareholder;
			this.shareholding = shareholding;
			this.percentage = percentage;
		}

		@Override
		public String toString() {
			return "Shareholding [shareholder=" + shareholder + ", shareholding=" + shareholding + ", percentage="
					+ percentage + "]";
		}
		
	}
}
