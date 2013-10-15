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
import org.junit.AfterClass;
import org.junit.BeforeClass;
import eu.europeana.uim.mintclient.ampq.MintAMPQClientSync;
import eu.europeana.uim.mintclient.ampq.MintClientFactory;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.plugins.mint.test.AbstractSyncMintTest;
import org.apache.log4j.Logger;


/**
 * Integration Tests for synchronous MINT client calls
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class MintSyncIntegrationTest extends AbstractSyncMintTest{
	private static org.apache.log4j.Logger log = Logger.getLogger(MintSyncIntegrationTest.class);
	
	/**
	 * Initialize test & superclass fields 
	 * 
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	@BeforeClass public static void initclient() throws MintOSGIClientException, MintRemoteException {
		MintClientFactory factory = new MintClientFactory();
		
		String proplocation = MintSyncIntegrationTest.class.getProtectionDomain().getCodeSource().getLocation() + "mintTestConfig.properties";
		String truncated = proplocation.replace("file:", "");
		client = (MintAMPQClientSync) factory.syncMode().createClient(truncated); 
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
		log.info("Destroyed Synchronous Client after test completion");
	}
	

	
}
