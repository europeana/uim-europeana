package eu.europeana.uim.plugin;

import eu.europeana.uim.Field;
import eu.europeana.uim.FieldRegistry;

public interface IngestionPlugin {

	public String getIdentifier();
	public String getDescription();
	
	public Field<FieldRegistry,?>[] getInputParameters();
	public Field<FieldRegistry,?>[] getOutputParameters();
	public Field<FieldRegistry,?>[] getTransientParameters();
	
	
}
