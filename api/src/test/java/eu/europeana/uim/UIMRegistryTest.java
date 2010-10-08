package eu.europeana.uim;

import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;

import eu.europeana.uim.store.memory.MemoryStorageEngine;


public class UIMRegistryTest {

	@BeforeClass
	public static void setup() {
		UIMRegistry.getInstance().addStorage(new MemoryStorageEngine());
	}
	
	@Test
	public void testStorageEngine() {
		assertNotNull(UIMRegistry.getInstance().getFirstStorage());
	}
}
