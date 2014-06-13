/**
 * 
 */
package eu.europeana.uim.neo4jplugin.service;

import eu.europeana.uim.neo4jplugin.impl.EDMRepositoryOSGIServiceProvider;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

/**
 * @author geomark
 *
 */
public class GraphImporterWorkflow<I> extends AbstractWorkflow<MetaDataRecord<I>,I> {

	public GraphImporterWorkflow(EDMRepositoryOSGIServiceProvider provider) {
		super("GraphImporterWorkflow", "GraphImporterWorkflow");
        setStart(new BatchWorkflowStart<I>());
        
        GraphImporterPlugin<I> graphPlugin = new GraphImporterPlugin<I>(provider);
        addStep(graphPlugin);
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.Workflow#isSavepoint(java.lang.String)
	 */
	@Override
	public boolean isSavepoint(String pluginIdentifier) {
		return false;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.workflow.Workflow#isMandatory(java.lang.String)
	 */
	@Override
	public boolean isMandatory(String pluginIdentifier) {
		return false;
	}
}
