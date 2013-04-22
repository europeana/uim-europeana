package eu.europeana.uim.deactivation.service;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

public interface DeactivationService {

	public HttpSolrServer getSolrServer();
	
	public ExtendedEdmMongoServer getMongoServer();

	public void initialize();
	
}
