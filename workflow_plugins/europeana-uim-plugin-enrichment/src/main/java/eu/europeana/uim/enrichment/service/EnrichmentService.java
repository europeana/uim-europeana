package eu.europeana.uim.enrichment.service;

import org.apache.solr.common.SolrInputDocument;

public interface EnrichmentService {

	SolrInputDocument enrich(SolrInputDocument solrDocument) throws Exception;
	
}
