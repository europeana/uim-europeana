package eu.europeana.uim.orchestration;

import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.api.Task;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.api.WorkflowStepStatus;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.orchestration.processing.TaskExecutor;
import eu.europeana.uim.orchestration.processing.TaskExecutorRegistry;
import eu.europeana.uim.store.DataSet;
import eu.europeana.uim.store.Execution;

/**
 * Orchestrates the ingestion job execution. The orchestrator keeps a map of WorkflowProcessors, one for each different workflow.
 * When a new request for workflow execution comes in, the WorkflowProcessor for the Workflow is retrieved, or created if it does not exist.
 */
public class UIMOrchestrator implements Orchestrator {

	private static Logger log = Logger.getLogger(UIMOrchestrator.class.getName());

	public static final int BATCH_SIZE = 100;

	private final Registry registry;
	private final UIMWorkflowProcessor processor = new UIMWorkflowProcessor(); 

	public UIMOrchestrator(Registry registry) {
		this.registry = registry;

		processor.startup();
	}

	@Override
	public String getIdentifier() {
		return UIMOrchestrator.class.getSimpleName();
	}

	/**
	 * Executes a given workflow. A new Execution is created and a WorkflowProcessor created if none exists for this workflow
	 *
	 * @param w the workflow to execute
	 * @param monitor the ProgressMonitor tracking this Execution
	 * @param dataset the data set on which this Execution runs
	 * @return a new ActiveExecution for this execution request
	 */
	@Override
	public ActiveExecution<Task> executeWorkflow(Workflow w, DataSet dataset, ProgressMonitor monitor) {
		monitor.beginTask(w.getName(), 1);

		try {
			Execution e = registry.getStorage().createExecution(dataset, w.getName());
			e.setActive(true);
			e.setStartTime(new Date());
			registry.getStorage().updateExecution(e);

			try {
				UIMActiveExecution activeExecution = new UIMActiveExecution(e, w, monitor, registry.getStorage());
				processor.schedule(activeExecution);

				return activeExecution;
			} catch (Throwable t) {
				log.severe("Could not update execution details: " + t.getMessage());
			}
		} catch (StorageEngineException e1) {
			log.severe("Could not update execution details: " + e1.getMessage());
			e1.printStackTrace();
		}
		return null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public List<ActiveExecution<Task>> getActiveExecutions() {
		return processor.getExecutions();
	}



	public void pause(ActiveExecution<Task> execution) {
		execution.setPaused(true);
	}

	public void resume(ActiveExecution<Task> execution) {
		execution.setPaused(false);
	}

	public void cancel(ActiveExecution<Task> execution) {
		execution.getMonitor().setCancelled(true);
	}



	@Override
	public void shutdown() {
		processor.shutdown();
		for(TaskExecutor executor : TaskExecutorRegistry.getInstance().getAllExecutor()) {
			executor.shutdownNow();
		}
	}



	@Override
	public List<WorkflowStepStatus> getRuntimeStatus(Workflow w) {
		return null; //processors.get(w).getRuntimeStatus(w);
	}



}
