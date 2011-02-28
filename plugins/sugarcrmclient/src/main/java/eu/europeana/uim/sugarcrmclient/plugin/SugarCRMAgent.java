package eu.europeana.uim.sugarcrmclient.plugin;

public interface SugarCRMAgent {
	
	public String showConnectionStatus();
	
	public String pollForHarvestInitiators();
	
	public String notifySugarForChanges();

}
