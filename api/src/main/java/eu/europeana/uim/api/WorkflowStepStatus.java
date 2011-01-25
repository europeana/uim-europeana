package eu.europeana.uim.api;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;

import java.io.Serializable;
import java.util.Map;

/**
 * The runtime status of a workflow execution
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface WorkflowStepStatus extends Serializable {

    WorkflowStep getStep();

    /** parent step, for now this can only be a ProcessingContainer **/
    WorkflowStep getParent();

    int queueSize();
    int successes();
    int failures();

    Map<MetaDataRecord<MDRFieldRegistry>, Throwable> getFailureDetail();
}
