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

/**
 * Builds the execution process for a given workflow, and executes one or more Executions for it. Also does exception handling, reporting etc.
 *
 * @author manu
 */
public class WorkflowProcessor implements Runnable {

    public static final int BATCH_SIZE = 100;

    private List<Execution> executions = new ArrayList<Execution>();

    private Orchestrator orchestrator;

    protected List<StepProcessor> workflowStepProcessors = new LinkedList<StepProcessor>();

    public WorkflowProcessor(Execution e, Workflow w, Orchestrator o) {
        this.orchestrator = o;
        this.executions.add(e);

        // construct the set of StepThreadPools based on the workflow
        for (WorkflowStep step : w.getSteps()) {
            workflowStepProcessors.add(new StepProcessor(step));
        }
    }

    public void execute() {
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

        // FIXME there's something better out there to loop like this I suppose
        while(true) {

            for(int i = 0; i < workflowStepProcessors.size(); i++) {
                StepProcessor sp = workflowStepProcessors.get(i);
                if(i == 0) {
                    // special treatment for the first step which gets MDRs directly from the storage
                    initialFillStepProcessorQueue(sp);
                } else {
                    StepProcessor previous = workflowStepProcessors.get(i-1);
                    fillStepProcessorQueue(sp, previous);
                }
            }
        }
    }

    private void initialFillStepProcessorQueue(StepProcessor sp) {
        // TODO we probably can do this dynamically. For this Orchestrator#getBatchFor needs to handle an argument
        // right now we have a fixed batch size that we use in order to refill the queues
        if(sp.getQueue().remainingCapacity() > BATCH_SIZE * executions.size()) {
            for(Execution e : executions) {
                List<UIMTask> tasks = new ArrayList<UIMTask>();
                for(long id : orchestrator.getBatchFor(e)) {
                    sp.getQueue().add(new UIMTask(getMetaDataRecord(id), sp));
                }
            }
        }
    }

    private MetaDataRecord<?> getMetaDataRecord(long id) {
        // TODO
        return null;
    }


    private void fillStepProcessorQueue(StepProcessor sp, StepProcessor previous) {
        // TODO pass tasks from one queue to another
        
    }


    public static void main(String... args) {
        LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>();
        for(int i = 0; i < 100; i++) {
            queue.add(new DummyRunnable());
        }
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(1, 10, StepProcessor.KEEP_ALIVE_TIME, TimeUnit.MILLISECONDS, queue);
        threadPoolExecutor.execute(new Thread());
    }

    private static class DummyRunnable implements Runnable {
        private static int count = 0;
        private int id = 0;
        public DummyRunnable() {
            id=count++;
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
