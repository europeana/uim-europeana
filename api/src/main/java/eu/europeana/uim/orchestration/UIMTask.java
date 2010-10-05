package eu.europeana.uim.orchestration;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.workflow.WorkflowStep;

/**
 * @author manu
 */
public class UIMTask implements Runnable {

    private MetaDataRecord<?> mdr;
    private WorkflowStep step;
    private WorkflowExecution parentExecution;

    private Throwable t;

    public UIMTask(MetaDataRecord<?> mdr, WorkflowStep step, WorkflowExecution parentExecution) {
        this.mdr = mdr;
        this.step = step;
        this.parentExecution = parentExecution;
    }

    public Throwable getThrowable() {
        return this.t;
    }

    @Override
    public void run() {

        try {
            step.processRecord(mdr);
        } catch(Throwable t) {
            this.t = t;    
        }

    }
}
