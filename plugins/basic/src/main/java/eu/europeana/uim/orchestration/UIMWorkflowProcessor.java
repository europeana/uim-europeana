package eu.europeana.uim.orchestration;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.api.Task;
import eu.europeana.uim.api.TaskStatus;
import eu.europeana.uim.api.WorkflowStart;
import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.orchestration.processing.TaskExecutorRegistry;
import eu.europeana.uim.orchestration.processing.TaskExecutorThread;
import eu.europeana.uim.orchestration.processing.TaskExecutorThreadFactory;
import eu.europeana.uim.util.BatchWorkflowStart;

public class UIMWorkflowProcessor implements Runnable {

	private static Logger log = Logger.getLogger(UIMWorkflowProcessor.class.getName());

	private TaskExecutorThreadFactory factory = new TaskExecutorThreadFactory("processor");
	private TaskExecutorThread dispatcherThread;

	private boolean running = false;

	private List<ActiveExecution<Task>> executions = new ArrayList<ActiveExecution<Task>>();

	public UIMWorkflowProcessor() {
	}


	public void run() {
		running = true;
		while(running){
			int total = 0;

			try {
				Iterator<ActiveExecution<Task>> iterator = executions.iterator();
				while (iterator.hasNext()) {
					ActiveExecution<Task> execution = iterator.next();
					total += execution.getProgressSize() + execution.getRemainingSize();

					// well we skip this execution if it is paused,
					// FIXME: if only paused executions are around then
					// we do somehow busy waiting - total count is > 0 
					if (execution.isPaused()) continue;


					try {
						// we ask teh workflow start if we have more to do
						if (execution.getProgressSize() == 0) {
							int tasks = execution.getWorkflow().getStart().createTasks(execution);

							//start cannot create more tasks and
							//we do not have more in the pipeline
							//so we 
							if (tasks == 0) {
								if (execution.isFinished()) {
									Thread.sleep(100);

									if (execution.isFinished()) {
										execution.setActive(false);
										execution.setEndTime(new Date());
										iterator.remove();
									}
								}
							}
						} 

						Queue<Task> success = execution.getSuccess(execution.getWorkflow().getStart().getIdentifier());

						List<WorkflowStep> steps = execution.getWorkflow().getSteps();
						for (WorkflowStep step : steps) {
							Queue<Task> thisSuccess = execution.getSuccess(step.getIdentifier());
							Queue<Task> thisFailure = execution.getFailure(step.getIdentifier());

							// get successfull tasks from previouse step
							// and schedule them into the step executor.
							Task task = null;
							synchronized (success) {
								task = success.poll();
							}
							while(task != null){
								task.setStep(step);
								task.setOnSuccess(thisSuccess);
								task.setOnFailure(thisFailure);

								task.setStatus(TaskStatus.QUEUED);
								step.getThreadPoolExecutor().execute(task);

								synchronized (success) {
									task = success.poll();
								}
							}

							// make the current success list the "next" input list
							success = thisSuccess;
						}

						// save and clean final
						Task task;
						synchronized (success) {
							task = success.poll();
						}
						while(task != null){
							task.save();
							execution.done(1);
							execution.getMonitor().worked(1);

							synchronized (success) {
								task = success.poll();
							}
						}						

					} catch (Throwable exc){
						log.log(Level.SEVERE, "Exception in workflow execution", exc);

						execution.setActive(false);
						execution.setThrowable(exc);
						iterator.remove();
					}
				}


				if (total == 0) {
					Thread.sleep(25);
				}
			} catch (Throwable exc){
				log.log(Level.SEVERE, "Exception in workflow executor", exc);
			}
		}
	}



	public synchronized void schedule(ActiveExecution<Task> execution)  throws StorageEngineException {
		if (execution.getWorkflow().getSteps().isEmpty()) throw new IllegalStateException("Empty workflow not allowed: " + execution.getWorkflow().getClass().getName());

		WorkflowStart start = execution.getWorkflow().getStart();
		if (start == null) {
			execution.getWorkflow().setStart(new BatchWorkflowStart());
		}

		start.initialize(execution);
		TaskExecutorRegistry.getInstance().initialize(start, start.getMaximumThreadCount());

		for (WorkflowStep step : execution.getWorkflow().getSteps()) {
			step.initialize(execution);
			TaskExecutorRegistry.getInstance().initialize(step, step.getMaximumThreadCount());
		}
		executions.add(execution);
	}




	public synchronized List<ActiveExecution<Task>> getExecutions() {
		return Collections.unmodifiableList(executions);
	}


	public void initialize() {
		running = false;
	}

	public void startup() {
		dispatcherThread = (TaskExecutorThread)factory.newThread(this);
		dispatcherThread.start();
	}

	public void shutdown() {
		running = false;
		dispatcherThread = null;
	}

	public void pause() {
		running = false;
		dispatcherThread = null;
	}

	public void resume() {
		running = true;
		dispatcherThread = (TaskExecutorThread)factory.newThread(this);
		dispatcherThread.start();
	}
}
