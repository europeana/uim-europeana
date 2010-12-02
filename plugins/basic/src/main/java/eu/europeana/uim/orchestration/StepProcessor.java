package eu.europeana.uim.orchestration;

import eu.europeana.uim.api.Task;
import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.workflow.ProcessingContainer;

import java.util.Iterator;
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
    private final ConcurrentHashMap<Task, Throwable> failures = new ConcurrentHashMap<Task, Throwable>();

    // synchronized Vector for collecting successful tasks
    private final Vector<UIMTask> successes = new Vector<UIMTask>();

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
    public void addRecords(UIMExecution e, long... ids) {
        for (long id : ids) {
            if (step instanceof ProcessingContainer) {
                ProcessingContainer pc = (ProcessingContainer) step;
                for (WorkflowStep s : pc.getSteps()) {
                    queue.add(createTask(id, s, e));
                }
            } else {
                queue.add(createTask(id, this.step, e));
            }
        }
    }

    public boolean addRecord(Task task) {
        // FIXME this is broken for ProcessingContainers...
        return queue.offer(task);
    }

    public int remainingCapacity() {
        return queue.remainingCapacity();
    }

    public int currentQueueSize() {
        return queue.size();
    }

    private UIMTask createTask(long id, WorkflowStep step, UIMExecution e) {
        UIMTask uimTask = new UIMTask(recordProvider.getMetaDataRecord(id), this, step);
        recordProvider.addTask(uimTask, e);
        return uimTask;
    }

    public void addSuccess(UIMTask t) {
        this.successes.add(t);
    }

    public void addFailure(Task t, Throwable throwable) {

        // FIXME we need to go over all processor's failures every now and then and clean up
        // this is a known memory leak, so to speak
        this.failures.put(t, throwable);
    }

    public Map<Task, Throwable> getFailedTasks() {
        return this.failures;
    }

    /**
     * If the processor is not started yet, start it, otherwise do nothing
     */
    public void startProcessing() {
        if(!started) {
            log.fine("StepProcessor for step '" + this.step.toString() + "' starting to process, having " + queue.size() + " elements in queue");
            started = true;
            threadPoolExecutor.execute(new Thread());
        }
    }

    public boolean isProcessing() {
        return started;
    }

    /**
     * passes the successful tasks to the another processor, starting it up if necessary
     */
    public void passToNext(StepProcessor next) {
        //log.info("Filling queue of next StepProcessor with capacity " + c + ", tasks available: " + this.getSuccessfulTasks().size());

        boolean giveItABreak = false;

        // TODO the following can probably be done in batches
        while (!giveItABreak && successes.size() != 0) {
            UIMTask t = successes.firstElement();
            giveItABreak = !next.addRecord(t);
            if(!giveItABreak) {
                successes.remove(t);
                t.changeStep(next, next.step);
            } else {
                // rollback
                t.changeStep(this, this.step);
            }
        }
    }

    /** clears the successful executions, for the last processor in queue **/
    public void clearSuccess() {
        synchronized (successes) {
            Iterator<UIMTask> it = successes.iterator();
            while(it.hasNext()) {
                it.next().markDone();
            }
            successes.clear();
        }
    }


    @Override
    public String toString() {
        return "SP '" + step.toString() + "', queue: " + currentQueueSize() + ", success: " + successes.size() + ", failures: " + failures.size();
    }
}
