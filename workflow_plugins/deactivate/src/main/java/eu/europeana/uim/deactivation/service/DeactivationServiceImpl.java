package eu.europeana.uim.deactivation.service;

import java.net.URL;
import java.net.UnknownHostException;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.europeana.corelib.solr.exceptions.MongoDBException;
import eu.europeana.corelib.solr.server.EdmMongoServer;
import eu.europeana.europeanauim.utils.PropertyReader;
import eu.europeana.europeanauim.utils.UimConfigurationProperty;
import eu.europeana.uim.common.BlockingInitializer;

public class DeactivationServiceImpl implements DeactivationService {

	private static HttpSolrServer solrServer;
	private static ExtendedEdmMongoServer mongoServer;
	private static String mongoDB;
	private static String mongoHost;
	private static String mongoPort;
	private static String solrUrl;
	private static String solrCore;
	
	
	
	public DeactivationServiceImpl(){
		
	}
	
	public HttpSolrServer getSolrServer() {
		return solrServer;
	}

	public ExtendedEdmMongoServer getMongoServer() {
		return mongoServer;
	}

	
	
	public void initialize(String path){
		try {
			if(path!=null){
				PropertyReader.loadPropertiesFromFile(path);
			}
			mongoDB=PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANA);
			mongoHost=PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTURL);
			mongoPort= PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTPORT);
			solrUrl= PropertyReader.getProperty(UimConfigurationProperty.SOLR_HOSTURL);
			solrCore=PropertyReader.getProperty(UimConfigurationProperty.SOLR_CORE);
			
			solrServer = new HttpSolrServer(new URL(solrUrl)+solrCore);
			BlockingInitializer initializer = new BlockingInitializer() {
				
				@Override
				protected void initializeInternal() {
					try {
						mongoServer = new ExtendedEdmMongoServer(new Mongo(mongoHost,Integer.parseInt(mongoPort)), mongoDB, "", "");
						mongoServer.getFullBean("test");
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MongoDBException e) {
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
			initializer.initialize(EdmMongoServer.class.getClassLoader());
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}