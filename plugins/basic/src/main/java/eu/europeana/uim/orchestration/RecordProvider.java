package eu.europeana.uim.orchestration;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.Task;

/**
 * TODO find a better name for this contract
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface RecordProvider {

    /**
     * Provides a MetaDataRecord for a given ID
     */
    MetaDataRecord<FieldRegistry> getMetaDataRecord(long id);

    /**
     * Register a task so it can be kept track of
     */
    void addTask(Task task, ActiveExecution execution);
}
