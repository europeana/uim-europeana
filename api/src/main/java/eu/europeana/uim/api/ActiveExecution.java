package eu.europeana.uim.api;

import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.UimEntity;

import java.util.Date;

/**
 * An Execution in a running state. It keeps track of the overall progress.
 */
public interface ActiveExecution extends Execution {

    /** start time  **/
    Date getStartTime();

    /** workflow for this execution **/
    Workflow getWorkflow();

    /** data set for this execution **/
    UimEntity getDataSet();

    /** progress monitor **/
    ProgressMonitor getMonitor();


}
