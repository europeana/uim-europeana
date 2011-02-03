package eu.europeana.uim.api;

import java.util.List;

import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.DataSet;

/**
 * Orchestrates the ingestion job execution.
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface Orchestrator {
	
	public String getIdentifier();

    ActiveExecution<?> executeWorkflow(Workflow w, DataSet dataset, ProgressMonitor monitor);


    <T> java.util.Collection<ActiveExecution<T>> getActiveExecutions();


    void shutdown();

    /**
     * Gets the next batch of MetaDataRecord IDs for a given Execution
     * @param e the Execution for which to retrieve the next batch of IDs
     * @return an array of MetaDataRecord IDs to process
     */
    //long[] getBatchFor(ActiveExecution e);

    /**
     * Gets the total number of items to be processed for this dataset
     */
    //int getTotal(ActiveExecution dataset);

    /**
     * Gets a snapshot of the runtime information for a given workflow.
     * As we run multiple executions through the same WorkflowProcessor, this is a global status, not related to a particular execution.
     */
    List<WorkflowStepStatus> getRuntimeStatus(Workflow w);

}
