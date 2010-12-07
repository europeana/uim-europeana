package eu.europeana.uim.workflow;

import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.orchestration.StepProcessor;
import eu.europeana.uim.orchestration.WorkflowProcessor;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class UIMStepProcessorProvider implements StepProcessorProvider {

    @Override
    public StepProcessor createStepProcessor(WorkflowStep step, WorkflowProcessor processor) {
        return new StepProcessor(step, processor);
    }
}
