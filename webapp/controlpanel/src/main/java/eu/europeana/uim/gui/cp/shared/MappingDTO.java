package eu.europeana.uim.gui.cp.shared;

import java.util.List;

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
	private List<EdmFieldDTO> mapped;

	public OriginalFieldDTO getOriginal() {
		return original;
	}

	public void setOriginal(OriginalFieldDTO original) {
		this.original = original;
	}

	public List<EdmFieldDTO> getMapped() {
		return mapped;
	}

	public void setMapped(List<EdmFieldDTO> mapped) {
		this.mapped = mapped;
	}

}
