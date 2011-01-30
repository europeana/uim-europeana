package eu.europeana.uim.store.memory;

import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.store.AbstractStorageEngineTest;

public class MemoryStorageEngineTest extends AbstractStorageEngineTest {

	@Override
	protected StorageEngine getStorageEngine() {
		return new MemoryStorageEngine();
	}

}
