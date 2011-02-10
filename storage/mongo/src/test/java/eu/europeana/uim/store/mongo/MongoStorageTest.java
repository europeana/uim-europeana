package eu.europeana.uim.store.mongo;

import com.mongodb.Mongo;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.store.AbstractStorageEngineTest;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class MongoStorageTest extends AbstractStorageEngineTest {

    @Override
    protected StorageEngine getStorageEngine() {
        try {
            MongoStorageEngine engine = new MongoStorageEngine("UIMTEST");
            Mongo m = new Mongo();
            m.dropDatabase("UIMTEST");
            engine.initialize();
            return engine;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return null;

    }
}
