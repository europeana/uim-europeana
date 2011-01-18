package eu.europeana.uim.plugin.dummy;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.TKey;
import eu.europeana.uim.api.IngestionPlugin;

import java.util.logging.Logger;

public class DummyPlugin implements IngestionPlugin {

    private static int counter = 0;

    private static Logger log = Logger.getLogger(DummyPlugin.class.getName());

	private String description;

	public DummyPlugin() {
	}
	

	public String getIdentifier() {
		return DummyPlugin.class.getSimpleName();
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
        counter++;
        if(counter % 50 == 0) {
            log.info("Dummy plugin is processing MDR " + mdr.getId());
        }
    }
}
