package eu.europeana.uim.workflow;

import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.orchestration.UIMOrchestrator;
import eu.europeana.uim.orchestration.WorkflowProcessor;

/**
 * @author manu
 */
public interface WorkflowProcessorProvider {

    WorkflowProcessor createProcessor(Workflow w, UIMOrchestrator o, Registry r);
}
