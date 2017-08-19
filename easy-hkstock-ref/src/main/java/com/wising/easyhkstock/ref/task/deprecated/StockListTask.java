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

import com.wising.easyhkstock.common.domain.Stock;

public class StockListTask {

	private static final Logger logger = LoggerFactory.getLogger(StockListTask.class);
	
	private static final String enUrlBase = "http://www.hkexnews.hk/sdw/search/stocklist.aspx?SortBy=StockCode";
	private static final String scUrlBase = "http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/stocklist_c.aspx?SortBy=StockCode";
	private static final String tcUrlBase = "http://www.hkexnews.hk/sdw/search/stocklist_c.aspx?SortBy=StockCode";

	private RestTemplate restTpl = new RestTemplate();
	private URI enUrl, scUrl, tcUrl;

	public StockListTask() {

		try {
			DateFormat df = new SimpleDateFormat("yyyymmdd");
			String today = df.format(new Date());
			String urlParam = "&ShareholdingDate=" + today;
			enUrl = new URI(enUrlBase + urlParam);
	    	scUrl = new URI(scUrlBase + urlParam);
	    	tcUrl = new URI(tcUrlBase + urlParam);
			restTpl.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public List<Stock> run() {

		List<Stock> result = new ArrayList<Stock>();
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
				String code = k;
				String enName = v;
				String scName = scResult.get(k);
				String tcName = tcResult.get(k);
				result.add(new Stock(code, enName, scName, tcName));
			});
		}
		return result;
	}

	private Map<String, String> doRun(URI uri) {

		Map<String, String> result = new HashMap<String, String>();
		ResponseEntity<String> response = restTpl.getForEntity(uri, String.class);
		if (response != null && HttpStatus.OK.equals(response.getStatusCode())) {
			String html = response.getBody();
//			CompanyListPage page = new CompanyListPage(html);
			StockListPage page = new StockListPage(html);
			result = page.getStocks();
		}
		return result;
	}


}
