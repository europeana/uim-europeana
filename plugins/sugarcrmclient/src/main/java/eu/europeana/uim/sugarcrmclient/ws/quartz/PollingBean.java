/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.sugarcrmclient.ws.quartz;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import eu.europeana.uim.sugarcrm.GenericSugarCrmException;
import eu.europeana.uim.sugarcrm.PollingListener;
import eu.europeana.uim.sugarcrm.SugarCrmRecord;
import eu.europeana.uim.sugarcrm.SugarCrmService;


/**
 * This Class implements the Quartz-based polling mechanism for sugarcrm
 * plugin.
 *   
 * @author Georgios Markakis
 */
public class PollingBean extends QuartzJobBean {

	  private SugarCrmService sugarcrmPlugin;
	  
	  /**
	   * Setter for sugarcrmPlugin spring injected property
	   */ 
	  public void setSugarcrmPlugin(SugarCrmService sugarcrmPlugin) {
	    this.sugarcrmPlugin = sugarcrmPlugin;
	  }	
	
	
	/* (non-Javadoc)
	 * @see org.springframework.scheduling.quartz.QuartzJobBean#executeInternal(org.quartz.JobExecutionContext)
	 */
	@Override
	protected void executeInternal(JobExecutionContext arg0)
			throws JobExecutionException {

		    LinkedHashMap<String,PollingListener>  pollingListeners = sugarcrmPlugin.getPollingListeners();
		    Iterator itr = pollingListeners.keySet().iterator();
		    
			if(pollingListeners != null){
				
				while(itr.hasNext()){
					
					try {
						PollingListener listener = pollingListeners.get(itr.next());
						List<SugarCrmRecord> results = sugarcrmPlugin.retrieveRecords(listener.getTrigger());
						
						listener.performAction(sugarcrmPlugin, results);
						
					} catch (GenericSugarCrmException e) {

						e.printStackTrace();
					}
				}
			}
			
			



	}

}
