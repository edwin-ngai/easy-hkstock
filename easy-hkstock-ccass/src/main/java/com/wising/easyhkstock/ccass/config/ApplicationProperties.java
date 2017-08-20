package com.wising.easyhkstock.ccass.config;

import java.net.URI;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.Validate;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "snapshot")
public class ApplicationProperties {

	private RequestProperties request = new RequestProperties();
	private DatabaseProperties database = new DatabaseProperties();
	private BuilderProperties builder = new BuilderProperties();
	

	public RequestProperties getRequest() {
		return request;
	}

	public void setRequest(RequestProperties request) {
		Validate.notNull(request);
		this.request = request;
	}

	public DatabaseProperties getDatabase() {
		return database;
	}

	public void setDatabase(DatabaseProperties database) {
		Validate.notNull(database);
		this.database = database;
	}

	public BuilderProperties getBuilder() {
		return builder;
	}

	public void setBuilder(BuilderProperties builder) {
		Validate.notNull(builder);
		this.builder = builder;
	}



	public static class BuilderProperties {
		private int corePoolSize = 20;
		private int queueCapacity = 20;
		public int getCorePoolSize() {
			return corePoolSize;
		}
		public void setCorePoolSize(int corePoolSize) {
			Validate.notNull(corePoolSize);
			this.corePoolSize = corePoolSize;
		}
		public int getQueueCapacity() {
			return queueCapacity;
		}
		public void setQueueCapacity(int queueCapacity) {
			Validate.notNull(queueCapacity);
			this.queueCapacity = queueCapacity;
		}
		
	}
	public static class DatabaseProperties {
		
		private String databaseName = "snapshot";

		public String getDatabaseName() {
			return databaseName;
		}

		public void setDatabaseName(String databaseName) {
			Validate.notNull(databaseName);
			this.databaseName = databaseName;
		}


	}
	public static class RequestProperties {
		private String viewState = "tDLPTY+9LGrF8dCGH1uMqVxtLCnImeR1Rg+mCQ2YRtBJ7LJZyIjC7fDgWmEBXh6Rc26rqjT/SDLA7nPWuqRVmoDs1WuFlUiFjdoXbFq7S/XusAC4squ7OTffwtr7/r7eWIdtFlrvTI1Q1h6fPgfi+6Bx0AXVk2MSkEsPdP6nA0H+JQQUFW+Hym+gGzTIcsg72aIOag==";
		private String eventValidation = "TulHaQrwALPwDAbcckpjWdZDj2rLKbe3VjkfCfUk1zXTXmsiLak4ewnQOJWIARc8LitRN6kNZ3Uoj3W1f4m71mit562o6mf7xyP+M5AIDOuDgOsrS8GcIXFvor8RFb5+WznvLlFMte/yQwsyho5MRMJNXtIXHVuVfgELbKhgjH/L0BVt5/ig8ej9MA197oH4RBwuLxDjiREP4L3ov07x7kocd9qJ85Chj6Cjr6XsWTwjrvmi6EUnB4f+XuPNgccXdRTl6OZqsFiKbOA+SdBEgZdoWyPMsBeOTjA8eEfN98QeafyCFkCawhvo2bTeHaizCN7q6h8Txxt3+LK+PCmD95GBBkgJ+HCOAg5y92kmfbKco5HVWpbqOK5WbmgSgTrjR5FwuhICtjEW+/hDLcM0jkMeP2XbcvUmD4KBsshb4uhwzbeoO/ZQuhwJeVDF1l1qc2l1lZKaSsvu6Z1IiwA6vH0s9syZm571RJl771LeJc4xSVWsXTRyWQe6j3l1Csfie2ddtnZVb/UicqsLaxiFn3eA6pGV2fH/J26xiD0GkwSYRPfj8gCpt1PKImpRU/ZbNiJtzRRVM4KGX08ejxYC731pC99TUehegGG829+avdttGSX8Txyo/QIGza5gl2xN1AMKfITpoxWRxLsvocgkztPUcEo8i7zYvmNgGCmBIFMbJ23NTTIzr2zjCCNjNEhAezssW/E/ubuV1WbzdCkHOJXZ7IH39RUT2SR09xIH0Np09eoJCAatB43AM4WJbrd4inU0uHkUbV5mwbp1XPSYpKjNKaqzp4c8418EIN8qyc7ggtUQ2DwljtQ05BGnB8cNvQkjWxwl1uzL8MHW5Pblj5gsbshdcsiwcRx3jE4gVsb2rhoB7gAmZCRrGt07k38sH9pK1LwwEBYV5jSN8/nhowu4rE3/n6tg0CNiZJ0Qk8RsBygvjSo3zhj/UKy83UgMHFAqlBh9JzuE+XkY/L8Q3zUk6kIOKBIHxwDKP7Budqq+qrTjYI5PfH/TfFPXngKviNjsVyqlLx4mT36wLEvImz+WxBPvk84to4ejbmvNjjFhlFu0v/B5xICkyOPpR67MAtax26eTVXCbc5RdgqbkwkZ5incX3tOqhe9M2alDrzszz8Age4CvmVlqrOzRuv2IhO7+qAWQrecISeMQZgdSCU+OUuOTRgBZMmQMcqyYXDE6fjUIL6skKPZi0nMNVGlWjFVL4TfRW6aEE8KPMAj0F8WVuW9Vmlk8Wc1kKThagKaY0Oc1t56eWS1cRgDj2fpRlZnvlw==";
		private String searchX = "16";
		private String searchY = "11";
		private LocalDate startDate = LocalDate.now().minusDays(1);
		private URI uri = URI.create("http://www.hkexnews.hk/sdw/search/searchsdw.aspx");
		private List<String> stocks = new ArrayList<String>();

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

		public String getSearchX() {
			return searchX;
		}

		public void setSearchX(String searchX) {
			Validate.notNull(searchX);
			this.searchX = searchX;
		}

		public String getSearchY() {
			return searchY;
		}

		public void setSearchY(String searchY) {
			Validate.notNull(searchY);
			this.searchY = searchY;
		}

		public LocalDate getStartDate() {
			return startDate;
		}

		public void setStartDate(LocalDate startDate) {
			Validate.notNull(startDate);
			this.startDate = startDate;
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

	}
}
