package eu.europeana.uim.api;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.store.Execution;

/**
 * Service for the reporting of the processing, to be used by the orchestrator and eventually plugins
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface LoggingEngine {

    enum Level {
        INFO, WARNING, SEVERE
    }

    /**
     * Logs a message
     * @param level the level of the message
     * @param message the message string
     * @param execution the execution during which this log was issues
     * @param mdr the record for which this log was issued
     * @param plugin the plugin reporting the log
     */
    void log(Level level, String message, Execution execution, MetaDataRecord<MDRFieldRegistry> mdr, IngestionPlugin plugin);

    /**
     * Logs a processing duration for a single MDR
     * @param plugin the plugin
     * @mdr the identifier of the MDR
     */
    void logDuration(IngestionPlugin plugin, long mdr);

    /**
     * Logs a processing duration for a batch of MDRs
     * @param plugin the plugin
     * @param mdrs the identifiers of the processed MDRs
     */
    void logDuration(IngestionPlugin plugin, long[] mdrs);

}