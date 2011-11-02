/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.europeanaspecific.workflowstarts.oaipmh;

import java.io.Serializable;
import java.util.List;

import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.workflow.AbstractWorkflowStart;
import eu.europeana.uim.workflow.Task;
import eu.europeana.uim.workflow.TaskCreator;
import eu.europeana.uim.workflow.WorkflowStartFailedException;

/**
 * 
 * @author Georgios Markakis
 */
public class OaiPMHWorkflowstart extends AbstractWorkflowStart{

    private static TKey<OaiPMHWorkflowstart, Data> DATA_KEY                            = TKey.register(
    		OaiPMHWorkflowstart.class,
            "data",
            Data.class);
	
	public OaiPMHWorkflowstart(String name, String description) {
		super(name, description);
		// TODO Auto-generated constructor stub
	}

	
	private final static class Data implements Serializable {

	       public OaiPmhLoader loader;
		        public Request<?>   request;
		
	            public int          maxrecords = 0;
		        public int          expected   = 0;
		    }
	
	
	
	@Override
	public List<String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getPreferredThreadCount() {
		return 5;
	}

	@Override
	public int getMaximumThreadCount() {
		return 10;
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public <I> TaskCreator<I> createLoader(final ExecutionContext<I> context,
            final StorageEngine<I> storage) {
        final Data value = context.getValue(DATA_KEY);
        if (!value.loader.isFinished()) { return new TaskCreator() {
            @Override
            public void run() {
                try {
                    List<MetaDataRecord> list = value.loader.doNext(100, false);

                    for (MetaDataRecord mdr : list) {
                        Task task = new Task(mdr, storage, context);
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

	@Override
	public <I> boolean isFinished(ExecutionContext<I> context,
			StorageEngine<I> storage) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public <I> void initialize(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <I> void completed(ExecutionContext<I> context,
			StorageEngine<I> storage) throws WorkflowStartFailedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <I> int getTotalSize(ExecutionContext<I> context) {
		// TODO Auto-generated method stub
		return 0;
	}

}
