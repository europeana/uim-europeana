
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

package eu.europeana.uim.sugarcrmclient.plugin;

import java.util.HashMap;

import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModulesResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFieldsResponse;
import eu.europeana.uim.sugarcrmclient.plugin.objects.ConnectionStatus;


/**
 * This is the main interface for the OSGI based SugarCrm
 * plugin OSGI service.
 *  
 * @author Georgios Markakis
 */
public interface SugarCRMAgent {
	
	
	/**
	 * This method shows the current connection status of the plugin instance.
	 * @return a ConnectionStatus object
	 */
	public ConnectionStatus showConnectionStatus();
	
	
	/**
	 * 
	 * @return
	 */
	public GetAvailableModulesResponse showAvailableModules();
	
	
	
	/**
	 * @param module
	 * @return
	 */
	public GetModuleFieldsResponse showModuleFields(String module);

	
	/**
	 * @return
	 */
	public String updateSession();
	
	
	/**
	 * @return
	 */
	public GetEntryListResponse  pollForHarvestInitiators();
	
	
	/**
	 * @param recordId
	 * @return
	 */
	public String notifySugarForIngestionSuccess(String recordId);
	
	
	
	/**
	 * @param recordId
	 * @return
	 */
	public String notifySugarForIngestionFailure(String recordId);

}
