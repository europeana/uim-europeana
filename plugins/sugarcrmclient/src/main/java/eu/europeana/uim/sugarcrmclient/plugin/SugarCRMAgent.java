package eu.europeana.uim.sugarcrmclient.plugin;

public interface SugarCRMAgent {
	
	public String showConnectionStatus();
	
	public String showAvailableModules();
	
	public String showModuleFields(String module);
	
	public String updateSession();
	
	public String pollForHarvestInitiators();
	
	public String notifySugarForIngestionSuccess();
	
	public String notifySugarForIngestionFailure();

}
