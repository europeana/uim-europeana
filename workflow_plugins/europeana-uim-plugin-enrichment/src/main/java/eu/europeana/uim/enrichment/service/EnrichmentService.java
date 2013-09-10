/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.enrichment.service;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;
import eu.europeana.uim.enrichment.utils.OsgiEdmMongoServer;


/**
 * Enrichment service, exposing various Storage functionality
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public interface EnrichmentService {

	/**
	 * Get the Solr Server to save the Enriched metadata to
	 * @return
	 */
	HttpSolrServer getSolrServer();

	/**
	 * Get the Mongo database name;
	 * @return
	 */
	String getMongoDB();

	public CollectionMongoServer getCollectionMongoServer();


	public EuropeanaIdMongoServer getEuropeanaIdMongoServer();

	public OsgiEdmMongoServer getEuropeanaMongoServer();
	

	
}
