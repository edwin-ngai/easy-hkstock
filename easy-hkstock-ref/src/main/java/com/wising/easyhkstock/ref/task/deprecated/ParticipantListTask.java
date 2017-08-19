package com.wising.easyhkstock.ref.task.deprecated;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import com.wising.easyhkstock.common.domain.Participant;
import com.wising.easyhkstock.common.domain.Stock;

public class ParticipantListTask {

	private static final Logger logger = LoggerFactory.getLogger(ParticipantListTask.class);
	private static final String enUrlBase = "http://www.hkexnews.hk/sdw/search/partlist.aspx?SortBy=PartID";
	private static final String scUrlBase = "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/partlist_c.aspx?SortBy=PartID";
	private static final String tcUrlBase = "http://www.hkexnews.hk/sdw/search/partlist_c.aspx?SortBy=PartID";

	private RestTemplate restTpl = new RestTemplate();
	private URI enUrl, scUrl, tcUrl;

	public ParticipantListTask() {

		try {
			DateFormat df = new SimpleDateFormat("yyyymmdd");
			String today = df.format(new Date());
			String urlParam = "&ShareholdingDate=" + today;
			URI enUrl = new URI(enUrlBase + urlParam);
	    	URI scUrl = new URI(scUrlBase + urlParam);
	    	URI tcUrl = new URI(tcUrlBase + urlParam);
			setEnUrl(enUrl);
			setScUrl(scUrl);
			setTcUrl(tcUrl);
			restTpl.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<Participant> run() {

		List<Participant> result = new ArrayList<Participant>();
		Map<String, String> enResult = doRun(enUrl);
		if (enResult.isEmpty()) {
			return result;
		}
		Map<String, String> scResult = doRun(scUrl);
		Map<String, String> tcResult = doRun(tcUrl);
		if (enResult.size() != scResult.size() || enResult.size() != tcResult.size()) {
			// TODO: something wrong here
			logger.error("The result sizes of different language are different");
		} else {
			enResult.forEach((k, v) -> {
				String id = k;
				String enName = v;
				String scName = scResult.get(k);
				String tcName = tcResult.get(k);
				result.add(new Participant(id, enName, scName, tcName));
			});
		}
		return result;
	}

	private Map<String, String> doRun(URI uri) {

		Map<String, String> result = new HashMap<String, String>();
		ResponseEntity<String> response = restTpl.getForEntity(uri, String.class);
		if (response != null && HttpStatus.OK.equals(response.getStatusCode())) {
			String html = response.getBody();
			ParticipantListPage page = new ParticipantListPage(html);
			result = page.getParticipants();
		}
		return result;
	}

	public URI getEnUrl() {
		return enUrl;
	}

	public void setEnUrl(URI enUrl) {
		Objects.requireNonNull(enUrl);
		this.enUrl = enUrl;
	}

	public URI getScUrl() {
		return scUrl;
	}

	public void setScUrl(URI scUrl) {
		Objects.requireNonNull(scUrl);
		this.scUrl = scUrl;
	}

	public URI getTcUrl() {
		return tcUrl;
	}

	public void setTcUrl(URI tcUrl) {
		Objects.requireNonNull(tcUrl);
		this.tcUrl = tcUrl;
	}
}
