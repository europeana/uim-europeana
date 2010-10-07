package eu.europeana.uim.workflow;

import eu.europeana.uim.Orchestrator;
import eu.europeana.uim.orchestration.WorkflowProcessor;
import eu.europeana.uim.store.Execution;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

/**
 * Test for UIMWorkflow construction
 *
 * @author manu
 */
public class WorkflowTest {

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

    @Test
    public void runWorkflow() {
        Workflow w = buildTestWorkflow();
        Orchestrator o = new MockUIMOrchestrator();
        Execution e = new Execution() { };
        WorkflowProcessor processor = new WorkflowProcessor(e, w, o);

        processor.start();

    }

}
