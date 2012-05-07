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

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrException;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.solr.beans.FullBean;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.denormalization.impl.VocabularyMongoServer;
import eu.europeana.corelib.solr.exceptions.MongoDBException;
import eu.europeana.corelib.solr.server.EdmMongoServer;
import eu.europeana.corelib.solr.server.impl.EdmMongoServerImpl;
import eu.europeana.corelib.solr.server.importer.util.Dereferencer;
import eu.europeana.corelib.solr.utils.MongoConstructor;
import eu.europeana.corelib.solr.utils.MongoUtils;
import eu.europeana.corelib.solr.utils.SolrConstructor;
import eu.europeana.uim.api.AbstractIngestionPlugin;
import eu.europeana.uim.api.CorruptedMetadataRecordException;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.europeanaspecific.EuropeanaModelRegistry;
import eu.europeana.uim.solr3.Solr3Initializer;
import eu.europeana.uim.store.MetaDataRecord;

/**
 * This is the main class implementing the UIM functionality for the solr
 * workflow plugin exposed as an OSGI service.
 * 
 * @author Georgios Markakis
 * @author Yorgos.Mamakis@ kb.nl
 * 
 */
public class SolrWorkflowPlugin extends AbstractIngestionPlugin {

	// @Value("#{europeanaProperties['solr.selectUrl']}")
	private static String solrUrl = "http://127.0.0.1:8282/";
	private static Mongo mongo;
	private static SolrServer solrServer;
	private static EdmMongoServer mongoServer;
	private static int recordNumber;
	private static final int RETRIES=10;
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
			MongoConstructor mongoConstructor = new MongoConstructor();
			RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));
			mongoConstructor.setMongoServer(mongoServer);
			FullBean fullBean = mongoConstructor.constructFullBean(rdf);

			if (mongoServer.getDatastore().find(FullBeanImpl.class)
					.filter("about", fullBean.getAbout()) != null) {
				MongoUtils.updateFullBean(fullBean, mongoServer);
			} else {
				mongoServer.getDatastore().save(fullBean);
			}

			
			int retries=0;
			while(retries<RETRIES ){
			try {
				solrServer.add(SolrConstructor.constructSolrDocument(rdf),1000);
				retries = RETRIES;
				recordNumber++;
				return true;
			} catch (SolrServerException e) {
				retries++;
			} catch (IOException e) {
				retries++;
			}
			catch(SolrException e){
				retries++;
			}
			}
			
			
		} catch (JiBXException e) {
			context.getLoggingEngine().logFailed(
					Level.SEVERE,
					this,
					e,
					"JiBX unmarshalling has failed with the following error: "
							+ e.getMessage());
			e.printStackTrace();
		} catch (InstantiationException e) {
			context.getLoggingEngine().logFailed(Level.SEVERE, this, e,
					"Unkwown error: " + e.getMessage());

		} catch (IllegalAccessException e) {
			context.getLoggingEngine().logFailed(Level.SEVERE, this, e,
					"Unknown error: " + e.getMessage());
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		Solr3Initializer solr3Initializer = new Solr3Initializer(solrUrl,
				"apache-solr-3.5.0");
		solr3Initializer.run();
		solrServer = solr3Initializer.getServer();
		
		try {
			mongo = new Mongo("127.0.0.1", 27017);
			mongoServer = new EdmMongoServerImpl(mongo,"europeana");
			mongoServer.getDatastore();
			
			Dereferencer.setServer(new VocabularyMongoServer(mongo, "vocabulary"));
			
		} catch (MongoDBException e) {
			context.getLoggingEngine().logFailed(Level.SEVERE, this, e,
					"Mongo DB server error: " + e.getMessage());
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public <I> void completed(ExecutionContext<I> context)
			throws IngestionPluginFailedException {
		try {
			solrServer.commit();
			solrServer.optimize();

		} catch (IOException e) {
			context.getLoggingEngine().logFailed(
					Level.SEVERE,
					this,
					e,
					"Input/Output exception occured in Solr with the following message: "
							+ e.getMessage());
		} catch (SolrServerException e) {
			context.getLoggingEngine().logFailed(
					Level.SEVERE,
					this,
					e,
					"Solr server exception occured in Solr with the following message: "
							+ e.getMessage());
		}
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
