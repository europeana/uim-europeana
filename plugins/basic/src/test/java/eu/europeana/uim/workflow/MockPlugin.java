package eu.europeana.uim.workflow;

import eu.europeana.uim.TKey;
import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.IngestionPlugin;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class MockPlugin implements IngestionPlugin {

    public static final TKey<MDRFieldRegistry, String> inputField = TKey.register(MDRFieldRegistry.class, "inputField", String.class);
    public static final TKey<MDRFieldRegistry, String> outputField = TKey.register(MDRFieldRegistry.class, "outputField", String.class);

    private String id;

    public MockPlugin(String id) {
        this.id = id;
    }


    @Override
    public String getIdentifier() {
        return id;
    }

    @Override
    public String getDescription() {
        return "Test plugin " + id;
    }

    @Override
    public TKey<MDRFieldRegistry, ?>[] getInputParameters() {
        return new TKey[] { inputField };
    }

    @Override
    public TKey<MDRFieldRegistry, ?>[] getOutputParameters() {
        return new TKey[] { outputField };
    }

    @Override
    public TKey<MDRFieldRegistry, ?>[] getTransientParameters() {
        return null;
    }

    @Override
    public void processRecord(MetaDataRecord<?> mdr) {
        //System.out.println(getIdentifier() + " is processing MDR " + mdr.getId());
    }
}
