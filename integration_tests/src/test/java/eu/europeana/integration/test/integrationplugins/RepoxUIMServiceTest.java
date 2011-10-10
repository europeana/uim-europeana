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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.felix;
import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemPackage;
import static org.ops4j.pax.exam.CoreOptions.wrappedBundle;

import java.io.StringWriter;
import java.util.HashSet;
import java.util.Set;


import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.CoreOptions.waitForFrameworkStartup;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.container.def.PaxRunnerOptions.scanFeatures;

import org.apache.karaf.testing.Helper;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Inject;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.container.def.PaxRunnerOptions;
import org.ops4j.pax.exam.junit.Configuration;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;

import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;


import eu.europeana.uim.repoxclient.jibxbindings.DataSources;
import eu.europeana.uim.repoxclient.plugin.RepoxRestClient;
import eu.europeana.uim.repoxclient.plugin.RepoxUIMService;
import eu.europeana.uim.repoxclient.rest.exceptions.RepoxException;

import org.apache.karaf.testing.AbstractIntegrationTest;
import org.apache.log4j.Logger;

/**
 * Class implementing Unit Tests for UIM Repox Service
 * This is a PAX-EXAM OSGI based test. This practically means that tests are executed within
 * an OSGI Karaf container created by PAX-EXAM. Make sure that no other instance of Karaf is
 * running locally during the execution of the tests (otherwise the tests will fail)
 * 
 * @author Georgios Markakis
 */

@RunWith(JUnit4TestRunner.class)
public class RepoxUIMServiceTest extends AbstractIntegrationTest{
	
	
	private static org.apache.log4j.Logger LOGGER = Logger.getLogger(RepoxUIMServiceTest.class);
	
	private final static String aggregatorName = "TestOSGIAggregator";
	private final static String aggregatorNameCode = "1742";
	private final static String aggregatorURI = "http://www.bbc.co.uk/";
	
	private final static String providerName = "TestOSGIProvider";
	private final static String providerNameCode = "76341";
	private final static String providerURI = "http://www.bbc.co.uk/";
	
	private final static String collectionName = "TestOSGICollection";
	private final static String collectionNameCode = "89543";
	private final static String collectionOAIPMHURI = "http://bd1.inesc-id.pt:8080/repoxel/OAIHandler";
	
    /**
     * This is the configuration section of the "virtual" Karaf container during the tests execution. It sets 
     * all the dependencies required for the installation of the UIM Core & Repox Plugin modules 
     * @return
     * @throws Exception
     */
    @Configuration
    public static Option[] configuration() throws Exception {
         return combine(
 				Helper.getDefaultOptions(
						systemProperty("karaf.name").value("junit"),
						systemProperty("org.ops4j.pax.logging.DefaultServiceLog.level").value("INFO")),

				
				//This section corresponds to the "spring-ws" & "spring-dm" features in spring-features.xml		
		        mavenBundle().groupId("javax.activation").artifactId("com.springsource.javax.activation").version("1.1.1"),
		        mavenBundle().groupId("javax.servlet").artifactId("com.springsource.javax.servlet").version("2.5.0"),
		        mavenBundle().groupId("javax.xml.stream").artifactId("com.springsource.javax.xml.stream").version("1.0.1"),
		        mavenBundle().groupId("org.apache.commons").artifactId("com.springsource.org.apache.commons.logging").version("1.1.1"),
		        mavenBundle().groupId("org.aopalliance").artifactId("com.springsource.org.aopalliance").version("1.0.0"),
	            scanFeatures("mvn:org.apache.karaf.assemblies.features/standard/2.2.0/xml/features","spring","spring-dm"),
		        mavenBundle().groupId("org.springframework").artifactId("org.springframework.oxm").version("3.0.5.RELEASE"),
		        mavenBundle().groupId("org.springframework").artifactId("org.springframework.web").version("3.0.5.RELEASE"),
		        mavenBundle().groupId("org.springframework").artifactId("org.springframework.web.servlet").version("3.0.5.RELEASE"),
		        mavenBundle().groupId("org.springframework.ws").artifactId("org.springframework.xml").version("2.0.0.RELEASE"),
		        mavenBundle().groupId("javax.xml.bind").artifactId("com.springsource.javax.xml.bind").version("2.2.0"),
		        mavenBundle().groupId("javax.xml.soap").artifactId("com.springsource.javax.xml.soap").version("1.3.0"),
		        mavenBundle().groupId("org.apache.commons").artifactId("com.springsource.org.apache.commons.codec").version("1.4.0"),
		        mavenBundle().groupId("org.apache.commons").artifactId("com.springsource.org.apache.commons.httpclient").version("3.1.0"),
		        mavenBundle().groupId("org.springframework.ws").artifactId("org.springframework.ws").version("2.0.0.RELEASE"),
		        mavenBundle().groupId("org.springframework").artifactId("org.springframework.jms").version("3.0.5.RELEASE"),
		        mavenBundle().groupId("javax.mail").artifactId("com.springsource.javax.mail").version("1.4.1"),
		        mavenBundle().groupId("javax.jms").artifactId("com.springsource.javax.jms").version("1.1.0"),
		        mavenBundle().groupId("javax.xml.rpc").artifactId("com.springsource.javax.xml.rpc").version("1.1.0"),
		        mavenBundle().groupId("javax.ejb").artifactId("com.springsource.javax.ejb").version("3.0.0"),
		        mavenBundle().groupId("javax.mail").artifactId("com.springsource.javax.mail").version("1.4.1"),
		        mavenBundle().groupId("org.springframework").artifactId("org.springframework.jms").version("3.0.5.RELEASE"),
		        mavenBundle().groupId("javax.xml.rpc").artifactId("com.springsource.javax.xml.rpc").version("1.1.0"),
		        mavenBundle().groupId("com.sun.xml").artifactId("com.springsource.com.sun.xml.messaging.saaj").version("1.3.2"),
		        mavenBundle().groupId("org.joda").artifactId("com.springsource.org.joda.time").version("1.6.0"),
		        mavenBundle().groupId("org.xmlpull").artifactId("com.springsource.org.xmlpull").version("1.1.4"),
		        mavenBundle().groupId("org.jibx").artifactId("jibx-run").version("1.2.3"),
		        mavenBundle().groupId("org.jibx").artifactId("jibx-extras").version("1.2.3"),
		        mavenBundle().groupId("org.apache.commons").artifactId("com.springsource.org.apache.commons.collections").version("3.2.1"),
		        mavenBundle().groupId("org.springframework").artifactId("org.springframework.transaction").version("3.0.5.RELEASE"),
		        mavenBundle().groupId("com.opensymphony.quartz").artifactId("com.springsource.org.quartz").version("1.6.2"),
				wrappedBundle(mavenBundle().groupId("stax").artifactId("stax").version("1.2.0")),	
		        
				//This feature corresponds to the "uim-core" feature in uim-features.xml
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-common").versionAsInProject(),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-api").versionAsInProject(),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-storage-memory").versionAsInProject(),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-plugin-basic").versionAsInProject(),
                mavenBundle().groupId("eu.europeana").artifactId("europeana-uim-plugin-repox").versionAsInProject(),
                
                felix(),

                waitForFrameworkStartup()
        );
    }
	
    
    
    
	/**
	 * @throws Exception
	 */
	@Test
	public void testCreateAggregator() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    Registry registry = getOsgiService(Registry.class);
		    
			StorageEngine<?> engine = registry.getStorageEngine();
			
			Provider aggregator = engine.createProvider();
			
			aggregator.setAggregator(true);
			aggregator.setMnemonic(aggregatorNameCode);
			aggregator.setName(aggregatorName);
			aggregator.setOaiBaseUrl(aggregatorURI);
			aggregator.putValue("sugarID", "123213123231");
			aggregator.setOaiMetadataPrefix("ese");   

        	engine.updateProvider(aggregator);
        	engine.checkpoint();
        	
		    repoxservice.createAggregatorfromUIMObj(aggregator, false);

		    boolean exists = repoxservice.aggregatorExists(aggregator);
		    
		    assertTrue(exists);
	
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testCreateProvider() throws Exception{

		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
			
		    Registry registry = getOsgiService(Registry.class);
		    
			StorageEngine<?> engine = registry.getStorageEngine();
			
			Provider provider = engine.createProvider();
			
			Provider aggregator = engine.createProvider();
			aggregator.setName(aggregatorName);
			aggregator.setMnemonic(aggregatorNameCode);
			aggregator.putValue("repoxID", aggregatorName +"r0");
			
			assertNotNull(aggregator);	
			
			provider.getRelatedOut().add(aggregator);
			
			provider.setAggregator(false);
			provider.setMnemonic(providerNameCode);
			provider.setName(providerName);
			provider.setOaiBaseUrl(providerURI);
			provider.putValue("repoxDescription", "Blablah...");
			provider.putValue("repoxCountry", "fr");
			provider.putValue("sugarID", "123213123231");
			provider.putValue("repoxProvType", "ARCHIVE");
			provider.setOaiMetadataPrefix("ese");   

			
			
        	engine.updateProvider(provider);
        	engine.checkpoint();
        	
		    repoxservice.createProviderfromUIMObj(provider, false);
			assertNotNull(repoxservice);
	
	}
    
	
	/**
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
			provider.putValue("repoxID", providerName + "r0");
			
		    Collection collection = engine.createCollection(provider);

		    collection.setLanguage("FR");
		    collection.setMnemonic(collectionNameCode);
		    collection.setName(collectionName);
		    collection.setOaiBaseUrl(collectionOAIPMHURI);
		    collection.setOaiMetadataPrefix("ese");
        	
			engine.updateCollection(collection);
			engine.checkpoint();
        	
			repoxservice.createDatasourcefromUIMObj(collection, provider);
			
			assertNotNull(repoxservice);
	
	}
    
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testUpdateAggregator() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    Registry registry = getOsgiService(Registry.class);
		    
			StorageEngine<?> engine = registry.getStorageEngine();
			
			Provider aggregator = engine.createProvider();
			aggregator.setName(aggregatorName);
			aggregator.setMnemonic(aggregatorNameCode);
			aggregator.putValue("repoxID", aggregatorName +"r0");
			aggregator.setOaiBaseUrl(aggregatorURI);
			aggregator.putValue("sugarID", "123213123231");
			aggregator.setOaiMetadataPrefix("ese");   
			aggregator.setAggregator(true);
			
			aggregator.setName("updatedAggr");
        	engine.updateProvider(aggregator);
        	engine.checkpoint();
        	
        	
		    repoxservice.updateAggregatorfromUIMObj(aggregator);
		    
		    
			assertNotNull(repoxservice);
	
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testUpdateProvider() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);

		    Registry registry = getOsgiService(Registry.class);
		    
			StorageEngine<?> engine = registry.getStorageEngine();
			
			Provider provider = engine.createProvider();
			provider.setName(providerName);
			provider.setMnemonic(providerNameCode);
			provider.putValue("repoxID", providerName + "r0");
			provider.setOaiBaseUrl(providerURI);
			provider.putValue("repoxDescription", "Blablah...");
			provider.putValue("repoxCountry", "it");
			provider.putValue("repoxProvType", "ARCHIVE");
			provider.setOaiMetadataPrefix("ese");   
			
			provider.setName("updatedProv");
        	engine.updateProvider(provider);
        	engine.checkpoint();
        	
		    repoxservice.updateProviderfromUIMObj(provider);

	
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testUpdateDatasource() throws Exception{
	    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);

	    Registry registry = getOsgiService(Registry.class);
	    
		StorageEngine<?> engine = registry.getStorageEngine();
		
		Provider provider = engine.createProvider();
		provider.setName(providerName);
		provider.setMnemonic(providerNameCode);
		provider.putValue("repoxID", providerName + "r0");
		
	    Collection collection = engine.createCollection(provider);
	    collection.setName(collectionName);
	    collection.setMnemonic(collectionNameCode);
	    collection.putValue("repoxID", collectionName);
	    collection.setLanguage("it");
	    collection.setOaiBaseUrl(collectionOAIPMHURI);
	    collection.setOaiMetadataPrefix("ese");
	    
		collection.setName("updatedCollection");
		
		repoxservice.updateDatasourcefromUIMObj(collection);
		
		assertNotNull(repoxservice);
	
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testHarvestingOperations() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    Registry registry = getOsgiService(Registry.class);
		    
			StorageEngine<?> engine = registry.getStorageEngine();
			
			Provider provider = engine.createProvider();
			provider.setName(providerName);
			provider.setMnemonic(providerNameCode);
			provider.putValue("repoxID", providerName + "r0");
			
		    Collection coll = engine.createCollection(provider);
		    coll.setName(collectionName);
		    coll.setMnemonic(collectionNameCode);
		    coll.putValue("repoxID", collectionName);
			

		    repoxservice.initiateHarvestingfromUIMObj(coll);
		  
		    repoxservice.getHarvestingStatus(coll);
		    
		    repoxservice.getActiveHarvestingSessions();
		    
		    repoxservice.cancelHarvesting(coll);
		    
			//TODO:Harvest Logs are not generated if harvesting is not complete. How to test this?
		    //String log = repoxservice.getHarvestLog(coll);
		    
		    
			assertNotNull(repoxservice);
	
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testRetrieveAggregatorsService() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    HashSet<Provider> aggr = (HashSet<Provider>) repoxservice.retrieveAggregators();

		    assertNotNull(aggr);
			assertFalse(aggr.isEmpty());
	
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testRetrieveProvidersService() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    HashSet<Provider> prov = (HashSet<Provider>) repoxservice.retrieveProviders();

		    assertNotNull(prov);
			assertFalse(prov.isEmpty());
	
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testRetrieveDataSourcesService() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
	
		    HashSet<Collection> coll = (HashSet<Collection>) repoxservice.retrieveDataSources();

		    assertNotNull(coll);
			assertFalse(coll.isEmpty());
	
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testDeleteDatasource() throws Exception{
	    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
		
	    Registry registry = getOsgiService(Registry.class);
	    
		StorageEngine<?> engine = registry.getStorageEngine();
		
		Provider provider = engine.createProvider();
		provider.setName(providerName);
		provider.setMnemonic(providerNameCode);
		provider.putValue("repoxID", providerName + "r0");
		
	    Collection coll = engine.createCollection(provider);
	    coll.setName(collectionName);
	    coll.setMnemonic(collectionNameCode);
	    coll.putValue("repoxID", collectionName);
		
		repoxservice.deleteDatasourcefromUIMObj(coll);
		
		boolean exists = repoxservice.datasourceExists(coll);
		
		assertFalse(exists);
	
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testDeleteProvider() throws Exception{
		    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
			
		    Registry registry = getOsgiService(Registry.class);
		    
			StorageEngine<?> engine = registry.getStorageEngine();
			
			Provider provider = engine.createProvider();
			provider.setName(providerName);
			provider.setMnemonic(providerNameCode);
			provider.putValue("repoxID", providerName + "r0");
			
			repoxservice.deleteProviderfromUIMObj(provider);
			
			boolean exists = repoxservice.providerExists(provider);
			
			assertFalse(exists);
	
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void testDeleteAggregator() throws Exception{
	    RepoxUIMService repoxservice = getOsgiService(RepoxUIMService.class);
		
	    Registry registry = getOsgiService(Registry.class);
	    
		StorageEngine<?> engine = registry.getStorageEngine();
		
		Provider aggregator = engine.createProvider();
		aggregator.setName(aggregatorName);
		aggregator.setMnemonic(aggregatorNameCode);
		aggregator.putValue("repoxID", aggregatorName +"r0");
		aggregator.setAggregator(true);
		
		repoxservice.deleteAggregatorfromUIMObj(aggregator);
		
		boolean exists = repoxservice.aggregatorExists(aggregator);
		
		assertFalse(exists);
	
	}
	
	

}
