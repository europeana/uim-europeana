/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.dedup.osgi.service;

import java.util.List;
import java.util.Map;

import eu.europeana.corelib.tools.lookuptable.LookupState;
import eu.europeana.dedup.osgi.service.exceptions.DeduplicationException;

/**
 * Service for splitting records and assigning unique identifiers
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 27 Sep 2012
 */
public interface DeduplicationService {

	
	/**
	 * Performs a splitting of the incoming record having as a basis the ore:Proxy object and 
	 * assigns unique identifiers
	 * 
	 * @param collectionID
	 * @param sessionid
	 * @param edmRecord
	 * @return
	 * @throws DeduplicationException
	 */
	public List<DeduplicationResult> deduplicateRecord(String collectionID,String sessionid, String edmRecord) throws DeduplicationException;
	
	/**
	 * Gets the failed records for a specific collection
	 * 
	 * @param collectionId
	 * @return
	 */
	public List<Map<String,String>> getFailedRecords(String collectionId);
	
	/**
	 * Retrieves one or more identifiers according to the identifier original value
	 * 
	 * @param oldid
	 * @return
	 */
	public List<String> retrieveEuropeanaIDFromOld(String oldID,String collectionID);
	
	/**
	 * Removes an identifier entry from the ID Registry
	 * 
	 * @param newEuropeanaID
	 */
	public void deleteEuropeanaID(String newEuropeanaID);
	
	
	/**
	 * Creates an entry in the failed records section
	 * 
	 * @param oldEuropeanaID
	 * @param collectionID
	 * @param state
	 */
	public void createUpdateIdStatus(String oldEuropeanaID,String newEuropeanaID,String collectionID,String xml,LookupState state);
	
	
	/**
	 * Deletes a failed record entry
	 * 
	 * @param europeanaId
	 */
	public void deleteFailedRecord(String europeanaId,String collectionID);
	
	
	
}
