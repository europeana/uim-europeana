package eu.europeana.uim.plugin.solr.service;

import com.google.code.morphia.Datastore;
import com.hp.hpl.jena.rdf.model.RDFReader;
import eu.europeana.uim.plugin.solr.utils.OsgiExtractor;

public interface SolrWorkflowService {

	OsgiExtractor getExtractor();


	 Datastore getDatastore();
	
	 RDFReader getRDFReader();
}
