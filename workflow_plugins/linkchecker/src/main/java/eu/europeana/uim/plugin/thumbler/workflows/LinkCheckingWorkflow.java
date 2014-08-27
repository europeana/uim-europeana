/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.europeana.uim.plugin.thumbler.workflows;

import eu.europeana.uim.plugin.thumbler.service.LinkCheckingPlugin;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.util.BatchWorkflowStart;
import eu.europeana.uim.workflow.AbstractWorkflow;

/**
 *
 * @author gmamakis
 */
public class LinkCheckingWorkflow<I> extends AbstractWorkflow<MetaDataRecord<I>,I>{

    public LinkCheckingWorkflow(){
       
        super("G: Link Checking",
                "Workflow which is used to trigger link checking.");
        setStart(new BatchWorkflowStart<I>());
        addStep(new LinkCheckingPlugin<I>("Link Checking Plugin", "Plugin that feeds links to the link checking application"));
    }

    @Override
    public boolean isSavepoint(String pluginIdentifier) {
        return false;
    }

    @Override
    public boolean isMandatory(String pluginIdentifier) {
       return false;
    }
    
}
