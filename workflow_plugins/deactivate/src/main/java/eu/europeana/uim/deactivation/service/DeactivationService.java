package eu.europeana.uim.deactivation.service;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;

public interface DeactivationService {

	public HttpSolrServer getSolrServer();
	
	public ExtendedEdmMongoServer getMongoServer();
	
	public CollectionMongoServer getCollectionMongoServer();

	public void initialize();
	
}
