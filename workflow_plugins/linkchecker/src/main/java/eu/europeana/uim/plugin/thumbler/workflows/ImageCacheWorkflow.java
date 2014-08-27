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

import eu.europeana.uim.plugin.thumbler.service.ImageCachingPlugin;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

/**
 * UIM Workflow for Image Caching. It is declared and exposed as a service 
 * in the linkchecker_plugin.xml blueprint declaration.
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 11 Jun 2012
 */  
public class ImageCacheWorkflow<I> extends AbstractWorkflow<MetaDataRecord<I>,I>{
	
	
	/**
	 * Initialise the workflow by creating a new instance of this class.
	 */
	public ImageCacheWorkflow(){
        super("H: Image Caching",
                "Workflow which is used to cache selected images into MongoDB.");

        setStart(new BatchWorkflowStart<I>());
        addStep(new ImageCachingPlugin<I>("Link Checking Plugin", "Plugin that feeds links to the link checking application"));
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
