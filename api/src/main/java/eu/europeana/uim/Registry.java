package eu.europeana.uim;

import eu.europeana.uim.plugin.IngestionPlugin;
import eu.europeana.uim.store.StorageEngine;
import eu.europeana.uim.workflow.Workflow;

/**
 * Registry for UIM services
 */
public interface Registry {
    
    void addPlugin(IngestionPlugin plugin);

    void removePlugin(IngestionPlugin plugin);

    void addStorage(StorageEngine storage);

    void removeStorage(StorageEngine storage);

    void addWorkflow(Workflow workflow);

    void removeWorkflow(Workflow workflow);

    StorageEngine getFirstStorage();

    void setFirstStorage(StorageEngine storage);
}
