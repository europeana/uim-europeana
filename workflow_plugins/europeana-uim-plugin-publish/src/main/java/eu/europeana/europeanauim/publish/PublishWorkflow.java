package eu.europeana.europeanauim.publish;

import eu.europeana.europeanauim.publish.service.PublishService;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

public class PublishWorkflow extends  AbstractWorkflow{

	
	public PublishWorkflow(PublishService publishService) {
		super("H: Publish Data", "Workflow that optimizes the Solr Index, cuilds uggesters and spellchecking");
		setStart(new BatchWorkflowStart());
		addStep(new PublishPlugin(publishService,"Publish Plugin", "Publish Plugin"));
	}

	public boolean isSavepoint(String pluginIdentifier) {
		return false;
	}

	public boolean isMandatory(String pluginIdentifier) {
		return false;
	}

}
