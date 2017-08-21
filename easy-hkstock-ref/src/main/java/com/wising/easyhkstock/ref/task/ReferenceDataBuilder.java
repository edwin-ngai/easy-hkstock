package com.wising.easyhkstock.ref.task;

import java.net.URI;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
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

import com.wising.easyhkstock.common.task.DataBuilder;

public class ReferenceDataBuilder<T> implements DataBuilder<T>{

	private static final Logger logger = LoggerFactory.getLogger(ReferenceDataBuilder.class);
	
	private RestTemplate restTpl = new RestTemplate();
	private BuilderConfiguration configuration;
	private BuilderHelper<T> helper;

	public ReferenceDataBuilder(BuilderConfiguration configuration, BuilderHelper<T> helper) {
		
		Objects.requireNonNull(configuration);
		Objects.requireNonNull(helper);
		this.configuration = configuration;
		this.helper = helper;
		restTpl.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
	}
	

	public List<T> build() {

		List<T> result = new ArrayList<T>();
		String pageName = configuration.getPageName();
		Map<String, String> enResult = doRun(pageName+"-en", configuration.getEnUri());
		if (enResult.isEmpty()) {
			logger.error("Cannot get data for [{}]", pageName);
			return result;
		}
		Map<String, String> scResult = doRun(pageName+"-sc", configuration.getScUri());
		Map<String, String> tcResult = doRun(pageName+"-tc", configuration.getTcUri());
		if (enResult.size() != scResult.size() || enResult.size() != tcResult.size()) {
			logger.error("The result sizes of different languages for [{}] are different", pageName);
		} else {
			enResult.forEach((k, v) -> {
				String code = k;
				String enName = v;
				String scName = scResult.get(k);
				String tcName = tcResult.get(k);
				result.add(helper.createData(code, enName, scName, tcName));
			});
		}
		return Collections.unmodifiableList(result);
	}

	private Map<String, String> doRun(String pageName, URI uri) {

		Map<String, String> result = new HashMap<String, String>();
		ResponseEntity<String> response = restTpl.getForEntity(uri, String.class);
		if (response != null && HttpStatus.OK.equals(response.getStatusCode())) {
			String html = response.getBody();
			ReferenceDataPage page = new ReferenceDataPage(pageName, configuration.getTableSelector(), html);
			if (!page.hasError()) {
				result = page.getData();
			}
		}else {
			logger.error("Got error when accessing {}", pageName);
		}
		return result;
	}


}
