package eu.europeana.uim.enrichment.service.impl;

import java.net.URL;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.annocultor.converters.europeana.Entity;
import eu.europeana.corelib.definitions.solr.beans.FullBean;
import eu.europeana.corelib.tools.lookuptable.Collection;
import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;
import eu.europeana.corelib.tools.lookuptable.EuropeanaId;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;
import eu.europeana.uim.common.BlockingInitializer;
import eu.europeana.uim.enrichment.service.EnrichmentService;
import eu.europeana.uim.enrichment.utils.EuropeanaEnrichmentTagger;
import eu.europeana.uim.enrichment.utils.PropertyReader;
import eu.europeana.uim.enrichment.utils.UimConfigurationProperty;

public class EnrichmentServiceImpl implements EnrichmentService {

	private final static String PORTALURL = "http://www.europeana.eu/portal/record";
	public static EuropeanaEnrichmentTagger tagger;
	private static HttpSolrServer solrServer;
	private static HttpSolrServer migrationServer;
	private static String mongoDB=PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANA);
	private static String mongoHost=PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTURL);
	private static String mongoPort= PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTPORT);
	private static String solrUrl= PropertyReader.getProperty(UimConfigurationProperty.SOLR_HOSTURL);
	private static String solrCore=PropertyReader.getProperty(UimConfigurationProperty.SOLR_CORE);
	private static String solrCoreMigration = PropertyReader.getProperty(UimConfigurationProperty.SOLR_CORE_MIGRATION);
	private static CollectionMongoServer cmServer;
	private  static EuropeanaIdMongoServer idserver;
	public EnrichmentServiceImpl(){
		tagger = new EuropeanaEnrichmentTagger();
		try {
		solrServer = new HttpSolrServer(new URL(solrUrl)+solrCore);
		migrationServer = new HttpSolrServer(new URL(solrUrl)+solrCoreMigration);
			tagger.init("Europeana");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
		BlockingInitializer initializer = new BlockingInitializer() {
			
			@Override
			protected void initializeInternal() {
				try {
					Morphia morphia = new Morphia();
					morphia.map(Collection.class);
					Datastore datastore = morphia.createDatastore(new Mongo(mongoHost,Integer.parseInt(mongoPort)), "collections");
					cmServer = new CollectionMongoServer();
					datastore.ensureIndexes();
					cmServer.setDatastore(datastore);
					BlockingInitializer colInitializer = new BlockingInitializer() {
						
						@Override
						protected void initializeInternal() {
							Collection col = new Collection();
							
						}
					};
					colInitializer.initialize(Collection.class.getClassLoader());
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
		initializer.initialize(CollectionMongoServer.class.getClassLoader());
		
		
		BlockingInitializer idInitializer = new BlockingInitializer() {
			
			@Override
			protected void initializeInternal() {
				// TODO Auto-generated method stub
				
				
					
				
				try {
					Morphia morphia = new Morphia();
					morphia.map(EuropeanaId.class);
					Datastore datastore = morphia.createDatastore(new Mongo(mongoHost,Integer.parseInt(mongoPort)), "collections");
					datastore.ensureIndexes();
					idserver = new EuropeanaIdMongoServer(new Mongo(mongoHost,Integer.parseInt(mongoPort)), "EuropeanaId");
					idserver.setDatastore(datastore);
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
				
				//idserver.createDatastore();
				
				
				
				}
		};
		idInitializer.initialize(EuropeanaIdMongoServer.class.getClassLoader());
		BlockingInitializer t = new BlockingInitializer() {
			
			@Override
			protected void initializeInternal() {
				// TODO Auto-generated method stub
				EuropeanaId id=idserver.find();
				System.out.println(id.getId().toString());
				boolean test= idserver.retrieveEuropeanaIdFromOld("test")!=null;
			}
		};
		t.initialize(EuropeanaId.class.getClassLoader());
	}
	

	

	@Override
	public  HttpSolrServer getSolrServer() {
		return solrServer;
	}

	

	@Override
	public List<Entity> enrich(SolrInputDocument solrDocument) throws Exception {
		return tagger.tagDocument(solrDocument);
	}
	@Override
	public String getMongoDB() {
		return mongoDB;
	}

	public void setMongoDB(String mongoDB) {
		EnrichmentServiceImpl.mongoDB = mongoDB;
	}

	public  String getMongoHost() {
		return mongoHost;
	}

	public void setMongoHost(String mongoHost) {
		EnrichmentServiceImpl.mongoHost = mongoHost;
	}

	public String getMongoPort() {
		return mongoPort;
	}

	public void setMongoPort(String mongoPort) {
		EnrichmentServiceImpl.mongoPort = mongoPort;
	}

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		EnrichmentServiceImpl.solrUrl = solrUrl;
	}

	public String getSolrCore() {
		return solrCore;
	}

	public void setSolrCore(String solrCore) {
		EnrichmentServiceImpl.solrCore = solrCore;
	}

	

	

	public void setSolrServer(HttpSolrServer solrServer) {
		EnrichmentServiceImpl.solrServer = solrServer;
	}

	public  EuropeanaEnrichmentTagger getTagger() {
		return tagger;
	}

	public void setTagger(EuropeanaEnrichmentTagger tagger) {
		EnrichmentServiceImpl.tagger = tagger;
	}

	@Override
	public HttpSolrServer getMigrationServer(){
		return EnrichmentServiceImpl.migrationServer;
	}




	@Override
	public CollectionMongoServer getCollectionMongoServer() {
		return cmServer;
	}

	@Override
	public EuropeanaIdMongoServer getEuropeanaIdMongoServer() {
		return idserver;
	}




	@Override
	public List<EuropeanaId> retrieveEuropeanaIdFromOld(String string) {
		
		return idserver.retrieveEuropeanaIdFromOld(string);
	}




	@Override
	public void saveEuropeanaId(EuropeanaId europeanaId) {
		idserver.saveEuropeanaId(europeanaId);
		
	}
	
	@Override
	public void createLookupEntry(FullBean fullBean, String collectionId, String hash) {
		
				
		EuropeanaId europeanaId = idserver.getDatastore().find(EuropeanaId.class,"oldId",PORTALURL+"/"+collectionId+"/"+hash).get();
		if (europeanaId==null){
			europeanaId = new EuropeanaId();
			europeanaId.setOldId(PORTALURL+"/"+collectionId+"/"+hash);
			europeanaId.setTimestamp(new Date().getTime());
		} 
		europeanaId.setNewId(fullBean.getAbout());
		saveEuropeanaId(europeanaId);

	}

}
