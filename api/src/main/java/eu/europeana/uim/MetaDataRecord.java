package eu.europeana.uim;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class MetaDataRecord<N> {

	private HashMap<Field<N,?>, Object> fields = new HashMap<Field<N,?>, Object>();
	
	
	public MetaDataRecord() {
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
	@SuppressWarnings("unchecked")
	public <T> void addField(Field<N, ArrayList<T>> key, T value){
		if (!fields.containsKey(key)) {
			fields.put(key, new ArrayList<T>());
		}
		((ArrayList<T>)fields.get(key)).add(value);
	}
	
	
	
}
