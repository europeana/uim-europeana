package eu.europeana.uim.workflow;

import java.util.LinkedList;
import java.util.List;

import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.api.WorkflowStep;

public class UIMWorkflow implements Workflow {

    private Long id;
    private String name;
    private String description;
    private List<WorkflowStep> steps;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    public UIMWorkflow(Long id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
        steps = new LinkedList<WorkflowStep>();
    }

    @Override
    public Long getId() {
        return this.id;
    }

    @Override
    public void addStep(WorkflowStep step) {
        steps.add(step);
    }

    @Override
    public List<WorkflowStep> getSteps() {
        return this.steps;
    }    
}
