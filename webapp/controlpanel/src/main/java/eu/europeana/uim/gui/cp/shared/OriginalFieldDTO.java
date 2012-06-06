package eu.europeana.uim.gui.cp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * An original controlled vocabulary field accessible through its DTO
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public class OriginalFieldDTO implements IsSerializable {
	String field;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}
	
}
