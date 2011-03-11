package eu.europeana.uim.sugarcrmclient.plugin;

import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.sugarcrmclient.jaxbbindings.Login;
import eu.europeana.uim.sugarcrmclient.ws.SugarWsClient;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LoginFailureException;


public class SugarCRMAgentImpl implements SugarCRMAgent{

	private SugarWsClient sugarwsClient;
	
	
	
	@Override
	public String pollForHarvestInitiators() {

		String teststring = "Polling for changes";
		
		return teststring;
	}

	@Override
	public String notifySugarForChanges() {

		String teststring = "Changes Notification (to be implemented)";
		
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

		Login login = ClientUtils.createStandardLoginObject("test", "test");
		try {
			connectionInfo.append(sugarwsClient.login(login));
		} catch (LoginFailureException e) {			
			connectionInfo.append("Invalid Session, login failed!");
			e.printStackTrace();
		}
		
		
		return connectionInfo.toString();
	}

	
	
	public void setSugarwsClient(SugarWsClient sugarwsClient) {
		this.sugarwsClient = sugarwsClient;
	}

	public SugarWsClient getSugarwsClient() {
		return sugarwsClient;
	}



}
