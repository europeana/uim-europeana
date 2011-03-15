package eu.europeana.uim.sugarcrmclient.plugin;

public interface SugarCRMAgent {
	
	public String showConnectionStatus();
	
	public String updateSession();
	
	public String pollForHarvestInitiators();
	
	public String notifySugarForIngestionSuccess();
	
	public String notifySugarForIngestionFailure();

}
