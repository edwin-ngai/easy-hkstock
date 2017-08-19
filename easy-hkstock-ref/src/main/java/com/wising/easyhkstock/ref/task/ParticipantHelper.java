package com.wising.easyhkstock.ref.task;

import java.net.URI;

import com.wising.easyhkstock.common.domain.Participant;
import com.wising.easyhkstock.common.domain.Stock;

public class ParticipantHelper extends ReferenceDataHelper<Participant> {

	private static final String enUrlBase = "http://www.hkexnews.hk/sdw/search/partlist.aspx?SortBy=PartID";
	private static final String scUrlBase = "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/partlist_c.aspx?SortBy=PartID";
	private static final String tcUrlBase = "http://www.hkexnews.hk/sdw/search/partlist_c.aspx?SortBy=PartID";

	@Override
	public Participant createData(String code, String enName, String scName, String tcName) {
		
		return new Participant(code, enName, scName, tcName);
	}

	@Override
	public URI getEnURI() {

		return getURI(enUrlBase);
	}

	@Override
	public URI getScURI() {
		return getURI(scUrlBase);

	}

	@Override
	public URI getTcURI() {
		return getURI(tcUrlBase);
	}

	@Override
	public String getPageName() {
		return "Participant List";
	}

}
