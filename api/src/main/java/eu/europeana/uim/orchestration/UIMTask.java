package eu.europeana.uim.orchestration;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.workflow.WorkflowStep;

/**
 * A task that runs a workflow step against a MetaDataRecord.
 * If the execution fails, the Throwable is kept and the task is added to the failure list of its StepProcessor.
 * If the execution succeeds, the task is added to the success list of its StepProcessor.
 * 
 * TODO: support multiple MDRs per task
 *
 * @author manu
 */
public class UIMTask implements Runnable {

    private MetaDataRecord<?> mdr;
    private WorkflowStep step;
    private StepProcessor processor;

    private Throwable t;

    public UIMTask(MetaDataRecord<?> mdr, StepProcessor processor) {
        this.mdr = mdr;
        this.processor = processor;
        this.step = processor.getStep();
    }

    public Throwable getThrowable() {
        return this.t;
    }

    @Override
    public void run() {

        boolean failed = false;
        try {
            step.processRecord(mdr);
        } catch (Throwable t) {
            this.t = t;
            failed = true;
            processor.addFailure(this);
        } finally {
            if (!failed) {
                processor.addSuccess(this);
            }
        }

    }
}
