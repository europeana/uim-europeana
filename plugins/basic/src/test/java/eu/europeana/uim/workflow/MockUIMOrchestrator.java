package eu.europeana.uim.workflow;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.common.parse.RecordMap;
import eu.europeana.uim.common.parse.RecordParser;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

/**
 * MockOrchestrator, for testing its clients
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class MockUIMOrchestrator implements Orchestrator {

    private HashMap<Long, MetaDataRecord<FieldRegistry>> testData =  new HashMap<Long, MetaDataRecord<FieldRegistry>>();

    public MockUIMOrchestrator() {

        // read test data
        // if we need this someplace else we may want to refactor this step into a separate TestData class

        InputStream stream = getClass().getResourceAsStream("/readingeurope.xml");
        RecordParser parser = new RecordParser();
        List<RecordMap> xml = null;
        try {
            xml = parser.parse(stream, "europeana:record");
        } catch (Exception e) {
            e.printStackTrace();
        }

        int id = 0;
        for (RecordMap record : xml) {
            MetaDataRecord<FieldRegistry> mdr = new MetaDataRecord<FieldRegistry>(id++);
            mdr.setField(FieldRegistry.title, record.getFirstByLocal("dc:title"));
            testData.put(mdr.getId(), mdr);
        }

    }



	@Override
    public Execution executeWorkflow(Workflow w, MetaDataRecord<?> mdr, ProgressMonitor monitor) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Execution executeWorkflow(Workflow w, Collection c, ProgressMonitor monitor) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Execution executeWorkflow(Workflow w, Request r, ProgressMonitor monitor) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public Execution executeWorkflow(Workflow w, Provider p, ProgressMonitor monitor) {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public long[] getBatchFor(Execution e) {
        long[] ids = new long[testData.keySet().size()];
        for(int i = 0; i < testData.keySet().size(); i++) {
            ids[i] = (Long) testData.keySet().toArray()[i];
        }
        return ids;
    }

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}
}
