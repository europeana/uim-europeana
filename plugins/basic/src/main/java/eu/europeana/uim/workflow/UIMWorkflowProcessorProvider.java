package eu.europeana.uim.workflow;

import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.orchestration.UIMOrchestrator;
import eu.europeana.uim.orchestration.WorkflowProcessor;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class UIMWorkflowProcessorProvider implements WorkflowProcessorProvider {

    private final StepProcessorProvider stepProcessorProvider;

    public UIMWorkflowProcessorProvider(StepProcessorProvider stepProcessorProvider) {
        this.stepProcessorProvider = stepProcessorProvider;
    }

    @Override
    public WorkflowProcessor createProcessor(Workflow w, UIMOrchestrator orchestrator, Registry registry) {
        return new WorkflowProcessor(w, orchestrator, registry, stepProcessorProvider);
    }
}
