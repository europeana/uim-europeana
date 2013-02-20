package eu.europeana.uim.plugin.solr.service;

import java.net.UnknownHostException;
import java.util.List;

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
//	private static OsgiExtractor extractor;
	private static Datastore datastore;

	public SolrWorkflowServiceImpl() {
		BlockingInitializer datastoreInitializer = new BlockingInitializer() {

			@Override
			protected void initializeInternal() {
			
				if (datastore == null) {
					Morphia morphia = new Morphia();
					morphia.map(ControlledVocabularyImpl.class);
					try {
						datastore = morphia
								.createDatastore(
										new Mongo(
												PropertyReader
														.getProperty(UimConfigurationProperty.MONGO_HOSTURL),
												Integer.parseInt(PropertyReader
														.getProperty(UimConfigurationProperty.MONGO_HOSTPORT))),
										PropertyReader
												.getProperty(UimConfigurationProperty.MONGO_DB_VOCABULARY));
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
					datastore.ensureIndexes();
				}

			}
		};
		datastoreInitializer.initialize(Datastore.class.getClassLoader());
		
		
		BlockingInitializer initializer = new BlockingInitializer() {

			@Override
			protected void initializeInternal() {
//				if(extractor == null){
					OsgiExtractor extractor = new OsgiExtractor();
//				}
				extractor.setDatastore(datastore);
				BlockingInitializer vocInitializer = new BlockingInitializer() {

					@Override
					protected void initializeInternal() {
						// TODO Auto-generated method stub
						ControlledVocabularyImpl voc = new ControlledVocabularyImpl();
						
					}
				};
				vocInitializer.initialize(ControlledVocabularyImpl.class
						.getClassLoader());
			}
		};
		initializer.initialize(OsgiExtractor.class.getClassLoader());
		
		
	}
	
	@Override
	public OsgiExtractor getExtractor() {
		OsgiExtractor extractor = new OsgiExtractor();
		extractor.setDatastore(datastore);
		return extractor;
	}

	@Override
	public Datastore getDatastore() {
		return datastore;
	}

}
