package eu.europeana.uim.common.parse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

public class RecordMap extends HashMap<RecordField, String> {

	private HashMap<RecordField, List<String>> localToPrefixed = new HashMap<RecordField, List<String>>();
	
	/**
	 */
	private static final long serialVersionUID = 1L;


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
				result.add(get(key));
			}
		}
		return result;
	}

	

	/* (non-Javadoc)
	 * @see java.util.HashMap#put(java.lang.Object, java.lang.Object)
	 */
	@Override
	public String put(RecordField arg0, String arg1) {
		if (!localToPrefixed.containsKey(arg0)){
			localToPrefixed.put(arg0, new ArrayList<String>());
		}
		
		localToPrefixed.get(arg0).add(arg0.getLocal());
		return super.put(arg0, arg1);
	}
	
	
	
}
