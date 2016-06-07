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
package eu.europeana.europeanauim.publish.utils;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

import eu.europeana.corelib.lookup.impl.EuropeanaIdMongoServerImpl;
import eu.europeana.corelib.tools.lookuptable.EuropeanaId;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;
import org.apache.commons.lang.StringUtils;

import java.util.List;

/**
 * TODO: change to reflect the changes in the Interface definition
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public class OsgiEuropeanaIdMongoServer extends EuropeanaIdMongoServerImpl implements EuropeanaIdMongoServer {

	public OsgiEuropeanaIdMongoServer(Mongo mongoServer, String databaseName,String username, String password) {
		super(mongoServer,databaseName,username,password);
		this.mongoServer = mongoServer;
		this.databaseName = databaseName;
		this.username = username;
		this.password = password;
	}
	@Override
	public void createDatastore(){
		Morphia morphia = new Morphia();

		morphia.map(EuropeanaId.class);
		datastore = morphia.createDatastore(mongoServer, databaseName);
		if(StringUtils.isNotBlank(this.username) && StringUtils.isNotBlank(this.password)) {
			datastore.getDB().authenticate(this.username, this.password.toCharArray());
		}
		datastore.ensureIndexes();
		super.setDatastore(datastore);
	}
	@Override
	public EuropeanaId retrieveEuropeanaIdFromOld(String oldId) {
		return datastore.find(EuropeanaId.class).field("oldId").equal(oldId).get();
	}

	@Override
	public List<EuropeanaId> retrieveEuropeanaIdFromNew(String newId) {
		return datastore.find(EuropeanaId.class).field("newId").equal(newId).asList();
	}
}
