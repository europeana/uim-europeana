package eu.europeana.uim;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import eu.europeana.uim.api.Registry;
import eu.europeana.uim.store.StorageEngineAdapter;

public class UIMRegistryTest {

    private Registry registry = new UIMRegistry();

	@Before
	public void setup() {
		registry.addStorage(new StorageEngineAdapter(){});
		
	}
	
	@Test
	public void testStorageEngine() {
		assertNotNull(registry.getStorage());
	}
}
