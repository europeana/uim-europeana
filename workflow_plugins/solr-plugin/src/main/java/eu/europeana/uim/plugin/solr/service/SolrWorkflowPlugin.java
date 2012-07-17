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

package eu.europeana.uim.plugin.solr.service;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import com.mongodb.MongoException;

import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.uim.api.AbstractIngestionPlugin;
import eu.europeana.uim.api.CorruptedMetadataRecordException;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.europeanaspecific.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.EuropeanaRetrievableField;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.sugarcrm.QueryResultException;
import eu.europeana.uim.sugarcrm.SugarCrmRecord;
import eu.europeana.uim.sugarcrm.SugarCrmService;

/**
 * This is the main class implementing the UIM functionality for the solr
 * workflow plugin exposed as an OSGI service.
 * 
 * @author Georgios Markakis
 * @author Yorgos.Mamakis@ kb.nl
 * 
 */
public class SolrWorkflowPlugin extends AbstractIngestionPlugin {

	private static String solrUrl;
	private static int recordNumber;

	private static SugarCrmService sugarCrmService;
	private static String previewsOnlyInPortal;
	
	private static String collections;
	private static String mongoHost;
	private static int mongoPort;
	private static String mongoDB;
	private static String solrCore;
	


	// GETTERS & SETTERS
	public SugarCrmService getSugarCrmService() {
		return sugarCrmService;
	}

	public void setSugarCrmService(SugarCrmService sugarCrmService) {
		SolrWorkflowPlugin.sugarCrmService = sugarCrmService;
	}

	

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		SolrWorkflowPlugin.solrUrl = solrUrl;
	}

	

	public String getCollections() {
		return collections;
	}

	public void setCollections(String collections) {
		SolrWorkflowPlugin.collections = collections;
	}

	public String getMongoHost() {
		return mongoHost;
	}

	public void setMongoHost(String mongoHost) {
		SolrWorkflowPlugin.mongoHost = mongoHost;
	}

	public int getMongoPort() {
		return mongoPort;
	}

	public void setMongoPort(int mongoPort) {
		SolrWorkflowPlugin.mongoPort = mongoPort;
	}

	public String getMongoDB() {
		return mongoDB;
	}

	public void setMongoDB(String mongoDB) {
		SolrWorkflowPlugin.mongoDB = mongoDB;
	}

	public String getSolrCore() {
		return solrCore;
	}

	public void setSolrCore(String solrCore) {
		SolrWorkflowPlugin.solrCore = solrCore;
	}



	/** Property which allows to overwrite base url from collection/provider */
	public static final String httpzipurl = "http.overwrite.zip.baseUrl";

	/**
	 * The parameters used by this WorkflowStart
	 */
	private static final List<String> params = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(httpzipurl);
		}
	};

	public SolrWorkflowPlugin() {
		super("solr_workflow", "Solr Repository Ingestion Plugin");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.api.IngestionPlugin#processRecord(eu.europeana.uim.
	 * MetaDataRecord, eu.europeana.uim.api.ExecutionContext)
	 */
	public <I> boolean processRecord(MetaDataRecord<I> mdr,
			ExecutionContext<I> context) throws IngestionPluginFailedException,
			CorruptedMetadataRecordException {

		try {
			IBindingFactory bfact = BindingDirectory.getFactory(RDF.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();

			String value = mdr.getValues(EuropeanaModelRegistry.EDMRECORD).get(
					0);
			RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));
			
					
					return true;
				
			

		} catch (JiBXException e) {
			context.getLoggingEngine().logFailed(
					Level.SEVERE,
					this,
					e,
					"JiBX unmarshalling has failed with the following error: "
							+ e.getMessage());
			e.printStackTrace();
		} 
		return false;
	}

	

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public void shutdown() {
		// TODO Auto-generated method stub

	}

	public int getPreferredThreadCount() {
		return 5;
	}

	public int getMaximumThreadCount() {
		return 10;
	}

	public <I> void initialize(ExecutionContext<I> context)
			throws IngestionPluginFailedException {
		

		try {
			
			
			
			@SuppressWarnings("rawtypes")
			Collection collection = (Collection) context.getExecution().getDataSet();
			String sugarCrmId = collection
					.getValue(ControlledVocabularyProxy.SUGARCRMID);

			SugarCrmRecord sugarCrmRecord = sugarCrmService
					.retrieveRecord(sugarCrmId);
			previewsOnlyInPortal = sugarCrmRecord
					.getItemValue(EuropeanaRetrievableField.PREVIEWS_ONLY_IN_PORTAL);

		}  catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public <I> void completed(ExecutionContext<I> context)
			throws IngestionPluginFailedException {
		
	}

	@Override
	public TKey<?, ?>[] getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TKey<?, ?>[] getOptionalFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TKey<?, ?>[] getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getParameters() {
		return params;
	}

	public static int getRecords() {
		return recordNumber;
	}
}
