package eu.europeana.uim.workflow;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.WorkflowStep;

import java.util.LinkedList;
import java.util.List;

/**
 * Container to hold multiple steps that should run in parallel
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class ProcessingContainer implements WorkflowStep {

    private List<WorkflowStep> steps;

    public ProcessingContainer() {
        this.steps = new LinkedList<WorkflowStep>();
    }

    public void addStep(WorkflowStep step) {
        this.steps.add(step);
    }

    public List<WorkflowStep> getSteps() {
        return this.steps;
    }

    @Override
    public String getIdentifier() {
        StringBuilder sb = new StringBuilder();
        sb.append("Processing container for steps ");
        for(WorkflowStep s : steps) {
            sb.append("'");
            sb.append(s.getIdentifier());
            sb.append("'");
            sb.append(" ");
        }
        return sb.toString();

    }

    @Override
    public void processRecord(MetaDataRecord mdr) {
        // this doesn't do a thing, as the container will never run tasks itself. Instead the StepProcessor takes care of that by fetching the steps
    }
}
