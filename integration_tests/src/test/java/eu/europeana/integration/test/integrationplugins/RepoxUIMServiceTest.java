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
     * Single Unit test testing all available RepoxUIM service functionality.
     * 
     * @throws Exception
     */
	@Test
	public void testFullRepoxIntegration() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
		    Registry registry = getOsgiService(Registry.class);
			StorageEngine<?> engine = registry.getStorageEngine();
			
			//Create an Aggregator
		    repoxservice.createAggregator(aggregatorCountryName,null);
		    boolean initaggexists = repoxservice.aggregatorExists(aggregatorCountryName);
		    assertTrue(initaggexists);
		    
			//Create a Provider
			Provider provider = engine.createProvider();		
			provider.setAggregator(false);
			provider.setMnemonic(providerNameCode);
			provider.setName(providerName);
			provider.setOaiBaseUrl(providerURI);
			provider.putValue(ControlledVocabularyProxy.PROVIDERDESCRIPTION, "Blablah");
			provider.putValue(ControlledVocabularyProxy.PROVIDERCOUNTRY, aggregatorCountryName);
			provider.putValue(ControlledVocabularyProxy.PROVIDERWEBSITE, providerURI);
			provider.putValue(ControlledVocabularyProxy.SUGARCRMID, "IRRELEVANT_IN_THIS_OPERATION");
			provider.putValue(ControlledVocabularyProxy.PROVIDERTYPE, "Library");
			provider.setOaiMetadataPrefix("ese");   
		    repoxservice.createProviderfromUIMObj(provider);
			assertTrue( repoxservice.providerExists(provider));
			
			//Create a Collection
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
			
			//Update the aggregator
		    repoxservice.updateAggregator(aggregatorCountryName,"ChangedName",aggregatorCountryNameCode,aggregatorURI);
			
		    
		    //Update the provider
			provider.setName("updatedProv");
		    repoxservice.updateProviderfromUIMObj(provider);
		    
		    //Update the datasourve via a collection
			collection.setName("updatedCollection");
			repoxservice.updateDatasourcefromUIMObj(collection);
			
			//Various Repox operations
		    repoxservice.initiateHarvestingfromUIMObj(collection,true);
			  
		    RepoxHarvestingStatus result =repoxservice.getHarvestingStatus(collection);
			assertNotNull(result);
		    repoxservice.getActiveHarvestingSessions();
		    
		    Thread.sleep(time2w84service);
		    String log = repoxservice.getHarvestLog(collection);	
			assertNotNull(log);
			 
		    repoxservice.cancelHarvesting(collection);
		    
		    //Get All aggregators
		    HashSet<Provider<?>> aggr =  (HashSet<Provider<?>>) repoxservice.retrieveAggregators();

		    assertNotNull(aggr);
			assertFalse(aggr.isEmpty());
			
			
			//Retrieve All providers
		    HashSet<Provider<?>> prov = (HashSet<Provider<?>>) repoxservice.retrieveProviders();
		    assertNotNull(prov);
			assertFalse(prov.isEmpty());
						
			//Retrieve all datasources
		    HashSet<Collection<?>> coll = (HashSet<Collection<?>>) repoxservice.retrieveDataSources();
		    assertNotNull(coll);
			assertFalse(coll.isEmpty());
			
			//Delete the Repox Datasource via a UIM Collection Reference
			repoxservice.deleteDatasourcefromUIMObj(collection);
			boolean dsexists = repoxservice.datasourceExists(collection);
			assertFalse(dsexists);
			
			//Delete the Repox Provider via a UIM Provider Reference
			repoxservice.deleteProviderfromUIMObj(provider);
			boolean prexists = repoxservice.providerExists(provider);
			assertFalse(prexists);
			
			//Delete the Aggregator
			repoxservice.deleteAggregator(aggregatorCountryName);
			boolean aggrexists = repoxservice.aggregatorExists(aggregatorCountryName);
			assertFalse(aggrexists);
	}
	
}
