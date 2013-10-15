/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.integration.test.nonosgi;

import java.io.IOException;
import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import eu.europeana.uim.mintclient.ampq.MintAMPQClientASync;
import eu.europeana.uim.mintclient.ampq.MintClientFactory;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.plugins.mint.test.AbstractAsyncMintTest;


/**
 * Integration Tests for asynchronous MINT client calls
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class MintAsyncIntegrationTest extends AbstractAsyncMintTest{

	private static Logger log = Logger.getLogger(MintAsyncIntegrationTest.class);


	/**
	 * Initialize test & superclass fields 
	 * 
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */

	@BeforeClass public static void initclient() throws MintOSGIClientException, MintRemoteException {
		MintClientFactory factory = new MintClientFactory();
		String proplocation = MintAsyncIntegrationTest.class.getProtectionDomain().getCodeSource().getLocation() + "mintTestConfig.properties";
		String truncated = proplocation.replace("file:", "");
		client = (MintAMPQClientASync) factory.asyncMode(TestListener.class).createClient(truncated);
		listener = (TestListener) client.getConsumer();
		log.info("Initialized test context & created Asynchronous Client");
	}
	    
	/**
	 * Closes the current connection used for tests, nullifies the client
	 * and forces garbage collection.
	 * 
	 * @throws IOException 
	 */

	@AfterClass public static void tearDown() throws IOException {
	   client.closeConnection();	
	   client = null;
	   System.gc();
	   log.info("Destroyed Asynchronous Client after test completion");
	}
	
	
}
