package eu.europeana.uim.workflow;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.orchestration.UIMOrchestrator;
import eu.europeana.uim.orchestration.WorkflowProcessor;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.memory.MemoryStorageEngine;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;
import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/META-INF/spring/test-bundle-context.xml")
public class OrchestratorTest {

    @Autowired
    private Registry registry;

    @Test
    public void testIdentifier() {
        Orchestrator o = new UIMOrchestrator(registry, new TestWorkflowProcessorProvider());
        assertNotNull(o.getIdentifier());

    }

    @Before
    public void setup() throws Exception {
        registry.addStorage(new MemoryStorageEngine());
    }

    @After
    public void tearDown() {
        registry.removeStorage(registry.getStorage());
    }


    @Test
    public void testWorkflowMdr() {

        final WorkflowProcessor mockProcessor = mock(WorkflowProcessor.class);

        WorkflowProcessorProvider p = new WorkflowProcessorProvider() {
            @Override
            public WorkflowProcessor createProcessor(Workflow w, UIMOrchestrator o, Registry r) {
                return mockProcessor;
            }
        };

        Orchestrator o = new UIMOrchestrator(registry, p);

        Workflow w = mock(Workflow.class);
        MetaDataRecord mdr = mock(MetaDataRecord.class);
        ProgressMonitor monitor = mock(ProgressMonitor.class);

        ActiveExecution e = o.executeWorkflow(w, mdr, monitor);

        assertNotNull(e);
        assertEquals(1, o.getTotal(e));
        assertTrue(e.getDataSet().equals(mdr));

        verify(mockProcessor).start();
    }

    @Test
    public void testWorkflowCollection() throws Exception {

        final int why = 4242;

        final WorkflowProcessor mockProcessor = mock(WorkflowProcessor.class);

        WorkflowProcessorProvider p = new WorkflowProcessorProvider() {
            @Override
            public WorkflowProcessor createProcessor(Workflow w, UIMOrchestrator o, Registry r) {
                return mockProcessor;
            }
        };

        Workflow w = mock(Workflow.class);
        ProgressMonitor monitor = mock(ProgressMonitor.class);
        Collection c = mock(Collection.class);

        // mock registry & storage
        Registry mockRegistry = mock(Registry.class);
        StorageEngine storage = mock(StorageEngine.class);
        when(mockRegistry.getStorage()).thenReturn(storage);
        when(storage.getTotalByCollection(c)).thenReturn(why);
        when(storage.getByCollection(c)).thenReturn(new long[4242]);
        Execution mockExecution = mock(Execution.class);
        when(storage.createExecution()).thenReturn(mockExecution);

        Orchestrator o = new UIMOrchestrator(mockRegistry, p);

        // this is what we actually test
        ActiveExecution e = o.executeWorkflow(w, c, monitor);

        assertNotNull(e);
        assertNotNull(e.getStartTime());
        assertNotNull(e.getMonitor());
        assertNotNull(e.getWorkflow());
        assertEquals(why, o.getTotal(e));
        assertTrue(e.getDataSet().equals(c));
        verify(mockProcessor).start();
        verify(mockExecution).setActive(true);
        verify(mockExecution).setWorkflowIdentifier(w.getName());

        for(int i = 0; i < 43; i++) {
            Thread.sleep(10);
            if(i < 42) {
                assertEquals(100, o.getBatchFor(e).length);
            } else if(i == 42) {
                assertEquals(42, o.getBatchFor(e).length);
            } else if(i == 43) {
                assertNull(o.getBatchFor(e));
            }

        }

        Thread.sleep(4000);
    }


    static class TestWorkflowProcessorProvider implements WorkflowProcessorProvider {

        List<WorkflowProcessor> provided = new ArrayList<WorkflowProcessor>();

        @Override
        public WorkflowProcessor createProcessor(Workflow w, UIMOrchestrator o, Registry r) {
            WorkflowProcessor p = mock(WorkflowProcessor.class);
            provided.add(p);
            return p;
        }

        public List<WorkflowProcessor> getProvided() {
            return provided;
        }
    }

    
}
