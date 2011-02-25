/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.ws;



import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.namespace.QName;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;


import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceMessageExtractor;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.xml.transform.StringResult;
import com.sugarcrm.sugarcrm.*;

import com.sugarcrm.sugarcrm.IsUserAdmin;
import com.sugarcrm.sugarcrm.IsUserAdminResponse;

import com.sugarcrm.sugarcrm.IsLoopback;
import com.sugarcrm.sugarcrm.IsLoopbackResponse;

import com.sugarcrm.sugarcrm.SeamlessLogin;
import com.sugarcrm.sugarcrm.SeamlessLoginResponse;

import com.sugarcrm.sugarcrm.GetEntryList;
import com.sugarcrm.sugarcrm.GetEntryListResponse;

import com.sugarcrm.sugarcrm.GetEntry;
import com.sugarcrm.sugarcrm.GetEntryResponse;

import com.sugarcrm.sugarcrm.GetEntries;
import com.sugarcrm.sugarcrm.GetEntriesResponse;


import com.sugarcrm.sugarcrm.SetEntry;
import com.sugarcrm.sugarcrm.SetEntryResponse;

import com.sugarcrm.sugarcrm.SetEntries;
import com.sugarcrm.sugarcrm.SetEntriesResponse;

import com.sugarcrm.sugarcrm.SetNoteAttachment;
import com.sugarcrm.sugarcrm.SetNoteAttachmentResponse;

import com.sugarcrm.sugarcrm.GetNoteAttachment;
import com.sugarcrm.sugarcrm.GetNoteAttachmentResponse;

import com.sugarcrm.sugarcrm.RelateNoteToModule;
import com.sugarcrm.sugarcrm.RelateNoteToModuleResponse;

import com.sugarcrm.sugarcrm.GetRelatedNotes;
import com.sugarcrm.sugarcrm.GetRelatedNotesResponse;

import com.sugarcrm.sugarcrm.Logout;
import com.sugarcrm.sugarcrm.LogoutResponse;

import com.sugarcrm.sugarcrm.GetModuleFields;
import com.sugarcrm.sugarcrm.GetModuleFieldsResponse;

import com.sugarcrm.sugarcrm.GetAvailableModules;
import com.sugarcrm.sugarcrm.GetAvailableModulesResponse;

import com.sugarcrm.sugarcrm.UpdatePortalUser;
import com.sugarcrm.sugarcrm.UpdatePortalUserResponse;

import com.sugarcrm.sugarcrm.GetUserId;
import com.sugarcrm.sugarcrm.GetUserIdResponse;


import com.sugarcrm.sugarcrm.GetModuleFields;
import com.sugarcrm.sugarcrm.GetModuleFieldsResponse;

import com.sugarcrm.sugarcrm.GetUserTeamIdResponse;
import com.sugarcrm.sugarcrm.GetServerTimeResponse;
import com.sugarcrm.sugarcrm.GetGmtTimeResponse;


import com.sugarcrm.sugarcrm.ContactByEmail;
import com.sugarcrm.sugarcrm.ContactByEmailResponse;

import org.apache.xerces.jaxp.SAXParserFactoryImpl;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;


/**
 * 
 * @author Georgios Markakis
 */
public class SugarWsClient {

	
	private WebServiceTemplate webServiceTemplate;

	public void setDefaultUri(String defaultUri) {
		webServiceTemplate.setDefaultUri(defaultUri);

	}

	
	/**
	 * @param <T>
	 * @param <S>
	 * @param wsOperation
	 * @return
	 */
	private <T,S> S invokeWSTemplate( T wsOperation){

		@SuppressWarnings("unchecked")
		S wsResponse = (S)webServiceTemplate.marshalSendAndReceive(wsOperation);
	
		return wsResponse;
	}
	
	
	
	
	/**
	 * @param login
	 * @return
	 */
	public String login(Login login){
		
		LoginResponse response =  invokeWSTemplate(login);
		String sessionID = response.getReturn().getId();
		
		return sessionID;
	}
	

	/**
	 * @param login
	 * @return
	 */
	public LoginResponse login2(Login login){
		
		LoginResponse response =  invokeWSTemplate(login);
		
		return response;
	}
	
	
	
	
	/**
	 * @param request
	 * @return
	 */
	public LogoutResponse logout(Logout request){
		
		LogoutResponse response =  invokeWSTemplate(request);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public IsUserAdminResponse is_user_admin(IsUserAdmin request){

		IsUserAdminResponse response = invokeWSTemplate(request);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public GetAvailableModulesResponse get_available_modules(GetAvailableModules request){

		GetAvailableModulesResponse response = invokeWSTemplate(request);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public GetModuleFieldsResponse get_module_fields(GetModuleFields request){

		GetModuleFieldsResponse response = invokeWSTemplate(request);
		
		return response;
	}

	
	/**
	 * @param request
	 * @return
	 */
	public GetEntryListResponse get_entry_list(GetEntryList request){
		
		GetEntryListResponse response = invokeWSTemplate(request);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public GetEntryResponse get_entry(GetEntry request){
		
		GetEntryResponse response = invokeWSTemplate(request);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public SetEntryResponse set_entry(SetEntry request){
		
		SetEntryResponse response = invokeWSTemplate(request);
		
		return response;
	}
	
	
	
	/**
	 * @param request
	 * @return
	 */
	public GetEntriesResponse get_entries(GetEntries request){
		
		GetEntriesResponse response = invokeWSTemplate(request);
		
		return response;
	}
	
	
	
	
	/**
	 * @param request
	 * @return
	 */
	public GetUserIdResponse get_user_id(GetUserId request){

		GetUserIdResponse response = invokeWSTemplate(request);
		
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
