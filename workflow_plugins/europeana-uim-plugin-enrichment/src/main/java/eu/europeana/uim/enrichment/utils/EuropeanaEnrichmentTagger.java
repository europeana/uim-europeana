package eu.europeana.uim.enrichment.utils;

import org.apache.solr.common.SolrInputDocument;

import eu.annocultor.converters.solr.BuiltinSolrDocumentTagger;

public class EuropeanaEnrichmentTagger extends BuiltinSolrDocumentTagger {

	@Override
	public boolean shouldReplicateThisField(String fieldName) {
		 if (fieldName.startsWith("europeana_")) {
	            return false;
	        }
	        return fieldName.contains("_") || fieldName.equalsIgnoreCase("timestamp");
	}

	@Override
	public void preProcess(SolrInputDocument document, String id) {
		// TODO Auto-generated method stub

	}

	
}
