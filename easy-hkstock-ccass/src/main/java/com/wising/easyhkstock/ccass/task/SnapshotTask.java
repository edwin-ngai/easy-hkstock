package com.wising.easyhkstock.ccass.task;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.wising.easyhkstock.ccass.domain.SnapshotDetail;
import com.wising.easyhkstock.ccass.domain.SnapshotSummary;


public class SnapshotTask {

	private static final Logger logger = LoggerFactory.getLogger(SnapshotTask.class);

	private RestTemplate restTpl = new RestTemplate();
	private URI url;

	public SnapshotTask() {

		try {
			url = new URI("http://www.hkexnews.hk/sdw/search/searchsdw.aspx");
//			URI url = new URI("http://localhost/blacklist/test");
//			restTpl.getMessageConverters().add(0,new FormHttpMessageConverter());
//			restTpl.getMessageConverters().add(0, new StringHttpMessageConverter(Charset.forName("UTF-8")));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public SimpleImmutableEntry<SnapshotSummary, SnapshotDetail> run() {

		SimpleImmutableEntry<SnapshotSummary, SnapshotDetail> result = null;
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("__VIEWSTATE", "tDLPTY+9LGrF8dCGH1uMqVxtLCnImeR1Rg+mCQ2YRtBJ7LJZyIjC7fDgWmEBXh6Rc26rqjT/SDLA7nPWuqRVmoDs1WuFlUiFjdoXbFq7S/XusAC4squ7OTffwtr7/r7eWIdtFlrvTI1Q1h6fPgfi+6Bx0AXVk2MSkEsPdP6nA0H+JQQUFW+Hym+gGzTIcsg72aIOag==");
		body.add("__VIEWSTATEGENERATOR", "A7B2BBE2");
		body.add("__EVENTVALIDATION", "TulHaQrwALPwDAbcckpjWdZDj2rLKbe3VjkfCfUk1zXTXmsiLak4ewnQOJWIARc8LitRN6kNZ3Uoj3W1f4m71mit562o6mf7xyP+M5AIDOuDgOsrS8GcIXFvor8RFb5+WznvLlFMte/yQwsyho5MRMJNXtIXHVuVfgELbKhgjH/L0BVt5/ig8ej9MA197oH4RBwuLxDjiREP4L3ov07x7kocd9qJ85Chj6Cjr6XsWTwjrvmi6EUnB4f+XuPNgccXdRTl6OZqsFiKbOA+SdBEgZdoWyPMsBeOTjA8eEfN98QeafyCFkCawhvo2bTeHaizCN7q6h8Txxt3+LK+PCmD95GBBkgJ+HCOAg5y92kmfbKco5HVWpbqOK5WbmgSgTrjR5FwuhICtjEW+/hDLcM0jkMeP2XbcvUmD4KBsshb4uhwzbeoO/ZQuhwJeVDF1l1qc2l1lZKaSsvu6Z1IiwA6vH0s9syZm571RJl771LeJc4xSVWsXTRyWQe6j3l1Csfie2ddtnZVb/UicqsLaxiFn3eA6pGV2fH/J26xiD0GkwSYRPfj8gCpt1PKImpRU/ZbNiJtzRRVM4KGX08ejxYC731pC99TUehegGG829+avdttGSX8Txyo/QIGza5gl2xN1AMKfITpoxWRxLsvocgkztPUcEo8i7zYvmNgGCmBIFMbJ23NTTIzr2zjCCNjNEhAezssW/E/ubuV1WbzdCkHOJXZ7IH39RUT2SR09xIH0Np09eoJCAatB43AM4WJbrd4inU0uHkUbV5mwbp1XPSYpKjNKaqzp4c8418EIN8qyc7ggtUQ2DwljtQ05BGnB8cNvQkjWxwl1uzL8MHW5Pblj5gsbshdcsiwcRx3jE4gVsb2rhoB7gAmZCRrGt07k38sH9pK1LwwEBYV5jSN8/nhowu4rE3/n6tg0CNiZJ0Qk8RsBygvjSo3zhj/UKy83UgMHFAqlBh9JzuE+XkY/L8Q3zUk6kIOKBIHxwDKP7Budqq+qrTjYI5PfH/TfFPXngKviNjsVyqlLx4mT36wLEvImz+WxBPvk84to4ejbmvNjjFhlFu0v/B5xICkyOPpR67MAtax26eTVXCbc5RdgqbkwkZ5incX3tOqhe9M2alDrzszz8Age4CvmVlqrOzRuv2IhO7+qAWQrecISeMQZgdSCU+OUuOTRgBZMmQMcqyYXDE6fjUIL6skKPZi0nMNVGlWjFVL4TfRW6aEE8KPMAj0F8WVuW9Vmlk8Wc1kKThagKaY0Oc1t56eWS1cRgDj2fpRlZnvlw==");
		body.add("today", "20170809");
		body.add("ddlShareholdingDay", "16");
		body.add("ddlShareholdingMonth", "07");
		body.add("ddlShareholdingYear", "2017");
		body.add("txtStockCode", "08182");
		body.add("btnSearch.x", "16");
		body.add("btnSearch.y", "11");
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(body, headers);
		ResponseEntity<String> response = restTpl.postForEntity(url, request, String.class);
		if (response != null && HttpStatus.OK.equals(response.getStatusCode())) {
			String html = response.getBody();
			SnapshotPage page = new SnapshotPage(html);
			if (!page.hasError()) {
//				String stockCode = page.getStockCode();
//				Date snapshotDate = page.getSnapshotDate();
//				long totalIssuedShares = page.getTotalIssuedShares();
//				long intermediaryShareholding = page.getIntermediaryShareholding();
//				short intermediaryNumber = page.getIntermediaryNumber();
//				long nonConsentingShareholding = page.getNonConsentingShareholding();
//				short nonConsentingInvestorNumber = page.getNonConsentingInvestorNumber();
//				Map<String, Long> details = page.getDetail();
//				SnapshotSummary summary = new SnapshotSummary(stockCode, snapshotDate,totalIssuedShares, 
//						intermediaryNumber, intermediaryShareholding, nonConsentingInvestorNumber, nonConsentingShareholding);
//				SnapshotDetail detail = new SnapshotDetail(stockCode, snapshotDate, details);
//				result = new SimpleImmutableEntry<SnapshotSummary, SnapshotDetail>(summary, detail);
			}
		}
		return result;
	}
}
