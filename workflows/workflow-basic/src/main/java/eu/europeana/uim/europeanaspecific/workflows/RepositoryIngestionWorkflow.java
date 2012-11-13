/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.europeanaspecific.workflows;

import eu.europeana.uim.plugin.solr.service.SolrWorkflowPlugin;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 23 May 2012
 */
public class RepositoryIngestionWorkflow<I> extends AbstractWorkflow<MetaDataRecord<I>,I>{

	/**
	 * 
	 */
	public RepositoryIngestionWorkflow(){
		super("D: Dereference Collection",
		        "Dereference functionality of UIM");

		        setStart(new BatchWorkflowStart<I>());
		        SolrWorkflowPlugin<I> solrPlugin = new SolrWorkflowPlugin<I>();
		        addStep(solrPlugin);
		        isSavepoint(solrPlugin.getIdentifier());
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.Workflow#isSavepoint(java.lang.String)
	 */
	@Override
	public boolean isSavepoint(String pluginIdentifier) {
		return true;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.Workflow#isMandatory(java.lang.String)
	 */
	@Override
	public boolean isMandatory(String pluginIdentifier) {
		return false;
	}

}
