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
package eu.europeana.integration.test.integrationplugins;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.HashSet;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.repox.RepoxUIMService;
import eu.europeana.uim.repox.model.RepoxHarvestingStatus;
import org.apache.log4j.Logger;

/**
 * Class implementing Unit Tests for UIM Repox Service
 * This is a PAX-EXAM OSGI based test. This practically means that tests are executed within
 * an OSGI Karaf container created by PAX-EXAM. Make sure that no other instance of Karaf is
 * running locally during the execution of the tests (otherwise the tests will fail)
 * 
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since May 30, 2011
 */

@RunWith(JUnit4TestRunner.class)
public class RepoxUIMServiceTest extends AbstractEuropeanaIntegrationTest{
	
	
	private static org.apache.log4j.Logger LOGGER = Logger.getLogger(RepoxUIMServiceTest.class);
	
	private final static String aggregatorCountryName = "al";
	private final static String aggregatorCountryNameCode = "1742";
	private final static String aggregatorURI = "http://www.in.gr";
	
	private final static String providerName = "TestOSGIProvider";
	private final static String providerNameCode = "76341";
	private final static String providerURI = "http://www.in.gr";
	
	private final static String collectionName = "TestOSGICollection";
	private final static String collectionNameCode = "89543";
	private final static String collectionOAIPMHURI = "http://bd1.inesc-id.pt:8080/repox2/OAIHandler";
	
	private final static String metadatanamespace = "http://www.europeana.eu/schemas/ese/";
	private final static String metadataschema = "http://www.europeana.eu/schemas/ese/ESE-V3.4.xsd";
	private final static String HARVESTING_TYPE_OAI = "oai_pmh";
	
	
	/**
	 * Create a (dummy) aggregator in Repox.
	 * @throws Exception
	 */
	@Test
	public void testCreateAggregator() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);        	
		    repoxservice.createAggregator(aggregatorCountryName,null);
		    boolean exists = repoxservice.aggregatorExists(aggregatorCountryName);
		    assertTrue(exists);
	}
	
	
	
	/**
	 * Create a Repox Provider from an UIM provider.
	 * @throws Exception
	 */
	@Test
	public void testCreateProvider() throws Exception{
            Thread.sleep(time2w84service);
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);			
		    Registry registry = getOsgiService(Registry.class);
			StorageEngine<?> engine = registry.getStorageEngine();
			Provider provider = engine.createProvider();
			
			provider.setAggregator(false);
			provider.setMnemonic(providerNameCode);
			provider.setName(providerName);
			provider.setOaiBaseUrl(providerURI);
			provider.putValue(ControlledVocabularyProxy.PROVIDERDESCRIPTION, "Blablah");
			provider.putValue(ControlledVocabularyProxy.PROVIDERCOUNTRY, aggregatorCountryName);
			provider.putValue(ControlledVocabularyProxy.PROVIDERWEBSITE, providerURI);
			
			provider.putValue(ControlledVocabularyProxy.SUGARCRMID, "123213123231");
			provider.putValue(ControlledVocabularyProxy.PROVIDERTYPE, "Library");
			provider.setOaiMetadataPrefix("ese");   

		    repoxservice.createProviderfromUIMObj(provider);
		    
		    
			assertTrue( repoxservice.providerExists(provider));
	
	}
    
	
	/**
	 * Create a Repox Datasource from a UIM collection.
	 * @throws Exception
	 */
	@Test
	public void testCreateDatasource() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    Registry registry = getOsgiService(Registry.class);
		    
			StorageEngine<?> engine = registry.getStorageEngine();
			
			Provider provider = engine.createProvider();
			provider.setName(providerName);
			provider.setMnemonic(providerNameCode);
			provider.putValue(ControlledVocabularyProxy.REPOXID, providerName + "r0");
			
		    Collection collection = engine.createCollection(provider);

		    collection.setLanguage("fr");
		    collection.setMnemonic(collectionNameCode);
		    collection.setName(collectionName);
		    collection.setOaiBaseUrl(collectionOAIPMHURI);
		    collection.setOaiMetadataPrefix("ese");
        	
		    collection.putValue(ControlledVocabularyProxy.METADATA_NAMESPACE, metadatanamespace);
	        collection.putValue(ControlledVocabularyProxy.METADATA_SCHEMA, metadataschema);
	        collection.putValue(ControlledVocabularyProxy.HARVESTING_TYPE, HARVESTING_TYPE_OAI);  
		            	
			repoxservice.createDatasourcefromUIMObj(collection, provider);
			
			assertTrue(repoxservice.datasourceExists(collection));
	
	}
    
	
	/**
	 * Update an existing (pseudo)aggregator
	 * @throws Exception
	 */
	@Test
	public void testUpdateAggregator() throws Exception{
            Thread.sleep(time2w84service);
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class); 	
		    repoxservice.updateAggregator(aggregatorCountryName,"ChangedName",aggregatorCountryNameCode,aggregatorURI);
			assertNotNull(repoxservice);
	}
	
	
	/**
	 * Update a Repox Provider from a UIM provider.
	 * @throws Exception
	 */
	@Test
	public void testUpdateProvider() throws Exception{
            Thread.sleep(time2w84service);
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);

		    Registry registry = getOsgiService(Registry.class);
		    
			StorageEngine<?> engine = registry.getStorageEngine();
			
			Provider provider = engine.createProvider();			
			provider.setAggregator(false);
			provider.setMnemonic(providerNameCode);
			provider.setName(providerName);
			provider.setOaiBaseUrl(providerURI);
			provider.putValue(ControlledVocabularyProxy.PROVIDERDESCRIPTION, "Blablah...");
			provider.putValue(ControlledVocabularyProxy.PROVIDERCOUNTRY, aggregatorCountryName);
			provider.putValue(ControlledVocabularyProxy.PROVIDERWEBSITE, providerURI);
			
			provider.putValue(ControlledVocabularyProxy.REPOXID, providerName + "r0");
			provider.putValue(ControlledVocabularyProxy.SUGARCRMID, "123213123231");
			provider.putValue(ControlledVocabularyProxy.PROVIDERTYPE, "Library");
			
			provider.setName("updatedProv");
        	
		    repoxservice.updateProviderfromUIMObj(provider);

	}
	
	
	/**
	 * Update a Repox Datasource from a UIM collection.
	 * @throws Exception
	 */
	@Test
	public void testUpdateDatasource() throws Exception{
        Thread.sleep(time2w84service);
	    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);

	    Registry registry = getOsgiService(Registry.class);
	    
		StorageEngine<?> engine = registry.getStorageEngine();
		
		Provider provider = engine.createProvider();
		provider.setName(providerName);
		provider.setMnemonic(providerNameCode);
		provider.putValue(ControlledVocabularyProxy.REPOXID, providerName + "r0");
		
	    Collection<?> collection = engine.createCollection(provider);
	    collection.setName(collectionName);
	    collection.setMnemonic(collectionNameCode);
	    collection.putValue(ControlledVocabularyProxy.REPOXID, collectionName + collectionNameCode);
	    collection.setLanguage("it");
	    collection.setOaiBaseUrl(collectionOAIPMHURI);
	    collection.setOaiMetadataPrefix("ese");
	    
		collection.setName("updatedCollection");
	    collection.putValue(ControlledVocabularyProxy.METADATA_NAMESPACE, metadatanamespace);
        collection.putValue(ControlledVocabularyProxy.METADATA_SCHEMA, metadataschema);
        collection.putValue(ControlledVocabularyProxy.HARVESTING_TYPE, HARVESTING_TYPE_OAI);  
        
		repoxservice.updateDatasourcefromUIMObj(collection);
		
		assertNotNull(repoxservice);
	
	}
	
	
	/**
	 * Test various REPOX Operations
	 * @throws Exception
	 */
	@Test
	public void testHarvestingOperations() throws Exception{
            Thread.sleep(time2w84service);
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    Registry registry = getOsgiService(Registry.class);
		    
			StorageEngine<?> engine = registry.getStorageEngine();
			
			Provider provider = engine.createProvider();
			provider.setName(providerName);
			provider.setMnemonic(providerNameCode);
			provider.putValue(ControlledVocabularyProxy.REPOXID, providerName + "r0");
			
		    Collection<?> coll = engine.createCollection(provider);
		    coll.setName(collectionName);
		    coll.setMnemonic(collectionNameCode);
		    coll.putValue(ControlledVocabularyProxy.REPOXID, collectionName + collectionNameCode);
			
		    repoxservice.initiateHarvestingfromUIMObj(coll,true);
		  
		    RepoxHarvestingStatus result =repoxservice.getHarvestingStatus(coll);
			assertNotNull(result);
		    repoxservice.getActiveHarvestingSessions();
		    
		    Thread.sleep(time2w84service);
		    String log = repoxservice.getHarvestLog(coll);	
			assertNotNull(log);
			 
		    repoxservice.cancelHarvesting(coll);
	    

	
	}
	
	
	/**
	 * 
	 * @throws Exception
	 */
	@Test
	public void testRetrieveAggregatorsService() throws Exception{
            Thread.sleep(time2w84service);
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    HashSet<Provider<?>> aggr =  (HashSet<Provider<?>>) repoxservice.retrieveAggregators();

		    assertNotNull(aggr);
			assertFalse(aggr.isEmpty());
	
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testRetrieveProvidersService() throws Exception{
            Thread.sleep(time2w84service);
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    HashSet<Provider<?>> prov = (HashSet<Provider<?>>) repoxservice.retrieveProviders();

		    assertNotNull(prov);
			assertFalse(prov.isEmpty());
	
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testRetrieveDataSourcesService() throws Exception{
		    Thread.sleep(time2w84service); 
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    HashSet<Collection<?>> coll = (HashSet<Collection<?>>) repoxservice.retrieveDataSources();

		    assertNotNull(coll);
			assertFalse(coll.isEmpty());
	
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testDeleteDatasource() throws Exception{
		Thread.sleep(time2w84service);
	    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
		
	    Registry registry = getOsgiService(Registry.class);
	    
		StorageEngine<?> engine = registry.getStorageEngine();
		
		Provider provider = engine.createProvider();
		provider.setName(providerName);
		provider.setMnemonic(providerNameCode);
		provider.putValue(ControlledVocabularyProxy.REPOXID, providerName + "r0");
		
	    Collection<?> coll = engine.createCollection(provider);
	    coll.setName(collectionName);
	    coll.setMnemonic(collectionNameCode);
	    coll.putValue(ControlledVocabularyProxy.REPOXID, collectionName +  collectionNameCode);
		
		repoxservice.deleteDatasourcefromUIMObj(coll);
		
		boolean exists = repoxservice.datasourceExists(coll);
		
		assertFalse(exists);
	
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testDeleteProvider() throws Exception{
		    Thread.sleep(time2w84service);
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
		    Registry registry = getOsgiService(Registry.class);
			StorageEngine<?> engine = registry.getStorageEngine();
			Provider<?> provider = engine.createProvider();
			provider.setName(providerName);
			provider.setMnemonic(providerNameCode);
			provider.putValue(ControlledVocabularyProxy.REPOXID, providerName + "r0");
			repoxservice.deleteProviderfromUIMObj(provider);
			boolean exists = repoxservice.providerExists(provider);
			assertFalse(exists);
	
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testDeleteAggregator() throws Exception{
		Thread.sleep(time2w84service);
	    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);			
		repoxservice.deleteAggregator(aggregatorCountryName);
		boolean exists = repoxservice.aggregatorExists(aggregatorCountryName);
		assertFalse(exists);
	}
	
	

}
