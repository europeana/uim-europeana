package eu.europeana.uim.deactivation;

import eu.europeana.uim.deactivation.service.DeactivationService;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

public class DeactivationWorkflow extends AbstractWorkflow {

	public DeactivationWorkflow(DeactivationService serv, String name, String description) {
		super("H. Deactivate Collection", "Deactivate a specific collection");
		setStart(new BatchWorkflowStart());
		addStep(new DeactivatePlugin(serv));
	}

	public boolean isMandatory(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isSavepoint(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
