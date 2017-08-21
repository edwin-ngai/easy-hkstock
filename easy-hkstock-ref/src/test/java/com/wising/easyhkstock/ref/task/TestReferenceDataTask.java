package com.wising.easyhkstock.ref.task;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wising.easyhkstock.common.domain.Participant;
import com.wising.easyhkstock.common.domain.Stock;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestReferenceDataTask {

    private static final Logger logger = LoggerFactory.getLogger(TestReferenceDataTask.class);

    @Test
    public void testRun() {
		Properties properties = System.getProperties();
		properties.put("http.proxyHost", "intpxy1.hk.hsbc");
		properties.put("http.proxyPort", "8080");
		
		Authenticator authenticator = new Authenticator() {

	        public PasswordAuthentication getPasswordAuthentication() {
	            return (new PasswordAuthentication("44016417",
	                    "!234Hsbc".toCharArray()));
	        }
	    };
	    Authenticator.setDefault(authenticator);

//	    List<Stock> stocks = new ReferenceDataBuilder<Stock>(new StockHelper()).build();
//    	stocks.forEach(stock -> logger.info(stock.toString()));
//    	
//	    List<Participant> participants = new ReferenceDataBuilder<Participant>(new ParticipantHelper()).build();
//	    participants.forEach(participant -> logger.info(participant.toString()));
    	
    }
}
