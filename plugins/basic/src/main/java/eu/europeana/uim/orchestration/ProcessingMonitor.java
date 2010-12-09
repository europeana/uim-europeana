package eu.europeana.uim.orchestration;

import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.Task;

/**
 * Contract that describes monitoring/reporting capabilities
 */
public interface ProcessingMonitor {

    /**
     * Register a task so it can be kept track of
     */
    void addTask(Task task, ActiveExecution execution);

}
