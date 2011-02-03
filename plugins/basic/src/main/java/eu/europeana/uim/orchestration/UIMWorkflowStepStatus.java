package eu.europeana.uim.orchestration;

import java.util.Map;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.api.WorkflowStepStatus;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class UIMWorkflowStepStatus implements WorkflowStepStatus {

    public UIMWorkflowStepStatus(WorkflowStep step, int queueSize, int successes, int failures, Map<MetaDataRecord, Throwable> failureDetail) {
        this.step = step;
        this.queueSize = queueSize;
        this.successes = successes;
        this.failures = failures;
        this.failureDetail = failureDetail;
    }

    private WorkflowStep step, parent;
    private int queueSize, successes, failures;
    private Map<MetaDataRecord, Throwable> failureDetail;

    @Override
    public WorkflowStep getStep() {
        return step;
    }

    @Override
    public int queueSize() {
        return queueSize;
    }

    @Override
    public int successes() {
        return successes;
    }

    @Override
    public int failures() {
        return failures;
    }

    @Override
    public Map<MetaDataRecord, Throwable> getFailureDetail() {
        return failureDetail;
    }

    @Override
    public WorkflowStep getParent() {
        return parent;
    }

    public void setParent(WorkflowStep parent) {
        this.parent = parent;
    }
}
