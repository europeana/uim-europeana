
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
import java.util.List;

import org.w3c.dom.Element;

import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModulesResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFieldsResponse;
import eu.europeana.uim.sugarcrmclient.plugin.objects.ConnectionStatus;
import eu.europeana.uim.sugarcrmclient.plugin.objects.SugarCrmRecord;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.FileAttachmentException;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LoginFailureException;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.QueryResultException;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.DatasetStates;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.SugarCrmField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.UpdatableField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.listeners.PollingListener;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.SimpleSugarCrmQuery;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.SugarCrmQuery;


/**
 * This is the main interface for the OSGI based SugarCrm
 * plugin OSGI service.
 *  
 * @author Georgios Markakis
 */
public interface SugarCRMService {
	
	
	/**
	 * This method shows the current connection status of the plugin instance.
	 * @return a ConnectionStatus object
	 */
	public ConnectionStatus showConnectionStatus();
	
	
	/**
	 * This operation 
	 * @return
	 */
	public String updateSession(String username, String password) throws LoginFailureException;
	
	

	
	/**
	 * @param recordId
	 * @param state
	 * @throws QueryResultException
	 */
	public void changeEntryStatus(String recordId,DatasetStates state) throws QueryResultException;
	

	/**
	 * @param recordID
	 * @param values
	 * @throws QueryResultException
	 */
	public void updateRecordData(String recordID, HashMap<UpdatableField,String> values) throws QueryResultException;
	
	
	
	/**
	 * @param record
	 * @throws QueryResultException
	 */
	public void updateRecordData(SugarCrmRecord record) throws QueryResultException;
	
	
	/**
	 * @param query
	 * @return
	 * @throws QueryResultException
	 */
	public List<SugarCrmRecord> retrieveRecords(SugarCrmQuery query) throws QueryResultException;
	
	/**
	 * @param query
	 * @return
	 * @throws QueryResultException
	 */
	public SugarCrmRecord retrieveRecord(String id) throws QueryResultException;
	
	
	/**
	 * @param worklfowName
	 * @param record
	 */
	public void initWorkflowFromRecord(String worklfowName,SugarCrmRecord record,DatasetStates endstate) throws QueryResultException,StorageEngineException;
	
	
	/**
	 * @param worklfowName
	 * @param record
	 */
	public void initWorkflowsFromRecords(String worklfowName,DatasetStates currentstate,DatasetStates endstate) throws QueryResultException,StorageEngineException;
	
	
	/**
	 * @param record
	 * @return
	 * @throws StorageEngineException
	 */
	public Provider<?> createProviderFromRecord(SugarCrmRecord record) throws StorageEngineException;
	

	/**
	 * @param record
	 * @param provider
	 * @return
	 * @throws StorageEngineException
	 */
	public Collection<?> createCollectionFromRecord(SugarCrmRecord record,Provider<?> provider) throws StorageEngineException;
	
	
	/**
	 * @param recordId
	 * @param message
	 * @throws FileAttachmentException
	 */
	public void addNoteAttachmentToRecord(String recordId,String message) throws FileAttachmentException;
	
	
	public void addPollingListener(PollingListener listener);
	
	public void removePollingListener(PollingListener listener);
	
	
	public List<PollingListener> getPollingListeners();
	
	public void setPollingListeners(List<PollingListener> listeners);
}
