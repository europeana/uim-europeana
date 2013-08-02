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
package eu.europeana.uim.enrichment.utils;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

import eu.europeana.corelib.tools.lookuptable.EuropeanaId;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;
import eu.europeana.corelib.tools.lookuptable.impl.EuropeanaIdMongoServerImpl;

/**
 * TODO: change to reflect the changes in the Interface definition
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public class OsgiEuropeanaIdMongoServer extends EuropeanaIdMongoServerImpl implements EuropeanaIdMongoServer {

	public OsgiEuropeanaIdMongoServer(Mongo mongoServer, String databaseName) {
		super(mongoServer,databaseName,"","");
		this.mongoServer = mongoServer;
		this.databaseName = databaseName;		
	}

	@Override
	public void createDatastore(){
		Morphia morphia = new Morphia();
		
		 morphia.map(EuropeanaId.class);
		 datastore = morphia.createDatastore(mongoServer, databaseName);
			datastore.ensureIndexes();
			super.setDatastore(datastore);
	}
	@Override
	public EuropeanaId retrieveEuropeanaIdFromOld(String oldId) {
		return datastore.find(EuropeanaId.class).field("oldId").equal(oldId).get();
	}
}
