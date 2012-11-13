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
package eu.europeana.uim.repoxclient.workflow;

import eu.europeana.uim.repox.RepoxUIMService;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.sugar.SugarCrmService;
import eu.europeana.uim.util.CollectionBatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 2 Apr 2012
 */
public class RepoxHarvestingWorkflow<I> extends AbstractWorkflow<Collection<I>,I>{

	
	public RepoxHarvestingWorkflow(RepoxUIMService repoxservice,
			SugarCrmService sugarservice) {
		super("A: Harvest Remote Repox Datasource", "Initiates a remote harvesting at " +
				"the remote Repox server");
		
        setStart(new CollectionBatchWorkflowStart<I>());
        addStep(new RepoxInitHarvestingPlugin<I>(repoxservice, sugarservice,
        		"Repox Harvesting Plugin","Preforms the remote operation"));
	}
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.Workflow#isMandatory(java.lang.String)
	 */
	@Override
	public boolean isMandatory(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.Workflow#isSavepoint(java.lang.String)
	 */
	@Override
	public boolean isSavepoint(String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
