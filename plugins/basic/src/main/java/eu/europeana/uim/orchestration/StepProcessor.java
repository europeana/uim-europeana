package eu.europeana.uim.orchestration;

import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.workflow.ProcessingContainer;

import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
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

    // synchronized map for collecting failing tasks
    private final ConcurrentHashMap<Long, Throwable> failures = new ConcurrentHashMap<Long, Throwable>();

    // synchronized Vector for collecting successful tasks
    private final Vector<Long> successes = new Vector<Long>();

    private final WorkflowStep step;

    private final RecordProvider recordProvider;

    private boolean started;

    public StepProcessor(WorkflowStep step, RecordProvider recordProvider) {
        this.step = step;
        this.recordProvider = recordProvider;
        int maxPoolSize = 1;
        if (step instanceof ProcessingContainer) {
            ProcessingContainer pc = (ProcessingContainer) step;
            // TODO check whether this makes any sense at all
            maxPoolSize = pc.getSteps().size();
        }
        threadPoolExecutor = new ThreadPoolExecutor(1, maxPoolSize, KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, queue);
    }

    /**
     * adds records to be processed
     */
    public void addRecords(long... ids) {
        for (long id : ids) {
            if (step instanceof ProcessingContainer) {
                ProcessingContainer pc = (ProcessingContainer) step;
                for (WorkflowStep s : pc.getSteps()) {
                    queue.add(createTask(id, s));
                }
            } else {
                queue.add(createTask(id, this.step));
            }
        }
    }

    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    public int currentQueueSize() {
        return queue.size();
    }

    private UIMTask createTask(long id, WorkflowStep step) {
        return new UIMTask(recordProvider.getMetaDataRecord(id), this, step);
    }

    public void addSuccess(Long id) {
        this.successes.add(id);
    }

    public void addFailure(Long id, Throwable throwable) {
        this.failures.put(id, throwable);
    }

    public Vector<Long> getSuccessfulTasks() {
        return this.successes;
    }

    public Map<Long, Throwable> getFailedTasks() {
        return this.failures;
    }

    public void startProcessing() {
        log.fine("StepProcessor for step '" + this.step.toString() + "' starting to process, having " + queue.size() + " elements in queue");
        started = true;
        threadPoolExecutor.execute(new Thread());
    }

    public boolean isProcessing() {
        return started;
    }

    /**
     * passes the successfull tasks to the another processor, starting it up if necessary
     */
    public void passToNext(StepProcessor next) {
        int c = next.remainingCapacity();
        log.info("Filling queue of next StepProcessor with capacity " + c + ", tasks available: " + this.getSuccessfulTasks().size());

        // TODO the following can probably be done in batches
        while (c > 0 && successes.size() > 0) {
            Long id = successes.firstElement();
            successes.remove(id);
            next.addRecords(id);
            c = next.remainingCapacity();
        }
    }
}
