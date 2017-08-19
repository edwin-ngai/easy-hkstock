package com.wising.easyhkstock.ref;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.wising.easyhkstock.common.domain.Participant;
import com.wising.easyhkstock.common.domain.Stock;
import com.wising.easyhkstock.common.task.DataTask;
import com.wising.easyhkstock.common.task.MongoDispatcher;
import com.wising.easyhkstock.ref.config.ReferenceMongoConfiguration;
import com.wising.easyhkstock.ref.task.ParticipantHelper;
import com.wising.easyhkstock.ref.task.ReferenceDataBuilder;
import com.wising.easyhkstock.ref.task.ReferenceDataHelper;
import com.wising.easyhkstock.ref.task.StockHelper;

//@SpringBootApplication
//@Configuration
@EnableAutoConfiguration
@Import(value=ReferenceMongoConfiguration.class)
public class Application implements CommandLineRunner {

	@Autowired
	private MongoRepository<Stock, String> stockRepository;
	@Autowired
	private MongoRepository<Participant, String> participantRepository;
	
	public static void main(String[] args) {

		SpringApplication.run(Application.class, args);
	}
	
	@Override
	public void run(String... args) throws Exception {
		
//		test();
		ReferenceDataHelper<Stock> stockHelper = new StockHelper();
		ReferenceDataBuilder<Stock> stockBuilder = new ReferenceDataBuilder<Stock>(stockHelper);
		DataTask<Stock> stockTask = new DataTask<Stock>(stockBuilder);
		stockTask.addDispatcher(new MongoDispatcher<Stock, String>(stockRepository));
		stockTask.run();
		
		ReferenceDataHelper<Participant> participantHelper = new ParticipantHelper();
		ReferenceDataBuilder<Participant> participantBuilder = new ReferenceDataBuilder<Participant>(participantHelper);
		DataTask<Participant> participantTask = new DataTask<Participant>(participantBuilder);
		participantTask.addDispatcher(new MongoDispatcher<Participant, String>(participantRepository));
		participantTask.run();
		
//		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
//		scheduler.setPoolSize(2);
//		scheduler.initialize();
//		scheduler.schedule(stockTask, new CronTrigger("0 0 0 * * *"));
//		scheduler.schedule(participantTask, new CronTrigger("0 15 0 * * *"));
	}
	
//	public void test() {
//		disableSslVerification();
//		RestTemplate restTpl = new RestTemplate();
//		try {
//			URI url = new URI("https://formsdev.eu418.p2g.netd2.hsbc.co.uk/manager/secure/rest/groovy-service-invoke/v1/cd-create-coredocs-application/v1/");
//			HttpHeaders headers = new HttpHeaders();
//			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//			headers.add("Authorization", "Basic NDQwMTY0MTc6SHNiYyEyMzQ=");
//			MultiValueMap<String, String> body = new LinkedMultiValueMap<String, String>();
//			body.add("bookingCountry", "FR");
//			body.add("messageSource", "TEST");
//			body.add("applicationData", "{\"BasicDetails\":[{\"BDF012\":\"Y\",\"BDF013\":\"ASEX\",\"BDF015\":\"APLE\",\"BDF016\":\"Other\",\"BDF017\":\"free text\",\"BDF018\":\"CCLC\",\"BDF025\":\"FR\",\"BDF026\":\"FR\",\"BDF029\":\"FR\",\"BDF032\":\"Y\",\"BDF041\":\"Y\",\"BDF061_REPEAT\":[{\"BDF071\":\"APPLICANT1234\"}]}],\"YourConnectedParties\":[{\"KYC183_REPEAT\":[{\"KYC149\":\"OTHER\",\"KYC155\":\"Joey Bob\",\"KYC161\":{\"addrLine1\":\"Flat A, Floor 99,\",\"addrLine2\":\"Elegant Tower\",\"city\":\"9 Shan On Street\",\"ctryCde\":\"FR\",\"postlCde\":\"510003\"},\"KYC162\":{\"addrLine1\":\"Flat A, Floor 99,\",\"addrLine2\":\"Elegant Tower\",\"city\":\"9 Shan On Street\",\"ctryCde\":\"FR\",\"postlCde\":\"510003\"},\"KYC163\":{\"addrLine1\":\"Flat A, Floor 99,\",\"addrLine2\":\"Elegant Tower\",\"city\":\"9 Shan On Street\",\"ctryCde\":\"FR\",\"postlCde\":\"510003\"},\"KYC164\":{\"addrLine1\":\"Flat A, Floor 99,\",\"addrLine2\":\"Elegant Tower\",\"city\":\"9 Shan On Street\",\"ctryCde\":\"FR\",\"postlCde\":\"510003\"},\"KYC165\":\"Jul 13, 2017 12:00:00 AM\",\"KYC167\":{\"addrLine1\":\"Flat A, Floor 99,\",\"addrLine2\":\"Elegant Tower\",\"city\":\"9 Shan On Street\",\"ctryCde\":\"FR\",\"postlCde\":\"510003\"},\"KYC168\":\"HK\",\"KYC170\":\"HK\",\"KYC293\":\"Y\"}],\"KYC236_REPEAT\":[{\"KYC214_REPEAT\":[]}]},{\"KYC183_REPEAT\":[{\"KYC149\":\"OTHER\",\"KYC155\":\"Joey Bob\",\"KYC170\":\"HK\",\"KYC293\":\"Y\"}],\"KYC236_REPEAT\":[{\"KYC193\":\"HK\",\"KYC214_REPEAT\":[{\"KYC213\":{\"addrLine1\":\"Flat A, Floor 99,\",\"addrLine2\":\"Elegant Tower\",\"city\":\"9 Shan On Street\",\"ctryCde\":\"FR\",\"postlCde\":\"510016\"}}]}]}],\"YourEntity\":[{\"KYC007\":\"Trading Name\",\"KYC010\":\"2010-01-12\",\"KYC011\":\"2345\",\"KYC012\":\"000001\",\"KYC018\":\"Y\",\"KYC030\":\"Business Nature Description\",\"KYC034\":\"HK\",\"KYC040_REPEAT\":[{\"KYC036\":\"HK\"},{\"KYC036\":\"CN\"}],\"KYC049\":\"000001\",\"KYC051\":\"Y\",\"KYC060\":{\"addrLine1\":\"Flat A, Floor 99,\",\"addrLine2\":\"Elegant Tower\",\"city\":\"9 Shan On Street\",\"ctryCde\":\"FR\",\"postlCde\":\"510006\"},\"KYE007\":\"2010-01-12\"}]}");
//			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(body, headers);
//			ResponseEntity<String> response = restTpl.postForEntity(url, request, String.class);
//			if (response != null && HttpStatus.OK.equals(response.getStatusCode())) {
//				String html = response.getBody();
//				System.out.println(html);
//			}
//		} catch (URISyntaxException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
//	
//	private void disableSslVerification() {
//	    try
//	    {
//	        // Create a trust manager that does not validate certificate chains
//	        TrustManager[] trustAllCerts = new TrustManager[] {new X509TrustManager() {
//	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
//	                return null;
//	            }
//	            public void checkClientTrusted(X509Certificate[] certs, String authType) {
//	            }
//	            public void checkServerTrusted(X509Certificate[] certs, String authType) {
//	            }
//	        }
//	        };
//
//	        // Install the all-trusting trust manager
//	        SSLContext sc = SSLContext.getInstance("SSL");
//	        sc.init(null, trustAllCerts, new java.security.SecureRandom());
//	        HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
//
//	        // Create all-trusting host name verifier
//	        HostnameVerifier allHostsValid = new HostnameVerifier() {
//	            public boolean verify(String hostname, SSLSession session) {
//	                return true;
//	            }
//	        };
//
//	        // Install the all-trusting host verifier
//	        HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
//	    } catch (NoSuchAlgorithmException e) {
//	        e.printStackTrace();
//	    } catch (KeyManagementException e) {
//	        e.printStackTrace();
//	    }
//	}
}
