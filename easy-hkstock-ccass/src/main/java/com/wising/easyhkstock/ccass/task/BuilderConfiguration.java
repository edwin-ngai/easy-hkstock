package com.wising.easyhkstock.ccass.task;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

public class BuilderConfiguration {

	private int 
			fetcherNo = 8,
			parserNo = 80,
			parserTimeout = 1000,
			corePoolSize = 240, 
			queueCapacity = Integer.MAX_VALUE,
			requestTimeout = 1000,
			connectTimeout = 1000,
			socketTimeout = -1,
			maxConnTotal = 8,
			searchX = 16, 
			searchY = 11;
	private List<Token> tokens = new ArrayList<Token>();
	private String viewState = "tDLPTY+9LGrF8dCGH1uMqVxtLCnImeR1Rg+mCQ2YRtBJ7LJZyIjC7fDgWmEBXh6Rc26rqjT/SDLA7nPWuqRVmoDs1WuFlUiFjdoXbFq7S/XusAC4squ7OTffwtr7/r7eWIdtFlrvTI1Q1h6fPgfi+6Bx0AXVk2MSkEsPdP6nA0H+JQQUFW+Hym+gGzTIcsg72aIOag==",
			eventValidation = "TulHaQrwALPwDAbcckpjWdZDj2rLKbe3VjkfCfUk1zXTXmsiLak4ewnQOJWIARc8LitRN6kNZ3Uoj3W1f4m71mit562o6mf7xyP+M5AIDOuDgOsrS8GcIXFvor8RFb5+WznvLlFMte/yQwsyho5MRMJNXtIXHVuVfgELbKhgjH/L0BVt5/ig8ej9MA197oH4RBwuLxDjiREP4L3ov07x7kocd9qJ85Chj6Cjr6XsWTwjrvmi6EUnB4f+XuPNgccXdRTl6OZqsFiKbOA+SdBEgZdoWyPMsBeOTjA8eEfN98QeafyCFkCawhvo2bTeHaizCN7q6h8Txxt3+LK+PCmD95GBBkgJ+HCOAg5y92kmfbKco5HVWpbqOK5WbmgSgTrjR5FwuhICtjEW+/hDLcM0jkMeP2XbcvUmD4KBsshb4uhwzbeoO/ZQuhwJeVDF1l1qc2l1lZKaSsvu6Z1IiwA6vH0s9syZm571RJl771LeJc4xSVWsXTRyWQe6j3l1Csfie2ddtnZVb/UicqsLaxiFn3eA6pGV2fH/J26xiD0GkwSYRPfj8gCpt1PKImpRU/ZbNiJtzRRVM4KGX08ejxYC731pC99TUehegGG829+avdttGSX8Txyo/QIGza5gl2xN1AMKfITpoxWRxLsvocgkztPUcEo8i7zYvmNgGCmBIFMbJ23NTTIzr2zjCCNjNEhAezssW/E/ubuV1WbzdCkHOJXZ7IH39RUT2SR09xIH0Np09eoJCAatB43AM4WJbrd4inU0uHkUbV5mwbp1XPSYpKjNKaqzp4c8418EIN8qyc7ggtUQ2DwljtQ05BGnB8cNvQkjWxwl1uzL8MHW5Pblj5gsbshdcsiwcRx3jE4gVsb2rhoB7gAmZCRrGt07k38sH9pK1LwwEBYV5jSN8/nhowu4rE3/n6tg0CNiZJ0Qk8RsBygvjSo3zhj/UKy83UgMHFAqlBh9JzuE+XkY/L8Q3zUk6kIOKBIHxwDKP7Budqq+qrTjYI5PfH/TfFPXngKviNjsVyqlLx4mT36wLEvImz+WxBPvk84to4ejbmvNjjFhlFu0v/B5xICkyOPpR67MAtax26eTVXCbc5RdgqbkwkZ5incX3tOqhe9M2alDrzszz8Age4CvmVlqrOzRuv2IhO7+qAWQrecISeMQZgdSCU+OUuOTRgBZMmQMcqyYXDE6fjUIL6skKPZi0nMNVGlWjFVL4TfRW6aEE8KPMAj0F8WVuW9Vmlk8Wc1kKThagKaY0Oc1t56eWS1cRgDj2fpRlZnvlw==";
	private URI uri = URI.create("http://www.hkexnews.hk/sdw/search/searchsdw.aspx");
	private LocalDate startDate = LocalDate.now().minusDays(1); //inclusive
	private LocalDate endDate = LocalDate.now(); //exclusive
	private List<String> stocks = new ArrayList<String>();

	public int getCorePoolSize() {
		return corePoolSize;
	}
	public void setCorePoolSize(int corePoolSize) {
		Validate.isTrue(corePoolSize > 0);
		this.corePoolSize = corePoolSize;
	}
	public int getQueueCapacity() {
		return queueCapacity;
	}
	public void setQueueCapacity(int queueCapacity) {
		Validate.isTrue(queueCapacity > 0);
		this.queueCapacity = queueCapacity;
	}

	public String getViewState() {
		return viewState;
	}

	public void setViewState(String viewState) {
		Validate.notNull(viewState);
		this.viewState = viewState;
	}

	public String getEventValidation() {
		return eventValidation;
	}

	public void setEventValidation(String eventValidation) {
		Validate.notNull(eventValidation);
		this.eventValidation = eventValidation;
	}

	public int getSearchX() {
		return searchX;
	}

	public void setSearchX(int searchX) {
		Validate.isTrue(searchX > 0);
		this.searchX = searchX;
	}

	public int getSearchY() {
		return searchY;
	}

	public void setSearchY(int searchY) {
		Validate.isTrue(searchY > 0);
		this.searchY = searchY;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		Validate.notNull(startDate);
		this.startDate = startDate;
	}
	
	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		Validate.notNull(endDate);
		this.endDate = endDate;
	}

	public URI getUri() {
		return uri;
	}

	public void setUri(URI uri) {
		Validate.notNull(uri);
		this.uri = uri;
	}

	public List<String> getStocks() {
		return stocks;
	}

	public void setStocks(List<String> stocks) {
		Validate.notNull(stocks);
		this.stocks = stocks;
	}
	public int getRequestTimeout() {
		return requestTimeout;
	}
	public void setRequestTimeout(int requestTimeout) {
		Validate.isTrue(requestTimeout > 0);
		this.requestTimeout = requestTimeout;
	}
	public int getConnectTimeout() {
		return connectTimeout;
	}
	public void setConnectTimeout(int connectTimeout) {
		Validate.isTrue(connectTimeout > 0);
		this.connectTimeout = connectTimeout;
	}
	public int getSocketTimeout() {
		return socketTimeout;
	}
	public void setSocketTimeout(int socketTimeout) {
		Validate.isTrue(socketTimeout > 0);
		this.socketTimeout = socketTimeout;
	}
	public int getMaxConnTotal() {
		return maxConnTotal;
	}
	public void setMaxConnTotal(int maxConnTotal) {
		Validate.isTrue(maxConnTotal > 0);
		this.maxConnTotal = maxConnTotal;
	}
	public int getFetcherNo() {
		return fetcherNo;
	}
	public void setFetcherNo(int fetcherNo) {
		Validate.isTrue(fetcherNo > 0);
		this.fetcherNo = fetcherNo;
	}
	public int getParserTimeout() {
		return parserTimeout;
	}
	public void setParserTimeout(int parserTimeout) {
		Validate.isTrue(parserTimeout > 0);
		this.parserTimeout = parserTimeout;
	}
	public int getParserNo() {
		return parserNo;
	}
	public void setParserNo(int parserNo) {
		Validate.isTrue(parserNo > 0);
		this.parserNo = parserNo;
	}


	public List<Token> getTokens() {
		return tokens;
	}
	public void setTokens(List<Token> tokens) {
		Validate.notNull(tokens);
		this.tokens = tokens;
	}


	public static class Token {
		
		private String viewState, eventValidation;

		public String getViewState() {
			return viewState;
		}

		public void setViewState(String viewState) {
			Validate.notBlank(viewState);
			this.viewState = viewState;
		}

		public String getEventValidation() {
			return eventValidation;
		}

		public void setEventValidation(String eventValidation) {
			Validate.notBlank(eventValidation);
			this.eventValidation = eventValidation;
		}
		
		
	}

}
