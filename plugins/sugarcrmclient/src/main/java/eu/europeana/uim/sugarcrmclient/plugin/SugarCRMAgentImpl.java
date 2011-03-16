package eu.europeana.uim.sugarcrmclient.plugin;


import java.util.ArrayList;

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
import eu.europeana.uim.sugarcrmclient.ws.SugarWsClient;
import eu.europeana.uim.sugarcrmclient.ws.SugarWsClientOSGI;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LoginFailureException;


public class SugarCRMAgentImpl implements SugarCRMAgent{

	private SugarWsClientOSGI sugarwsClient;
	
	
	
	@Override
	public String pollForHarvestInitiators() {

		GetEntryList request = new GetEntryList();
		
		ArrayList <String> fieldnames = new  ArrayList<String>();
		
		fieldnames.add("id");
		fieldnames.add("first_name");
		fieldnames.add("last_name");
		fieldnames.add("salutation");
		
		SelectFields fields = ClientUtils.generatePopulatedSelectFields(fieldnames);
		
  		request.setModuleName("Contacts");	
		request.setSelectFields(fields); //stuaoqenh2va9negtmvb1ohbm1
		//request.setSession(sugarwsClient.getSessionID());
  		request.setSession("821gms22fmgq8n9720dv36vk65");
		request.setOrderBy("last_name");
		request.setMaxResults(100);
		request.setOffset(10);
		//request.setQuery("(contacts.salutation = 'Mr.' AND contacts.title LIKE 'doctor appointment%')");
		request.setQuery("(contacts.first_name LIKE '%M%')");
		

		String response =  sugarwsClient.get_entry_list(request);
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
		
		/*
		*/
		
		
		return connectionInfo.toString();
	}

	@Override
	public String notifySugarForIngestionSuccess() {
		String teststring = "Notify Sugar For Ingestion Success (to be implemented)";
		
		return teststring;
	}

	@Override
	public String notifySugarForIngestionFailure() {
		String teststring = "Notify Sugar For Ingestion Failure (to be implemented)";
		
		return teststring;
	}
	


	@Override
	public String showConnectionStatus() {

		StringBuffer connectionInfo = new StringBuffer();
		
		String defaultURI = sugarwsClient.getDefaultUri();
		
		connectionInfo.append("Pointing at:");
		connectionInfo.append(defaultURI);
		
		connectionInfo.append("/n");
		connectionInfo.append("Session Id:");
				
		return connectionInfo.toString();
	}


	
	@Override
	public String showAvailableModules() {
		GetAvailableModules request = new GetAvailableModules();
		//request.setSession(sessionID);
  		request.setSession("821gms22fmgq8n9720dv36vk65");
  		
		String response =  sugarwsClient.get_available_modules(request);

		return response;
	}



	@Override
	public String showModuleFields(String module) {

		GetModuleFields request = new GetModuleFields();
		//request.setSession(sessionID);
  		request.setSession("821gms22fmgq8n9720dv36vk65");
		request.setModuleName(module);
		
		String response =  sugarwsClient.get_module_fields(request);

		return response;
	}
	
	
	public void testSetEntry(){
		SetEntry request = new SetEntry();
		
		
		//NameValue nv1 = new NameValue();
		//nv1.setName("id");
		//nv1.setValue("99f37146-8e19-473d-171c-4d66de7024c0");
		
		NameValue nv0 = new NameValue();
		nv0.setName("first_name");
		nv0.setValue("JohnX");

		NameValue nv2 = new NameValue();
		nv2.setName("last_name");
		nv2.setValue("SmithX");		

		ArrayList <NameValue> nvList = new  ArrayList <NameValue>();
		
		nvList.add(nv0);
		nvList.add(nv2);
		
		
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
