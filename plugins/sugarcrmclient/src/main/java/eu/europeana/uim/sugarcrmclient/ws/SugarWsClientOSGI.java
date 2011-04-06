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

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallable;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.xml.sax.SAXException;

import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModules;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Login;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Logout;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntry;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LoginFailureException;



/**
 * @author Georgios Markakis
 *
 */
public class SugarWsClientOSGI {

	private WebServiceTemplate webServiceTemplate;

	private String sessionID;
	
	
	
	/**
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public  SugarWsClientOSGI createInstance(String userName, String password){
		
		SugarWsClientOSGI client = new SugarWsClientOSGI();
		client.setWebServiceTemplate(webServiceTemplate);
		
		try {
			client.sessionID = client.login(ClientUtils.createStandardLoginObject(userName,password) );
		} catch (LoginFailureException e) {
			client.sessionID = "-1";
			e.printStackTrace();
		}
		
		return client;
	}
	
	
	/**
	 * 
	 * @return
	 */
	private String invokeWSPlainString(Object request){
	
		StringWriter sourceWriter = new StringWriter();
		IBindingFactory bfact;
		try {
			bfact = BindingDirectory.getFactory(request.getClass());
		    IMarshallingContext mctx = bfact.createMarshallingContext();
		    mctx.setOutput(sourceWriter);
		    ((IMarshallable)request).marshal(mctx);
		    mctx.getXmlWriter().flush();
		    
			StringWriter resultWriter = new StringWriter();
		    
			StreamSource source = new StreamSource(new StringReader(sourceWriter.toString()));
	        StreamResult result = new StreamResult(resultWriter);
	        
			webServiceTemplate.sendSourceAndReceiveToResult(source,result);
		 
			ClientUtils.responseFactory(resultWriter.toString());
			
			return ClientUtils.extractSimpleResponse(resultWriter.toString());
			
		} catch (JiBXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} 

				
		return null;
	}
	
	
	
	
	/**
	 * 
	 * @return
	 */
	private HashMap<String, HashMap<String, String>> invokeWSInfoMap(Object request){
	
		StringWriter sourceWriter = new StringWriter();
		IBindingFactory bfact;
		try {
			bfact = BindingDirectory.getFactory(request.getClass());
		    IMarshallingContext mctx = bfact.createMarshallingContext();
		    mctx.setOutput(sourceWriter);
		    ((IMarshallable)request).marshal(mctx);
		    mctx.getXmlWriter().flush();
		    
			StringWriter resultWriter = new StringWriter();
		    
			StreamSource source = new StreamSource(new StringReader(sourceWriter.toString()));
	        StreamResult result = new StreamResult(resultWriter);
			webServiceTemplate.sendSourceAndReceiveToResult(source,result);
		 
			//bfact.createUnmarshallingContext();
			
			
			return ClientUtils.responseFactory(resultWriter.toString());
			
		} catch (JiBXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (XPathExpressionException e) {
			e.printStackTrace();
		} 

				
		return null;
	}
	
	
	
	
	
	
	/**
	 * 
	 * @param login
	 * @return
	 */
	public String login(Login login) throws LoginFailureException{
		String sessionID =  invokeWSPlainString(login);
		if("-1".equals(sessionID)){			
			throw new LoginFailureException(sessionID);
		}
		return sessionID;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public String logout(Logout request){
		
		String response =  invokeWSPlainString(request);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public HashMap<String, HashMap<String, String>> get_entry_list(GetEntryList request){
		
		HashMap<String, HashMap<String, String>> response = invokeWSInfoMap(request);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public String get_entry(GetEntry request){
		
		String response = invokeWSPlainString(request);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public String set_entry(SetEntry request){
		
		String response = invokeWSPlainString(request);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public String get_available_modules(GetAvailableModules request){

		String response = invokeWSPlainString(request);
		
		return response;
	}
	
	
	/**
	 * @param request
	 * @return
	 */
	public String get_module_fields(GetModuleFields request){

		String response = invokeWSPlainString(request);
		
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

	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}

	public String getSessionID() {
		return sessionID;
	}
	
	public void setDefaultUri(String defaultUri) {
		
		webServiceTemplate.setDefaultUri(defaultUri);
	}

	public String getDefaultUri() {
		return webServiceTemplate.getDefaultUri();
	}
}
