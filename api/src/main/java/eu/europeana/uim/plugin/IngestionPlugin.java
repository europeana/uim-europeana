package eu.europeana.uim.plugin;

import eu.europeana.uim.Field;
import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.workflow.WorkflowStep;

public interface IngestionPlugin extends WorkflowStep {

	public String getIdentifier();
	public String getDescription();
	
	public Field<FieldRegistry,?>[] getInputParameters();
	public Field<FieldRegistry,?>[] getOutputParameters();
	public Field<FieldRegistry,?>[] getTransientParameters();

    public void processRecord(MetaDataRecord<?> mdr);
	
	
}
