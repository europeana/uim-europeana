package eu.europeana.uim.common.parse;


public interface RecordHandler {

	public String getRecordElement();
	
	public void record(RecordMap record);
	
}
