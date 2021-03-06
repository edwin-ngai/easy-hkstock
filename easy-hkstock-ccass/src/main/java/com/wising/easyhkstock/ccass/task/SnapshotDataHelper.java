package com.wising.easyhkstock.ccass.task;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

public class SnapshotDataHelper {

	private final static String VIEW_STATE = "tDLPTY+9LGrF8dCGH1uMqVxtLCnImeR1Rg+mCQ2YRtBJ7LJZyIjC7fDgWmEBXh6Rc26rqjT/SDLA7nPWuqRVmoDs1WuFlUiFjdoXbFq7S/XusAC4squ7OTffwtr7/r7eWIdtFlrvTI1Q1h6fPgfi+6Bx0AXVk2MSkEsPdP6nA0H+JQQUFW+Hym+gGzTIcsg72aIOag==";
	private final static String EVENT_VALIDATION = "TulHaQrwALPwDAbcckpjWdZDj2rLKbe3VjkfCfUk1zXTXmsiLak4ewnQOJWIARc8LitRN6kNZ3Uoj3W1f4m71mit562o6mf7xyP+M5AIDOuDgOsrS8GcIXFvor8RFb5+WznvLlFMte/yQwsyho5MRMJNXtIXHVuVfgELbKhgjH/L0BVt5/ig8ej9MA197oH4RBwuLxDjiREP4L3ov07x7kocd9qJ85Chj6Cjr6XsWTwjrvmi6EUnB4f+XuPNgccXdRTl6OZqsFiKbOA+SdBEgZdoWyPMsBeOTjA8eEfN98QeafyCFkCawhvo2bTeHaizCN7q6h8Txxt3+LK+PCmD95GBBkgJ+HCOAg5y92kmfbKco5HVWpbqOK5WbmgSgTrjR5FwuhICtjEW+/hDLcM0jkMeP2XbcvUmD4KBsshb4uhwzbeoO/ZQuhwJeVDF1l1qc2l1lZKaSsvu6Z1IiwA6vH0s9syZm571RJl771LeJc4xSVWsXTRyWQe6j3l1Csfie2ddtnZVb/UicqsLaxiFn3eA6pGV2fH/J26xiD0GkwSYRPfj8gCpt1PKImpRU/ZbNiJtzRRVM4KGX08ejxYC731pC99TUehegGG829+avdttGSX8Txyo/QIGza5gl2xN1AMKfITpoxWRxLsvocgkztPUcEo8i7zYvmNgGCmBIFMbJ23NTTIzr2zjCCNjNEhAezssW/E/ubuV1WbzdCkHOJXZ7IH39RUT2SR09xIH0Np09eoJCAatB43AM4WJbrd4inU0uHkUbV5mwbp1XPSYpKjNKaqzp4c8418EIN8qyc7ggtUQ2DwljtQ05BGnB8cNvQkjWxwl1uzL8MHW5Pblj5gsbshdcsiwcRx3jE4gVsb2rhoB7gAmZCRrGt07k38sH9pK1LwwEBYV5jSN8/nhowu4rE3/n6tg0CNiZJ0Qk8RsBygvjSo3zhj/UKy83UgMHFAqlBh9JzuE+XkY/L8Q3zUk6kIOKBIHxwDKP7Budqq+qrTjYI5PfH/TfFPXngKviNjsVyqlLx4mT36wLEvImz+WxBPvk84to4ejbmvNjjFhlFu0v/B5xICkyOPpR67MAtax26eTVXCbc5RdgqbkwkZ5incX3tOqhe9M2alDrzszz8Age4CvmVlqrOzRuv2IhO7+qAWQrecISeMQZgdSCU+OUuOTRgBZMmQMcqyYXDE6fjUIL6skKPZi0nMNVGlWjFVL4TfRW6aEE8KPMAj0F8WVuW9Vmlk8Wc1kKThagKaY0Oc1t56eWS1cRgDj2fpRlZnvlw==";
	private final static String SEARCH_X = "16";
	private final static String SEARCH_Y = "11";
	
	public URI getURI() {
		return URI.create("http://www.hkexnews.hk/sdw/search/searchsdw.aspx");
	}
	
	public HttpEntity<MultiValueMap<String, String>> getHttpEntity(String year, String month, String day, String stockCode) {
		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
		body.add("__VIEWSTATE", VIEW_STATE);
//		body.add("__VIEWSTATEGENERATOR", "A7B2BBE2");
		body.add("__EVENTVALIDATION", EVENT_VALIDATION);
		body.add("btnSearch.x", SEARCH_X);
		body.add("btnSearch.y", SEARCH_Y);
//		body.add("today", "20170809");
		body.add("ddlShareholdingDay", day);
		body.add("ddlShareholdingMonth", month);
		body.add("ddlShareholdingYear", year);
		body.add("txtStockCode", stockCode);
		return new HttpEntity<MultiValueMap<String, String>>(body, headers);
	}
	
	public List<String> getStockList() {
		return Arrays.asList(new String[]{"01357"});
	}
	
	public LocalDate getStartDate() {
		return LocalDate.now().minusDays(1);
	}
	
}
