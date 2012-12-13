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
package eu.europeana.uim.plugin.thumbler.workflows;

import org.theeuropeanlibrary.uim.check.weblink.LinkCheckIngestionPlugin;

import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.util.LoggingIngestionPlugin;
import eu.europeana.uim.workflow.AbstractWorkflow;

/**
 * UIM Workflow for Linkchecking. The LinkCheckIngestionPlugin
 * is imported from the relevant TEL plugin.It is declared and exposed 
 * as a service in the linkchecker_plugin.xml blueprint declaration.
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 11 Jun 2012
 */
public class LinkCheckWorkflow<I> extends AbstractWorkflow<MetaDataRecord<I>,I> {
    /**
     * Creates a new instance of this class.
     */
    public LinkCheckWorkflow() {
        super("F: Link Validation",
                "Workflow which is used to submit links to be checked.");

        //Load metadata records from storage engine and offer them
        //to the declared plugins in batches. The size of the batch 
        //is determined by the relevant property defined by the batch workflow.
        setStart(new BatchWorkflowStart<I>());
        //Add the Link Checking Plugin as a step
        addStep(new LinkCheckIngestionPlugin<I>());
        //Performs logging of TKey<LoggingIngestionPlugin, Data> DATA_KEY 
        //typed key values, previously stored by the Linkchecking plugin.
        //These values are used in a later phase in order to generate 
        //BIRT reports. Note here that although this step is compatible with
        //both logging engine implementations (memory & database), you
        //should use the database (postgres specific module) in order for
        //this to work properly.
        addStep(new LoggingIngestionPlugin<MetaDataRecord<I>,I>());
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
