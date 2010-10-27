package eu.europeana.uim.common.parse;

import java.util.ArrayList;
import java.util.List;

public class MemoryRecordHandler implements RecordHandler {

	private final String recordElement;
	private final List<RecordMap> memory = new ArrayList<RecordMap>();
	
	public MemoryRecordHandler(String recordElement){
		this.recordElement = recordElement;
	}
	
	@Override
	public String getRecordElement() {
		return this.recordElement;
	}

	
	@Override
	public void record(RecordMap record) {
		memory.add(record);
	}

	
	/**
	 * @return the memory
	 */
	public List<RecordMap> getMemory() {
		return memory;
	}

	
	
}
