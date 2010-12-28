package eu.europeana.uim.orchestration;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.UIMError;
import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.UimEntity;
import eu.europeana.uim.workflow.WorkflowProcessorProvider;

import java.util.HashMap;
import java.util.Map;

/**
 * Orchestrates the ingestion job execution. The orchestrator keeps a map of WorkflowProcessors, one for each different workflow.
 * When a new request for workflow execution comes in, the WorkflowProcessor for the Workflow is retrieved, or created if it does not exist.
 */
public class UIMOrchestrator implements Orchestrator {

    public static final int BATCH_SIZE = 100;

    private final Registry registry;

    private WorkflowProcessorProvider processorProvider;

    private Map<Workflow, WorkflowProcessor> processors = new HashMap<Workflow, WorkflowProcessor>();

    private Map<ActiveExecution, Integer> executionTotals = new HashMap<ActiveExecution, Integer>();


    public UIMOrchestrator(Registry registry, WorkflowProcessorProvider processorProvider) {
        this.registry = registry;
        this.processorProvider = processorProvider;
    }

    @Override
    public String getIdentifier() {
        return UIMOrchestrator.class.getSimpleName();
    }

    @Override
    public ActiveExecution executeWorkflow(Workflow w, MetaDataRecord<?> mdr, ProgressMonitor monitor) {
        monitor.beginTask(w.getName(), 1);
        return executeWorkflow(w, monitor, mdr);
    }

    @Override
    public ActiveExecution executeWorkflow(Workflow w, Collection c, ProgressMonitor monitor) {
        return executeWorkflow(w, monitor, c);
    }


    @Override
    public ActiveExecution executeWorkflow(Workflow w, Request r, ProgressMonitor monitor) {
        return executeWorkflow(w, monitor, r);
    }

    @Override
    public ActiveExecution executeWorkflow(Workflow w, Provider p, ProgressMonitor monitor) {
        return executeWorkflow(w, monitor, p);
    }

    @Override
    public java.util.Collection<ActiveExecution> getActiveExecutions() {
        return executionTotals.keySet();
    }

    @Override
    public void pause(ActiveExecution execution) {
        throw new RuntimeException("No can do");
    }

    @Override
    public void cancel(ActiveExecution execution) {
        throw new RuntimeException("No can do");
    }

    public boolean allDataProcessed(ActiveExecution e) {
        return executionTotals.get(e) == getTotal(e);
    }

    /**
     * Executes a given workflow. A new Execution is created and a WorkflowProcessor created if none exists for this workflow
     *
     * @param w the workflow to execute
     * @return a new ActiveExecution for this execution request
     */
    private ActiveExecution executeWorkflow(Workflow w, ProgressMonitor monitor, UimEntity dataset) {
        Execution e = registry.getStorage().createExecution();
        if(hasExecution(e)) {
            throw new UIMError("Execution " + e.getId() + " is already running");
        }
        UIMExecution activeExecution = new UIMExecution(e.getId(), dataset, monitor, w);
        executionTotals.put(activeExecution, 0);

        WorkflowProcessor wp = processors.get(w);
        if (wp == null) {
            wp = processorProvider.createProcessor(w, this, registry);
            processors.put(w, wp);
            wp.start();
        }
        wp.addExecution(activeExecution);
        return activeExecution;
    }

    private boolean hasExecution(Execution e) {
        for(Execution exec : executionTotals.keySet()) {
            if(exec.getId() == e.getId()) {
                return true;
            }
        }
        return false;
    }


    @Override
    public synchronized long[] getBatchFor(ActiveExecution e) {

        UIMExecution ae = (UIMExecution) e;
        Integer counter = executionTotals.get(ae);
        int total = getTotal(ae);
        long[] all;
        long[] result = new long[BATCH_SIZE];

        UimEntity dataset = ae.getDataSet();
        if(dataset instanceof MetaDataRecord<?>) {
            return new long[] { ((MetaDataRecord<?>) ae.getDataSet()).getId() };
        } else if(dataset instanceof Collection) {
             all = registry.getStorage().getByCollection((Collection)dataset);
        } else if(dataset instanceof Provider) {
            all = registry.getStorage().getByProvider((Provider)dataset, false);
        } else {
            throw new RuntimeException("Should not be here");
        }

        int remaining = total - counter;
        if(remaining > BATCH_SIZE) {
            counter += BATCH_SIZE;
            executionTotals.put(e, counter);
            // TODO room for optimization
            System.arraycopy(all, counter.intValue(), result, 0, BATCH_SIZE);
        } else if(remaining < BATCH_SIZE && remaining > 0) {
            counter = total;
            executionTotals.put(e, counter);
            System.arraycopy(all, counter.intValue(), result, 0, remaining);
        } else if(remaining == 0) {
            return null;
        }

        return result;
    }

    @Override
    public int getTotal(ActiveExecution e) {
        UimEntity dataSet = e.getDataSet();
        if(dataSet instanceof MetaDataRecord<?>) {
            return 1;
        } else if(dataSet instanceof Collection) {
            return registry.getStorage().getTotalByCollection((Collection)dataSet);
        } else if(dataSet instanceof Provider) {
            return registry.getStorage().getTotalByProvider((Provider)dataSet, false);
        } else {
            throw new RuntimeException("Should not be here");
        }
    }

    protected void notifyExecutionDone(ActiveExecution e) {
        executionTotals.remove(e);
    }
}
