package eu.europeana.uim;

import eu.europeana.uim.api.Registry;
import eu.europeana.uim.store.memory.MemoryStorageEngine;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "/META-INF/spring/test-bundle-context.xml")
public class UIMRegistryTest {

    @Autowired
    private Registry registry;

	@Before
	public void setup() {
		registry.setFallbackStore(new MemoryStorageEngine());
		
	}
	
	@Test
	public void testStorageEngine() {
		assertNotNull(registry.getActiveStorage());
	}
}
