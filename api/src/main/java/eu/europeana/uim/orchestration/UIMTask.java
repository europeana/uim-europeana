package eu.europeana.uim.orchestration;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.workflow.WorkflowStep;

/**
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
