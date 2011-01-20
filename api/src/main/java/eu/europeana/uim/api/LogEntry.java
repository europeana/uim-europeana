package eu.europeana.uim.api;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.store.Execution;

import java.util.Date;

/**
 * Log entry
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface LogEntry {

    Date getDate();
    Execution getExecution();
    IngestionPlugin getPlugin();
    MetaDataRecord<MDRFieldRegistry> getMetaDataRecord();
    String getMessage();

}
