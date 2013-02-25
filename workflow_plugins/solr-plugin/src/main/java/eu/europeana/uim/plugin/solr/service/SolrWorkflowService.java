package eu.europeana.uim.plugin.solr.service;

import com.google.code.morphia.Datastore;
import com.hp.hpl.jena.rdf.model.RDFReaderF;

import eu.europeana.uim.plugin.solr.utils.OsgiExtractor;

public interface SolrWorkflowService {

	public OsgiExtractor getExtractor();
	
	public Datastore getDatastore();
	
	public RDFReaderF getRDFReaderF();
}
