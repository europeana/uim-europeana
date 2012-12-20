package eu.europeana.uim.enrichment.service;

import java.util.List;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import eu.annocultor.converters.europeana.Entity;
import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;


public interface EnrichmentService {

	List<Entity> enrich(SolrInputDocument solrDocument) throws Exception;


	HttpSolrServer getSolrServer();

	String getMongoDB();
	
	public HttpSolrServer getSuggestionServer();

	public CollectionMongoServer getCollectionMongoServer();
}
