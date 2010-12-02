package eu.europeana.uim;

import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.UimEntity;

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
public class MetaDataRecord<N> implements UimEntity {

	private HashMap<Field<N,?>, Object> fields = new HashMap<Field<N,?>, Object>();
	private HashMap<Field<N,?>, Map<String, Object>> qFields = new HashMap<Field<N,?>, Map<String, Object>>();

    private long id;
    private Request request;

    public MetaDataRecord() {

	}

    public MetaDataRecord(long id) {
    	this.id = id;
	}


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }



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
	public <T extends Serializable> void setField(Field<N, T> key, T value){
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
	public <T extends Serializable> void setQField(Field<N, T> key, String qualifier, T value){
		if (!qFields.containsKey(key)) {
			qFields.put(key, new HashMap<String, Object>());
		}
		qFields.get(key).put(qualifier, value);
	}


	/**
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public <T> void addField(Field<N, ArrayList<T>> key, T value){
		if (!fields.containsKey(key)) {
			fields.put(key, new ArrayList<T>());
		}
		((ArrayList<T>)fields.get(key)).add(value);
	}


	/**
	 * @param key
	 * @param value
	 */
	@SuppressWarnings("unchecked")
	public <T> void addQField(Field<N, ArrayList<T>> key, String qualifier, T value){
		if (!qFields.containsKey(key)) {
			qFields.put(key, new HashMap<String, Object>());
		}
		
		if (!qFields.get(key).containsKey(qualifier)) {
			qFields.get(key).put(qualifier, new ArrayList<T>());
		}
		((ArrayList<T>)qFields.get(key).get(qualifier)).add(value);
	}



}
