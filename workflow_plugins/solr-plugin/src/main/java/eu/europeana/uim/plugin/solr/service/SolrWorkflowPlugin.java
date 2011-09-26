/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.plugin.solr.service;


import java.util.Collections;
import java.util.List;


import eu.europeana.uim.model.GlobalModelRegistry;
import eu.europeana.uim.model.qualifier.AgentRelation;
import eu.europeana.uim.model.qualifier.ConceptLevel;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.api.AbstractIngestionPlugin;
import eu.europeana.uim.api.CorruptedMetadataRecordException;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPlugin;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;

/**
 * This is the main class implementing the UIM functionality for 
 * the solr workflow plugin exposed as an OSGI service.
 * 
 * @author Georgios Markakis
 *
 */
public class SolrWorkflowPlugin extends AbstractIngestionPlugin {


	public SolrWorkflowPlugin() {
		super("solr_workflow", "Solr Repository Ingestion Plugin");
	}




	/* (non-Javadoc)
	 * @see eu.europeana.uim.api.IngestionPlugin#processRecord(eu.europeana.uim.MetaDataRecord, eu.europeana.uim.api.ExecutionContext)
	 */
	public   <I> boolean processRecord(MetaDataRecord<I> mdr, ExecutionContext<I> context)	
    throws IngestionPluginFailedException, CorruptedMetadataRecordException{

		//mdr.getQField(GlobalModelRegistry.AGENT, ConceptLevel.AGGREGATION);

		/*

		mdr.getQField(GlobalModelRegistry.AGENT, new HashSet<Enum<?>>(){{
		
			add(ConceptLevel.AGGREGATION);
			add(AgentRelation.CREATOR);
		}
		});
		 * 
		 */
		return false;
	}



	
	public TKey<?, ?>[] getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public TKey<?, ?>[] getOptionalFields() {
	     return new TKey[0];
	}

	public TKey<?, ?>[] getOutputFields() {
	     return new TKey[0];
	}

	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	public List<String> getParameters() {
        return Collections.EMPTY_LIST;
	}

	public int getPreferredThreadCount() {
		return 5;
	}

	public int getMaximumThreadCount() {
		return 10;
	}

	public void initialize(ExecutionContext context)
			throws IngestionPluginFailedException {
		// TODO Put initialization code here (Sugarcrm Plugin change state)
		
	}

	public void completed(ExecutionContext context)
			throws IngestionPluginFailedException {
		// TODO Put finalization code here (Sugarcrm Plugin change state)
		
	}
	

}
