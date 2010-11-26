package eu.europeana.uim.workflow;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.WorkflowStep;
import eu.europeana.uim.orchestration.StepProcessor;
import eu.europeana.uim.orchestration.UIMTask;
import org.junit.Test;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.*;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class UIMTaskTest {

    @Test
    public void success() {

        MetaDataRecord mdr = mock(MetaDataRecord.class);
        StepProcessor sp = mock(StepProcessor.class);
        WorkflowStep step = mock(WorkflowStep.class);
        UIMTask t = new UIMTask(mdr, sp, step);

        Executor e = new DirectExecutor();
        e.execute(t);

        verify(sp).addSuccess(mdr.getId());
    }

    @Test
    public void failure() {
        MetaDataRecord mdr = mock(MetaDataRecord.class);
        StepProcessor sp = mock(StepProcessor.class);
        Throwable failure = new Exception("Terrible things happen");
        WorkflowStep step = new FailingPlugin(failure);
        UIMTask t = new UIMTask(mdr, sp, step);

        Executor e = new DirectExecutor();
        e.execute(t);

        verify(sp).addFailure(mdr.getId(), failure);
    }

    class DirectExecutor implements Executor {
        public void execute(Runnable r) {
            r.run();
        }
    }

    class FailingPlugin implements WorkflowStep {

        private final Throwable failure;

        public FailingPlugin(Throwable failure) {
            this.failure = failure;
        }
        @Override
        public void processRecord(MetaDataRecord<?> mdr) throws Throwable {
            throw failure;
        }
    }
}
