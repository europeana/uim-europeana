package eu.europeana.uim.orchestration;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.StorageEngineException;

/**
 * Contract that describes record manipulation capabilities required by the StepProcessor
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface RecordProvider {

    /**
     * Gets the actual MetaDataRecord based on its ID
     */
    MetaDataRecord<FieldRegistry> getMetaDataRecord(long id);

    /**
     * Persists the given MetaDataRecord
     */
    void updateMetaDataRecord(MetaDataRecord<FieldRegistry> mdr) throws StorageEngineException;
}