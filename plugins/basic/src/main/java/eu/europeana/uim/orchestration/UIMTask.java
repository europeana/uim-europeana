package eu.europeana.uim.orchestration;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.WorkflowStep;

/**
 * A task that runs a workflow step against a MetaDataRecord.
 * If the execution fails, the Throwable is kept and the task is added to the failure list of its StepProcessor.
 * If the execution succeeds, the task is added to the success list of its StepProcessor.
 * 
 * TODO: maybe for optimization, support multiple MDRs per task...
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class UIMTask implements Runnable {

    private final MetaDataRecord<?> mdr;
    private final WorkflowStep step;
    private final StepProcessor processor;

    public UIMTask(MetaDataRecord<?> mdr, StepProcessor processor, WorkflowStep step) {
        this.mdr = mdr;
        this.processor = processor;
        this.step = step;
    }

    @Override
    public void run() {

        boolean failed = false;
        try {
            step.processRecord(mdr);
        } catch (Throwable t) {
            failed = true;
            processor.addFailure(mdr.getId(), t);
        } finally {
            if (!failed) {
                processor.addSuccess(mdr.getId());
            }
        }

    }
}
