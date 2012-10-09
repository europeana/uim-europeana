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

import eu.europeana.corelib.tools.lookuptable.EuropeanaIdRegistryMongoServer;
import eu.europeana.corelib.tools.lookuptable.FailedRecord;
import eu.europeana.dedup.osgi.service.exceptions.DeduplicationException;

/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 27 Sep 2012
 */
public interface DeduplicationService {

	
	/**
	 * @param collectionID
	 * @param sessionid
	 * @param edmRecord
	 * @return
	 * @throws DeduplicationException
	 */
	public List<DeduplicationResult> deduplicateRecord(String collectionID,String sessionid, String edmRecord) throws DeduplicationException;
	
	public List<FailedRecord> getFailedRecords(String collectionId);
}
