/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.repoxclient.test;

import static org.junit.Assert.*;
import javax.annotation.Resource;
import java.util.ArrayList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.apache.log4j.Logger;

import eu.europeana.uim.repoxclient.rest.RepoxRestClient;
import eu.europeana.uim.repoxclient.rest.exceptions.RepoxException;



/**
 * Class implementing Unit Tests for RepoxClient
 * 
 * @author Georgios Markakis
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:testContext.xml"})
public class RepoxClientTest {
	
	private static org.apache.log4j.Logger LOGGER = Logger.getLogger(RepoxClientTest.class);
	
	@Resource
	RepoxRestClient repoxclient;
	
	@Test
	public void testRetrieveDataSources(){
		try {
			repoxclient.retrieveDataSources();
		} catch (RepoxException e) {
			e.printStackTrace();
		}
	}
	
	

}
