package eu.europeana.uim.sugarcrmclient.ws;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallable;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.xml.sax.SAXException;

import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntries;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntriesResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Login;
import eu.europeana.uim.sugarcrmclient.jibxbindings.LoginResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Logout;
import eu.europeana.uim.sugarcrmclient.jibxbindings.LogoutResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntryResponse;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LoginFailureException;

public class SugarWsClientNonBinding {

	private WebServiceTemplate webServiceTemplate;

	private String sessionID;
	

	
	
	
	
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
			
			return resultWriter.toString();
			
		} catch (JiBXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 

				
		return null;
	}
	
	
	/**
	 * @param login
	 * @return
	 */
	public String login(Login login) throws LoginFailureException{
			
		String response =  invokeWSPlainString(login);
		
		/*
		String sessionID = response.getReturn().getId();
		
		if("-1".equals(sessionID)){			
			throw new LoginFailureException(response);
		}
		
		*/
		
		return response;
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
	public String get_entry_list(GetEntryList request){
		
		String response = invokeWSPlainString(request);
		
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
