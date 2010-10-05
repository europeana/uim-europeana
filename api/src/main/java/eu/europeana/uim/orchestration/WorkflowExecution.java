package eu.europeana.uim.orchestration;

import eu.europeana.uim.plugin.IngestionPlugin;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.workflow.ProcessingContainer;
import eu.europeana.uim.workflow.Workflow;
import eu.europeana.uim.workflow.WorkflowStep;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Builds the execution process for a given workflow, executes it and handles exception handling, reporting etc.
 *
 * @author manu
 */
public class WorkflowExecution {

    // TODO add thread-safe collection implementation to store the exceptions returned by the UIMTask-s

    // TODO configuration?
    private static final long KEEP_ALIVE_TIME = 1000l;

    private Execution execution;

    protected List<StepThreadPool> workflowStepThreadPools = new LinkedList<StepThreadPool>();

    public WorkflowExecution(Execution e, Workflow w) {
        this.execution = e;
        
        // construct the set of StepThreadPools based on the workflow
        for(WorkflowStep step : w.getSteps()) {
            workflowStepThreadPools.add(new StepThreadPool(step));
        }
    }

    public void execute(long[] mdrIds) {

        // asynchronous: start new thread that will
        // - for the first WorkflowStepTreadPool, retrieve actual MDRs from the storage and pass them to the first queue
        // - walk over all pools and move tasks from one queue to the next depending on how full the next one is
        // - handle reporting (using the throwables
        // - handle cancelling
        // - handle logging
        // - become idle if there's nothing much to do (optimization)
        // - implement WorldPeace
    }





    private static class StepThreadPool {

        private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

        private ThreadPoolExecutor threadPoolExecutor;

        private WorkflowStep step;

        public StepThreadPool(WorkflowStep step) {
            this.step = step;
            int maxPoolSize = 1;
            if(step instanceof IngestionPlugin) {
                ProcessingContainer pc = (ProcessingContainer) step;
                maxPoolSize = pc.getSteps().size();
            }

            threadPoolExecutor = new ThreadPoolExecutor(1, maxPoolSize, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, queue);
        }

        public LinkedBlockingQueue<Runnable> getQueue() {
            return queue;
        }

        public ThreadPoolExecutor getThreadPoolExecutor() {
            return threadPoolExecutor;
        }
    }

}
