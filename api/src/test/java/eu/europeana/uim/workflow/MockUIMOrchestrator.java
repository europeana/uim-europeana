package eu.europeana.uim.workflow;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.Orchestrator;
import eu.europeana.uim.common.ese.ESEParser;
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
        ESEParser parser = new ESEParser();
        List<HashMap<String,Object>> xml = null;
        try {
            xml = parser.importXml(stream);
        } catch (Exception e) {
            e.printStackTrace();
        }

        for (HashMap<String, Object> record : xml) {
            MetaDataRecord<FieldRegistry> mdr = new MetaDataRecord<FieldRegistry>();
            mdr.setField(FieldRegistry.field0, (String) record.get("title"));
            testData.put(mdr.getId(), mdr);
        }

    }

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
