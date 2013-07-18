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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import java.util.logging.Level;
import java.util.logging.Logger;
import org.theeuropeanlibrary.model.common.qualifier.Status;
import eu.europeana.dedup.osgi.service.DeduplicationService;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.storage.StorageEngine;
import eu.europeana.uim.storage.StorageEngineException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.store.Collection;
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
		
		public HashSet<String> deletioncandidates;
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
							
							if(mdr != null){

							Task<MetaDataRecord<I>, I> task = new Task<MetaDataRecord<I>, I>(mdr,context);
							synchronized (getQueue()) {
								getQueue().offer(task);
							}
							
						}
						}

					} catch (Throwable t) {
						throw new RuntimeException("Failed to retrieve MDRs from storage. "); //+ context.getExecution().toString(), t);
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
				e.printStackTrace();
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
		if (dataset instanceof Collection) {

			Collection<I> collection = (Collection<I>) dataset;

			//
			Data value = new Data();
			value.deletioncandidates = new HashSet<String>();
			
			try {
				I[] availableMDRs = storage.getByCollection(collection);
				
				for(int i=0; i<availableMDRs.length; i++){
					value.deletioncandidates.add((String) availableMDRs[i]);
				}
				
			} catch (StorageEngineException e) {
				e.printStackTrace();
			}
			
			
			
			URL url = null;
			String httpzipurlprop = context.getProperties().getProperty(
					httpzipurl);
			
			String forceupdate = context.getProperties().getProperty(
					importidenticals);
			

			try {

				if (httpzipurlprop != null) {
					url = new URL(httpzipurlprop);
				} else {
					url = new URL(
							collection
									.getValue(ControlledVocabularyProxy.MINTPUBLICATIONLOCATION));
				}

				Request<I> request = storage.createRequest(collection,new Date());

				HttpRetriever retriever = new HttpRetriever().createInstance(url);

				ZipLoader<I> loader = new ZipLoader<I>(retriever.getNumber_of_recs(),
						retriever,context,request,dedup,forceupdate);

				value.loader = loader;

				context.putValue(DATA_KEY, value);

			} catch (MalformedURLException e) {
				throw new WorkflowStartFailedException("HttpZipWorkflowStart:Error accessing URL exposed by MINT ",e);
			} catch (IOException e) {
				throw new WorkflowStartFailedException("HttpZipWorkflowStart:Error reading zip file ",e);
			} catch (StorageEngineException e) {
				throw new WorkflowStartFailedException("HttpZipWorkflowStart:Error accessigng UIM storage engine",e);
			}

		} else if (dataset instanceof Request) {
			throw new WorkflowStartFailedException("HttpZipWorkflowStart:A request cannot be the basis for a new import.");
		} else {
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
		Data value = context.getValue(DATA_KEY);
		value.loader.close();

		StorageEngine<I> uimengine = context.getStorageEngine();
		
		if(!value.deletioncandidates.isEmpty()){
			Iterator<String> it = value.deletioncandidates.iterator();
			
			while(it.hasNext()){
				try {
					MetaDataRecord<I> mdr = uimengine.getMetaDataRecord((I) it.next());
					mdr.deleteValues(EuropeanaModelRegistry.STATUS);
					mdr.addValue(EuropeanaModelRegistry.STATUS, Status.DELETED);
					uimengine.updateMetaDataRecord(mdr);
					LOGGER.info("Record" + mdr.getId() + "Deleted" );
				} catch (StorageEngineException e) {
					LOGGER.log(Level.WARNING,"HttpZipWorkflowStart:",e );
				}
			}
		}
		
		Execution<I> execution = context.getExecution();
		execution.putValue("Deleted", Integer.toString(value.deletioncandidates.size()));

		Collection<I> collection = (Collection<I>) context.getDataSet();
		collection.putValue("Deleted", Integer.toString(value.deletioncandidates.size()));
		try {
			context.getStorageEngine().updateExecution(execution);
			context.getStorageEngine().updateCollection(collection);
		} catch (StorageEngineException e) {
			e.printStackTrace();
		}
		
		LOGGER.info("Number of deleted Records: " + value.deletioncandidates.size() );
		
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
