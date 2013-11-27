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
package eu.europeana.uim.europeanaspecific.workflowstarts.httpzip;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.theeuropeanlibrary.model.common.qualifier.Status;

import eu.europeana.dedup.osgi.service.DeduplicationService;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.storage.StorageEngine;
import eu.europeana.uim.storage.StorageEngineException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.logging.LoggingEngine;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.ControlledVocabularyKeyValue;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.UimDataSet;
import eu.europeana.uim.store.mongo.MongoStorageEngine;
import eu.europeana.uim.plugin.source.AbstractWorkflowStart;
import eu.europeana.uim.plugin.source.Task;
import eu.europeana.uim.plugin.source.TaskCreator;
import eu.europeana.uim.plugin.source.WorkflowStartFailedException;

/**
 * Worflow start used in the current Mint implementation. It retrieves data from
 * a remote zip location.
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 5 Mar 2012
 */
public class HttpZipWorkflowStart<I> extends AbstractWorkflowStart<MetaDataRecord<I>, I> {

	
	/**
	 * Static Logger reference
	 */
	private static Logger LOGGER = Logger.getLogger(HttpZipWorkflowStart.class.getName());
	
	
	/**
	 * The deduplication service reference (null if not available)
	 */
	DeduplicationService dedup;
	
	/** Property which allows to overwrite base url from collection/provider */
	public static final String httpzipurl = "http.overwrite.zip.baseUrl";
	
	/** Property which forces records tobe overwritten even if identical  */
	public static final String importidenticals = "http.overwrite.even.if.identical";

	/**
	 * The parameters used by this WorkflowStart
	 */
	private static final List<String> params = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(httpzipurl);
			add(importidenticals);
		}
	};

	/**
	 * TKEY used for storing the Data class in the execution context
	 */
	protected static TKey<HttpZipWorkflowStart, Data> DATA_KEY = TKey.register(
			HttpZipWorkflowStart.class, "data", Data.class);

	/**
	 * Private static class used as a container
	 * 
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 5 Mar 2012
	 */
	protected final static class Data implements Serializable {

		private static final long serialVersionUID = 1L;

		public ZipLoader loader;
		public Request<?> request;

		public int maxrecords = 0;
		public int expected = 0;
		public boolean isNew=true;
		public Set<String> deletioncandidates;
	}

	/**
	 * Default constructor
	 * 
	 * @param name
	 *            workflow name
	 * @param description
	 *            workflow description
	 */
	public HttpZipWorkflowStart(String name, String description) {
		super(name, description);
	}

	
	/**
	 * Default constructor
	 * 
	 * @param name
	 *            workflow name
	 * @param description
	 *            workflow description
	 */
	public HttpZipWorkflowStart(String name, String description,DeduplicationService dedup) {
		super(name, description);
		this.dedup = dedup;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.workflow.WorkflowStart#getParameters()
	 */
	@Override
	public List<String> getParameters() {
		return params;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.workflow.WorkflowStart#getPreferredThreadCount()
	 */
	@Override
	public int getPreferredThreadCount() {
		return 5;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.workflow.WorkflowStart#getMaximumThreadCount()
	 */
	@Override
	public int getMaximumThreadCount() {
		return 15;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#createLoader(eu.europeana.uim
	 * .api.ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public TaskCreator<MetaDataRecord<I>, I> createLoader(final ExecutionContext<MetaDataRecord<I>, I> context) throws WorkflowStartFailedException {

		final Data value = context.getValue(DATA_KEY);

		if (!value.loader.isFinished()) {
			return new TaskCreator<MetaDataRecord<I>, I>() {
				@Override
				public void run() {
					try {
						List<MetaDataRecord<I>> list = value.loader.doNext(100,
								false);

						for (MetaDataRecord<I> mdr : list) {
							
							Task<MetaDataRecord<I>, I> task = new Task<MetaDataRecord<I>, I>(mdr,context);
							synchronized (getQueue()) {
								getQueue().offer(task);
							}
						}

					} catch (Throwable t) {
						throw new RuntimeException(
								"Failed to retrieve MDRs from storage. "
										+ context.getExecution().toString(), t);
					} finally {
						setDone(true);
					}
				}
			};
		}

		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#isFinished(eu.europeana.uim.api
	 * .ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public boolean isFinished(ExecutionContext<MetaDataRecord<I>, I> context) {
		Data value = context.getValue(DATA_KEY);
		boolean finished = value== null? true:value.loader.isFinished();
		
		if(finished){			
			Execution<I> execution = context.getExecution();
			execution.putValue("Created", Integer.toString(value.loader.getCreated()));		
			execution.putValue("Updated", Integer.toString(value.loader.getUpdated()));		
			execution.putValue("Omitted", Integer.toString(value.loader.getOmitted()));			
			execution.putValue("Generated", Integer.toString(value.loader.getGenerated()));			
			execution.putValue("Discarded", Integer.toString(value.loader.getDiscarded()));			
			try {
				context.getStorageEngine().updateExecution(execution);
			} catch (StorageEngineException e) {
				context.getLoggingEngine().log(context.getExecution(), Level.SEVERE, "HttpZipWorkflowStart", "Error updating execution " +
						" with deleted records information due to UIM storage engine failure " + e.getMessage());
			}
		}
		
		return finished;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#initialize(eu.europeana.uim.api
	 * .ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public void initialize(ExecutionContext<MetaDataRecord<I>, I> context) throws WorkflowStartFailedException {
		
		StorageEngine<I> storage = context.getStorageEngine();
		UimDataSet<I> dataset = context.getDataSet();
		
		context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Initializing import from MINT for collection:" +
				dataset.getId());
				
		if (dataset instanceof Collection) {

			Collection<I> collection = (Collection<I>) dataset;
			Data value = new Data();
			
			value.deletioncandidates = Collections.synchronizedSet(new HashSet<String>());
			
			try {
				I[] availableMDRs = storage.getByCollection(collection);
				if(availableMDRs.length>0){
					value.isNew = false;
				}
				for(int i=0; i<availableMDRs.length; i++){
					value.deletioncandidates.add((String) availableMDRs[i]);
				}
			} catch (StorageEngineException e) {
				context.getLoggingEngine().log(context.getExecution(), Level.SEVERE, "HttpZipWorkflowStart", "Error seting deletion " +
						"candidates on HttpZipWorkflowStart:" + e.getMessage());
			}

			URL url = null;
			
			String httpzipurlprop = context.getProperties().getProperty(
					httpzipurl);
			
			String forceupdate = context.getProperties().getProperty(
					importidenticals);
			

		    context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Using update identical records value:" + forceupdate);


			
			
			try {

				if (httpzipurlprop != null) {
					url = new URL(httpzipurlprop);
					context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Location of TAR.GZIP data (overriden by user): " +
							httpzipurlprop);
				} else {
					url = new URL(
							collection
									.getValue(ControlledVocabularyProxy.MINTPUBLICATIONLOCATION));
					context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Location of TAR.GZIP data (set by MINT): " +
							httpzipurlprop);
				}
				
				//If the zip location of the data is not found then throw an exception
				if(url == null){
					context.getLoggingEngine().log(context.getExecution(), Level.SEVERE, "HttpZipWorkflowStart", "The zip location of the data " +
							"is not defined. Try importing the dataset from MINT or setting the 'http.overwrite.zip.baseUrl' value on the workflow start");
					throw new WorkflowStartFailedException("The zip location of the data is not defined.");
				}

				Request<I> request = storage.createRequest(collection,new Date());

				HttpRetriever retriever = new HttpRetriever().createInstance(url);

				context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Initializing Duplicate Identifier Detection:");
				
				ZipLoader<I> loader = new ZipLoader<I>(retriever.getNumber_of_recs(),
						retriever,context,request,dedup,forceupdate);

				value.loader = loader;

				context.putValue(DATA_KEY, value);

			} catch (MalformedURLException e) {
				context.getLoggingEngine().log(context.getExecution(), Level.SEVERE, "HttpZipWorkflowStart", "Error accessing URL exposed by MINT: " +
						e.getMessage());
				throw new WorkflowStartFailedException("HttpZipWorkflowStart:Error accessing URL exposed by MINT ",e);
			} catch (IOException e) {
				context.getLoggingEngine().log(context.getExecution(), Level.SEVERE, "HttpZipWorkflowStart", "Error reading tar.gz file: " +
						e.getMessage());
				throw new WorkflowStartFailedException("HttpZipWorkflowStart:Error reading tar.gz file ",e);
			} catch (StorageEngineException e) {
				context.getLoggingEngine().log(context.getExecution(), Level.SEVERE, "HttpZipWorkflowStart", "Error accessigng UIM storage engine: " +
						e.getMessage());
				throw new WorkflowStartFailedException("HttpZipWorkflowStart:Error accessigng UIM storage engine",e);
			}

		} else if (dataset instanceof Request) {
			context.getLoggingEngine().log(context.getExecution(), Level.SEVERE, "HttpZipWorkflowStart", "A request cannot be the basis for a new import: ");
			throw new WorkflowStartFailedException("HttpZipWorkflowStart:A request cannot be the basis for a new import.");
		} else {
			context.getLoggingEngine().log(context.getExecution(), Level.SEVERE, "HttpZipWorkflowStart", "Unsupported dataset <"+ context.getDataSet() + ">");
			throw new WorkflowStartFailedException("Unsupported dataset <"+ context.getDataSet() + ">");
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#completed(eu.europeana.uim.api
	 * .ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public void completed(ExecutionContext<MetaDataRecord<I>, I> context) throws WorkflowStartFailedException {
	
		context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Initializing Data statistics analysis for imports:");

		
		Data value = context.getValue(DATA_KEY);
		context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Records created for the first time:" + Integer.toString(value.loader.getCreated()));
		context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Records updated:" + Integer.toString(value.loader.getUpdated()));
		context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Records not reimported because found identical:" + Integer.toString(value.loader.getOmitted()));
		context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Records generated due to splitting process:" + Integer.toString(value.loader.getGenerated()));
		context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Records discarded during the import process:" + Integer.toString(value.loader.getDiscarded()));
		
		value.loader.close();

		StorageEngine<I> uimengine = context.getStorageEngine();
		
		if(!value.deletioncandidates.isEmpty()){
			context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Finished analysis, checking for records that should be marked as deleted.");
			context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Number of deletion candidates found :" + value.deletioncandidates.size());
			
			Iterator<String> it = value.deletioncandidates.iterator();
			
			while(it.hasNext()){
				String id = it.next();
				try {
					MetaDataRecord<I> mdr = uimengine.getMetaDataRecord((I) id);
					mdr.deleteValues(EuropeanaModelRegistry.STATUS);
					mdr.addValue(EuropeanaModelRegistry.STATUS, Status.DELETED);
					mdr.deleteValues(EuropeanaModelRegistry.UIMUPDATEDDATE);
					mdr.addValue(EuropeanaModelRegistry.UIMUPDATEDDATE, new Date().toString());
					uimengine.updateMetaDataRecord(mdr);
					context.getLoggingEngine().log(context.getExecution(), Level.INFO, "HttpZipWorkflowStart", "Marked Record " + mdr.getId() + " as Deleted." );
				} catch (StorageEngineException e) {
					context.getLoggingEngine().log(context.getExecution(), Level.SEVERE, "HttpZipWorkflowStart", "Error marking record " + id + 
							" as deleted due to UIM storage engine failure " + e.getMessage());
				}
			}
		}
		
		Execution<I> execution = context.getExecution();
		execution.putValue("Deleted", Integer.toString(value.deletioncandidates.size()));

		Collection<I> collection = (Collection<I>) context.getDataSet();
		collection.putValue("Deleted", Integer.toString(value.deletioncandidates.size()));
		collection.putValue(ControlledVocabularyProxy.ISNEW.toString(), Boolean.toString(value.isNew));
		collection.putValue(ControlledVocabularyProxy.LASTINGESTION_DATE.toString(),Long.toString(new Date().getTime()));
		try {
			context.getStorageEngine().updateExecution(execution);
			context.getStorageEngine().updateCollection(collection);
		} catch (StorageEngineException e) {
			context.getLoggingEngine().log(context.getExecution(), Level.SEVERE, "HttpZipWorkflowStart", "Error updating execution or collection " +
					" with deleted records information due to UIM storage engine failure " + e.getMessage());
		}
		
		if (context.getExecution().isCanceled()) {
			value.request.setFailed(true);
		}
		
		MongoStorageEngine mgengine = new MongoStorageEngine();
		mgengine.initialize();
		mgengine.flushCollectionMDRS((String) context.getDataSet().getId());

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#getTotalSize(eu.europeana.uim
	 * .api.ExecutionContext)
	 */
	@Override
	public int getTotalSize(ExecutionContext<MetaDataRecord<I>, I> context) {
		Data value = context.getValue(DATA_KEY);
		if (value == null)
			return Integer.MAX_VALUE;

		if (value.expected > 0) {
			return value.expected;
		}

		if (value.loader.getExpectedRecordCount() > 0) {
			return value.loader.getExpectedRecordCount();
		}
		return Integer.MAX_VALUE;

	}


	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.Plugin#initialize()
	 */
	@Override
	public void initialize() {		
	}


	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.Plugin#shutdown()
	 */
	@Override
	public void shutdown() {
	}

}
