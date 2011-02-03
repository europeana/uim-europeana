package eu.europeana.uim.api;

import java.util.List;


/**
 * UIM UIMWorkflow definition
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface Workflow {

    public String getName();

    public String getDescription();

    public WorkflowStart getStart();

    public List<WorkflowStep> getSteps();

	public void setStart(WorkflowStart start);

}
