package eu.europeana.uim.europeanaspecific.workflows;

import eu.europeana.uim.enrichment.EnrichmentPlugin;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

public class EnrichmentWorkflow extends AbstractWorkflow {



	public EnrichmentWorkflow(){
			super("E: Enrich Collection",
			        "Enrich and Ingest Records into SOLR and MONGODB");
	
			        setStart(new BatchWorkflowStart());
			        addStep(new EnrichmentPlugin());

		}
	
	@Override
	public boolean isSavepoint(String pluginIdentifier) {
		return false;
	}

	@Override
	public boolean isMandatory(String pluginIdentifier) {
		return false;
	}

}
