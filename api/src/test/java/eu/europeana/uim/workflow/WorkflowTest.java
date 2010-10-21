package eu.europeana.uim.workflow;

import eu.europeana.uim.Orchestrator;
import eu.europeana.uim.Registry;
import eu.europeana.uim.orchestration.WorkflowProcessor;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.memory.MemoryStorageEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test for UIMWorkflow construction
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/META-INF/spring/test-bundle-context.xml")
public class WorkflowTest {

    @Autowired
    private Registry registry;

    @Test
    public void buildWorkfowRepresentation() {

        Workflow w = buildTestWorkflow();

        assertEquals(0l, w.getId().longValue());
        assertEquals("First workflow", w.getName());
        assertEquals("Test workflow", w.getDescription());
        WorkflowStep s = w.getSteps().get(2);
        assertTrue(s instanceof ProcessingContainer);
        assertEquals(3, ((ProcessingContainer)s).getSteps().size());
    }

    private Workflow buildTestWorkflow() {
        Workflow w = new UIMWorkflow(0l, "First workflow", "Test workflow");
        w.addStep(new MockPlugin("Plugin1"));
        w.addStep(new MockPlugin("Plugin2"));
        ProcessingContainer c = new ProcessingContainer();
        w.addStep(c);
        c.addStep(new MockPlugin("ParallelPlugin1"));
        c.addStep(new MockPlugin("ParallelPlugin2"));
        c.addStep(new MockPlugin("ParallelPlugin3"));
        w.addStep(new MockPlugin("FinalPlugin"));
        return w;
    }

    @Before
    public void setup() {
        registry.addStorage(new MemoryStorageEngine());
    }
    
    @Test
    public void runWorkflow() {
//        UIMFile testData = new UIMFile(registry);
        Orchestrator o = new MockUIMOrchestrator();
        Workflow w = buildTestWorkflow();
        Execution e = new Execution() { public long getId() { return 0;} };
        WorkflowProcessor processor = new WorkflowProcessor(e, w, o);

        processor.start();

    }

}
