package eu.europeana.uim.workflow;

import eu.europeana.uim.MetaDataRecord;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

/**
 * Container to hold multiple steps that should run in parallel
 *
 * @author manu
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
    public void processRecord(MetaDataRecord<?> mdr) {
        System.out.println("I am a ProcessingContainer running following steps: " + Arrays.toString(steps.toArray()) );
    }
}
