package eu.europeana.uim.workflow;

import eu.europeana.uim.Field;
import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.plugin.IngestionPlugin;

/**
 * @author manu
 */
public class MockPlugin implements IngestionPlugin {

    public static final Field<FieldRegistry, String> inputField = Field.register(FieldRegistry.class, "inputField", String.class);
    public static final Field<FieldRegistry, String> outputField = Field.register(FieldRegistry.class, "outputField", String.class);

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
    public Field<FieldRegistry, ?>[] getInputParameters() {
        return new Field[] { inputField };
    }

    @Override
    public Field<FieldRegistry, ?>[] getOutputParameters() {
        return new Field[] { outputField };
    }

    @Override
    public Field<FieldRegistry, ?>[] getTransientParameters() {
        return null;
    }

    @Override
    public void processRecord(MetaDataRecord<?> mdr) {
        System.out.println("MockPlugin is processing MDR " + mdr);
    }
}
