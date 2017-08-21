package com.wising.easyhkstock.ccass.task;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.util.Properties;
import java.util.AbstractMap.SimpleImmutableEntry;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wising.easyhkstock.ccass.domain.SnapshotDetail;
import com.wising.easyhkstock.ccass.domain.SnapshotSummary;


@RunWith(SpringJUnit4ClassRunner.class)
public class TestSnapshotTask {

    private static final Logger logger = LoggerFactory.getLogger(TestSnapshotTask.class);

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
//	    SimpleImmutableEntry<SnapshotSummary, SnapshotDetail> result = new SnapshotTask().run();
//	    logger.info(result.getKey().toString());
//	    logger.info(result.getValue().toString());
    	
    }
}
