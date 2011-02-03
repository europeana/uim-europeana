package eu.europeana.uim.workflow.dummy;

import java.util.logging.Logger;

import eu.europeana.uim.api.AbstractWorkflow;
import eu.europeana.uim.api.IngestionPlugin;
import eu.europeana.uim.api.IngestionWorkflowStep;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.Workflow;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class DummyWorkflow extends AbstractWorkflow implements Workflow {

    private static Logger log = Logger.getLogger(DummyWorkflow.class.getSimpleName());

    public DummyWorkflow(Registry registry) {
    	//can but doesnt need to be.
    	//setStart(new BatchWorkflowStart());
    	setName("Dummy workflow");
    	setDescription("This awesome workflow demonstrates the capabilities of the UIM");
    	
        // that's a very exciting worklow
        IngestionPlugin plugin1 = registry.getPlugin("DummyPlugin");
        IngestionPlugin plugin2 = registry.getPlugin("DummyPlugin");
        
        addStep(new IngestionWorkflowStep(plugin1));
        addStep(new IngestionWorkflowStep(plugin2));
    }



}
