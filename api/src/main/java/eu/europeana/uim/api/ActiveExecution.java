package eu.europeana.uim.api;

import java.util.Queue;

import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.DataSet;
import eu.europeana.uim.store.Execution;

/**
 * An Execution in a running state. It keeps track of the overall progress.
 */
public interface ActiveExecution<T> extends Execution {

	StorageEngine getStorageEngine();
	
    /** workflow for this execution **/
    Workflow getWorkflow();

    DataSet getDataSet();
    
    /** progress monitor **/
    ProgressMonitor getMonitor();

    public void setPaused(boolean paused);
    boolean isPaused();

    /** test the execution if all tasks are done eather completly finished
     * or failed. so if true: scheduled == finished + failed
     * 
     * @return
     */
    boolean isFinished();

	void setThrowable(Throwable throwable);
	Throwable getThrowable();


	Queue<T> getSuccess(String identifier);
	Queue<T> getFailure(String identifier);

	void done(int count);
	
	/** gives an estimate of tasks/records not yet started.
	 * 
	 * @return
	 */
	int getRemainingSize();
	
	/** gives an estimate of tasks/records which are currently in the pipeline.
	 * Note that failed tasks are not counted. The system can not guarantee the 
	 * number of records, due to the problem that some of the tasks might change 
	 * their status during the time of counting.
	 * 
	 * @return
	 */
	int getProgressSize();
	
	/** gives the number of tasks/records which are completly finished successful
	 * by all steps.
	 * 
	 * @return
	 */
	int getCompletedSize();

	/** gives the number of tasks/records which have failed on the way through
	 * the workflow no matter where.
	 * 
	 * @return
	 */
	int getFailureSize();

	/** gives the number of tasks/records which have been scheduled to be processed
	 * in the first place. So scheduled = remaining + progress + finished + failure.
	 * 
	 * @return
	 */
	int getScheduledSize();

	void addBatch(long[] ids);
	
	long[] nextBatch();
	
	public void waitUntilFinished();
	
}
