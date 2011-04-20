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

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.DataSet;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModules;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModulesResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFieldsResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Login;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NameValue;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NameValueList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SelectFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntryResponse;
import eu.europeana.uim.sugarcrmclient.plugin.objects.ConnectionStatus;
import eu.europeana.uim.sugarcrmclient.ws.SugarWsClient;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LoginFailureException;
import eu.europeana.uim.sugarcrmclient.internal.helpers.DatasetStates;
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
public class SugarCRMAgentImpl implements SugarCRMAgent{

	private SugarWsClient sugarwsClient;
	private Orchestrator orchestrator;
	private Registry registry;
	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgent#pollForHarvestInitiators()
	 */
	@Override
	public GetEntryListResponse pollForHarvestInitiators() {

		GetEntryList request = new GetEntryList();
				
		SelectFields fields = new SelectFields(); //We want to retrieve all fields
		request.setSelectFields(fields); 
  		request.setModuleName("Opportunities");	
		request.setSession(sugarwsClient.getSessionID());
		request.setOrderBy("date_entered");
		request.setMaxResults(10000);
		request.setOffset(0);
		
		request.setQuery("(opportunities.sales_stage LIKE 'Needs%Analysis')");

		GetEntryListResponse response =  sugarwsClient.get_entry_list(request);
		
		initiateWorkflowsFromTriggers(response);
		
		
		return response;
	
	}

	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgent#updateSession()
	 */
	@Override
	public String updateSession() {
		StringBuffer connectionInfo = new StringBuffer();
		
		Login login = ClientUtils.createStandardLoginObject("test", "test");
		
		try {
		   connectionInfo.append(sugarwsClient.login(login));
		} catch (LoginFailureException e) {			
			connectionInfo.append("Invalid Session, login failed!");
			e.printStackTrace();
		}

		return connectionInfo.toString();
	}
 
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgent#notifySugarForIngestionSuccess(java.lang.String)
	 */
	@Override
	public String notifySugarForIngestionSuccess(String recordId) {
		String teststring = "Notify Sugar For Ingestion Success";
		alterSugarCRMItemStatus(recordId, DatasetStates.READY_FOR_REPLICATION.getSysId());
		return teststring;
	}

	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgent#notifySugarForIngestionFailure(java.lang.String)
	 */
	@Override
	public String notifySugarForIngestionFailure(String recordId) {
		String teststring = "Notify Sugar For Ingestion Failure";
		
		alterSugarCRMItemStatus(recordId, DatasetStates.DISABLED_AND_REPLACED.getSysId());
		return teststring;
	}
	


	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgent#showConnectionStatus()
	 */
	@Override
	public ConnectionStatus showConnectionStatus() {
		
		ConnectionStatus cstatus = new ConnectionStatus();
		
		cstatus.setDefaultURI(sugarwsClient.getDefaultUri());
		cstatus.setSessionID(sugarwsClient.getSessionID());

		return cstatus;
	}


	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgent#showAvailableModules()
	 */
	@Override
	public GetAvailableModulesResponse showAvailableModules() {
		GetAvailableModules request = new GetAvailableModules();
		request.setSession(sugarwsClient.getSessionID());
		GetAvailableModulesResponse response =  sugarwsClient.get_available_modules(request);

		return response;
	}



	/* (non-Javadoc)
	 * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgent#showModuleFields(java.lang.String)
	 */
	@Override
	public GetModuleFieldsResponse showModuleFields(String module) {

		GetModuleFields request = new GetModuleFields();
		request.setSession(sugarwsClient.getSessionID());
		request.setModuleName(module);		
		GetModuleFieldsResponse response =  sugarwsClient.get_module_fields(request);

		return response;
	}
	
	
	/*
	 * Private methods
	 */
	
	
	/**
	 * @param triggers
	 */
	private void initiateWorkflowsFromTriggers(GetEntryListResponse triggers){
				
		StorageEngine<?> engine = registry.getStorage();
		Workflow w = registry.getWorkflow("SysoutWorkflow");
		
		if(triggers.getReturn().getEntryList().getArray() != null){
			
		List<Element> anyList = triggers.getReturn().getEntryList().getArray().getAnyList();
		
		Iterator<Element> it = anyList.iterator();
		
		
		while (it.hasNext()){	
			try {
				Collection<?> dataset = inferCollection(engine,it.next());
				ActiveExecution<?> execution = orchestrator.executeWorkflow(w, dataset);
				alterSugarCRMItemStatus(dataset.getMnemonic(), DatasetStates.MAPPING_AND_NORMALIZATION.getSysId());	
			
				execution.waitUntilFinished();	
			} catch (StorageEngineException e) {
				e.printStackTrace();

			}	
		}
	   }
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
	 */
	private void alterSugarCRMItemStatus(String id, String status){
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
