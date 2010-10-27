package eu.europeana.uim.api;

import java.util.List;


/**
 * UIM UIMWorkflow definition
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface Workflow {

    public Long getId();

    public String getName();

    public String getDescription();

    public void addStep(WorkflowStep step);

    public List<WorkflowStep> getSteps();

}
