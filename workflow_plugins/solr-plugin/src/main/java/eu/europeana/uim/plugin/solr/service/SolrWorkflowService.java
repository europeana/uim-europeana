package eu.europeana.uim.plugin.solr.service;

import com.google.code.morphia.Datastore;

import eu.europeana.uim.plugin.solr.utils.OsgiExtractor;

public interface SolrWorkflowService {

	public OsgiExtractor getExtractor();
	
	public Datastore getDatastore();
}
