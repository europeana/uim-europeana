package eu.europeana.uim.sugarcrmclient.plugin;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.DataSet;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModules;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Login;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NameValue;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NameValueList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SelectFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntry;
import eu.europeana.uim.sugarcrmclient.ws.SugarWsClientOSGI;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LoginFailureException;
import eu.europeana.uim.sugarcrmclient.internal.helpers.DatasetStates;
import eu.europeana.uim.workflow.Workflow;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;



public class SugarCRMAgentImpl implements SugarCRMAgent{

	private SugarWsClientOSGI sugarwsClient;
	private Orchestrator orchestrator;
	private Registry registry;
	
	
	@Override
	public HashMap<String, HashMap<String, String>> pollForHarvestInitiators() {

		GetEntryList request = new GetEntryList();
				
		SelectFields fields = new SelectFields(); //We want to retrieve all fields
		request.setSelectFields(fields); 
  		request.setModuleName("Opportunities");	

		request.setSession(sugarwsClient.getSessionID());
		request.setOrderBy("date_entered");
		request.setMaxResults(10000);
		request.setOffset(0);
		
		request.setQuery("(opportunities.sales_stage LIKE 'Needs%Analysis')");

		HashMap<String, HashMap<String, String>> response =  sugarwsClient.get_entry_list(request);
		
		initiateWorkflowsFromTriggers(response);
		
		
		return response;
	
	}

	
	
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

	@Override
	public String notifySugarForIngestionSuccess(String recordId) {
		String teststring = "Notify Sugar For Ingestion Success";
		alterSugarCRMItemStatus(recordId, DatasetStates.READY_FOR_REPLICATION.getSysId());
		return teststring;
	}

	@Override
	public String notifySugarForIngestionFailure(String recordId) {
		String teststring = "Notify Sugar For Ingestion Failure";
		
		alterSugarCRMItemStatus(recordId, DatasetStates.DISABLED_AND_REPLACED.getSysId());
		return teststring;
	}
	


	@Override
	public String showConnectionStatus() {

		StringBuffer connectionInfo = new StringBuffer();
		
		String defaultURI = sugarwsClient.getDefaultUri();
		
		connectionInfo.append("Pointing at:");
		connectionInfo.append(defaultURI);
		
		connectionInfo.append("\n");
		connectionInfo.append("Session Id:");
		connectionInfo.append(sugarwsClient.getSessionID());
		
		
		return connectionInfo.toString();
	}


	
	@Override
	public String showAvailableModules() {
		GetAvailableModules request = new GetAvailableModules();
		request.setSession(sugarwsClient.getSessionID());
		String response =  sugarwsClient.get_available_modules(request);

		return response;
	}



	@Override
	public String showModuleFields(String module) {

		GetModuleFields request = new GetModuleFields();
		request.setSession(sugarwsClient.getSessionID());
		request.setModuleName(module);
		
		String response =  sugarwsClient.get_module_fields(request);

		return response;
	}
	
	
	/*
	 * Private methods
	 */
	
	
	private void initiateWorkflowsFromTriggers(HashMap<String, HashMap<String, String>> triggers){
				
		StorageEngine engine = registry.getStorage();
		
		
		Workflow w = registry.getWorkflow("SysoutWorkflow");
		
		
		Iterator it = triggers.keySet().iterator();
		
		
		while (it.hasNext()){
			
			try {
				
				Collection dataset = inferCollection(engine,triggers.get(it.next()) );
				orchestrator.executeWorkflow(w, dataset);
				alterSugarCRMItemStatus(dataset.getMnemonic(), DatasetStates.READY_FOR_REPLICATION.getSysId());
				
				
			} catch (StorageEngineException e) {
				e.printStackTrace();
			}
			
			
		}
		

		

		

		
	}
	
	
	
	private Collection inferCollection(StorageEngine engine, HashMap<String, String> trigger) throws StorageEngineException{
		    
		    String collectionName = trigger.get("name");
		    String providerName = trigger.get("account_name");
		    String providerAcronymName = trigger.get("name_acronym_c");
		    String mnemonicCode = trigger.get("id");
		    String countryCode = trigger.get("country_c");
		    String harvestUrl = trigger.get("harvest_url_c");
		    
		    
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
		
		String response =  sugarwsClient.set_entry(request);

				
	}
	
	
	
	
	
	
	
	public void setSugarwsClient(SugarWsClientOSGI sugarwsClient) {
		this.sugarwsClient = sugarwsClient;
	}

	public SugarWsClientOSGI getSugarwsClient() {
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
