package eu.europeana.uim.gui.cp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A Mapping between an original controlled vocabulary field and an EDM field,
 * both accessible through their DTOs
 * 
 * @author Yorgos.Mamakis@ kb.nl
 * 
 */
public class MappingDTO implements IsSerializable {

	private OriginalFieldDTO original;
	private EdmFieldDTO mapped;

	public OriginalFieldDTO getOriginal() {
		return original;
	}

	public void setOriginal(OriginalFieldDTO original) {
		this.original = original;
	}

	public EdmFieldDTO getMapped() {
		return mapped;
	}

	public void setMapped(EdmFieldDTO mapped) {
		this.mapped = mapped;
	}

}
