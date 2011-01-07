package eu.europeana.uim.store.memory;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.TKey;
import eu.europeana.uim.store.Request;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/** Core and generic representation of a meta data record. 
 * 
 * @author andreas.juffinger@kb.nl
 *
 * @param <N>
 */
public class MemoryMetaDataRecord<N> implements MetaDataRecord<N> {

	private HashMap<TKey<N,?>, Object> fields = new HashMap<TKey<N,?>, Object>();
	private HashMap<TKey<N,?>, Map<String, Object>> qFields = new HashMap<TKey<N,?>, Map<String, Object>>();

    private long id;
    private Request request;

    public MemoryMetaDataRecord() {

	}

    public MemoryMetaDataRecord(long id) {
    	this.id = id;
	}


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



	@Override
    public Request getRequest() {
		return request;
	}


	public void setRequest(Request request) {
		this.request = request;
	}


	/**
	 * @param key
	 * @param value
	 */
	@Override
    public <T extends Serializable> void setField(TKey<N, T> key, T value){
		if (!fields.containsKey(key)) {
			fields.put(key, value);
		} else {
			//TODO: collission handling
		}
	}


	/**
	 * @param key
	 * @param value
	 */
	@Override
    public <T extends Serializable> void setQField(TKey<N, T> key, String qualifier, T value){
		if (!qFields.containsKey(key)) {
			qFields.put(key, new HashMap<String, Object>());
		}
		qFields.get(key).put(qualifier, value);
	}


	/**
	 * @param key
	 * @param value
	 */
	@Override
    @SuppressWarnings("unchecked")
	public <T> void addField(TKey<N, ArrayList<T>> key, T value){
		if (!fields.containsKey(key)) {
			fields.put(key, new ArrayList<T>());
		}
		((ArrayList<T>)fields.get(key)).add(value);
	}


	/**
	 * @param key
	 * @param value
	 */
	@Override
    @SuppressWarnings("unchecked")
	public <T> void addQField(TKey<N, ArrayList<T>> key, String qualifier, T value){
		if (!qFields.containsKey(key)) {
			qFields.put(key, new HashMap<String, Object>());
		}
		
		if (!qFields.get(key).containsKey(qualifier)) {
			qFields.get(key).put(qualifier, new ArrayList<T>());
		}
		((ArrayList<T>)qFields.get(key).get(qualifier)).add(value);
	}



}
