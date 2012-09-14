package eu.europeana.uim.gui.cp.shared.validation;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class representing a specific record as it is stored in SOLR and in Mongo
 * @author gmamakis
 *
 */
public class EdmRecordDTO implements IsSerializable{
	
	private EdmFieldRecordDTO solrRecord;
	private EdmFieldRecordDTO mongoRecord;
	
	public EdmFieldRecordDTO getSolrRecord() {
		return solrRecord;
	}
	public void setSolrRecord(EdmFieldRecordDTO solrRecord) {
		this.solrRecord = solrRecord;
	}
	public EdmFieldRecordDTO getMongoRecord() {
		return mongoRecord;
	}
	public void setMongoRecord(EdmFieldRecordDTO mongoRecord) {
		this.mongoRecord = mongoRecord;
	}
	
	
}
