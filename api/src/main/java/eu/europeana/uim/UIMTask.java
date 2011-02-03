package eu.europeana.uim;

import java.util.Queue;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.api.Task;
import eu.europeana.uim.api.TaskStatus;
import eu.europeana.uim.api.WorkflowStep;

public class UIMTask implements Task {

	private TaskStatus status = TaskStatus.NEW;
	private Throwable throwable;
	
	private Queue<Task> success = null;
	private Queue<Task> failure = null;
	
	private WorkflowStep step;
	
	private final StorageEngine engine;
	private final MetaDataRecord record;
	
	
	public UIMTask(MetaDataRecord record, StorageEngine engine) {
		super();
		this.engine = engine;
		this.record = record;
	}

	
	@Override
	public void run() {
		step.processRecord(record);
	}

	@Override
	public TaskStatus getStatus() {
		return status;
	}

	@Override
	public void setStatus(TaskStatus status) {
		this.status = status;
	}


	@Override
	public void setUp() {
	}
	
	

	@Override
	public void tearDown() {
	}
	
	
	


	@Override
	public void save() throws StorageEngineException {
		engine.updateMetaDataRecord(record);
	}


	@Override
	public WorkflowStep getStep() {
		return step;
	}


	@Override
	public void setStep(WorkflowStep step) {
		this.step = step;
	}

	@Override
	public void setOnSuccess(Queue<Task> success) {
		this.success = success;
	}

	@Override
	public Queue<Task> getOnSuccess() {
		return success;
	}

	@Override
	public void setOnFailure(Queue<Task> failure) {
		this.failure = failure;
	}

	@Override
	public Queue<Task> getOnFailure() {
		return failure;
	}

	@Override
	public void setThrowable(Throwable throwable) {
		this.throwable =throwable;
	}

	@Override
	public Throwable getThrowable() {
		return throwable;
	}
	
	
}
