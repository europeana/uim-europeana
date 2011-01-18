package eu.europeana.uim.orchestration;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.api.WorkflowStepStatus;

import java.util.Map;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class UIMWorkflowStepStatus implements WorkflowStepStatus {

    public UIMWorkflowStepStatus(WorkflowStep step, int queueSize, int successes, int failures, Map<MetaDataRecord<MDRFieldRegistry>, Throwable> failureDetail) {
        this.step = step;
        this.queueSize = queueSize;
        this.successes = successes;
        this.failures = failures;
        this.failureDetail = failureDetail;
    }

    private WorkflowStep step;
    private int queueSize, successes, failures;
    private Map<MetaDataRecord<MDRFieldRegistry>, Throwable> failureDetail;

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
    public Map<MetaDataRecord<MDRFieldRegistry>, Throwable> getFailureDetail() {
        return failureDetail;
    }
}
