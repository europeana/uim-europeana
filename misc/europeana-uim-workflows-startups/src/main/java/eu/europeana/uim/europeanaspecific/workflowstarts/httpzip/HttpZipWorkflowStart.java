/**
 * 
 */
package eu.europeana.uim.europeanaspecific.workflowstarts.httpzip;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
 * @author Georgios Markakis
 * 
 */
public class HttpZipWorkflowStart extends AbstractWorkflowStart {

	/** Property which allows to overwrite base url from collection/provider */
	public static final String httpzipurl = "http.overwrite.zip.baseUrl";

	private static final List<String> params = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

		{
			add(httpzipurl);
		}
	};

	
    private static TKey<HttpZipWorkflowStart, Data> DATA_KEY                            = TKey.register(
    		HttpZipWorkflowStart.class,
            "data",
            Data.class);
	
	//private HttpRetriever retriever;
	//private static ZipLoader loader;      

	private final static class Data implements Serializable {

	       public ZipLoader loader;
		        public Request<?>   request;
		
	            public int          maxrecords = 0;
		        public int          expected   = 0;
		    }
	
	/**
	 * @param name
	 * @param description
	 */
	public HttpZipWorkflowStart(String name, String description) {
		super(name, description);
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

        if (!value.loader.isFinished()) { return new TaskCreator<I>() {
            @Override
            public void run() {
                try {
                    List<MetaDataRecord<I>> list = value.loader.doNext(100, false);

                    for (MetaDataRecord<I> mdr : list) {
                        Task<I> task = new Task<I>(mdr, storage, context);
                        synchronized (getQueue()) {
                            getQueue().offer(task);
                        }
                    }

                } catch (Throwable t) {
                    throw new RuntimeException("Failed to retrieve MDRs from storage. " +
                                               context.getExecution().toString(), t);
                } finally {
                    setDone(true);
                }
            }
        }; }

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
        boolean finished = value.loader.isFinished();
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
			
			Collection<I> collection = (Collection<I>)dataset;
			

			
			URL url = null;
			String httpzipurlprop = context.getProperties().getProperty(httpzipurl);
			
			
			try {
				
				if(httpzipurlprop != null){ 
					url = new URL(httpzipurlprop);
				}
				else{
					url = new URL(collection.getValue(ControlledVocabularyProxy.MINTPUBLICATIONLOCATION));
				}
				
				Request request = storage.createRequest(collection, new Date());
	            storage.updateRequest(request);

	            
	            
	            HttpRetriever retriever = HttpRetriever.createInstance(url);
				
	            ZipLoader loader =  new ZipLoader(retriever,storage, request,context.getMonitor(), context.getLoggingEngine());

	            value.loader = loader;
	            
	            context.putValue(DATA_KEY, value);


			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Retriever exception
				e.printStackTrace();
			} catch (StorageEngineException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else if (dataset instanceof Request) {
			throw new IllegalArgumentException(
					"A request cannot be the basis for a new import.");
		} else {
			throw new IllegalStateException("Unsupported dataset <"
					+ context.getDataSet() + ">");
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
		// TODO Auto-generated method stub

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
        if (value == null) return Integer.MAX_VALUE;

        if (value.expected > 0) { return value.expected; }

        if (value.loader.getExpectedRecordCount() > 0) { return value.loader.getExpectedRecordCount(); }
        return Integer.MAX_VALUE;
		
	}

}
