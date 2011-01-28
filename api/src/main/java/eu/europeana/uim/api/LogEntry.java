package eu.europeana.uim.api;

import java.io.Serializable;
import java.util.Date;

/**
 * Log entry
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface LogEntry<T extends Serializable> {

    LoggingEngine.Level getLevel();
    Date getDate();
    Long getExecutionId();
    String getPluginIdentifier();
    Long getMetaDataRecordId();
    T getMessage();

}
