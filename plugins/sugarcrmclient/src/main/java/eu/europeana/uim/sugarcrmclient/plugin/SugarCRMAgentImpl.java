package eu.europeana.uim.sugarcrmclient.plugin;


import java.util.ArrayList;
import java.util.HashMap;

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


public class SugarCRMAgentImpl implements SugarCRMAgent{

	private SugarWsClientOSGI sugarwsClient;
	
	
	
	@Override
	public HashMap<String, HashMap<String, String>> pollForHarvestInitiators() {

		GetEntryList request = new GetEntryList();
				
		SelectFields fields = new SelectFields(); //We want to retrieve all fields
		request.setSelectFields(fields); 
  		request.setModuleName("Opportunities");	

		request.setSession(sugarwsClient.getSessionID());
		
		request.setOrderBy("date_entered");
		request.setMaxResults(100);
		request.setOffset(0);
		//request.setQuery("(contacts.salutation = 'Mr.' AND contacts.title LIKE 'doctor appointment%')");
		request.setQuery("(opportunities.sales_stage LIKE 'Needs%Analysis')");
		//request.setQuery("(opportunities.sales_stage LIKE 'Id%Decision%Makers')");
		

		HashMap<String, HashMap<String, String>> response =  sugarwsClient.get_entry_list(request);
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
		
		alterSugarCRMItemStatus(recordId, "Closed Won");
		
		return teststring;
	}

	@Override
	public String notifySugarForIngestionFailure(String recordId) {
		String teststring = "Notify Sugar For Ingestion Failure";
		
		alterSugarCRMItemStatus(recordId, "");
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

		//valueList.setId("99f37146-8e19-473d-171c-4d66de7024c0");
		
		request.setNameValueList(valueList);
		request.setModuleName("Contacts");
		request.setSession("sessionID");	

		String response =  sugarwsClient.set_entry(request);

				
	}
	
	
	
	
	
	
	
	public void setSugarwsClient(SugarWsClientOSGI sugarwsClient) {
		this.sugarwsClient = sugarwsClient;
	}

	public SugarWsClientOSGI getSugarwsClient() {
		return sugarwsClient;
	}









}
