package com.wising.easyhkstock.ref.task;

import java.net.Authenticator;
import java.net.PasswordAuthentication;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Properties;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.wising.easyhkstock.common.domain.Participant;
import com.wising.easyhkstock.ref.task.deprecated.ParticipantListTask;

@RunWith(SpringJUnit4ClassRunner.class)
public class TestParticipantTask {

    private static final Logger logger = LoggerFactory.getLogger(TestParticipantTask.class);

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
//		URI enUrl = new URI("http://www.hkexnews.hk/sdw/search/partlist.aspx?SortBy=PartID&ShareholdingDate=20170807");
//    	URI scUrl = new URI("http://sc.hkexnews.hk/TuniS/www.hkexnews.hk/sdw/search/partlist_c.aspx?SortBy=PartID&ShareholdingDate=20170807");
//    	URI tcUrl = new URI("http://www.hkexnews.hk/sdw/search/partlist_c.aspx?SortBy=PartID&ShareholdingDate=20170807");
    	List<Participant> participants = new ParticipantListTask().run();
    	participants.forEach(participant -> logger.info(participant.toString()));

    }
}
