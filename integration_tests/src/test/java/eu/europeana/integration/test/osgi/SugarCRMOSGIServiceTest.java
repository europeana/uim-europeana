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
package eu.europeana.integration.test.osgi;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.HashMap;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.sugar.ConnectionStatus;
import eu.europeana.uim.sugar.SugarCrmQuery;
import eu.europeana.uim.sugar.SugarCrmRecord;
import eu.europeana.uim.sugar.SugarCrmService;
import eu.europeana.uim.sugar.model.DatasetStates;
import eu.europeana.uim.sugar.model.UpdatableField;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.EuropeanaDatasetStates;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.EuropeanaRetrievableField;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.EuropeanaUpdatableField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.ComplexSugarCrmQuery;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.CustomSugarCrmQuery;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.EqOp;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.SimpleSugarCrmQuery;

import eu.europeana.uim.workflow.Workflow;

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
public class SugarCRMOSGIServiceTest  extends AbstractEuropeanaIntegrationTest{

	
	private static org.apache.log4j.Logger LOGGER = Logger.getLogger(SugarCRMOSGIServiceTest.class);

	
	
    /**
     * Single Unit test testing all available SugarCRM service functionality.
     * 
     * @throws Exception
     */
	//TODO:Ignore until slf4j issue is fixed
	@Ignore
    @Test
    public void testFullSugarCRMServiceFunctionality() throws Exception{
        Thread.sleep(time2w84service);
        SugarCrmService service = getOsgiService(SugarCrmService.class);
		assertNotNull(service);
		
		//Get Connection Info
		ConnectionStatus status = service.showConnectionStatus();
		assertNotNull(status.getDefaultURI());
		assertNotNull(status.getSessionID());
		LOGGER.debug(status);   	
		
		//Tests the Update Session functionality
    	String sessionIDbefore = status.getSessionID();	
    	String username = "test";
		String password = "test";		
		String sessionIDafter = service.updateSession(username, password);		
		assertNotNull(sessionIDbefore);
		assertNotNull(sessionIDafter);
		assertTrue(!sessionIDbefore.equals(sessionIDafter));
		
		
		//Tests the execution of a Simple Query
		EuropeanaDatasetStates dsstatus = EuropeanaDatasetStates.MAPPING_AND_NORMALIZATION;
		SimpleSugarCrmQuery query =  new SimpleSugarCrmQuery(dsstatus);
		query.setMaxResults(1000);
		query.setOffset(0);
		query.setOrderBy(EuropeanaRetrievableField.DATE_ENTERED);
		List<SugarCrmRecord> records = service.retrieveRecords(query);
		LOGGER.debug("Number of Records retrieved: " + records.size());
		LOGGER.debug("NO | RECORD ID                          | COLLECTION NAME");

		for(int i=0; i< records.size(); i++){
			LOGGER.debug( (i+1) + " : " + records.get(i).getItemValue(EuropeanaRetrievableField.ID) + " | " +
					records.get(i).getItemValue(EuropeanaRetrievableField.NAME)	) ;
		}
		
		//Tests the execution of a Complex Query
		ComplexSugarCrmQuery cquery =  new ComplexSugarCrmQuery(EuropeanaRetrievableField.NAME ,EqOp.LIKE,"00101_M_PT_Gulbenkian_biblioteca_digital" );
		List<SugarCrmRecord> cqrecords = service.retrieveRecords(cquery);
		LOGGER.debug("Number of Records retrieved: " + cqrecords.size());
		LOGGER.debug("NO | RECORD ID                          | COLLECTION NAME");

		for(int i=0; i< cqrecords.size(); i++){
			LOGGER.debug( (i+1) + " : " + cqrecords.get(i).getItemValue(EuropeanaRetrievableField.ID) + " | " +
					cqrecords.get(i).getItemValue(EuropeanaRetrievableField.NAME)	) ;
		}

		
		//Tests the execution of a Custom Query
		CustomSugarCrmQuery cusquery =  new CustomSugarCrmQuery("opportunities.sales_stage LIKE '" + EuropeanaDatasetStates.MAPPING_AND_NORMALIZATION.getSysId() +"'");
		
		List<SugarCrmRecord> cusrecords = service.retrieveRecords(cusquery);
		LOGGER.debug("Number of Records retrieved: " + cusrecords.size());
		LOGGER.debug("NO | RECORD ID                          | COLLECTION NAME");

		for(int i=0; i< cusrecords.size(); i++){
			LOGGER.debug( (i+1) + " : " + cusrecords.get(i).getItemValue(EuropeanaRetrievableField.ID) + " | " +
					cusrecords.get(i).getItemValue(EuropeanaRetrievableField.NAME)	) ;
		}
		
		
		
    	String recordID = "a2098f49-37db-2362-3e4b-4c5861d23639";
		//Tests the fetch Record functionality 
    	SugarCrmRecord rec = service.retrieveRecord(recordID);
		assertNotNull(rec);
		
		
		//Tests the Update Record functionality 
		String threcords = "100";
		String himages = "50";
		String htetx = "30";
		String hvideo = "10";
		String hsound = "10";
		
		HashMap<UpdatableField, String> values  = new HashMap<UpdatableField, String>();
		values.put(EuropeanaUpdatableField.TOTAL_INGESTED, threcords);
		values.put(EuropeanaUpdatableField.INGESTED_IMAGE, himages);
		values.put(EuropeanaUpdatableField.INGESTED_TEXT, htetx);			
		values.put(EuropeanaUpdatableField.INGESTED_VIDEO, hvideo);
		values.put(EuropeanaUpdatableField.INGESTED_SOUND, hsound);
		service.updateRecordData(recordID, values);
		
		//Tests the Change Record Status functionality
		EuropeanaDatasetStates chstate = EuropeanaDatasetStates.INGESTION_COMPLETE; 
		service.changeEntryStatus(recordID, chstate);
		
		//Tests the Change Record Status functionality 
		EuropeanaDatasetStates changestate = EuropeanaDatasetStates.INGESTION_COMPLETE; 
		service.changeEntryStatus(recordID, chstate);
		
	    ///Tests the Populate UIM from Record functionality (automatically infer 
	    //Providers and Collections from a record)
		SugarCrmRecord re = service.retrieveRecord(recordID);
		Provider prov = service.updateProviderFromRecord(re);
		Collection coll = service.updateCollectionFromRecord(re, prov);
		
	    //Tests the Initialize Workflow from a SugarCRM Record with a specific ID functionality.
		String worklfowName = "SysoutWorkflow";
		SugarCrmRecord record = service.retrieveRecord(recordID);
		assertNotNull(record);
		EuropeanaDatasetStates endstate = EuropeanaDatasetStates.HARVESTING_PENDING; 
		Workflow wf = service.initWorkflowFromRecord(worklfowName, record, endstate);
		assertNotNull(wf);
		
	    //Tests the Initialize multiple Workflows from many SugarCRM Records having the same state functionality.
		EuropeanaDatasetStates currentstate = EuropeanaDatasetStates.HARVESTING_PENDING;
		EuropeanaDatasetStates ndstate = EuropeanaDatasetStates.INGESTION_COMPLETE; 
		List<Workflow<?, ?>> wfs = service.initWorkflowsFromRecords(worklfowName, currentstate, ndstate);
		
	    //Tests the Add Note Attachment to record functionality
		String message = "Exception Stacktrace....";
    	service.addNoteAttachmentToRecord(recordID, message);
    }
    
}
