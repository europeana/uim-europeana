package eu.europeana.uim.api;

import eu.europeana.uim.store.StorageEngine;

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

    StorageEngine getActiveStorage();

    void setFallbackStore(StorageEngine storage);
}
