package eu.europeana.uim.workflow;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.orchestration.WorkflowProcessor;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.memory.MemoryStorageEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
        assertEquals(3, ((ProcessingContainer) s).getSteps().size());
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
    public void setup() throws Exception {

        // FIXME
        // we need to provision test data
        // for this refactoring the UIMStore to be separated from the Gogo action seems to be necessary

        registry.addStorage(new MemoryStorageEngine());
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PrintStream ps = new PrintStream(baos);
//        UIMStore s = new UIMStore(registry);
//        s.loadSampleData(registry.getActiveStorage(), ps);
    }

    @Test
    public void runWorkflow() throws Exception {

        Workflow w = buildTestWorkflow();

        Execution e = mock(Execution.class);
        when(e.getId()).thenReturn(0l);

        Orchestrator o = mock(Orchestrator.class);

        long[] a1 = new long[100];
        long[] a2 = new long[100];
        long[] a3 = new long[100];
        long[] a4 = new long[100];
        long[] a5 = new long[100];
        long[] a6 = new long[100];
        long[] a7 = new long[100];
        long[] a8 = new long[100];
        long[] a9 = new long[100];

        for (int i = 0; i < 100; i++) {
            a1[i] = i;
            a2[i] = 100 + i;
            a3[i] = 200 + i;
            a3[i] = 300 + i;
            a4[i] = 400 + i;
            a5[i] = 500 + i;
            a6[i] = 600 + i;
            a7[i] = 700 + i;
            a8[i] = 800 + i;
            a9[i] = 900 + i;
        }

        MetaDataRecord<FieldRegistry>[] mdrs = registry.getActiveStorage().getMetaDataRecords(a1);
        for(MetaDataRecord<FieldRegistry> r : mdrs) {
//            System.out.println("    " + r.getId());
        }


        when(o.getBatchFor(e)).thenReturn(a1, a2, a3, a4, a5, a6, a7, a8, a9);

        WorkflowProcessor processor = new WorkflowProcessor(e, w, o, registry);


        processor.start();

    }

}
