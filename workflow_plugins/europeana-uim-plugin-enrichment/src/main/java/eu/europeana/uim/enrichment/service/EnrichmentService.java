package eu.europeana.uim.enrichment.service;

import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import eu.annocultor.converters.europeana.Entity;
import eu.europeana.corelib.definitions.solr.beans.FullBean;
import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;
import eu.europeana.corelib.tools.lookuptable.EuropeanaId;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;


public interface EnrichmentService {

	//List<Entity> enrich(SolrInputDocument solrDocument) throws Exception;


	HttpSolrServer getSolrServer();

	String getMongoDB();
	
	public HttpSolrServer getMigrationServer();

	public CollectionMongoServer getCollectionMongoServer();


	public EuropeanaIdMongoServer getEuropeanaIdMongoServer();


	List<EuropeanaId> retrieveEuropeanaIdFromOld(String string);


	void saveEuropeanaId(EuropeanaId europeanaId);


	void createLookupEntry(FullBean fullBean, String collectionId, String hash);


	
}
