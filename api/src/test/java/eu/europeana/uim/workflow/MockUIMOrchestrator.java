package eu.europeana.uim.workflow;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.Orchestrator;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;

/**
 * MockOrchestrator, for testing its clients
 * @author manu
 */
public class MockUIMOrchestrator implements Orchestrator {

	@Override
    public Execution executeWorkflow(Workflow w, MetaDataRecord mdr) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Execution executeWorkflow(Workflow w, Collection c) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Execution executeWorkflow(Workflow w, Request r) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Execution executeWorkflow(Workflow w, Provider p) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long[] getBatchFor(Execution e) {
        // TODO test data
        return new long[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}
}
