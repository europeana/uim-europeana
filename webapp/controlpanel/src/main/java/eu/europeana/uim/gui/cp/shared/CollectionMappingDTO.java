package eu.europeana.uim.gui.cp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class CollectionMappingDTO implements IsSerializable {
	
	private String originalCollection;
	private String newCollection;
	
	public String getOriginalCollection() {
		return originalCollection;
	}
	public void setOriginalCollection(String originalCollection) {
		this.originalCollection = originalCollection;
	}
	public String getNewCollection() {
		return newCollection;
	}
	public void setNewCollection(String newCollection) {
		this.newCollection = newCollection;
	}
	
	
}
