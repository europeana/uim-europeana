package eu.europeana.uim.plugin.solr.service;

import java.net.UnknownHostException;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
import eu.europeana.uim.common.BlockingInitializer;
import eu.europeana.uim.plugin.solr.utils.OsgiExtractor;
import eu.europeana.uim.plugin.solr.utils.PropertyReader;
import eu.europeana.uim.plugin.solr.utils.UimConfigurationProperty;

public class SolrWorkflowServiceImpl implements SolrWorkflowService {
	private static OsgiExtractor extractor;
	private static Datastore datastore;
	@Override
	public OsgiExtractor getExtractor() {
		
		
		BlockingInitializer initializer = new BlockingInitializer() {
			
			@Override
			protected void initializeInternal() {
				Morphia morphia = new Morphia();
				morphia.map(ControlledVocabularyImpl.class);
				
				try {
					if(datastore==null){
					datastore = morphia
							.createDatastore(
									new Mongo(
											PropertyReader
													.getProperty(UimConfigurationProperty.MONGO_HOSTURL),
											Integer.parseInt(PropertyReader
													.getProperty(UimConfigurationProperty.MONGO_HOSTPORT))),
									PropertyReader
											.getProperty(UimConfigurationProperty.MONGO_DB_VOCABULARY));
					datastore.ensureIndexes();
					}
					extractor = OsgiExtractor.getInstance(datastore);
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (UnknownHostException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (MongoException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
		};
		initializer.initialize(OsgiExtractor.class.getClassLoader());
		return extractor;
	}
	@Override
	public Datastore getDatastore() {
		return datastore;
	}

}
