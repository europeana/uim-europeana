package eu.europeana.europeanauim.publish.service;

import org.apache.solr.client.solrj.SolrServer;

import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;

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
	public SolrServer getSolrServer();
	public EuropeanaIdMongoServer getEuropeanaIdMongoServer();
	public EuropeanaIdMongoServer getEuropeanaIdMongoServerProduction();
}
