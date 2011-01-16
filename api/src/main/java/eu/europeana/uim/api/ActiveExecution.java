package eu.europeana.uim.api;

import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.Execution;

/**
 * An Execution in a running state. It keeps track of the overall progress.
 */
public interface ActiveExecution extends Execution {

    /** workflow for this execution **/
    Workflow getWorkflow();

    /** progress monitor **/
    ProgressMonitor getMonitor();

}
