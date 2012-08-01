package eu.europeana.uim.europeanaspecific.workflows;

import eu.europeana.uim.enrichment.EnrichmentPlugin;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

public class EnrichmentWorkflow extends AbstractWorkflow {

	@Override
	public boolean isSavepoint(String pluginIdentifier) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isMandatory(String pluginIdentifier) {
		// TODO Auto-generated method stub
		return false;
	}

	public EnrichmentWorkflow(){
			super("Enrich Collection",
			        "Enrich and Ingest Records into SOLR and MONGODB");
	
			        setStart(new BatchWorkflowStart());
	
			        addStep(new EnrichmentPlugin());
		}

}