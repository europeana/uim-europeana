package eu.europeana.uim.sugarcrmclient.plugin;

import eu.europeana.uim.sugarcrmclient.ws.SugarWsClient;


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
		
		connectionInfo.append(defaultURI);
		
		return connectionInfo.toString();
	}

	
	
	public void setSugarwsClient(SugarWsClient sugarwsClient) {
		this.sugarwsClient = sugarwsClient;
	}

	public SugarWsClient getSugarwsClient() {
		return sugarwsClient;
	}



}
