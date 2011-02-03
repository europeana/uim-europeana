package eu.europeana.uim.orchestration;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Queue;

import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.Task;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.api.WorkflowStart;
import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.DataSet;
import eu.europeana.uim.store.Execution;

public class UIMActiveExecution implements ActiveExecution<Task> {

	private HashMap<String, LinkedList<Task>> success = new LinkedHashMap<String, LinkedList<Task>>();
	private HashMap<String, LinkedList<Task>> failure = new LinkedHashMap<String, LinkedList<Task>>();
	
	private final StorageEngine engine;
	
	private final Execution execution;
	private final Workflow workflow;
	private final ProgressMonitor monitor;
	
	private boolean paused;
	private Throwable throwable;
	
	private int current = 0;
	private ArrayList<long[]> batches = new ArrayList<long[]>();
	
	private int completed = 0;
	
	public UIMActiveExecution(Execution execution, Workflow workflow, ProgressMonitor monitor, StorageEngine engine){
		this.execution = execution;
		this.workflow = workflow;
		this.monitor = monitor;
		this.engine = engine;
		
		WorkflowStart start = workflow.getStart();
		success.put(start.getIdentifier(), new LinkedList<Task>());
		failure.put(start.getIdentifier(), new LinkedList<Task>());
		
		for (WorkflowStep step : workflow.getSteps()) {
			success.put(step.getIdentifier(), new LinkedList<Task>());
			failure.put(step.getIdentifier(), new LinkedList<Task>());
		}
	}

	@Override
	public StorageEngine getStorageEngine() {
		return engine;
	}

	public long getId() {
		return execution.getId();
	}

	public boolean isActive() {
		return execution.isActive();
	}

	public void setActive(boolean active) {
		execution.setActive(active);
	}

	public Date getStartTime() {
		return execution.getStartTime();
	}

	public void setStartTime(Date start) {
		execution.setStartTime(start);
	}

	public Date getEndTime() {
		return execution.getEndTime();
	}

	public void setEndTime(Date end) {
		execution.setEndTime(end);
	}

	public DataSet getDataSet() {
		return execution.getDataSet();
	}

	public void setDataSet(DataSet entity) {
		execution.setDataSet(entity);
	}

	public String getWorkflowName() {
		return execution.getWorkflowName();
	}

	public void setWorkflowName(String name) {
		execution.setWorkflowName(name);
	}

	@Override
	public ProgressMonitor getMonitor() {
		return monitor;
	}


	@Override
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	@Override
	public boolean isPaused() {
		return paused;
	}

	@Override
	public void setThrowable(Throwable throwable) {
		this.throwable = throwable;
	}

	@Override
	public Throwable getThrowable() {
		return throwable;
	}


	@Override
	public Queue<Task> getSuccess(String identifier) {
		return success.get(identifier);
	}

	@Override
	public Queue<Task> getFailure(String identifier) {
		return failure.get(identifier);
	}

	@Override
	public void done(int count) {
		completed += count;
	}
	
	@Override
	public int getRemainingSize() {
		int size = 0;
		for (int i = current; i < batches.size(); i ++) {
			size += batches.get(i).length;
		}
		return size;
	}

	@Override
	public int getProgressSize() {
		int size = 0;
		for (LinkedList<Task> tasks : success.values()) {
			size += tasks.size();
		}
		
		// we need to subtract the success from the last step - tasks
		// in that step success pipe are considered as completed.
		size -= getCompletedSize();
		return size;
	}

	@Override
	public int getFailureSize() {
		// count elements in failure queues
		int size = 0;
		for (LinkedList<Task> tasks : failure.values()) {
			size += tasks.size();
		}
		return size;
	}

	
	@Override
	public int getScheduledSize() {
		int size = 0;
		
		// count elements in batches
		for (long[] batch : batches) {
			size += batch.length;
		}
		return size;
	}

	@Override
	public int getCompletedSize() {
		return completed;
	}
	
	
	@Override
	public boolean isFinished() {
		return getScheduledSize() == getFailureSize() + getCompletedSize();
	}

	
	@Override
	public long[] nextBatch() {
		if (current >= batches.size()) return null;
		
		return batches.get(current++);
	}

	@Override
	public void addBatch(long[] ids) {
		batches.add(ids);
	}

	
	@Override
	public Workflow getWorkflow() {
		return workflow;
	}
	
	
	@Override
	public void waitUntilFinished() {
		while (!isFinished()) {
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
			}
		}
		
		System.out.println("Finished:" + getCompletedSize());
		System.out.println("Failed:" + getFailureSize());
	}

}
