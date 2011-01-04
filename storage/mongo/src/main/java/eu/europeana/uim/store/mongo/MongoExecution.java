package eu.europeana.uim.store.mongo;

import com.google.code.morphia.annotations.Entity;
import eu.europeana.uim.store.Execution;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
@Entity
public class MongoExecution extends AbstractMongoEntity implements Execution {

    public MongoExecution() {
    }

    public MongoExecution(long id) {
        super(id);
    }


}
