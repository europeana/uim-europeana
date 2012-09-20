package eu.europeana.uim.enrichment.service;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.mongodb.Mongo;


public interface EnrichmentService {

	SolrInputDocument enrich(SolrInputDocument solrDocument) throws Exception;

//	Mongo getMongo();

	CommonsHttpSolrServer getSolrServer();

	String getMongoDB();

	
}
