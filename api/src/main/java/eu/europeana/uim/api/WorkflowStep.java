package eu.europeana.uim.api;

import java.util.concurrent.ThreadPoolExecutor;

import eu.europeana.uim.MetaDataRecord;



/**
 * Step in a UIM workflow. We use this in order to implement the command pattern for workflow execution.
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface WorkflowStep {

    String getIdentifier();

    int getPreferredThreadCount();
    
    int getMaximumThreadCount();

    void setThreadPoolExecutor(ThreadPoolExecutor executor);
    ThreadPoolExecutor getThreadPoolExecutor();
    
    boolean isSavepoint();
    
    <T> void initialize(ActiveExecution<T> visitor) throws StorageEngineException;

    void processRecord(MetaDataRecord mdr, ExecutionContext context);

}
