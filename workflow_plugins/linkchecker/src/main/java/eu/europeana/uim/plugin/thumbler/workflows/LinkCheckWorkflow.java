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

import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.util.LoggingIngestionPlugin;
import eu.europeana.uim.workflow.AbstractWorkflow;

/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 11 Jun 2012
 */
public class LinkCheckWorkflow extends AbstractWorkflow {
    /**
     * Creates a new instance of this class.
     */
    public LinkCheckWorkflow() {
        super("Link Validation",
                "Workflow which is used to submit links to be checked.");

        setStart(new BatchWorkflowStart());
        addStep(new LinkCheckIngestionPlugin());
        addStep(new LoggingIngestionPlugin());
    }

	public boolean isSavepoint(String pluginIdentifier) {
		return false;
	}

	public boolean isMandatory(String pluginIdentifier) {
		return false;
	}


}
