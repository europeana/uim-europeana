package eu.europeana.uim.workflow;

import java.util.List;

/**
 * UIM UIMWorkflow definition
 *
 * @author manu
 */
public interface Workflow {

    public Long getId();

    public String getName();

    public String getDescription();

    public void addStep(WorkflowStep step);

    public List<WorkflowStep> getSteps();

}
