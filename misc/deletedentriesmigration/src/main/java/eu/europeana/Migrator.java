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
package eu.europeana;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import org.theeuropeanlibrary.model.common.qualifier.Status;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdRegistryMongoServer;
import eu.europeana.corelib.tools.lookuptable.impl.EuropeanaIdRegistryMongoServerImpl;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.mongo.MongoStorageEngine;
import eu.europeana.uim.storage.StorageEngine;
import eu.europeana.uim.storage.StorageEngineException;


/**
 * @author Georgios Markakis (gwarkx@hotmail.com)
 *
 * @since Jan 30, 2014
 */
public class Migrator {

	private static EuropeanaIdRegistryMongoServer  idregistry;
	private static StorageEngine<String> uimstorage;

	
	public Migrator(){
		Mongo mongo = null;
		try {
		   mongo = new Mongo();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		idregistry = new EuropeanaIdRegistryMongoServerImpl(mongo, "EuropeanaIdRegistry");
		uimstorage = new MongoStorageEngine();
		uimstorage.initialize();
	}
	
	/**
	 * @throws StorageEngineException
	 */
	public void populateDeleteStatus() throws StorageEngineException{
		
		// Get all collections from storage engine
		ArrayList<Collection<String>> collidlist = (ArrayList<Collection<String>>) uimstorage.getAllCollections();
		
		for(Collection<String> coll :collidlist){
			System.out.println("Processing Collection:" + coll.getMnemonic());
		    // For each collection get the recordIDs
			String recids[] = uimstorage.getByCollection(coll);
			
			for(int i=0; i < recids.length; i++){
				// For each record ID fetch the record
				MetaDataRecord<String> mdr = uimstorage.getMetaDataRecord(recids[i]);
				System.out.println("Processing Record:" + mdr.getId() + ":");
				// From the Typed Keys included in the record get the "status" label
				List<Status> statuslist = mdr.getValues(EuropeanaModelRegistry.STATUS);
				
				Status status = null;
				for(Status st : statuslist){
					if(st.equals(Status.DELETED)){
						status = Status.DELETED;
					}
				}
				
				// If the status is deleted
				if (status.equals(Status.DELETED)) {
					// Get the record ID and lookup the entry in the EuropeanaIDRegistry
					// And set the deleted column value to true
					idregistry.markdeleted(mdr.getId(), true);
					System.out.print("(deleted)");
				}
				else{
					// Else set to false.
					idregistry.markdeleted(mdr.getId(), false);
					System.out.print("(not deleted)");
				}
			}
		}
	}
	
}
