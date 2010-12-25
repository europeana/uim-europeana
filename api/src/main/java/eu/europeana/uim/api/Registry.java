package eu.europeana.uim.api;


import java.util.List;

/**
 * Registry for UIM services
 */
public interface Registry {
    
    void addPlugin(IngestionPlugin plugin);

    IngestionPlugin getPlugin(String identifier);

    void removePlugin(IngestionPlugin plugin);

    void addStorage(StorageEngine storage);

    void removeStorage(StorageEngine storage);

    void addWorkflow(Workflow workflow);

    List<Workflow> getWorkflows();

    Workflow getWorkflow(String identifier);

    Workflow getWorkflow(Long id);

    void removeWorkflow(Workflow workflow);

    StorageEngine getStorage();

    StorageEngine getStorage(String identifier);

}
