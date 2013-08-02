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
package eu.europeana.uim.enrichment.service.impl;

import java.net.URL;
import java.net.UnknownHostException;

import org.apache.solr.client.solrj.impl.HttpSolrServer;


import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.europeana.corelib.solr.exceptions.MongoDBException;
import eu.europeana.corelib.solr.server.EdmMongoServer;
import eu.europeana.corelib.tools.lookuptable.Collection;
import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;

import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;
import eu.europeana.corelib.tools.lookuptable.impl.CollectionMongoServerImpl;
import eu.europeana.uim.common.BlockingInitializer;
import eu.europeana.uim.enrichment.service.EnrichmentService;
import eu.europeana.uim.enrichment.utils.OsgiEdmMongoServer;
import eu.europeana.uim.enrichment.utils.OsgiEuropeanaIdMongoServer;
import eu.europeana.uim.enrichment.utils.PropertyReader;
import eu.europeana.uim.enrichment.utils.UimConfigurationProperty;

/**
 * See EnrichmentService
 * 
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public class EnrichmentServiceImpl implements EnrichmentService {

	private static HttpSolrServer solrServer;
	private static String mongoDB=PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANA);
	private static String mongoHost=PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTURL);
	private static String mongoPort= PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTPORT);
	private static String solrUrl= PropertyReader.getProperty(UimConfigurationProperty.SOLR_HOSTURL);
	private static String solrCore=PropertyReader.getProperty(UimConfigurationProperty.SOLR_CORE);
	private static CollectionMongoServer cmServer;
	private  static OsgiEuropeanaIdMongoServer idserver;
	private static OsgiEdmMongoServer server;
	public EnrichmentServiceImpl(){
		
		try {
			
		solrServer = new HttpSolrServer(new URL(solrUrl)+solrCore);
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		
		BlockingInitializer initializer = new BlockingInitializer() {
			
			@Override
			protected void initializeInternal() {
				try {
					Morphia morphia = new Morphia();
					morphia.map(Collection.class);
					Datastore datastore = morphia.createDatastore(new Mongo(mongoHost,Integer.parseInt(mongoPort)), "collections");
					cmServer = new CollectionMongoServerImpl();
					datastore.ensureIndexes();
					cmServer.setDatastore(datastore);
					BlockingInitializer colInitializer = new BlockingInitializer() {
						
						@Override
						protected void initializeInternal() {
							Collection col = new Collection();
							cmServer.findOldCollectionId("test");
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
		
		
		
		initializer.initialize(CollectionMongoServerImpl.class.getClassLoader());
		
		
		
					try {
						String uname = PropertyReader
								.getProperty(UimConfigurationProperty.MONGO_USERNAME) != null ? PropertyReader
								.getProperty(UimConfigurationProperty.MONGO_USERNAME) : "";
						String pass = PropertyReader
								.getProperty(UimConfigurationProperty.MONGO_PASSWORD) != null ? PropertyReader
								.getProperty(UimConfigurationProperty.MONGO_PASSWORD) : "";
						server = new OsgiEdmMongoServer(new Mongo(mongoHost,Integer.parseInt(mongoPort)), "europeana", uname,pass);
						server.createDatastore(new Morphia());
						idserver = new OsgiEuropeanaIdMongoServer(new Mongo(mongoHost,Integer.parseInt(mongoPort)), "EuropeanaId");
						idserver.createDatastore();
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MongoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MongoDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
		
	}
	

	

	@Override
	public  HttpSolrServer getSolrServer() {
		return solrServer;
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






	@Override
	public CollectionMongoServer getCollectionMongoServer() {
		return cmServer;
	}

	@Override
	public EuropeanaIdMongoServer getEuropeanaIdMongoServer() {
		return idserver;
	}




	@Override
	public EdmMongoServer getEuropeanaMongoServer() {
		// TODO Auto-generated method stub
		return server;
	}




	

}
