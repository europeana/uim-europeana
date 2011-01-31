package eu.europeana.uim.workflow;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.TKey;
import eu.europeana.uim.api.IngestionPlugin;
import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.orchestration.StepProcessor;
import eu.europeana.uim.orchestration.UIMExecution;
import eu.europeana.uim.orchestration.UIMTask;
import org.junit.Test;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class UIMTaskTest {

    @Test
    public void success() {

        MetaDataRecord mdr = mock(MetaDataRecord.class);
        StepProcessor sp = mock(StepProcessor.class);
        WorkflowStep step = mock(IngestionPlugin.class);
        UIMExecution ae = mock(UIMExecution.class);
        UIMTask t = new UIMTask(mdr, sp, step);

        Executor e = new DirectExecutor();
        e.execute(t);

        verify(sp).addSuccess(t);
    }

    @Test
    public void failure() {
        MetaDataRecord mdr = mock(MetaDataRecord.class);
        StepProcessor sp = mock(StepProcessor.class);
        UIMExecution ae = mock(UIMExecution.class);
        RuntimeException failure = new RuntimeException("Terrible things happen");
        WorkflowStep step = new FailingPlugin(failure);
        UIMTask t = new UIMTask(mdr, sp, step);

        Executor e = new DirectExecutor();
        e.execute(t);

        verify(sp).addFailure(mdr, failure);
    }

    @Test
    public void changeStep() {
        MetaDataRecord mdr = mock(MetaDataRecord.class);
        StepProcessor sp = mock(StepProcessor.class);
        UIMExecution ae = mock(UIMExecution.class);
        WorkflowStep step = mock(WorkflowStep.class);

        UIMTask t = new UIMTask(mdr, sp, step);

        StepProcessor newProcessor = mock(StepProcessor.class);
        WorkflowStep newStep = mock(WorkflowStep.class);

        t.changeStep(newProcessor, newStep);
    }

    class DirectExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }

    class FailingPlugin implements IngestionPlugin {

        private final RuntimeException failure;

        @Override
        public String getIdentifier() {
            return "Failing plugin";
        }

        public FailingPlugin(RuntimeException failure) {
            this.failure = failure;
        }
        @Override
        public void processRecord(MetaDataRecord mdr) {
            throw failure;
        }

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TKey<MDRFieldRegistry, ?>[] getInputParameters() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TKey<MDRFieldRegistry, ?>[] getOutputParameters() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public TKey<MDRFieldRegistry, ?>[] getTransientParameters() {
			// TODO Auto-generated method stub
			return null;
		}
    }
}
