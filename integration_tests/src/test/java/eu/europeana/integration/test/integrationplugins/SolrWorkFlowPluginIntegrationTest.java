package eu.europeana.integration.test.integrationplugins;

import static org.junit.Assert.assertNotNull;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.junit.JUnit4TestRunner;

import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.common.MemoryProgressMonitor;
import eu.europeana.uim.europeanaspecific.workflows.InitialIngestionWorkflow;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.bean.CollectionBean;
import eu.europeana.uim.workflow.Workflow;

@RunWith(JUnit4TestRunner.class)
public class SolrWorkFlowPluginIntegrationTest extends
		AbstractEuropeanaIntegrationTest {

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testSolrWorkflowPlugin(){
		//Get registry from karaf
		Registry registry = getOsgiService(Registry.class);
		assertNotNull(registry);
		
		//Get workflow from karaf
		Workflow ingestionWorkflow = registry.getWorkflow(InitialIngestionWorkflow.class.getSimpleName());
		assertNotNull(ingestionWorkflow);
		
		//Get orchestrator
		Orchestrator orchestrator = registry.getOrchestrator();
		assertNotNull(orchestrator);
		
		//Create and load dataset
		Collection dataset = new CollectionBean();
		dataset.putValue(ControlledVocabularyProxy.MINTPUBLICATIONLOCATION, "http://sip-manager.isti.cnr.it/geomark/testrecs.zip");
		ActiveExecution execution = orchestrator.executeWorkflow(ingestionWorkflow,dataset);
		assertNotNull(execution);
		
		//Assert number of records
		MemoryProgressMonitor monitor = new MemoryProgressMonitor();
		execution.getMonitor().addListener(monitor);
		execution.waitUntilFinished();
		Assert.assertEquals(execution.getCompletedSize(),205);
	}
}
