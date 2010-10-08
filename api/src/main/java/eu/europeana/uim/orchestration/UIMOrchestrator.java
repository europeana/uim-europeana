package eu.europeana.uim.orchestration;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.Orchestrator;
import eu.europeana.uim.UIMRegistry;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.workflow.Workflow;

/**
 * Orchestrates the ingestion job execution. The orchestrator keeps a map of WorkflowProcessors, one for each different workflow.
 * When a new request for workflow execution comes in, the WorkflowProcessor for the Workflow is retrieved, or created if it does not exist.
 */
public class UIMOrchestrator implements Orchestrator {

    private static Logger log = Logger.getLogger(UIMOrchestrator.class.getName());

    private UIMRegistry registry;
    
    private Map<Workflow, WorkflowProcessor> processors = new HashMap<Workflow, WorkflowProcessor>();

    
    public UIMOrchestrator(){
    }
    
	@Override
	public String getIdentifier() {
		return UIMOrchestrator.class.getSimpleName();
	}
    
    
    
    @Override
    public Execution executeWorkflow(Workflow w, MetaDataRecord mdr) {
        return executeWorkflow(w);
    }

    @Override
    public Execution executeWorkflow(Workflow w, Collection c) {
        return executeWorkflow(w);
    }


    @Override
    public Execution executeWorkflow(Workflow w, Request r) {
        return executeWorkflow(w);
    }

    @Override
    public Execution executeWorkflow(Workflow w, Provider p) {
        return executeWorkflow(w);
    }

    /**
     * Executes a given workflow. A new Execution is created and a WorkflowProcessor created if none exists for this workflow
     * @param w the workflow to execute
     * @return a new Execution for this execution request
     */
    private Execution executeWorkflow(Workflow w) {
        Execution e = registry.getFirstStorage().createExecution();

        WorkflowProcessor we = processors.get(w);
        if(we == null) {
            we = new WorkflowProcessor(e, w, this);
            processors.put(w, we);
        } else {
            we.addExecution(e);
        }
        we.start();
        return e;
    }

    
    @Override
    public long[] getBatchFor(Execution e) {

        // TODO: get next batch for an execution from the storage
        return null;
    }
    
    

	public UIMRegistry getRegistry() {
		return registry;
	}

	public void setRegistry(UIMRegistry registry) {
		this.registry = registry;
	}

}
