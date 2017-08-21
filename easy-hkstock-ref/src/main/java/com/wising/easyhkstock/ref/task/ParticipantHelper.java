package com.wising.easyhkstock.ref.task;

import com.wising.easyhkstock.common.domain.Participant;

public class ParticipantHelper extends BuilderHelper<Participant> {

	@Override
	public Participant createData(String code, String enName, String scName, String tcName) {
		
		return new Participant(code, enName, scName, tcName);
	}

}
