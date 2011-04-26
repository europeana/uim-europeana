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
package eu.europeana.uim.sugarcrmclient.ws;

import org.springframework.ws.client.core.WebServiceTemplate;

import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.sugarcrmclient.jibxbindings.IsUserAdmin;
import eu.europeana.uim.sugarcrmclient.jibxbindings.IsUserAdminResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntries;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntriesResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntryResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Logout;
import eu.europeana.uim.sugarcrmclient.jibxbindings.LogoutResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFieldsResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModules;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModulesResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetUserId;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetUserIdResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Login;
import eu.europeana.uim.sugarcrmclient.jibxbindings.LoginResponse;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.*;

/**
 * The core class for performing SOAP based sugarCRM operations  
 * 
 * @author Georgios Markakis
 */
public class SugarWsClient {

	
	private WebServiceTemplate webServiceTemplate;

	private String sessionID;
	

	/**
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public  SugarWsClient createInstance(String userName, String password){
		
		SugarWsClient client = new SugarWsClient();
		client.setWebServiceTemplate(webServiceTemplate);
		
		try {
			client.sessionID = client.login(ClientUtils.createStandardLoginObject(userName,password) );
		} catch (LoginFailureException e) {
			client.sessionID = "-1";
			e.printStackTrace();
		} catch (GenericSugarCRMException e) {
			e.printStackTrace();
		}
		
		return client;
	}
	
	
	
	/**
	 * Generic auxiliary method for marshalling and unmarshalling requests
	 * and responses via Sring-WS
	 * 
	 * @param <T> Class of the request Object
	 * @param <S> Class of the response object
	 * @param wsOperation the instance of the request operation
	 * @return the unmarshalled response object 
	 */
	private <T,S> S invokeWSTemplate( T wsOperation, Class<S> responseClass){

		@SuppressWarnings("unchecked")
		S wsResponse = (S)webServiceTemplate.marshalSendAndReceive(wsOperation);
		
		return wsResponse;
	}
	
	

	
	
	
	/**
	 * Public method for performing Login operations (see Junit test for usage example) 
	 * 
	 * @param login the Login object
	 * @return a String 
	 * @throws LoginFailureException 
	 * @throws GenericSugarCRMException
	 */
	public String login(Login login) throws LoginFailureException,GenericSugarCRMException{
		
		try{
		LoginResponse response =  invokeWSTemplate(login,LoginResponse.class);
		String sessionID = response.getReturn().getId();
		if("-1".equals(sessionID)){			
			throw new LoginFailureException(response.getReturn().getError());
		}
		  return sessionID;
		}
		catch(Exception e){
			throw new GenericSugarCRMException();

		}
	}
	

	/**
	 * @param login
	 * @return
	 */
	public LoginResponse login2(Login login) throws LoginFailureException{
		
		LoginResponse response =  invokeWSTemplate(login,LoginResponse.class);
		
		if("-1".equals(response.getReturn().getId())){			
			throw new LoginFailureException(response.getReturn().getError());
		}
		
		return response;
	}
	
	
	
	
	/**
	 * @param request
	 * @return
	 */
	public LogoutResponse logout(Logout request){
		
		LogoutResponse response =  invokeWSTemplate(request,LogoutResponse.class);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public IsUserAdminResponse is_user_admin(IsUserAdmin request){

		IsUserAdminResponse response = invokeWSTemplate(request,IsUserAdminResponse.class);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public GetAvailableModulesResponse get_available_modules(GetAvailableModules request){

		GetAvailableModulesResponse response = invokeWSTemplate(request,GetAvailableModulesResponse.class);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public GetModuleFieldsResponse get_module_fields(GetModuleFields request){

		GetModuleFieldsResponse response = invokeWSTemplate(request,GetModuleFieldsResponse.class);
		
		return response;
	}

	
	/**
	 * @param request
	 * @return
	 */
	public GetEntryListResponse get_entry_list(GetEntryList request){
		
		GetEntryListResponse response = invokeWSTemplate(request,GetEntryListResponse.class);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public GetEntryResponse get_entry(GetEntry request){
		
		GetEntryResponse response = invokeWSTemplate(request,GetEntryResponse.class);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public SetEntryResponse set_entry(SetEntry request){
		
		SetEntryResponse response = invokeWSTemplate(request,SetEntryResponse.class);
		
		return response;
	}
	
	
	
	/**
	 * @param request
	 * @return
	 */
	public GetEntriesResponse get_entries(GetEntries request){
		
		GetEntriesResponse response = invokeWSTemplate(request,GetEntriesResponse.class);
		
		return response;
	}
	
	
	
	
	/**
	 * @param request
	 * @return
	 */
	public GetUserIdResponse get_user_id(GetUserId request){

		GetUserIdResponse response = invokeWSTemplate(request,GetUserIdResponse.class);
		
		return response;
	}

	
	/**
	 * @return
	 */
	public WebServiceTemplate getWebServiceTemplate(){
		return this.webServiceTemplate;
	}
	
	
	
	/**
	 * @param webServiceTemplate
	 */
	public void setWebServiceTemplate(WebServiceTemplate webServiceTemplate){
		this.webServiceTemplate = webServiceTemplate;
	}
	
	
	public void setDefaultUri(String defaultUri) {
		
		webServiceTemplate.setDefaultUri(defaultUri);
	}

	public String getDefaultUri() {
		return webServiceTemplate.getDefaultUri();
	}
	
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getSessionID() {
		return sessionID;
	}

}
