package eu.europeana.uim.gui.cp.shared.validation;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FailedRecordDTO implements IsSerializable{

	/**
	 * The generated europeana ID
	 */
	private String europeanaId;
	
	/**
	 * The original provider's id
	 */
	private String originalId;
	
	/**
	 * The collection Id
	 */
	private String collectionId;
	
	/**
	 * The reason it failed to ingest
	 */
	private String lookupState;
	
	/**
	 * The resulting EDM
	 */
	private String edm;

	public String getEuropeanaId() {
		return europeanaId;
	}

	public void setEuropeanaId(String europeanaId) {
		this.europeanaId = europeanaId;
	}

	public String getOriginalId() {
		return originalId;
	}

	public void setOriginalId(String originalId) {
		this.originalId = originalId;
	}

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String getLookupState() {
		return lookupState;
	}

	public void setLookupState(String lookupState) {
		this.lookupState = lookupState;
	}

	public String getEdm() {
		return edm;
	}

	public void setEdm(String edm) {
		this.edm = edm;
	}
	
	

}
