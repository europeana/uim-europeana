package eu.europeana.uim.orchestration;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface RecordProvider {

    MetaDataRecord<FieldRegistry> getMetaDataRecord(long id);
}
