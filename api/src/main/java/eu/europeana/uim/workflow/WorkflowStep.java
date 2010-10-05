package eu.europeana.uim.workflow;

import eu.europeana.uim.MetaDataRecord;

/**
 * Step in a UIM workflow. We use this in order to implement the command pattern for workflow execution.
 *
 * @author manu
 */
public interface WorkflowStep {

    public void processRecord(MetaDataRecord<?> mdr);
}
