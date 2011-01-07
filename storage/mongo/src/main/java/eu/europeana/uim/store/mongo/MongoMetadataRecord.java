package eu.europeana.uim.store.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.TKey;
import eu.europeana.uim.store.Request;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class MongoMetadataRecord<N> implements MetaDataRecord<N> {

    private DBObject object = new BasicDBObject();
    private Request request;
    private long lid;

    public MongoMetadataRecord(DBObject object, Request request, long lid) {
        this.object = object;
        this.request = request;
        this.lid = lid;
    }

    public Request getRequest() {
        return request;
    }

    public <T extends Serializable> void setField(TKey<N, T> nttKey, T value) {
    }

    public <T extends Serializable> void setQField(TKey<N, T> nttKey, String qualifier, T value) {
    }

    public <T> void addField(TKey<N, ArrayList<T>> nArrayListTKey, T value) {
    }

    public <T> void addQField(TKey<N, ArrayList<T>> nArrayListTKey, String qualifier, T value) {
    }

    public long getId() {
        return lid;
    }

    public void setObject(BasicDBObject object) {
        this.object = object;
    }

    public DBObject getObject() {
        return object;
    }
}
