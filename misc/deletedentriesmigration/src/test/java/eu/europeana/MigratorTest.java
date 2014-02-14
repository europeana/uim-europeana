package eu.europeana;

import eu.europeana.uim.storage.StorageEngineException;
import org.junit.Test;

/**
 * Unit test for simple App.
 */
public class MigratorTest 
{


    /**
     * Rigourous Test :-)
     */
    @Test 
    public void testMigration()
    {
    	Migrator m = new Migrator();
    	try {
    		Migrator.populateDeleteStatus();
		} catch (StorageEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
}
