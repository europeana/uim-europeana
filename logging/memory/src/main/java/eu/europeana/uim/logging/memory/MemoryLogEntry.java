package eu.europeana.uim.logging.memory;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.IngestionPlugin;
import eu.europeana.uim.api.LogEntry;
import eu.europeana.uim.store.Execution;

import java.util.Date;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class MemoryLogEntry implements LogEntry {

    private Date date;
    private Execution execution;
    private IngestionPlugin plugin;
    private MetaDataRecord<MDRFieldRegistry> mdr;
    private String message;

    @Override
    public Date getDate() {
        return date;
    }

    @Override
    public Execution getExecution() {
        return execution;
    }

    @Override
    public IngestionPlugin getPlugin() {
        return plugin;
    }

    @Override
    public MetaDataRecord<MDRFieldRegistry> getMetaDataRecord() {
        return mdr;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public MemoryLogEntry(Date date, Execution execution, IngestionPlugin plugin, MetaDataRecord<MDRFieldRegistry> mdr, String message) {
        this.date = date;
        this.execution = execution;
        this.plugin = plugin;
        this.mdr = mdr;
        this.message = message;
    }
}
