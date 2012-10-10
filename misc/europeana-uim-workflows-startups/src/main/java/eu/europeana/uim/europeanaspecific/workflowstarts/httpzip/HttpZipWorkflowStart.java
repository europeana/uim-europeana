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
import java.util.List;

import eu.europeana.dedup.osgi.service.DeduplicationService;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.UimDataSet;
import eu.europeana.uim.workflow.AbstractWorkflowStart;
import eu.europeana.uim.workflow.Task;
import eu.europeana.uim.workflow.TaskCreator;
import eu.europeana.uim.workflow.WorkflowStartFailedException;

/**
 * Worflow start used in the current Mint implementation. It retrieves data from
 * a remote zip location.
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 5 Mar 2012
 */
public class HttpZipWorkflowStart extends AbstractWorkflowStart {

	/**
	 * The deduplication service reference (null if not available)
	 */
	DeduplicationService dedup;
	
	
	/** Property which allows to overwrite base url from collection/provider */
	public static final String httpzipurl = "http.overwrite.zip.baseUrl";
	
	/** Property which defines whether the records collection should be  */
	public static final String purgecollection = "http.overwrite.zip.purgable";

	/**
	 * The parameters used by this WorkflowStart
	 */
	private static final List<String> params = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(httpzipurl);
			add(purgecollection);
		}
	};

	/**
	 * TKEY used for storing the Data class in the execution context
	 */
	private static TKey<HttpZipWorkflowStart, Data> DATA_KEY = TKey.register(
			HttpZipWorkflowStart.class, "data", Data.class);

	/**
	 * Private static class used as a container
	 * 
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 5 Mar 2012
	 */
	private final static class Data implements Serializable {

		public ZipLoader loader;
		public Request<?> request;

		public int maxrecords = 0;
		public int expected = 0;
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
		return 5;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#createLoader(eu.europeana.uim
	 * .api.ExecutionContext, eu.europeana.uim.api.StorageEngine)
	 */
	@Override
	public <I> TaskCreator<I> createLoader(final ExecutionContext<I> context,
			final StorageEngine<I> storage) throws WorkflowStartFailedException {

		final Data value = context.getValue(DATA_KEY);

		if (!value.loader.isFinished()) {
			return new TaskCreator<I>() {
				@Override
				public void run() {
					try {
						List<MetaDataRecord<I>> list = value.loader.doNext(100,
								false);

						for (MetaDataRecord<I> mdr : list) {
							Task<I> task = new Task<I>(mdr, storage, context);
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
	public <I> boolean isFinished(ExecutionContext<I> context,
			StorageEngine<I> storage) {
		Data value = context.getValue(DATA_KEY);
		boolean finished = value== null? true:value.loader.isFinished();
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
	public <I> void initialize(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {
		Data value = new Data();

		UimDataSet<I> dataset = context.getDataSet();
		if (dataset instanceof Collection) {

			Collection<I> collection = (Collection<I>) dataset;

			URL url = null;
			String httpzipurlprop = context.getProperties().getProperty(
					httpzipurl);
			
			String should_be_purged = context.getProperties().getProperty(
					purgecollection);
			

			try {

				if (httpzipurlprop != null) {
					url = new URL(httpzipurlprop);
				} else {
					url = new URL(
							collection
									.getValue(ControlledVocabularyProxy.MINTPUBLICATIONLOCATION));
				}

				Request<I> request = storage.createRequest(collection,
						new Date());
				storage.updateRequest(request);

				HttpRetriever retriever = HttpRetriever.createInstance(url);

				ZipLoader loader = new ZipLoader(retriever.getNumber_of_recs(),
						retriever, storage, request, context.getMonitor(),
						context.getLoggingEngine(),dedup);

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
	public <I> void completed(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {
		Data value = context.getValue(DATA_KEY);
		value.loader.close();

		if (context.getExecution().isCanceled()) {
			value.request.setFailed(true);
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.workflow.WorkflowStart#getTotalSize(eu.europeana.uim
	 * .api.ExecutionContext)
	 */
	@Override
	public <I> int getTotalSize(ExecutionContext<I> context) {
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

}
