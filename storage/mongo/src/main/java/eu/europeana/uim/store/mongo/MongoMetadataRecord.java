package eu.europeana.uim.store.mongo;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.TKey;
import eu.europeana.uim.store.Request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class MongoMetadataRecord<N> implements MetaDataRecord<N> {

    private DBObject object = new BasicDBObject();
    private Request request;

    public MongoMetadataRecord(DBObject object, Request request, long lid) {
        this.object = object;
        this.request = request;
        object.put(AbstractMongoEntity.LID, lid);
        object.put("request", request.getId());
    }

    public long getId() {
        return (Long) object.get(AbstractMongoEntity.LID);
    }

    public void setObject(BasicDBObject object) {
        this.object = object;
    }

    public DBObject getObject() {
        return object;
    }

    public Request getRequest() {
        return request;
    }

    public <T extends Serializable> void setFirstField(TKey<N, T> nttKey, T value) {
        BasicDBObject unqualifiedFields = (BasicDBObject) object.get(fieldName(nttKey.getFullName()));
        if(unqualifiedFields == null) {
            unqualifiedFields = new BasicDBObject();
            object.put(fieldName(nttKey.getFullName()), unqualifiedFields);
        }
        unqualifiedFields.put("field0", value);
    }

    public <T extends Serializable> T getFirstField(TKey<N, T> nttKey) {
        return (T) ((DBObject)object.get(fieldName(nttKey.getFullName()))).get(fieldName(nttKey.getFullName()));
    }

    public <T extends Serializable> void setQField(TKey<N, T> nttKey, String qualifier, T value) {
        BasicDBObject qualifiedFields = (BasicDBObject) object.get(fieldName(nttKey.getFullName()));
        if(qualifiedFields == null) {
            qualifiedFields = new BasicDBObject();
            object.put(fieldName(nttKey.getFullName()), qualifiedFields);
        }
        BasicDBObject values = (BasicDBObject) qualifiedFields.get(qualifier);
        if(values == null) {
            values = new BasicDBObject();
            qualifiedFields.put(qualifier, values);
        }
        values.put("field0", value);
    }

    public <T> void addField(TKey<N, ArrayList<T>> nArrayListTKey, T value) {
        BasicDBObject unqualifiedFields = (BasicDBObject) object.get(fieldName(nArrayListTKey.getFullName()));
        if(unqualifiedFields == null) {
            unqualifiedFields = new BasicDBObject();
            object.put(fieldName(nArrayListTKey.getFullName()), unqualifiedFields);
        }
        unqualifiedFields.put("field" + new Integer(unqualifiedFields.size()).toString(), value);
    }

    public <T> void addQField(TKey<N, ArrayList<T>> nArrayListTKey, String qualifier, T value) {
        BasicDBObject qFields = (BasicDBObject) object.get(fieldName(nArrayListTKey.getFullName()));
        if(qFields == null) {
            qFields = new BasicDBObject();
            object.put(fieldName(nArrayListTKey.getFullName()), qFields);

        }
        BasicDBObject values = (BasicDBObject) qFields.get(qualifier);
        if(values == null) {
            values = new BasicDBObject();
            qFields.put(qualifier, values);
        }
        values.put("field" + new Integer(values.size()).toString(), value);
    }

    public <T extends Serializable> List<T> getField(TKey<N, ArrayList<T>> nttKey) {
        List<T> res = new ArrayList<T>();
        BasicDBObject values = (BasicDBObject) object.get(fieldName(nttKey.getFullName()));
        for (String s : values.keySet()) {
            if(s.startsWith("field")) {
                res.add((T)values.get(s));
            }
        }
        return res;
    }

    public <T extends Serializable> List<T> getQField(TKey<N, ArrayList<T>> nttKey, String qualifier) {
        List<T> res = new ArrayList<T>();
        BasicDBObject data = (BasicDBObject) object.get(fieldName(nttKey.getFullName()));
        if(data == null) {
            data = new BasicDBObject();
            object.put(fieldName(nttKey.getFullName()), data);
        }
        BasicDBObject qualifiedValues = (BasicDBObject) data.get(qualifier);
        if(qualifiedValues == null) {
            qualifiedValues = new BasicDBObject();
            data.put(qualifier, qualifiedValues);
        }
        for (String s : qualifiedValues.keySet()) {
            if(s.startsWith("field")) {
                res.add((T)qualifiedValues.get(s));
            }
        }
        return res;
    }

    private String fieldName(String name) {
        return name.replaceAll(".", "_");
    }
}
