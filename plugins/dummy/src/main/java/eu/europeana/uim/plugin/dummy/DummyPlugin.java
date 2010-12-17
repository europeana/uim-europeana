package eu.europeana.uim.plugin.dummy;

import eu.europeana.uim.TKey;
import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.IngestionPlugin;

public class DummyPlugin implements IngestionPlugin {

	private String description;

	public DummyPlugin() {
	}
	

	public String getIdentifier() {
		return DummyPlugin.class.getName();
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	@Override
	public TKey<MDRFieldRegistry, ?>[] getInputParameters() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public TKey<MDRFieldRegistry, ?>[] getOutputParameters() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public TKey<MDRFieldRegistry, ?>[] getTransientParameters() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void processRecord(MetaDataRecord<?> mdr) {
        System.out.println("Dummy plugin is processing MDR " + mdr.getId());
    }
}
