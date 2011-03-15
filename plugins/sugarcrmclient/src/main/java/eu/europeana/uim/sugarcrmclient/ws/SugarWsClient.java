/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.ws;





import java.io.StringReader;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.ws.client.core.WebServiceTemplate;

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
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFieldsResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Login;
import eu.europeana.uim.sugarcrmclient.jibxbindings.LoginResponse;

import eu.europeana.uim.sugarcrmclient.ws.exceptions.*;

/**
 * 
 * @author Georgios Markakis
 */
public class SugarWsClient {

	
	private WebServiceTemplate webServiceTemplate;

	public void setDefaultUri(String defaultUri) {
				
		webServiceTemplate.setDefaultUri(defaultUri);
	}

	public String getDefaultUri() {
		return webServiceTemplate.getDefaultUri();
	}
	
	/**
	 * @param <T>
	 * @param <S>
	 * @param wsOperation
	 * @return
	 */
	private <T,S> S invokeWSTemplate( T wsOperation, Class<S> responseClass){

		@SuppressWarnings("unchecked")
		S wsResponse = (S)webServiceTemplate.marshalSendAndReceive(wsOperation);
		
		return wsResponse;
	}
	
	

	
	
	
	/**
	 * @param login
	 * @return
	 */
	public String login(Login login) throws LoginFailureException{
		
		LoginResponse response =  invokeWSTemplate(login,LoginResponse.class);
		String sessionID = response.getReturn().getId();
		
		if("-1".equals(sessionID)){			
			throw new LoginFailureException(response);
		}
		
		
		return sessionID;
	}
	

	/**
	 * @param login
	 * @return
	 */
	public LoginResponse login2(Login login) throws LoginFailureException{
		
		LoginResponse response =  invokeWSTemplate(login,LoginResponse.class);
		
		if("-1".equals(response.getReturn().getId())){			
			throw new LoginFailureException(response);
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
	

}
