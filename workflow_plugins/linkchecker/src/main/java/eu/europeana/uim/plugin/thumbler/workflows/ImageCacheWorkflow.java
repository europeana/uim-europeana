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

import eu.europeana.uim.plugin.thumbler.service.ThumblerPlugin;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.util.CollectionBatchWorkflowStart;
import eu.europeana.uim.util.LoggingIngestionPlugin;
import eu.europeana.uim.util.RecordAwareCBWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

/**
 * UIM Workflow for Image Caching. It is declared and exposed as a service 
 * in the linkchecker_plugin.xml blueprint declaration.
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 11 Jun 2012
 */  
public class ImageCacheWorkflow<I> extends AbstractWorkflow<Collection<I>,I>{
	
	
	/**
	 * Initialise the workflow by creating a new instance of this class.
	 */
	public ImageCacheWorkflow(){
        super("G: Image Caching",
                "Workflow which is used to cache selected images into MongoDB.");

        //Load metadata records from storage engine and offer them
        //to the declared plugins in batches. The size of the batch 
        //is determined by the relevant property defined by the batch workflow.
        setStart(new RecordAwareCBWorkflowStart<I>());
        //Add the Link Checking Plugin as a step
        addStep(new ThumblerPlugin<I>());
        //Performs logging of TKey<LoggingIngestionPlugin, Data> DATA_KEY 
        //typed key values, previously stored by the ThumblerPlugin plugin.
        //These values are used in a later phase in order to generate 
        //BIRT reports. Note here that although this step is compatible with
        //both logging engine implementations (memory & database), you
        //should use the database (postgres specific module) in order for
        //this to work properly.
        addStep(new LoggingIngestionPlugin());
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
