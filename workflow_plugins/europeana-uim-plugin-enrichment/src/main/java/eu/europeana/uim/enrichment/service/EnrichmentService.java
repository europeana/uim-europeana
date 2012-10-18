package eu.europeana.uim.enrichment.service;

import java.util.List;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;


import eu.annocultor.converters.europeana.Entity;


public interface EnrichmentService {

	List<Entity> enrich(SolrInputDocument solrDocument) throws Exception;


	CommonsHttpSolrServer getSolrServer();

	String getMongoDB();
	
	public CommonsHttpSolrServer getSuggestionServer();

}
