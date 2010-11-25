package eu.europeana.uim.orchestration;

import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.workflow.ProcessingContainer;

import java.util.Vector;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Processor for a workflow step, capable of running several UIMTasks concurrently. The processor holds a ThreadPoolExecutor
 * and a BlockingQueue, as well as a list of successful and failed tasks.
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class StepProcessor {

    private final static Logger log = Logger.getLogger(WorkflowProcessor.class.getName());

    // TODO configuration?
    static final long KEEP_ALIVE_TIME = 1000l;

    private final LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();

    private final ThreadPoolExecutor threadPoolExecutor;

    // synchronized Vector for collecting failing tasks
    private final Vector<UIMTask> failures = new Vector<UIMTask>();

    // synchronized Vector for collecting successful tasks
    private final Vector<UIMTask> successes = new Vector<UIMTask>();

    private final WorkflowStep step;

    private final RecordProvider recordProvider;

    public StepProcessor(WorkflowStep step, RecordProvider recordProvider) {
        this.step = step;
        this.recordProvider = recordProvider;
        int maxPoolSize = 1;
        if (step instanceof ProcessingContainer) {
            ProcessingContainer pc = (ProcessingContainer) step;
            maxPoolSize = pc.getSteps().size();
        }
        threadPoolExecutor = new ThreadPoolExecutor(1, maxPoolSize, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, queue);
    }

    /**
     * adds records to be processed
     */
    public void addRecords(long... ids) {
        for(long id : ids) {
            queue.add(createTask(id));
        }
    }

    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    public int currentQueueSize() {
        return queue.size();
    }

    private UIMTask createTask(long id) {
        return new UIMTask(recordProvider.getMetaDataRecord(id), this, step);
    }

    public void addSuccess(UIMTask task) {
        this.successes.add(task);
    }

    public void addFailure(UIMTask task) {
        this.failures.add(task);
    }

    public Vector<UIMTask> getSuccessfulTasks() {
        return this.successes;
    }

    public Vector<UIMTask> getFailedTasks() {
        return this.failures;
    }

    public void startProcessing() {
        log.fine("StepProcessor for step '" + this.step.toString() + "' starting to process, having " + queue.size() + " elements in queue");
        threadPoolExecutor.execute(new Thread());
    }

    /**
     * passes the successfull tasks to another processor
     */
    public void passToNext(StepProcessor next) {
        int c = next.remainingCapacity();
        log.fine("Filling queue of next StepProcessor with capacity " + c + ", tasks available: " + this.getSuccessfulTasks().size());
        while(c > 0 && successes.size() > 0) {
            successes.remove(successes.firstElement());
            c = next.remainingCapacity();
        }
    }
}
