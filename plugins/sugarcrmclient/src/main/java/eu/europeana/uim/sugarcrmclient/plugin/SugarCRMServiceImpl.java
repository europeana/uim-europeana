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

package eu.europeana.uim.sugarcrmclient.plugin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.DataSet;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;

import eu.europeana.uim.sugarcrmclient.jibxbindings.Login;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NameValue;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NameValueList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SelectFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntryResponse;
import eu.europeana.uim.sugarcrmclient.plugin.objects.ConnectionStatus;
import eu.europeana.uim.sugarcrmclient.plugin.objects.SugarCrmRecord;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.DatasetStates;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.RetrievableField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.UpdatableField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.SimpleSugarCrmQuery;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.SugarCrmQuery;
import eu.europeana.uim.sugarcrmclient.ws.SugarWsClient;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.GenericSugarCRMException;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LoginFailureException;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.QueryResultException;
import eu.europeana.uim.workflow.Workflow;
import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;



/**
 * This is the implementing class for the OSGI based SugarCrm plugin OSGI service.
 * 
 * @author Georgios Markakis
 */
public class SugarCRMServiceImpl implements SugarCRMService{

	private SugarWsClient sugarwsClient;
	private Orchestrator orchestrator;
	private Registry registry;
	
	


	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#showConnectionStatus()
	 */
	@Override
	public ConnectionStatus showConnectionStatus() {
		
		ConnectionStatus cstatus = new ConnectionStatus();
		
		cstatus.setDefaultURI(sugarwsClient.getDefaultUri());
		cstatus.setSessionID(sugarwsClient.getSessionID());

		return cstatus;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#updateSession()
	 */
	@Override
	public String updateSession(String username, String password) throws LoginFailureException {
		StringBuffer connectionInfo = new StringBuffer();
		Login login = ClientUtils.createStandardLoginObject(username, password);
        connectionInfo.append(sugarwsClient.login(login));
		return connectionInfo.toString();
	}
 
	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#changeEntryStatus(java.lang.String, eu.europeana.uim.sugarcrmclient.plugin.objects.data.DatasetStates)
	 */
	@Override
	public void changeEntryStatus(String recordId, DatasetStates state)
			throws QueryResultException {
		alterSugarCRMItemStatus(recordId, state.getSysId());
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#updateRecordData(java.lang.String, java.util.HashMap)
	 */
	@Override
	public void updateRecordData(String recordID,
			HashMap<UpdatableField, String> values) throws QueryResultException {

		SetEntry request = new SetEntry();
		ArrayList <NameValue> nvList = new  ArrayList <NameValue>();
		Iterator<?> it = values.entrySet().iterator();

	    while (it.hasNext()) {
	        Map.Entry<UpdatableField, String> pairs = (Map.Entry<UpdatableField, String>)it.next();
			NameValue nv = new NameValue();
			nv.setName(pairs.getKey().getFieldId());
			nv.setValue(pairs.getValue());

			nvList.add(nv);
	    }
	    
		NameValueList valueList = ClientUtils.generatePopulatedNameValueList(nvList);
		
		request.setNameValueList(valueList);
		request.setModuleName("Opportunities");
		request.setSession(sugarwsClient.getSessionID());	
		
		sugarwsClient.set_entry(request);		
	}

	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#updateRecordData(eu.europeana.uim.sugarcrmclient.plugin.objects.SugarCrmRecord)
	 */
	@Override
	public void updateRecordData(SugarCrmRecord record) throws QueryResultException {
		HashMap<UpdatableField, String> values = new HashMap<UpdatableField, String>();
		
		String recordId = record.getItemValue(RetrievableField.ID);

		values.put(UpdatableField.AMOUNT, record.getItemValue(UpdatableField.AMOUNT));
		values.put(UpdatableField.INGESTED_IMAGE, record.getItemValue(UpdatableField.INGESTED_IMAGE));
		values.put(UpdatableField.INGESTED_SOUND, record.getItemValue(UpdatableField.INGESTED_SOUND));
		values.put(UpdatableField.INGESTED_TEXT, record.getItemValue(UpdatableField.INGESTED_TEXT));
		values.put(UpdatableField.INGESTED_VIDEO, record.getItemValue(UpdatableField.INGESTED_VIDEO));
		values.put(UpdatableField.NEXT_STEP, record.getItemValue(UpdatableField.NEXT_STEP));
		values.put(UpdatableField.TOTAL_INGESTED, record.getItemValue(UpdatableField.TOTAL_INGESTED));

		updateRecordData(recordId,values);
	}
	
	
	
	@Override
	public List<SugarCrmRecord> retrieveRecords(SugarCrmQuery query)
			throws QueryResultException {
		
		GetEntryList request = new GetEntryList();
		
		SelectFields fields = new SelectFields(); //We want to retrieve all fields
		request.setSelectFields(fields); 
  		request.setModuleName("Opportunities");	
		request.setSession(sugarwsClient.getSessionID());
		request.setOrderBy(query.getOrderBy().getFieldId());
		request.setMaxResults(query.getMaxResults());
		request.setOffset(query.getOffset());
		
		request.setQuery(query.toString());

		GetEntryListResponse response =  sugarwsClient.get_entry_list(request);

		ArrayList<Element> list = (ArrayList<Element>) response.getReturn().getEntryList().getArray().getAnyList();

		ArrayList<SugarCrmRecord> returnList = wrapDomElements2Objects(list);

		return returnList;
	}
	
	
	@Override
	public void initiateWorkflowFromRecord(String worklfowName,SugarCrmRecord record,DatasetStates endstate) throws QueryResultException,StorageEngineException {

			StorageEngine<?> engine = registry.getStorage();
			Workflow w = registry.getWorkflow(worklfowName);
			
			Collection<?> dataset = inferCollection(engine,record.getRecord());
			
			ActiveExecution<?> execution = orchestrator.executeWorkflow(w, dataset);
			alterSugarCRMItemStatus(dataset.getMnemonic(), endstate.getSysId());	
		
			execution.waitUntilFinished();			
	}
	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#pollForHarvestInitiators()
	 */
	@Override
	public void pollForHarvestInitiators() throws QueryResultException {

	}
	
	
	/*
	 * Private methods
	 */
	
	/**
	 * @param list
	 * @return
	 */
	private ArrayList<SugarCrmRecord>  wrapDomElements2Objects(ArrayList<Element> list){
		
		ArrayList<SugarCrmRecord> returnList = new ArrayList<SugarCrmRecord>();
		
		for (int i=0; i < list.size(); i++){
			
			SugarCrmRecord record = SugarCrmRecord.getInstance(list.get(i));
			returnList.add(record);
		}
		
		return returnList;
	}
	
	

	/**
	 * @param engine
	 * @param trigger
	 * @return
	 * @throws StorageEngineException
	 */
	private Collection inferCollection(StorageEngine engine, Element trigger) throws StorageEngineException{
		    
		    String collectionName = extractFromElement("name",trigger);
		    String providerName = extractFromElement("account_name",trigger); 
		    String providerAcronymName = extractFromElement("name_acronym_c",trigger);  
		    String mnemonicCode = extractFromElement("id",trigger); 
		    String countryCode = extractFromElement("country_c",trigger);
		    String harvestUrl = extractFromElement("harvest_url_c",trigger);
		    
		    
		    Provider cuurprovider = engine.findProvider(mnemonicCode);
		    Collection currcollection = engine.findCollection(mnemonicCode);

		    
            if (cuurprovider == null){

            	cuurprovider = engine.createProvider();
            	cuurprovider.setAggregator(true);
            	cuurprovider.setMnemonic(mnemonicCode);
            	cuurprovider.setName(providerName);
            	cuurprovider.setOaiBaseUrl(harvestUrl);
            	cuurprovider.setOaiMetadataPrefix("?");
            	
            	engine.updateProvider(cuurprovider);
            	engine.checkpoint();
            }
		    
		    
            if(currcollection == null){
            	
            	currcollection = engine.createCollection(cuurprovider);
            	currcollection.setLanguage(countryCode);
            	currcollection.setMnemonic(mnemonicCode);
            	currcollection.setName(collectionName);
            	currcollection.setOaiBaseUrl(harvestUrl);
            	currcollection.setOaiMetadataPrefix("?");
            	
    			engine.updateCollection(currcollection);
    			engine.checkpoint();
            }
			
		return currcollection;	
	}
	
	
	
	/**
	 * @param value
	 * @param el
	 * @return
	 */
	private String extractFromElement(String value, Element el){
		
		NodeList nl =el.getElementsByTagName(value);
		
		if(nl.getLength() != 0){
			
			return nl.item(0).getTextContent();
		}
		
		
		return null;
	}
	
	
	/**
	 * @param id
	 * @param status
	 * @throws QueryResultException 
	 */
	private void alterSugarCRMItemStatus(String id, String status) throws QueryResultException{
		SetEntry request = new SetEntry();
		
		
		NameValue nv0 = new NameValue();
		nv0.setName("sales_stage");
		nv0.setValue(status);
		
		NameValue nv1 = new NameValue();
		nv1.setName("id");
		nv1.setValue(id);
		
		ArrayList <NameValue> nvList = new  ArrayList <NameValue>();
		
		nvList.add(nv0);
		nvList.add(nv1);
		
		NameValueList valueList = ClientUtils.generatePopulatedNameValueList(nvList);
		
		request.setNameValueList(valueList);
		request.setModuleName("Opportunities");
		request.setSession(sugarwsClient.getSessionID());	
		
		SetEntryResponse response =  sugarwsClient.set_entry(request);

				
	}
	
	

	
	
	/*
	 * Getters & Setters
	 */
	
	
	public void setSugarwsClient(SugarWsClient sugarwsClient) {
		this.sugarwsClient = sugarwsClient;
	}

	public SugarWsClient getSugarwsClient() {
		return sugarwsClient;
	}



	public void setOrchestrator(Orchestrator orchestrator) {
		this.orchestrator = orchestrator;
	}



	public Orchestrator getOrchestrator() {
		return orchestrator;
	}



	public void setRegistry(Registry registry) {
		this.registry = registry;
	}



	public Registry getRegistry() {
		return registry;
	}







































}
