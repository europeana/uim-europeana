package eu.europeana.uim.europeanaspecific.workflows;

import eu.europeana.uim.enrichment.LookupCreationPlugin;
import eu.europeana.uim.plugin.ingestion.IngestionPlugin;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

public class LookupTableWorkflow<I> extends AbstractWorkflow<MetaDataRecord<I>, I> {

	
	public LookupTableWorkflow() {
		super("E. Create Record Redirects", "Create Record Redirects between old and new record IDs");
		// TODO Auto-generated constructor stub
		   setStart(new BatchWorkflowStart());
	        addStep((IngestionPlugin<MetaDataRecord<I>, I>) new LookupCreationPlugin());
	}

	@Override
	public boolean isSavepoint(String pluginIdentifier) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public boolean isMandatory(String pluginIdentifier) {
		// TODO Auto-generated method stub
		return false;
	}

}
