package eu.europeana.uim.workflow;

import eu.europeana.uim.api.AbstractWorkflow;
import eu.europeana.uim.api.IngestionWorkflowStep;
import eu.europeana.uim.util.BatchWorkflowStart;

public class SyserrWorkflow extends AbstractWorkflow {

	public SyserrWorkflow(int batchSize, boolean randsleep) {
		setName(SyserrWorkflow.class.getSimpleName()); 
		setDescription("Simple workflow which uses a SyserrPlugins to fail all records");
		setStart(new BatchWorkflowStart(batchSize));

		addStep(new IngestionWorkflowStep(new SyserrPlugin("", 1, randsleep)));

	}


}
