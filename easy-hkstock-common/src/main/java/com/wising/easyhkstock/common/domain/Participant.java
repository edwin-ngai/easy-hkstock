package com.wising.easyhkstock.common.domain;

import java.util.Objects;

import org.apache.commons.lang3.Validate;
import org.springframework.data.annotation.Id;

public class Participant {

	@Id
	private String id;
	private String enName;
	private String scName;
	private String tcName;
	
	public Participant(String id, String enName, String scName, String tcName) {
		
		Validate.notBlank(id);
		Validate.notBlank(enName);
		Validate.notBlank(scName);
		Validate.notBlank(tcName);
		
		this.id = id;
		this.enName = enName;
		this.scName = scName;
		this.tcName = tcName;
	}

	
	public String getId() {
		return id;
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
		if (!(o instanceof Participant)) {
			return false;
		}
		return Objects.equals(id, ((Participant)o).id);
	}

	@Override
    public int hashCode() {
        return Objects.hash(id);
    }

	@Override
	public String toString() {
		return "Participant [id=" + id + ", enName=" + enName + ", scName=" + scName + ", tcName=" + tcName + "]";
	}
}
