package eu.europeana.uim.api;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.TKey;

public interface IngestionPlugin {

	
	public TKey<MDRFieldRegistry,?>[] getInputParameters();
	public TKey<MDRFieldRegistry,?>[] getOutputParameters();
	public TKey<MDRFieldRegistry,?>[] getTransientParameters();

    String getIdentifier();
    public String getDescription();

    int getPreferredThreadCount();
    int getMaximumThreadCount();

    public void processRecord(MetaDataRecord mdr);
	
	
}
