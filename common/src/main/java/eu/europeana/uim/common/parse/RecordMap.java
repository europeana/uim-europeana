package eu.europeana.uim.common.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class RecordMap extends HashMap<RecordField, List<String>> {

	private HashMap<RecordField, List<String>> localToPrefixed = new HashMap<RecordField, List<String>>();
	
	/**
	 */
	private static final long serialVersionUID = 1L;


	public String getFirst(RecordField arg0){
		List<String> list = super.get(arg0);
		if (list != null && !list.isEmpty()){
			return list.iterator().next();
		}
		return null;
	}


	public String getMerged(RecordField arg0, String separator){
		StringBuilder builder = new StringBuilder();
		List<String> list = super.get(arg0);
		if (list != null && !list.isEmpty()){
			for (String string : list) {
				if (builder.length() > 0) {
					builder.append(separator);
				}
				builder.append(string);
			}
			return builder.toString();
		}
		return null;
	}


	public String getFirstByLocal(String local){
		List<String> list = getValueByLocal(local);
		if (list != null && !list.isEmpty()){
			return list.iterator().next();
		}
		return null;
	}

	
	public List<String> getValueByLocal(String local){
		Set<RecordField> keys = new HashSet<RecordField>();
		Set<Entry<RecordField,List<String>>> set = localToPrefixed.entrySet();
		for (Entry<RecordField, List<String>> entry : set) {
			if (entry.getValue().contains(local)) {
				keys.add(entry.getKey());
			}
		}
		
		List<String> result = new ArrayList<String>();
		for (RecordField key : keys) {
			if (containsKey(key)) {
				result.addAll(get(key));
			}
		}
		return result;
	}
	
	

	@Override
	public List<String> put(RecordField arg0, List<String> arg1) {
		if (!localToPrefixed.containsKey(arg0)){
			localToPrefixed.put(arg0, new ArrayList<String>());
		}
		
		localToPrefixed.get(arg0).add(arg0.getLocal());
		return super.put(arg0, arg1);
	}

	
	

	public void add(RecordField arg0, String arg1) {
		if (!localToPrefixed.containsKey(arg0)){
			localToPrefixed.put(arg0, new ArrayList<String>());
		}
		
		localToPrefixed.get(arg0).add(arg0.getLocal());
		
		if (!super.containsKey(arg0)) {
			super.put(arg0, new ArrayList<String>());
		}
		super.get(arg0).add(arg1);
	}
	
	
}
