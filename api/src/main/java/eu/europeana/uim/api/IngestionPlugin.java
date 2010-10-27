package eu.europeana.uim.api;

import eu.europeana.uim.Field;
import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;

public interface IngestionPlugin extends WorkflowStep {

	public String getIdentifier();
	public String getDescription();
	
	public Field<FieldRegistry,?>[] getInputParameters();
	public Field<FieldRegistry,?>[] getOutputParameters();
	public Field<FieldRegistry,?>[] getTransientParameters();

    public void processRecord(MetaDataRecord<?> mdr);
	
	
}
