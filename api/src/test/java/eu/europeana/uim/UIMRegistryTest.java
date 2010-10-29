package eu.europeana.uim;

import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;

import eu.europeana.uim.api.Registry;
import eu.europeana.uim.store.memory.MemoryStorageEngine;

public class UIMRegistryTest {

    private Registry registry = new UIMRegistry();

	@Before
	public void setup() {
		registry.setFallbackStore(new MemoryStorageEngine());
		
	}
	
	@Test
	public void testStorageEngine() {
		assertNotNull(registry.getActiveStorage());
	}
}
