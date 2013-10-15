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
package eu.europeana.uim.repoxclient.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import org.apache.log4j.Logger;
import org.junit.Test;
import eu.europeana.uim.repoxclient.jibxbindings.Aggregator;
import eu.europeana.uim.repoxclient.jibxbindings.Aggregators;
import eu.europeana.uim.repoxclient.jibxbindings.DataProviders;
import eu.europeana.uim.repoxclient.jibxbindings.DataSources;
import eu.europeana.uim.repoxclient.jibxbindings.Description;
import eu.europeana.uim.repoxclient.jibxbindings.HarvestingStatus;
import eu.europeana.uim.repoxclient.jibxbindings.Name;
import eu.europeana.uim.repoxclient.jibxbindings.NameCode;
import eu.europeana.uim.repoxclient.jibxbindings.Provider;
import eu.europeana.uim.repoxclient.jibxbindings.RunningTasks;
import eu.europeana.uim.repoxclient.jibxbindings.Source;
import eu.europeana.uim.repoxclient.jibxbindings.Success;
import eu.europeana.uim.repoxclient.plugin.RepoxRestClient;
import eu.europeana.uim.repoxclient.utils.TestUtils;

/**
 * Unit tests implementation for Repox client used by both
 * mockito based and integration tests
 * 
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since Oct 10, 2013
 */
public abstract class AbstractRepoxClientTest {

	protected RepoxRestClient repoxRestClient;	
	
	private static org.apache.log4j.Logger LOGGER = Logger.getLogger(AbstractRepoxClientTest.class);	


	/*
	 * Retrieval operations
	 */

	/**
	 * @throws Exception
	 */
	@Test
	public void testGetDatasources() throws Exception{
		DataSources ds =repoxRestClient.retrieveDataSources();
		assertNotNull(ds);
		TestUtils.logMarshalledObject(ds,LOGGER);	
	}


	/**
	 * @throws Exception
	 */
	@Test
	public void testGetAggregators() throws Exception{
		Aggregators aggrs =repoxRestClient.retrieveAggregators();
		assertNotNull(aggrs);
		TestUtils.logMarshalledObject(aggrs,LOGGER);
	}
		

	/**
	 * @throws Exception
	 */
	@Test
	public void testGetProviders() throws Exception{
		DataProviders prov =repoxRestClient.retrieveProviders();
		assertNotNull(prov);
		TestUtils.logMarshalledObject(prov,LOGGER);
	}


	/*
	 * Creation operations Tests
	 */



	/**
	 * @throws Exception
	 */
	@Test
	public void testCreateUpdateDeleteAggregator() throws Exception{

		//Initialize the Aggregator Object
		Aggregator aggr = TestUtils.createAggregatorObj("aggr10", "7777", "http://www.in.gr");
		//Create the Aggregator
		Aggregator rtAggr =  repoxRestClient.createAggregator(aggr);
		assertNotNull(rtAggr);
		assertEquals(aggr.getName().getName(),rtAggr.getName().getName());
		assertEquals(aggr.getNameCode().getNameCode(),rtAggr.getNameCode().getNameCode());
		assertEquals(aggr.getUrl().getUrl(),rtAggr.getUrl().getUrl());
		TestUtils.logMarshalledObject(rtAggr,LOGGER);
		//Update the Aggregator
		NameCode upnamecode = new NameCode();
		upnamecode.setNameCode("77777");
		rtAggr.setNameCode(upnamecode);
		Aggregator upAggr =  repoxRestClient.updateAggregator(rtAggr);
		assertNotNull(upAggr);
		assertEquals(rtAggr.getId(),upAggr.getId());
		assertEquals(rtAggr.getName().getName(),upAggr.getName().getName());
		assertEquals(rtAggr.getNameCode().getNameCode(),upAggr.getNameCode().getNameCode());
		assertEquals(rtAggr.getUrl().getUrl(),upAggr.getUrl().getUrl());
		TestUtils.logMarshalledObject(upAggr,LOGGER);
		
		//Retrieve the Agrrgator by its ID
		Aggregator retrievedAggr  = repoxRestClient.retrieveAggregator(rtAggr.getId());
		
		assertNotNull(retrievedAggr);
		assertEquals(retrievedAggr.getId(),upAggr.getId());
		assertEquals(retrievedAggr.getName().getName(),upAggr.getName().getName());
		assertEquals(retrievedAggr.getNameCode().getNameCode(),upAggr.getNameCode().getNameCode());
		assertEquals(retrievedAggr.getUrl().getUrl(),upAggr.getUrl().getUrl());
		TestUtils.logMarshalledObject(upAggr,LOGGER);
		
		//Delete the Aggregator
		Success res = repoxRestClient.deleteAggregator(rtAggr.getId());
		assertNotNull(res);
		
		//Retrieve the Agrrgator by its ID
		Aggregator retrieveddelAggr  = repoxRestClient.retrieveAggregator(rtAggr.getId());
		assertNull(retrieveddelAggr);
		
		TestUtils.logMarshalledObject(res,LOGGER);
	}





	/**
	 * Test the creation, Update & Deletion of a Provider
	 *  
	 * @throws Exception
	 */
	@Test
	public void testCreateUpdateDeleteProvider() throws Exception{
		
		//Create an Aggregator for testing purposes
		Aggregator aggr = 	TestUtils.createAggregatorObj("aggr0","7777","www.in.gr");
		Aggregator rtAggr =  repoxRestClient.createAggregator(aggr);	
		assertNotNull(rtAggr);
		TestUtils.logMarshalledObject(rtAggr,LOGGER);
		//Create a Provider
		Provider prov = TestUtils.createProviderObj();	
		Provider respprov =  repoxRestClient.createProvider(prov, rtAggr);
		assertNotNull(respprov);
		TestUtils.logMarshalledObject(respprov,LOGGER);
		//Update the provider
	    Name name3 = new Name();
	    name3.setName("JunitContainerProviderUPD");
	    respprov.setName(name3);
	    Provider upprov =  repoxRestClient.updateProvider(respprov);
		assertNotNull(upprov);
		assertEquals(respprov.getId(),upprov.getId());
		assertEquals(respprov.getName().getName(),upprov.getName().getName());
		TestUtils.logMarshalledObject(upprov,LOGGER);
		
		//Retrieve the created provider
		 Provider retprov =  repoxRestClient.retrieveProvider(upprov.getId());
		 assertNotNull(retprov);
		 assertEquals(retprov.getId(),upprov.getId());
		 assertEquals(retprov.getName().getName(),upprov.getName().getName());
		 TestUtils.logMarshalledObject(upprov,LOGGER);
		 
		 
		//Delete the Provider
		Success res = repoxRestClient.deleteProvider(upprov.getId());
		assertNotNull(res);
		TestUtils.logMarshalledObject(res,LOGGER);
		
		//Check if the provider has already been deleted
		Provider retNullProv =  repoxRestClient.retrieveProvider(upprov.getId());
		assertNull(retNullProv);
		
		//Delete the Aggregator
		Success aggres = repoxRestClient.deleteAggregator(rtAggr.getId());
		assertNotNull(aggres);
		TestUtils.logMarshalledObject(aggres,LOGGER);
	}




	/**
	 * Tests a series of OAIPMH functionalities
	 * 
	 */
	@Test
	public void testCreateUpdateDeleteOAIDataSource() throws Exception{
		
		
		try{
		//Create an Aggregator for testing purposes
		Aggregator aggr = 	TestUtils.createAggregatorObj("aggr1","7777","http://www.in.gr");
		Aggregator rtAggr =  repoxRestClient.createAggregator(aggr);	
		assertNotNull(rtAggr);
		TestUtils.logMarshalledObject(rtAggr,LOGGER);
		//Create a Provider
		Provider prov = TestUtils.createProviderObj();	
		Provider respprov =  repoxRestClient.createProvider(prov, rtAggr);
		assertNotNull(respprov);
		TestUtils.logMarshalledObject(respprov,LOGGER);
		//Create an OAI PMH Datasource
		Source oaids = TestUtils.createOAIDataSource("bdaSet0");
		Source respOaids = repoxRestClient.createDatasourceOAI(oaids, respprov);
		TestUtils.logMarshalledObject(respOaids,LOGGER);
		//Update an OAI PMH Datasource
		Description description = new Description();
		respOaids.setMetadataFormat("edm");	
		Source updOaids = repoxRestClient.updateDatasourceOAI(respOaids);	
		assertNotNull(updOaids);	
		assertEquals("edm",updOaids.getMetadataFormat());	
		
		//Retrieve a OAI PMH Datasource
		Source retrOaids = repoxRestClient.retrieveDataSource(updOaids.getId());
		assertNotNull(retrOaids);	
		assertEquals("edm",retrOaids.getMetadataFormat());	
		
		
		//Initialize a harvesting session
		Success harvestRes = repoxRestClient.initiateHarvesting(updOaids.getId(),true);
		assertNotNull(harvestRes);
		TestUtils.logMarshalledObject(harvestRes,LOGGER);	
		RunningTasks rt = repoxRestClient.getActiveHarvestingSessions();
		assertNotNull(rt);
		TestUtils.logMarshalledObject(rt,LOGGER);
		//Gets the Harvesting Status for the created datasource
		HarvestingStatus status =repoxRestClient.getHarvestingStatus(updOaids.getId());
		assertNotNull(status);
		TestUtils.logMarshalledObject(status,LOGGER);
		
		Success cancelled = repoxRestClient.cancelHarvesting(updOaids.getId());
	    assertNotNull(cancelled);
		TestUtils.logMarshalledObject(cancelled,LOGGER);
		


		Success deleted = repoxRestClient.deleteDatasource(updOaids.getId());
	    assertNotNull(deleted);
		TestUtils.logMarshalledObject(deleted,LOGGER);
		
		Success aggres = repoxRestClient.deleteAggregator(rtAggr.getId());
		assertNotNull(aggres);
		
		}
		catch(Exception ex){
			Aggregators aggrs = repoxRestClient.retrieveAggregators();
			for (Aggregator aggr : aggrs.getAggregatorList()){
				if (aggr.getName().getName().equals("aggr1") && aggr.getNameCode().getNameCode().equals("7777")){
					repoxRestClient.deleteAggregator(aggr.getId());
				}
			}
			
			throw ex;
		
		}
		
	}


	/**
	 * Tests FTP operations 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUpdateDeleteFtpDataSource() throws Exception{
		

		try{
		//Create an Aggregator for testing purposes
		Aggregator aggr = 	TestUtils.createAggregatorObj("aggr5", "7777", "http://www.in.gr");
		Aggregator rtAggr =  repoxRestClient.createAggregator(aggr);	
		assertNotNull(rtAggr);
		TestUtils.logMarshalledObject(rtAggr,LOGGER);
		
		//Create a Provider
		Provider prov = TestUtils.createProviderObj();	
		//Order places an important role?
		Provider respprov =  repoxRestClient.createProvider(prov, rtAggr);
		assertNotNull(respprov);
		TestUtils.logMarshalledObject(respprov,LOGGER);
		
		//Create an FTP Datasource
		Source Ftpds = TestUtils.createFtpDataSource("bdaSet4");		
		Source respFtpds = repoxRestClient.createDatasourceFtp(Ftpds, respprov);
		//TestUtils.logMarshalledObject(respFtpds,LOGGER);

		//Update an FTP Datasource
		Description description = new Description();
		description.setDescription("altered!@#$%");
		respFtpds.setDescription(description);
		Source updFtpds = repoxRestClient.updateDatasourceFtp(Ftpds);
		assertNotNull(updFtpds);

		//Initialize a harvesting session
		Success harvestRes = repoxRestClient.initiateHarvesting(updFtpds.getId(),true);
		assertNotNull(harvestRes);
		TestUtils.logMarshalledObject(harvestRes,LOGGER);
		
		RunningTasks rt = repoxRestClient.getActiveHarvestingSessions();
		assertNotNull(rt);
		TestUtils.logMarshalledObject(rt,LOGGER);
		
		//Gets the Harvesting Status for the created datasource
		HarvestingStatus status =repoxRestClient.getHarvestingStatus(updFtpds.getId());
		assertNotNull(status);
		TestUtils.logMarshalledObject(status,LOGGER);
		
		Success cancelled = repoxRestClient.cancelHarvesting(updFtpds.getId());
	    assertNotNull(cancelled);
		TestUtils.logMarshalledObject(cancelled,LOGGER);
		
		Success deleted = repoxRestClient.deleteDatasource(updFtpds.getId());
	    assertNotNull(deleted);
		TestUtils.logMarshalledObject(deleted,LOGGER);
		
		Success aggres = repoxRestClient.deleteAggregator(rtAggr.getId());
		assertNotNull(aggres);
		
		}
		catch(Exception ex){
			Aggregators aggrs = repoxRestClient.retrieveAggregators();
			for (Aggregator aggr : aggrs.getAggregatorList()){
				if (aggr.getName().getName().equals("aggr5") && aggr.getNameCode().getNameCode().equals("7777")){
					repoxRestClient.deleteAggregator(aggr.getId());
				}
			}
			
			throw ex;
		
		}
	}


	/**
	 * Tests Http operations 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUpdateDeleteHttpDataSource() throws Exception{
		

		try{
		//Create an Aggregator for testing purposes
		Aggregator aggr = 	TestUtils.createAggregatorObj("aggr6", "7777", "http://www.in.gr");
		Aggregator rtAggr =  repoxRestClient.createAggregator(aggr);	
		assertNotNull(rtAggr);
		TestUtils.logMarshalledObject(rtAggr,LOGGER);
		
		//Create a Provider
		Provider prov = TestUtils.createProviderObj();	
		Provider respprov =  repoxRestClient.createProvider(prov, rtAggr);
		assertNotNull(respprov);
		TestUtils.logMarshalledObject(respprov,LOGGER);
		
		//Create an HTTP Datasource - NPE????
		Source Httpds = TestUtils.createHttpDataSource("bdaSet5");
		
		Source respHttpds = repoxRestClient.createDatasourceHttp(Httpds, respprov);
		//TestUtils.logMarshalledObject(respHttpds,LOGGER);

		//Update an HTTP Datasource
		Description description = new Description();
		description.setDescription("altered!@#$%");
		respHttpds.setDescription(description );
		Source updHttpds = repoxRestClient.updateDatasourceHttp(Httpds);
		assertNotNull(updHttpds);
		
		//Initialize a harvesting session
		Success harvestRes = repoxRestClient.initiateHarvesting(updHttpds.getId(),true);
		assertNotNull(harvestRes);
		TestUtils.logMarshalledObject(harvestRes,LOGGER);
		
		RunningTasks rt = repoxRestClient.getActiveHarvestingSessions();
		assertNotNull(rt);
		TestUtils.logMarshalledObject(rt,LOGGER);
		
		//Gets the Harvesting Status for the created datasource
		HarvestingStatus status =repoxRestClient.getHarvestingStatus(updHttpds.getId());
		assertNotNull(status);
		TestUtils.logMarshalledObject(status,LOGGER);
		
		Success cancelled = repoxRestClient.cancelHarvesting(updHttpds.getId());
	    assertNotNull(cancelled);
		TestUtils.logMarshalledObject(cancelled,LOGGER);
		
		Success deleted = repoxRestClient.deleteDatasource(updHttpds.getId());
	    assertNotNull(deleted);
		TestUtils.logMarshalledObject(deleted,LOGGER);
		
		Success aggres = repoxRestClient.deleteAggregator(rtAggr.getId());
		assertNotNull(aggres);
		
		}
		catch(Exception ex){
			Aggregators aggrs = repoxRestClient.retrieveAggregators();
			for (Aggregator aggr : aggrs.getAggregatorList()){
				if (aggr.getName().getName().equals("aggr6") && aggr.getNameCode().getNameCode().equals("7777")){
					repoxRestClient.deleteAggregator(aggr.getId());
				}
			}
			
			throw ex;
		
		}
	}


	/**
	 * Tests Folder operations 
	 * 
	 * @throws Exception
	 */
	@Test
	public void testCreateUpdateDeleteFolderDataSource() throws Exception{
		

		try{
		//Create an Aggregator for testing purposes
		Aggregator aggr = 	TestUtils.createAggregatorObj("aggr7", "7777", "http://www.in.gr");
		Aggregator rtAggr =  repoxRestClient.createAggregator(aggr);	
		assertNotNull(rtAggr);
		TestUtils.logMarshalledObject(rtAggr,LOGGER);
		
		//Create a Provider
		Provider prov = TestUtils.createProviderObj();	
	    //Order plays an important role????
		Provider respprov =  repoxRestClient.createProvider(prov, rtAggr);
		assertNotNull(respprov);
		TestUtils.logMarshalledObject(respprov,LOGGER);
		
		//Create an Folder Datasource
		Source folderds = TestUtils.createFolderDataSource("bdaSet6");
		
		Source respfolderds = repoxRestClient.createDatasourceFolder(folderds, respprov);
		//TestUtils.logMarshalledObject(respfolderds,LOGGER);

		//Update an Folder Datasource
		Description description = new Description();
		description.setDescription("altered!@#$%");
		respfolderds.setDescription(description );
	
		Source updfolderds = repoxRestClient.updateDatasourceFolder(folderds);
		assertNotNull(updfolderds);		
		//Initialize a harvesting session
		Success harvestRes = repoxRestClient.initiateHarvesting(updfolderds.getId(),true);
		assertNotNull(harvestRes);
		TestUtils.logMarshalledObject(harvestRes,LOGGER);

		RunningTasks rt = repoxRestClient.getActiveHarvestingSessions();
		assertNotNull(rt);
		TestUtils.logMarshalledObject(rt,LOGGER);

		
		//Gets the Harvesting Status for the created datasource
		HarvestingStatus status =repoxRestClient.getHarvestingStatus(updfolderds.getId());
		assertNotNull(status);
		TestUtils.logMarshalledObject(status,LOGGER);
		
		Success cancelled = repoxRestClient.cancelHarvesting(updfolderds.getId());
	    assertNotNull(cancelled);
		TestUtils.logMarshalledObject(cancelled,LOGGER);
		
		Success deleted = repoxRestClient.deleteDatasource(updfolderds.getId());
	    assertNotNull(deleted);
		TestUtils.logMarshalledObject(deleted,LOGGER);
		
		Success aggres = repoxRestClient.deleteAggregator(rtAggr.getId());
		assertNotNull(aggres);
		
		}
		catch(Exception ex){
			Aggregators aggrs = repoxRestClient.retrieveAggregators();
			for (Aggregator aggr : aggrs.getAggregatorList()){
				if (aggr.getName().getName().equals("aggr7") && aggr.getNameCode().getNameCode().equals("7777")){
					repoxRestClient.deleteAggregator(aggr.getId());
				}
			}
			
			throw ex;
		
		}
		
	}
	
}
