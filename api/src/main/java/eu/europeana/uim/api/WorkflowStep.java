package eu.europeana.uim.api;

/**
 * Step in a UIM workflow. We use this in order to implement the command pattern for workflow execution.
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface WorkflowStep {

    String getIdentifier();

}
