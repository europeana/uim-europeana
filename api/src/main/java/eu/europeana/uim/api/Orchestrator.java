package eu.europeana.uim.api;

import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.DataSet;
import eu.europeana.uim.workflow.Workflow;

/**
 * Orchestrates the ingestion job execution.
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface Orchestrator {

    public String getIdentifier();

    ActiveExecution<?> executeWorkflow(Workflow w, DataSet dataset, ProgressMonitor monitor);

    <T> ActiveExecution<T> getActiveExecution(long id);

    <T> java.util.Collection<ActiveExecution<T>> getActiveExecutions();

    void shutdown();

}
