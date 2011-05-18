/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.plugin.objects.listeners;

import java.util.List;

import eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService;
import eu.europeana.uim.sugarcrmclient.plugin.objects.SugarCrmRecord;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.DatasetStates;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.GenericSugarCRMException;

/**
 * @author Georgios Markakis
 *
 */
public interface PollingListener {
	
	public DatasetStates getTrigger();
	
	public void performAction(SugarCRMService pluginReference, 
			List<SugarCrmRecord> retrievedRecords) throws GenericSugarCRMException;

}
