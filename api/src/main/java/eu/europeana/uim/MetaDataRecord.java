package eu.europeana.uim;

import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.UimEntity;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract MetaDataRecord. StorageEngine implementations provide their own implementation of it.
 *
 * TODO: documentation
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface MetaDataRecord<N> extends UimEntity {
    Request getRequest();

    <T extends Serializable> void setFirstField(TKey<N, T> key, T value);

    <T extends Serializable> T getFirstField(TKey<N, T> key);

    <T extends Serializable> void setQField(TKey<N, T> key, String qualifier, T value);

    @SuppressWarnings("unchecked")
    <T> void addField(TKey<N, ArrayList<T>> key, T value);

    <T extends Serializable> List<T> getField(TKey<N, ArrayList<T>> key);

    @SuppressWarnings("unchecked")
    <T> void addQField(TKey<N, ArrayList<T>> key, String qualifier, T value);

    <T extends Serializable> List<T> getQField(TKey<N, ArrayList<T>> key, String qualifier);


}
