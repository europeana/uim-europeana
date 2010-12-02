package eu.europeana.uim.api;

import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.UimEntity;

/**
 * An Execution in a running state. It keeps track of the overall progress and knows about all pending tasks
 */
public interface ActiveExecution extends Execution {

    ProgressMonitor getMonitor();

    UimEntity getDataSet();
}
