package eu.europeana.uim.gui.cp.server;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

import eu.europeana.corelib.dereference.VocabularyMongoServer;
import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
import eu.europeana.corelib.dereference.impl.VocabularyMongoServerImpl;



public class OsgiVocabularyMongoServer extends VocabularyMongoServerImpl implements VocabularyMongoServer{
	private Mongo mongoServer;
	private String databaseName;
	private Datastore datastore;
	
	@Override
	public Datastore getDatastore() {
		return this.datastore;
	}

	@Override
	public void close() {
		mongoServer.close();
	}

	
	public OsgiVocabularyMongoServer(Mongo mongoServer, String databaseName) {
		this.mongoServer = mongoServer;
		this.databaseName = databaseName;
		createDatastore();
	}

	private void createDatastore() {

		Morphia morphia = new Morphia();
		morphia.map(ControlledVocabularyImpl.class);
		datastore = morphia.createDatastore(mongoServer, databaseName);
		datastore.ensureIndexes();
	}
}
