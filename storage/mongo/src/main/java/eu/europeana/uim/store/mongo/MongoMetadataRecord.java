package eu.europeana.uim.store.mongo;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.TKey;
import eu.europeana.uim.store.Request;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class MongoMetadataRecord<N> implements MetaDataRecord<N> {

    public Request getRequest() {
        return null;
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
        return 0;
    }
}
