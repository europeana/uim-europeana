/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.ws;



import org.springframework.ws.client.core.WebServiceTemplate;

import com.sugarcrm.sugarcrm.IsUserAdmin;
import com.sugarcrm.sugarcrm.IsUserAdminResponse;
import com.sugarcrm.sugarcrm.GetEntryList;
import com.sugarcrm.sugarcrm.GetEntryListResponse;
import com.sugarcrm.sugarcrm.GetEntry;
import com.sugarcrm.sugarcrm.GetEntryResponse;
import com.sugarcrm.sugarcrm.GetEntries;
import com.sugarcrm.sugarcrm.GetEntriesResponse;
import com.sugarcrm.sugarcrm.SetEntry;
import com.sugarcrm.sugarcrm.SetEntryResponse;
import com.sugarcrm.sugarcrm.Logout;
import com.sugarcrm.sugarcrm.LogoutResponse;
import com.sugarcrm.sugarcrm.GetModuleFields;
import com.sugarcrm.sugarcrm.GetModuleFieldsResponse;
import com.sugarcrm.sugarcrm.GetAvailableModules;
import com.sugarcrm.sugarcrm.GetAvailableModulesResponse;
import com.sugarcrm.sugarcrm.GetUserId;
import com.sugarcrm.sugarcrm.GetUserIdResponse;
import com.sugarcrm.sugarcrm.GetModuleFields;
import com.sugarcrm.sugarcrm.GetModuleFieldsResponse;
import com.sugarcrm.sugarcrm.Login;
import com.sugarcrm.sugarcrm.LoginResponse;

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
