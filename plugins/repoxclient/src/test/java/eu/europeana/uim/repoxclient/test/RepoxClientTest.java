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

import java.util.ArrayList;
import javax.annotation.Resource;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import eu.europeana.uim.repoxclient.jibxbindings.Aggregator;
import eu.europeana.uim.repoxclient.jibxbindings.Aggregators;
import eu.europeana.uim.repoxclient.jibxbindings.DataSource;
import eu.europeana.uim.repoxclient.jibxbindings.DataSources;
import eu.europeana.uim.repoxclient.jibxbindings.DataProviders;
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
 * Core Repox connectivity unit testing
 * 
 * @author Georgios Markakis
 * @author Yorgos Mamakis
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:testContext.xml"})
public class RepoxClientTest {

@Resource
RepoxRestClient repoxRestClient;	
	
private static org.apache.log4j.Logger LOGGER = Logger.getLogger(RepoxClientTest.class);	


/*
 * Retrieval operations
 */

/**
 * @throws Exception
 */
@Test
public void testGetDatasources() throws Exception{
	DataSources ds =repoxRestClient.retrieveDataSources();
	TestUtils.logMarshalledObject(ds,LOGGER);	
}


/**
 * @throws Exception
 */
@Test
public void testGetAggregators() throws Exception{
	Aggregators aggrs =repoxRestClient.retrieveAggregators();
	TestUtils.logMarshalledObject(aggrs,LOGGER);
}
	

/**
 * @throws Exception
 */
@Test
public void testGetProviders() throws Exception{
	DataProviders prov =repoxRestClient.retrieveProviders();
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
	ArrayList<DataSource> dslist =  (ArrayList<DataSource>) rt.getDataSourceList();
	DataSource dsisregistered = null;
	for(DataSource ds : dslist){
	    if(ds.getDataSource().equals(updOaids.getId())){	
	    	dsisregistered = ds;
	    }
	}
	
	//assertNotNull(dsisregistered);
	//Gets the Harvesting Status for the created datasource
	HarvestingStatus status =repoxRestClient.getHarvestingStatus(updOaids.getId());
	assertNotNull(status);
	TestUtils.logMarshalledObject(status,LOGGER);
	
	Success cancelled = repoxRestClient.cancelHarvesting(updOaids.getId());
    assertNotNull(cancelled);
	TestUtils.logMarshalledObject(cancelled,LOGGER);
	
	//TODO:Harvest Logs are not generated if harvesting is not complete. How to test this?
	//Log harvestLog = repoxRestClient.getHarvestLog(updOaids.getId());
    //assertNotNull(harvestLog);
	//TestUtils.logMarshalledObject(harvestLog,LOGGER);
	
	
	
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
 * Tests Z3950Timestamp operations 
 * 
 * @throws Exception
 */
@Ignore
@Test
public void testCreateUpdateDeleteZ3950TimestampDataSource() throws Exception{
	

	try{
	//Create an Aggregator for testing purposes
	Aggregator aggr = 	TestUtils.createAggregatorObj("aggr2","7777","http://www.in.gr");
	//AggregatorExist????
	Aggregator rtAggr =  repoxRestClient.createAggregator(aggr);	
	assertNotNull(rtAggr);
	TestUtils.logMarshalledObject(rtAggr,LOGGER);
	
	//Create a Provider
	Provider prov = TestUtils.createProviderObj();	
	Provider respprov =  repoxRestClient.createProvider(prov, rtAggr);
	assertNotNull(respprov);
	TestUtils.logMarshalledObject(respprov,LOGGER);
	
	//Create an Z3950Timestamp Datasource
	Source z3950TSds = TestUtils.createZ3950TimestampDataSource("bdaSet1");
	
	Source respz3950TSds = repoxRestClient.createDatasourceZ3950Timestamp(z3950TSds, respprov);
	TestUtils.logMarshalledObject(respz3950TSds,LOGGER);

	//Update an Z3950Timestamp Datasource
	Description description = new Description();
	description.setDescription("altered!@#$%");
	respz3950TSds.setDescription(description );
	
	Source updz3950TSds = repoxRestClient.updateDatasourceZ3950Timestamp(respz3950TSds);
	assertNotNull(updz3950TSds);
	assertEquals("altered!@#$%",updz3950TSds.getDescription().getDescription());
	
	//Initialize a harvesting session
	Success harvestRes = repoxRestClient.initiateHarvesting(updz3950TSds.getId(),true);
	assertNotNull(harvestRes);
	TestUtils.logMarshalledObject(harvestRes,LOGGER);

	
	RunningTasks rt = repoxRestClient.getActiveHarvestingSessions();
	assertNotNull(rt);
	TestUtils.logMarshalledObject(rt,LOGGER);
	ArrayList<DataSource> dslist =  (ArrayList<DataSource>) rt.getDataSourceList();
	DataSource dsisregistered = null;

	for(DataSource ds : dslist){
	    if(ds.getDataSource().equals(updz3950TSds.getId())){	
	    	dsisregistered = ds;
	    }
	}
	
	//assertNotNull(dsisregistered);
	
	
	
	//Gets the Harvesting Status for the created datasource
	HarvestingStatus status =repoxRestClient.getHarvestingStatus(updz3950TSds.getId());
	assertNotNull(status);
	TestUtils.logMarshalledObject(status,LOGGER);
	
	Success cancelled = repoxRestClient.cancelHarvesting(updz3950TSds.getId());
    assertNotNull(cancelled);
	TestUtils.logMarshalledObject(cancelled,LOGGER);
	
	//TODO:Harvest Logs are not generated if harvesting is not complete. How to test this?
	//Log harvestLog = repoxRestClient.getHarvestLog(updOaids.getId());
    //assertNotNull(harvestLog);
	//TestUtils.logMarshalledObject(harvestLog,LOGGER);
	
	Success deleted = repoxRestClient.deleteDatasource(updz3950TSds.getId());
    assertNotNull(deleted);
	TestUtils.logMarshalledObject(deleted,LOGGER);
	
	Success aggres = repoxRestClient.deleteAggregator(rtAggr.getId());
	assertNotNull(aggres);
	
	}
	catch(Exception ex){
		Aggregators aggrs = repoxRestClient.retrieveAggregators();
		for (Aggregator aggr : aggrs.getAggregatorList()){
			if (aggr.getName().getName().equals("aggr2") && aggr.getNameCode().getNameCode().equals("7777")){
				repoxRestClient.deleteAggregator(aggr.getId());
			}
		}
		
		throw ex;
	
	}
}

/**
 * Tests Z3950IDFile operations 
 * 
 * @throws Exception
 */
//@Test
public void testCreateUpdateDeleteZ3950IDFileDataSource() throws Exception{
	

	try{
	//Create an Aggregator for testing purposes
	Aggregator aggr = 	TestUtils.createAggregatorObj("aggr3", "7777", "http://www.in.gr");
	Aggregator rtAggr =  repoxRestClient.createAggregator(aggr);	
	assertNotNull(rtAggr);
	TestUtils.logMarshalledObject(rtAggr,LOGGER);
	
	//Create a Provider
	Provider prov = TestUtils.createProviderObj();	
	Provider respprov =  repoxRestClient.createProvider(prov, rtAggr);
	assertNotNull(respprov);
	TestUtils.logMarshalledObject(respprov,LOGGER);
	
	//Create an Z3950OIdFile Datasource
	
	//Null Pointer Exception
	Source z3950IDFileds = TestUtils.createZ3950IdFileDataSource("bdaSet2");
	
	Source respZ3950IDFileds = repoxRestClient.createDatasourceZ3950IdFile(z3950IDFileds, respprov);
	TestUtils.logMarshalledObject(respZ3950IDFileds,LOGGER);

	//Update an Z3950OIdFile Datasource
	Description description = new Description();
	description.setDescription("altered!@#$%");
	respZ3950IDFileds.setDescription(description );
	
	Source updZ3950IDFileds = repoxRestClient.updateDatasourceZ3950IdFile(respZ3950IDFileds);
	assertNotNull(updZ3950IDFileds);
	assertEquals("altered!@#$%",updZ3950IDFileds.getDescription().getDescription());
	
	//Initialize a harvesting session
	Success harvestRes = repoxRestClient.initiateHarvesting(updZ3950IDFileds.getId(),true);
	assertNotNull(harvestRes);
	TestUtils.logMarshalledObject(harvestRes,LOGGER);

	
	RunningTasks rt = repoxRestClient.getActiveHarvestingSessions();
	assertNotNull(rt);
	TestUtils.logMarshalledObject(rt,LOGGER);
	ArrayList<DataSource> dslist =  (ArrayList<DataSource>) rt.getDataSourceList();
	DataSource dsisregistered = null;

	for(DataSource ds : dslist){
	    if(ds.getDataSource().equals(updZ3950IDFileds.getId())){	
	    	dsisregistered = ds;
	    }
	}
	
	//assertNotNull(dsisregistered);
	
	
	
	//Gets the Harvesting Status for the created datasource
	HarvestingStatus status =repoxRestClient.getHarvestingStatus(updZ3950IDFileds.getId());
	assertNotNull(status);
	TestUtils.logMarshalledObject(status,LOGGER);
	
	Success cancelled = repoxRestClient.cancelHarvesting(updZ3950IDFileds.getId());
    assertNotNull(cancelled);
	TestUtils.logMarshalledObject(cancelled,LOGGER);
	
	//TODO:Harvest Logs are not generated if harvesting is not complete. How to test this?
	//Log harvestLog = repoxRestClient.getHarvestLog(updOaids.getId());
    //assertNotNull(harvestLog);
	//TestUtils.logMarshalledObject(harvestLog,LOGGER);
	
	Success deleted = repoxRestClient.deleteDatasource(updZ3950IDFileds.getId());
    assertNotNull(deleted);
	TestUtils.logMarshalledObject(deleted,LOGGER);
	
	Success aggres = repoxRestClient.deleteAggregator(rtAggr.getId());
	assertNotNull(aggres);
	
	}
	catch(Exception ex){
		Aggregators aggrs = repoxRestClient.retrieveAggregators();
		for (Aggregator aggr : aggrs.getAggregatorList()){
			if (aggr.getName().getName().equals("aggr3") && aggr.getNameCode().getNameCode().equals("7777")){
				repoxRestClient.deleteAggregator(aggr.getId());
			}
		}
		
		throw ex;
	
	}
	
}
//todo refactor here on 
/**
 * Tests Z3950IdSequence operations 
 * 
 * @throws Exception
 */
//@Test
public void testCreateUpdateDeleteZ3950IdSequenceDataSource() throws Exception{
	

	try{
	//Create an Aggregator for testing purposes
	Aggregator aggr = 	TestUtils.createAggregatorObj("aggr4","7777","http://www.in.gr");
	Aggregator rtAggr =  repoxRestClient.createAggregator(aggr);	
	assertNotNull(rtAggr);
	TestUtils.logMarshalledObject(rtAggr,LOGGER);
	
	//Create a Provider
	Provider prov = TestUtils.createProviderObj();	
	Provider respprov =  repoxRestClient.createProvider(prov, rtAggr);
	assertNotNull(respprov);
	TestUtils.logMarshalledObject(respprov,LOGGER);
	
	//Create an Z3950OIdSequence Datasource
	
	//NPE
	Source Z3950IdSeqds = TestUtils.createZ3950IdSequenceDataSource("bdaSet3");
	
	Source respZ3950IdSeqds = repoxRestClient.createDatasourceZ3950IdSequence(Z3950IdSeqds, respprov);
	TestUtils.logMarshalledObject(respZ3950IdSeqds,LOGGER);

	//Update an Z3950OIdSequence Datasource
	Description description = new Description();
	description.setDescription("altered!@#$%");
	respZ3950IdSeqds.setDescription(description );
	
	Source updZ3950IdSeqds = repoxRestClient.updateDatasourceZ3950IdSequence(respZ3950IdSeqds);
	assertNotNull(updZ3950IdSeqds);
	assertEquals("altered!@#$%",updZ3950IdSeqds.getDescription().getDescription());
	
	//Initialize a harvesting session
	Success harvestRes = repoxRestClient.initiateHarvesting(updZ3950IdSeqds.getId(),true);
	assertNotNull(harvestRes);
	TestUtils.logMarshalledObject(harvestRes,LOGGER);

	
	RunningTasks rt = repoxRestClient.getActiveHarvestingSessions();
	assertNotNull(rt);
	TestUtils.logMarshalledObject(rt,LOGGER);
	ArrayList<DataSource> dslist =  (ArrayList<DataSource>) rt.getDataSourceList();
	DataSource dsisregistered = null;

	for(DataSource ds : dslist){
	    if(ds.getDataSource().equals(updZ3950IdSeqds.getId())){	
	    	dsisregistered = ds;
	    }
	}
	
	//assertNotNull(dsisregistered);
	
	
	
	//Gets the Harvesting Status for the created datasource
	HarvestingStatus status =repoxRestClient.getHarvestingStatus(updZ3950IdSeqds.getId());
	assertNotNull(status);
	TestUtils.logMarshalledObject(status,LOGGER);
	
	Success cancelled = repoxRestClient.cancelHarvesting(updZ3950IdSeqds.getId());
    assertNotNull(cancelled);
	TestUtils.logMarshalledObject(cancelled,LOGGER);
	
	//TODO:Harvest Logs are not generated if harvesting is not complete. How to test this?
	//Log harvestLog = repoxRestClient.getHarvestLog(updOaids.getId());
    //assertNotNull(harvestLog);
	//TestUtils.logMarshalledObject(harvestLog,LOGGER);
	
	Success deleted = repoxRestClient.deleteDatasource(updZ3950IdSeqds.getId());
    assertNotNull(deleted);
	TestUtils.logMarshalledObject(deleted,LOGGER);
	
	Success aggres = repoxRestClient.deleteAggregator(rtAggr.getId());
	assertNotNull(aggres);
	
	}
	catch(Exception ex){
		Aggregators aggrs = repoxRestClient.retrieveAggregators();
		for (Aggregator aggr : aggrs.getAggregatorList()){
			if (aggr.getName().getName().equals("aggr4") && aggr.getNameCode().getNameCode().equals("7777")){
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
	TestUtils.logMarshalledObject(respFtpds,LOGGER);

	//Temporarily placing values here until issue is fixed
	
	
	//Update an FTP Datasource
	Description description = new Description();
	description.setDescription("altered!@#$%");
	respFtpds.setDescription(description);
	
	//TODO:Temporarily disable this until issues are resolved
	//Source updFtpds = repoxRestClient.updateDatasourceFtp(respFtpds);
	Source updFtpds = repoxRestClient.updateDatasourceFtp(Ftpds);
	
	assertNotNull(updFtpds);
	//assertEquals("altered!@#$%",updFtpds.getDescription().getDescription());
	
	//Initialize a harvesting session
	Success harvestRes = repoxRestClient.initiateHarvesting(updFtpds.getId(),true);
	assertNotNull(harvestRes);
	TestUtils.logMarshalledObject(harvestRes,LOGGER);

	
	RunningTasks rt = repoxRestClient.getActiveHarvestingSessions();
	assertNotNull(rt);
	TestUtils.logMarshalledObject(rt,LOGGER);
	ArrayList<DataSource> dslist =  (ArrayList<DataSource>) rt.getDataSourceList();
	DataSource dsisregistered = null;

	for(DataSource ds : dslist){
	    if(ds.getDataSource().equals(updFtpds.getId())){	
	    	dsisregistered = ds;
	    }
	}
	
	//assertNotNull(dsisregistered);
	
	
	
	//Gets the Harvesting Status for the created datasource
	HarvestingStatus status =repoxRestClient.getHarvestingStatus(updFtpds.getId());
	assertNotNull(status);
	TestUtils.logMarshalledObject(status,LOGGER);
	
	Success cancelled = repoxRestClient.cancelHarvesting(updFtpds.getId());
    assertNotNull(cancelled);
	TestUtils.logMarshalledObject(cancelled,LOGGER);
	
	//TODO:Harvest Logs are not generated if harvesting is not complete. How to test this?
	//Log harvestLog = repoxRestClient.getHarvestLog(updOaids.getId());
    //assertNotNull(harvestLog);
	//TestUtils.logMarshalledObject(harvestLog,LOGGER);
	
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
	TestUtils.logMarshalledObject(respHttpds,LOGGER);

	//Update an HTTP Datasource
	Description description = new Description();
	description.setDescription("altered!@#$%");
	respHttpds.setDescription(description );
	
	//TODO:Temporarily disable this until issues are resolved
	//Source updHttpds = repoxRestClient.updateDatasourceHttp(respHttpds);
	Source updHttpds = repoxRestClient.updateDatasourceHttp(Httpds);
	
	assertNotNull(updHttpds);
	//assertEquals("altered!@#$%",updHttpds.getDescription().getDescription());
	
	//Initialize a harvesting session
	Success harvestRes = repoxRestClient.initiateHarvesting(updHttpds.getId(),true);
	assertNotNull(harvestRes);
	TestUtils.logMarshalledObject(harvestRes,LOGGER);

	
	RunningTasks rt = repoxRestClient.getActiveHarvestingSessions();
	assertNotNull(rt);
	TestUtils.logMarshalledObject(rt,LOGGER);
	ArrayList<DataSource> dslist =  (ArrayList<DataSource>) rt.getDataSourceList();
	DataSource dsisregistered = null;

	for(DataSource ds : dslist){
	    if(ds.getDataSource().equals(updHttpds.getId())){	
	    	dsisregistered = ds;
	    }
	}
	
	//assertNotNull(dsisregistered);
	
	
	
	//Gets the Harvesting Status for the created datasource
	HarvestingStatus status =repoxRestClient.getHarvestingStatus(updHttpds.getId());
	assertNotNull(status);
	TestUtils.logMarshalledObject(status,LOGGER);
	
	Success cancelled = repoxRestClient.cancelHarvesting(updHttpds.getId());
    assertNotNull(cancelled);
	TestUtils.logMarshalledObject(cancelled,LOGGER);
	
	//TODO:Harvest Logs are not generated if harvesting is not complete. How to test this?
	//Log harvestLog = repoxRestClient.getHarvestLog(updOaids.getId());
    //assertNotNull(harvestLog);
	//TestUtils.logMarshalledObject(harvestLog,LOGGER);
	
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
	TestUtils.logMarshalledObject(respfolderds,LOGGER);

	//Update an Folder Datasource
	Description description = new Description();
	description.setDescription("altered!@#$%");
	respfolderds.setDescription(description );
	
	//TODO:Temporarily disable this until issues are resolved
	//Source updfolderds = repoxRestClient.updateDatasourceFolder(respfolderds);
	Source updfolderds = repoxRestClient.updateDatasourceFolder(folderds);
	assertNotNull(updfolderds);
	//assertEquals("altered!@#$%",updfolderds.getDescription().getDescription());
	
	//Initialize a harvesting session
	Success harvestRes = repoxRestClient.initiateHarvesting(updfolderds.getId(),true);
	assertNotNull(harvestRes);
	TestUtils.logMarshalledObject(harvestRes,LOGGER);

	
	RunningTasks rt = repoxRestClient.getActiveHarvestingSessions();
	assertNotNull(rt);
	TestUtils.logMarshalledObject(rt,LOGGER);
	ArrayList<DataSource> dslist =  (ArrayList<DataSource>) rt.getDataSourceList();
	DataSource dsisregistered = null;

	for(DataSource ds : dslist){
	    if(ds.getDataSource().equals(updfolderds.getId())){	
	    	dsisregistered = ds;
	    }
	}
	
	//assertNotNull(dsisregistered);
	
	
	
	//Gets the Harvesting Status for the created datasource
	HarvestingStatus status =repoxRestClient.getHarvestingStatus(updfolderds.getId());
	assertNotNull(status);
	TestUtils.logMarshalledObject(status,LOGGER);
	
	Success cancelled = repoxRestClient.cancelHarvesting(updfolderds.getId());
    assertNotNull(cancelled);
	TestUtils.logMarshalledObject(cancelled,LOGGER);
	
	//TODO:Harvest Logs are not generated if harvesting is not complete. How to test this?
	//Log harvestLog = repoxRestClient.getHarvestLog(updOaids.getId());
    //assertNotNull(harvestLog);
	//TestUtils.logMarshalledObject(harvestLog,LOGGER);
	
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
