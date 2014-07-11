package eu.europeana.uim.deactivation.service;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;
import org.neo4j.rest.graphdb.RestGraphDatabase;

public interface DeactivationService {

	public HttpSolrServer getSolrServer();
	
	public ExtendedEdmMongoServer getMongoServer();
	
	public CollectionMongoServer getCollectionMongoServer();
        
        public RestGraphDatabase getGraphDb();

        public String getNeo4jIndex();
        
	public void initialize();
	
}
