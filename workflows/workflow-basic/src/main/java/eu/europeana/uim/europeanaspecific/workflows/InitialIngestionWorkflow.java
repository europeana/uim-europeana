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

import eu.europeana.dedup.osgi.service.DeduplicationService;
import eu.europeana.uim.europeanaspecific.workflowstarts.httpzip.HttpZipWorkflowStart;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.workflow.AbstractWorkflow;


/**
 * Workflow used to import material to europeana from scratch
 * 
 * @author Georgios Markakis
 */
public class InitialIngestionWorkflow<I> extends AbstractWorkflow<MetaDataRecord<I>,I>{


	/**
	 * Initial Ingestion Workflow Constructor
	 */
	public InitialIngestionWorkflow(DeduplicationService service) {

		super("C: (Re)Import Data from Mint Mapping Tool",
        "Populates a UIM collection with specific EDM data from Mint");

        setStart(new HttpZipWorkflowStart<I>("HttpZipWorkflowStart","Downloads zipped EDM file",service));

        addStep(new DummyPlugin<I>());
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.Workflow#isMandatory(java.lang.String)
	 */
	public boolean isMandatory(String arg0) {
		return false;
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.Workflow#isSavepoint(java.lang.String)
	 */
	public boolean isSavepoint(String arg0) {
		return false;
	}

}
