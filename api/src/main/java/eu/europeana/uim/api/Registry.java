package eu.europeana.uim.api;


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

}
