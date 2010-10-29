package eu.europeana.uim.plugin.dummy;

import eu.europeana.uim.Field;
import eu.europeana.uim.FieldRegistry;
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
	public Field<FieldRegistry, ?>[] getInputParameters() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Field<FieldRegistry, ?>[] getOutputParameters() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Field<FieldRegistry, ?>[] getTransientParameters() {
		// TODO Auto-generated method stub
		return null;
	}

    @Override
    public void processRecord(MetaDataRecord<?> mdr) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
