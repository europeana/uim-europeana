package eu.europeana.uim.plugin.solr.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.mapping.DefaultCreator;
import com.hp.hpl.jena.rdf.model.RDFReaderF;
import com.hp.hpl.jena.rdf.model.impl.RDFReaderFImpl;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
import eu.europeana.uim.common.BlockingInitializer;
import eu.europeana.uim.plugin.solr.utils.OsgiExtractor;
import eu.europeana.uim.plugin.solr.utils.PropertyReader;
import eu.europeana.uim.plugin.solr.utils.UimConfigurationProperty;

public class SolrWorkflowServiceImpl implements SolrWorkflowService {
	private static OsgiExtractor extractor;
	private static Datastore datastore;

	public SolrWorkflowServiceImpl() {
		BlockingInitializer datastoreInitializer = new BlockingInitializer() {

			@Override
			protected void initializeInternal() {
			
				if (datastore == null) {
					Morphia morphia = new Morphia();
					 morphia.getMapper().getOptions().setObjectFactory(new DefaultCreator() {
			              @Override
			              protected ClassLoader getClassLoaderForClass(String clazz, DBObject object) {
			                  return MongoBundleActivator.getBundleClassLoader();
			              }
			       });
					morphia.map(ControlledVocabularyImpl.class);
					try {
					  List<ServerAddress> addresses = new ArrayList<ServerAddress>();
					  String[] mongoHost = PropertyReader.getProperty(
					      UimConfigurationProperty.MONGO_HOSTURL).split(",");
			            for (String mongoStr : mongoHost) {
			              ServerAddress address = new ServerAddress(mongoStr, 27017);
			              addresses.add(address);
			          }
			          Mongo tgtMongo = new Mongo(addresses);
						datastore = morphia
								.createDatastore(
										tgtMongo,
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
		
		final OsgiExtractor extractor = new OsgiExtractor();
		BlockingInitializer initializer = new BlockingInitializer() {

			@Override
			protected void initializeInternal() {
//				if(extractor == null){
				
//				}
				extractor.setDatastore(datastore);
				
				
			}
		};
		initializer.initialize(OsgiExtractor.class.getClassLoader());
		
		BlockingInitializer vocInitializer = new BlockingInitializer() {

			@Override
			protected void initializeInternal() {
				// TODO Auto-generated method stub
				List<ControlledVocabularyImpl> vocabularies = extractor.getControlledVocabularies();
				
			}
		};
		vocInitializer.initialize(ControlledVocabularyImpl.class
				.getClassLoader());
		
		BlockingInitializer rdfReaderInitializer = new BlockingInitializer() {
			
			@Override
			protected void initializeInternal() {
				new RDFReaderFImpl();
				
			}
		};
		rdfReaderInitializer.initialize(RDFReaderFImpl.class.getClassLoader());
	}
	
	@Override
	public OsgiExtractor getExtractor() {
		//OsgiExtractor extractor = new OsgiExtractor(this);
		if(extractor==null){
			extractor = new OsgiExtractor(this);
			extractor.setDatastore(datastore);
		}
		
		return extractor;
	}

	@Override
	public Datastore getDatastore() {
		return datastore;
	}

	@Override
	public RDFReaderF getRDFReaderF() {
		return new RDFReaderFImpl();
	}

}
