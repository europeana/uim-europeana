package eu.europeana.uim.europeanaspecific.workflows;

import eu.europeana.uim.enrichment.EnrichmentPlugin;
import eu.europeana.uim.enrichment.LookupCreationPlugin;
import eu.europeana.uim.plugin.ingestion.IngestionPlugin;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

public class EnrichmentWorkflow<I> extends AbstractWorkflow<MetaDataRecord<I>,I> {



	public EnrichmentWorkflow(){
			super("F: Enrich Collection",
			        "Enrich and Ingest Records into SOLR and MONGODB");
				
			        setStart(new BatchWorkflowStart());
			       // addStep((IngestionPlugin<MetaDataRecord<I>, I>) new LookupCreationPlugin());
			        addStep((IngestionPlugin<MetaDataRecord<I>, I>) new EnrichmentPlugin());

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
