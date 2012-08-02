package eu.europeana.uim.enrichment.service.impl;

import org.apache.solr.common.SolrInputDocument;

import eu.europeana.uim.enrichment.service.EnrichmentService;
import eu.europeana.uim.enrichment.utils.EuropeanaEnrichmentTagger;

public class EnrichmentServiceImpl implements EnrichmentService {

	public static EuropeanaEnrichmentTagger tagger;
	
	public EnrichmentServiceImpl(){
		tagger = new EuropeanaEnrichmentTagger();
		try {
			tagger.init("Europeana");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public SolrInputDocument enrich(SolrInputDocument solrDocument) throws Exception {
		return tagger.tagDocument(solrDocument);
	}

	public  EuropeanaEnrichmentTagger getTagger() {
		return tagger;
	}

	public void setTagger(EuropeanaEnrichmentTagger tagger) {
		EnrichmentServiceImpl.tagger = tagger;
	}

}
