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
package eu.europeana.uim.mintclient.workflows;

import eu.europeana.uim.mintclient.service.MintUIMService;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.sugar.SugarCrmService;
import eu.europeana.uim.util.CollectionBatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

/**
 * Collection-based Workflow that initiates the import from Mint to UIM 
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 2 Apr 2012
 */
public class MintImportWorkflow<I> extends AbstractWorkflow<Collection<I>,I>{

	public MintImportWorkflow(MintUIMService mintservice,
			SugarCrmService sugarservice) {
		super("B: Import Repox data into Mint", "Imports data from Repox into Mint, thus creating a mapping session");
		setStart(new CollectionBatchWorkflowStart<I>());
	    addStep(new MintImportPlugin<I>(mintservice, sugarservice,
        		"Mint Importer Plugin","Preforms the migration from Repox to Mint"));
	    
	}

	@Override
	public boolean isMandatory(String arg0) {
		return false;
	}

	@Override
	public boolean isSavepoint(String arg0) {
		return false;
	}

}
