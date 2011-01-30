package eu.europeana.uim;

import java.io.Serializable;
import java.util.List;

import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.UimEntity;

/**
 * Abstract MetaDataRecord. StorageEngine implementations provide their own implementation of it.
 *
 * TODO: documentation
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface MetaDataRecord extends UimEntity {
    
	Request getRequest();

    String getIdentifier();
    
    void setIdentifier(String identifier);
    
    <N, T extends Serializable> void setFirstField(TKey<N, T> key, T value);

    <N, T extends Serializable> T getFirstField(TKey<N, T> key);

    <N, T extends Serializable> void setFirstQField(TKey<N, T> key, String qualifier, T value);

    <N, T extends Serializable> T getFirstQField(TKey<N, T> key, String qualifier);

    <N, T extends Serializable> void addField(TKey<N, T> key, T value);

    <N, T extends Serializable> List<T> getField(TKey<N, T> key);

    <N, T extends Serializable> void addQField(TKey<N, T> key, String qualifier, T value);

    <N, T extends Serializable> List<T> getQField(TKey<N, T> key, String qualifier);

}
