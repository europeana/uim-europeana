package eu.europeana.uim.sugarcrmclient.ws;

import org.apache.log4j.Logger;
import org.springframework.ws.client.core.WebServiceTemplate;

import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.JIXBLoginFailureException;

/**
 * 
 * @author Georgios Markakis
 */
public class ClientFactory {

	
	private WebServiceTemplate webServiceTemplate;
	private static org.apache.log4j.Logger LOGGER = Logger.getLogger(ClientUtils.class);	

	/**
	 * Internal factory method used by Spring 
	 * 
	 * @param userName
	 * @param password
	 * @return
	 */
	public  SugarWsClientImpl createInstance(String userName, String password){
		
		SugarWsClientImpl client = new SugarWsClientImpl();
		client.setWebServiceTemplate(webServiceTemplate);
		
		try {
			client.setSessionID(client.login(ClientUtils.createStandardLoginObject(userName,password)));
		} catch (JIXBLoginFailureException e) {
			client.setSessionID("-1");
			e.printStackTrace();
		} catch (Exception e){
			LOGGER.info("======= Warning: could not connect to SugarCrm Server =====");

			e.printStackTrace();
		}
	
		
		return client;
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
