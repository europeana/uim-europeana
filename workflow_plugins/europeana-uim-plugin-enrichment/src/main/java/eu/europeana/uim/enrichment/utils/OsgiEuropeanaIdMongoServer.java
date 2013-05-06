package eu.europeana.uim.enrichment.utils;

import java.util.List;

import com.google.code.morphia.Morphia;
import com.google.code.morphia.mapping.DefaultCreator;
import com.mongodb.DBObject;
import com.mongodb.Mongo;

import eu.europeana.corelib.tools.lookuptable.EuropeanaId;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;
import eu.europeana.uim.enrichment.MongoBundleActivator;

public class OsgiEuropeanaIdMongoServer extends EuropeanaIdMongoServer {

	public OsgiEuropeanaIdMongoServer(Mongo mongoServer, String databaseName) {
		super(mongoServer,databaseName);
		this.mongoServer = mongoServer;
		this.databaseName = databaseName;		
	}

	@Override
	public void createDatastore(){
		Morphia morphia = new Morphia();
		 morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
             @Override
             protected ClassLoader getClassLoaderForClass(String clazz, DBObject object) {
                 return MongoBundleActivator.getBundleClassLoader();
             }
         });
		 morphia.map(EuropeanaId.class);
		 datastore = morphia.createDatastore(mongoServer, databaseName);
			datastore.ensureIndexes();
			super.setDatastore(datastore);
	}
	@Override
	public List<EuropeanaId> retrieveEuropeanaIdFromOld(String oldId) {
		return datastore.find(EuropeanaId.class).field("oldId").equal(oldId).asList();
	}
}
