package eu.europeana.uim.orchestration.processing;

import java.util.Queue;

import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.api.WorkflowStep;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface Task extends Runnable {

    TaskStatus getStatus();
    void setStatus(TaskStatus status);

    void setUp();
    void tearDown();
    
    void save() throws StorageEngineException;

	void setStep(WorkflowStep step);
	WorkflowStep getStep();
	
	void setOnSuccess(Queue<Task> thisSuccess);
	Queue<Task> getOnSuccess();
	
	void setOnFailure(Queue<Task> failure);
	Queue<Task> getOnFailure();

	void setThrowable(Throwable throwable);
	Throwable getThrowable();

	
}
