package eu.europeana.europeanauim.publish.service;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

/**
 * Interface exposing the a Solr Server
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public interface PublishService {

	/**
	 * Retrieve the Solr Server to Optimize
	 * @return
	 */
	public HttpSolrServer getSolrServer();
}
