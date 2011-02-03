package eu.europeana.uim.orchestration;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.AbstractWorkflowStart;
import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.api.WorkflowStart;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.DataSet;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;


public class BatchWorkflowStart extends AbstractWorkflowStart implements WorkflowStart {

	private int batchSize = 250;

	public BatchWorkflowStart() {
		super(BatchWorkflowStart.class.getName());
	}

	public BatchWorkflowStart(int batchSize) {
		super(BatchWorkflowStart.class.getName());
		this.batchSize = batchSize;
	}


	@Override
	public int getPreferredThreadCount() {
		return 2;
	}

	@Override
	public int getMaximumThreadCount() {
		return 2;
	}


	@Override
	public <T> void initialize(ActiveExecution<T> visitor) throws StorageEngineException {
		long[] ids;

		DataSet dataSet = visitor.getDataSet();
		if (dataSet instanceof Provider) {
			ids = visitor.getStorageEngine().getByProvider((Provider)dataSet, false);
		} else if (dataSet instanceof Collection) {
			ids = visitor.getStorageEngine().getByCollection((Collection)dataSet);
		} else if (dataSet instanceof Request) {
			ids = visitor.getStorageEngine().getByRequest((Request)dataSet);
		} else if (dataSet instanceof MetaDataRecord) {
			ids = new long[]{((MetaDataRecord)dataSet).getId()};
		} else {
			throw new IllegalStateException("Unsupported dataset <" + visitor.getDataSet() + ">");
		}

		if (ids.length > batchSize) {
			int batches = (int)Math.ceil(1.0 * ids.length / batchSize);
			for (int i = 0; i < batches; i++) {
				int end = Math.min(ids.length, (i + 1) * batchSize);
				int start = i * batchSize;

				long[] batch = new long[end - start];
				System.arraycopy(ids, start, batch, 0, end-start);
				visitor.addBatch(batch);
			}

		} else {
			visitor.addBatch(ids);
		}

	}


	@SuppressWarnings("unchecked")
	@Override
	public <T> int createTasks(ActiveExecution<T> visitor) {
		try {
			long[] batch;
			synchronized(visitor) {
				batch = visitor.nextBatch();
			}

			if (batch != null) {
				MetaDataRecord[] mdrs = visitor.getStorageEngine().getMetaDataRecords(batch);
				for (MetaDataRecord mdr : mdrs) {
					visitor.getSuccess(this.getIdentifier()).add((T)new UIMTask(mdr, visitor.getStorageEngine()));
				}
				return mdrs.length;
			}


		} catch (StorageEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}


	@Override
	public void processRecord(MetaDataRecord mdr) {
	}

}
