/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.ws.quartz;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;
import eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgentImpl;
import eu.europeana.uim.sugarcrmclient.plugin.SugarCRMAgent;


/**
 * @author georgiosmarkakis
 *
 */
public class PollingBean extends QuartzJobBean {

	  private SugarCRMAgentImpl sugarcrmPlugin;
	  
	  /**
	   * Setter called after the ExampleJob is instantiated
	   * with the value from the JobDetailBean (5)
	   */ 
	  public void setSugarcrmPlugin(SugarCRMAgentImpl sugarcrmPlugin) {
	    this.sugarcrmPlugin = sugarcrmPlugin;
	  }	
	
	
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {

 		System.out.println("Executing");
		sugarcrmPlugin.pollForHarvestInitiators();
	}

}
