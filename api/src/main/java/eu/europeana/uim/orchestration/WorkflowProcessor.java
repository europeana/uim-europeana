package eu.europeana.uim.orchestration;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.Orchestrator;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.workflow.Workflow;
import eu.europeana.uim.workflow.WorkflowStep;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

/**
 * Builds the execution process for a given workflow, and executes one or more Executions for it. Also does exception handling, reporting etc.
 * A WorkflowProcessor handles the processing on a per-record basis for multiple Executions.
 * <p/>
 * When created, the WorkflowProcessor creates a list of StepProcessors, one for each workflow step.
 * When executed, the WorkflowProcessor starts itself (as separate Thread) and walks over the list of StepProcessors, refilling the queues as necessary.
 * It communicates with a parent Orchestrator in order to perform storage operations and retrieve the next elements to process.
 *
 * @author manu
 */
public class WorkflowProcessor implements Runnable {

    private static Logger log = Logger.getLogger(WorkflowProcessor.class.getName());


    public static final int BATCH_SIZE = 100;

    private Workflow workflow;

    private List<Execution> executions = new ArrayList<Execution>();

    private Orchestrator orchestrator;

    protected List<StepProcessor> workflowStepProcessors = new LinkedList<StepProcessor>();

    /**
     * Creates a new WorkflowProcessor and adds the Execution to it
     *
     * @param e the Execution this processor will handle
     * @param w the Workflow this processor follows
     * @param o the Orchestrator for this processor
     */
    public WorkflowProcessor(Execution e, Workflow w, Orchestrator o) {
        this.orchestrator = o;
        this.executions.add(e);
        this.workflow = w;

        // construct the set of StepThreadPools based on the workflow
        for (WorkflowStep step : w.getSteps()) {
            // TODO use a provider here so we can test this
            workflowStepProcessors.add(new StepProcessor(step));
        }
    }

    /**
     * Adds a new Execution to the processor
     * @param e the Execution to be handled by the processor
     */
    public void addExecution(Execution e) {
        this.executions.add(e);
    }

    /**
     * Removes an Execution from the processor. As a result, a graceful shutdown of the Execution will occur
     * @param e the Execution to remove
     */
    public void removeExecution(Execution e) {
        this.executions.remove(e);
    }

    /**
     * Starts the processor
     */
    public void start() {
        Thread t = new Thread(this);
        t.start();
    }

    @Override
    public void run() {

        // asynchronous: start new thread that will
        // - for the first WorkflowStepTreadPool, retrieve actual MDRs from the storage and pass them to the first queue
        // - walk over all pools and move tasks from one queue to the next depending on how full the next one is
        // - handle reporting (using the throwables)
        // - handle cancelling
        // - handle logging
        // - become idle if there's nothing much to do (optimization)
        // - implement WorldPeace

        log.info("Starting new WorkflowProcessor for Workfow " + workflow.getName());

        // FIXME there's something better out there to loop like this I suppose
        while (true) {

            for (int i = 0; i < workflowStepProcessors.size(); i++) {
                StepProcessor sp = workflowStepProcessors.get(i);
                if (i == 0) {
                    // special treatment for the first step which gets MDRs directly from the storage
                    fillFirstStepProcessorQueue(sp);
                } else {
                    StepProcessor previous = workflowStepProcessors.get(i - 1);
                    fillStepProcessorQueue(sp, previous);
                }
            }
        }
    }

    /**
     * For the first step in the workflow, retrieve batches MetaDataRecords and create UIMTasks out of them
     *
     * @param sp the StepProcessor for the worklow
     */
    private void fillFirstStepProcessorQueue(StepProcessor sp) {
        // TODO we probably can do this dynamically. For this Orchestrator#getBatchFor needs to handle an argument
        // right now we have a fixed batch size that we use in order to refill the queues
        if (sp.getQueue().remainingCapacity() > BATCH_SIZE * executions.size()) {
            for (Execution e : executions) {
                List<UIMTask> tasks = new ArrayList<UIMTask>();
                for (long id : orchestrator.getBatchFor(e)) {
                    sp.getQueue().add(new UIMTask(getMetaDataRecord(id), sp));
                }
            }
        }
    }

    /**
     * Passes MDRs from one queue to another
     *
     * @param sp
     * @param previous
     */
    private void fillStepProcessorQueue(StepProcessor sp, StepProcessor previous) {
        // TODO pass MDRs from one queue to another, creating new UIMTasks for them
        // hmmmm, all this creation of new tasks does not seem to be too efficient, maybe we can re-use the UIMTask object and simply update
        // the step

    }

    /**
     * Gets the actual MetaDataRecord based on its ID
     *
     * @param id the ID of the MetaDataRecord to retrieve
     * @return the MetaDataRecord provided by the storage
     */
    private MetaDataRecord<?> getMetaDataRecord(long id) {
        // TODO
        return null;
    }


    public static void main(String... args) {

        // testing how the ThreadPoolExecutor works

        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        for (int i = 0; i < 100; i++) {
            queue.add(new DummyRunnable());
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 10, StepProcessor.KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, queue);
        threadPoolExecutor.execute(new Thread());
    }

    private static class DummyRunnable implements Runnable {
        private static int count = 0;
        private int id = 0;

        public DummyRunnable() {
            id = count++;
        }

        @Override
        public void run() {
            System.out.println("DummyRunnable " + id);
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
    }

}
