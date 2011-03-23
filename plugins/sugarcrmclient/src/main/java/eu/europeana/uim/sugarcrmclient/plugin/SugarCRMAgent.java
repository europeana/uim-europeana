package eu.europeana.uim.sugarcrmclient.plugin;

import java.util.HashMap;

public interface SugarCRMAgent {
	
	public String showConnectionStatus();
	
	public String showAvailableModules();
	
	public String showModuleFields(String module);
	
	public String updateSession();
	
	public HashMap<String, HashMap<String, String>>  pollForHarvestInitiators();
	
	public String notifySugarForIngestionSuccess(String recordId);
	
	public String notifySugarForIngestionFailure(String recordId);

}
