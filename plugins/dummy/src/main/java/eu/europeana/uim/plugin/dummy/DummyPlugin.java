package eu.europeana.uim.plugin.dummy;

import eu.europeana.uim.plugin.IngestionPlugin;

public class DummyPlugin implements IngestionPlugin {

	private String identifier;
	private String description;

	public DummyPlugin() {
		super();
	}
	

	public String getIdentifier() {
		return identifier;
	}

	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}




	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
