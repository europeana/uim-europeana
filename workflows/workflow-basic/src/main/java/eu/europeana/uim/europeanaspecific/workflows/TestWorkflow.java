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
package eu.europeana.uim.europeanaspecific.workflows;

import eu.europeana.uim.plugin.solr.service.SolrWorkflowPlugin;
import eu.europeana.uim.workflow.AbstractWorkflow;
import eu.europeana.uim.europeanaspecific.workflowstarts.oaipmh.OaiPMHWorkflowstart;


/**
 * 
 * @author Georgios Markakis
 */
public class TestWorkflow extends AbstractWorkflow {

	
	public TestWorkflow() {

		super("Europeana Test Workflow",
        "Demonstrates Basic functionality");

        setStart(new OaiPMHWorkflowstart("Europeana Test Workflow", "Demonstrates Basic functionality"));
        addStep(new SolrWorkflowPlugin());
	}
	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.Workflow#isSavepoint(java.lang.String)
	 */
	public boolean isSavepoint(String pluginIdentifier) {

		return false;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.Workflow#isMandatory(java.lang.String)
	 */
	public boolean isMandatory(String pluginIdentifier) {

		return false;
	}

}
