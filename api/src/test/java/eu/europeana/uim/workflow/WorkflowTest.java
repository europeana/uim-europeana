package eu.europeana.uim.workflow;

import junit.framework.TestCase;
import static junit.framework.Assert.*;
import org.junit.Test;

/**
 * Test for UIMWorkflow construction
 *
 * @author manu
 */
public class WorkflowTest {

    @Test
    public void buildWorkfowRepresentation() {

        Workflow w = new UIMWorkflow(0l, "First workflow", "Test workflow");
        w.addStep(new MockPlugin("Plugin1"));
        w.addStep(new MockPlugin("Plugin2"));
        ProcessingContainer c = new ProcessingContainer();
        w.addStep(c);
        c.addStep(new MockPlugin("ParallelPlugin1"));
        c.addStep(new MockPlugin("ParallelPlugin2"));
        c.addStep(new MockPlugin("ParallelPlugin3"));
        w.addStep(new MockPlugin("FinalPlugin"));

        assertEquals(0l, w.getId().longValue());
        assertEquals("First workflow", w.getName());
        assertEquals("Test workflow", w.getDescription());
        assertEquals(3, c.getSteps().size());
        


    }
}
