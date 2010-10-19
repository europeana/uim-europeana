package eu.europeana.uim.orchestration;

import eu.europeana.uim.workflow.ProcessingContainer;
import eu.europeana.uim.workflow.WorkflowStep;

import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Processor for a workflow step, capable of running several UIMTasks concurrently. The processor holds a ThreadPoolExecutor
 * and a BlockingQueue, as well as a list of successful and failed tasks.
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class StepProcessor {

    // TODO configuration?
    static final long KEEP_ALIVE_TIME = 1000l;


    private LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

    private ThreadPoolExecutor threadPoolExecutor;

    // synchronized Vector for collecting failing tasks
    private Vector<UIMTask> failures = new Vector<UIMTask>();

    // synchronized Vector for collecting successful tasks
    private Vector<UIMTask> successes = new Vector<UIMTask>();

    private WorkflowStep step;


    public StepProcessor(WorkflowStep step) {
        this.step = step;
        int maxPoolSize = 1;
        if (step instanceof ProcessingContainer) {
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

    public void addSuccess(UIMTask task) {
        this.successes.add(task);
    }

    public void addFailure(UIMTask task) {
        this.failures.add(task);
    }

    public WorkflowStep getStep() {
        return step;
    }

    public Vector<UIMTask> getSuccessfulTasks() {
        return this.successes;
    }

    public Vector<UIMTask> getFailedTasks() {
        return this.failures;
    }
}
