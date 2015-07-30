package eu.europeana.europeanauim.publish.service;

import eu.europeana.corelib.mongo.server.EdmMongoServer;
import eu.europeana.europeanauim.publish.OsgiEdmMongoServer;
import org.apache.solr.client.solrj.SolrServer;

import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;

/**
 * Interface exposing the a Solr Server
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public interface PublishService {

	OsgiEdmMongoServer getMongoIngestion();

	OsgiEdmMongoServer getMongoProduction();

	/**
	 * Retrieve the Solr Server to Optimize
	 * @return
	 */
	SolrServer getSolrServer();
	EuropeanaIdMongoServer getEuropeanaIdMongoServer();
	EuropeanaIdMongoServer getEuropeanaIdMongoServerProduction();
	SolrServer getSolrIngestionServer();
}
