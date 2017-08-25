package com.wising.easyhkstock.common.domain;

import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Id;

public class Stock {

	@Id
	private String code;
	private String enName;
	private String scName;
	private String tcName;
	
	public Stock(String code, String enName, String scName, String tcName) {
		
		Validate.notBlank(code);
		Validate.notBlank(enName);
		Validate.notBlank(scName);
		Validate.notBlank(tcName);
		this.code = code;
		this.enName = enName;
		this.scName = scName;
		this.tcName = tcName;
	}
	
	public String getCode() {
		return code;
	}

	public String getEnName() {
		return enName;
	}

	public String getScName() {
		return scName;
	}

	public String getTcName() {
		return tcName;
	}

	@Override
	public boolean equals(Object o) {

		if (o == this)
			return true;
		if (!(o instanceof Stock)) {
			return false;
		}
		return Objects.equals(code, ((Stock)o).code);
	}

	@Override
    public int hashCode() {
        return Objects.hash(code);
    }



	@Override
	public String toString() {
		return "Stock [code=" + code + ", enName=" + enName + ", scName=" + scName + ", tcName=" + tcName + "]";
	}
}
