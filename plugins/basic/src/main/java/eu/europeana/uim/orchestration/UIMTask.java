package eu.europeana.uim.orchestration;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.api.Task;
import eu.europeana.uim.api.TaskStatus;
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
public class UIMTask implements Task {

    private final MetaDataRecord<MDRFieldRegistry> mdr;

    // mutable fields - a task "wanders" through the workflow, i.e. through a chain of StepProcessor-s
    private StepProcessor processor;
    private WorkflowStep step;
    private TaskStatus status;


    public UIMTask(MetaDataRecord<MDRFieldRegistry> mdr, StepProcessor processor, WorkflowStep step) {
        this.mdr = mdr;
        this.processor = processor;
        this.step = step;
        this.status = TaskStatus.NEW;
        
    }

    @Override
    public void run() {

        boolean failed = false;
        try {
            status = TaskStatus.PROCESSING;
            step.processRecord(mdr);
        } catch (Throwable t) {
            failed = true;
            status = TaskStatus.FAILED;
            processor.addFailure(this, t);
        } finally {
            if (!failed) {
                status = TaskStatus.IN_QUEUE;
                processor.addSuccess(this);
            }
        }
    }

    @Override
    public TaskStatus getStatus() {
        return status;
    }

    public void changeStep(StepProcessor nextProcessor, WorkflowStep nextStep) {
        if(this.status == TaskStatus.PROCESSING) {
            throw new RuntimeException("Can't change step of a processing task!");
        }
        this.processor = nextProcessor;
        this.step = nextStep;
    }

    @Override
    public void markDone() {
        this.status = TaskStatus.DONE;
    }

    public void save(RecordProvider handler) throws StorageEngineException {
        // can't wait for closures
        handler.updateMetaDataRecord(this.mdr);
    }

}
