package eu.europeana.uim.logging.memory;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.IngestionPlugin;
import eu.europeana.uim.api.LogEntry;
import eu.europeana.uim.api.LoggingEngine;
import eu.europeana.uim.store.Execution;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Simplistic implementation of the logging service.
 * In this implementation we do not care to keep track of the MDRs responsible for a duration. This feature would be useful in order to see exactly what MDR is causing what delay.
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class MemoryLoggingEngine implements LoggingEngine {

    private Map<Execution, List<LogEntry>> executionLogs = new HashMap<Execution, List<LogEntry>>();

    private Map<IngestionPlugin, List<Long>> durations = new HashMap<IngestionPlugin, List<Long>>();

    @Override
    public void log(Level level, String message, Execution execution, MetaDataRecord<MDRFieldRegistry> mdr, IngestionPlugin plugin) {
        List<LogEntry> logs = executionLogs.get(execution);
        if (logs == null) {
            logs = new ArrayList<LogEntry>();
            executionLogs.put(execution, logs);
        }
        logs.add(new MemoryLogEntry(new Date(), execution, plugin, mdr, message));
    }

    @Override
    public List<LogEntry> getExecutionLog(Execution execution) {
        return executionLogs.get(execution);
    }

    private List<Long> getDurations(IngestionPlugin plugin) {
        List<Long> d = durations.get(plugin);
        if (d == null) {
            d = new ArrayList<Long>();
            durations.put(plugin, d);
        }
        return d;
    }

    @Override
    public void logDuration(IngestionPlugin plugin, Long duration, long... mdrs) {
        List<Long> d = getDurations(plugin);
        // don't show this to hardcore statisticians
        for (int i = 0; i < mdrs.length; i++) {
            d.add(duration / mdrs.length);
        }
    }

    @Override
    public void logDuration(IngestionPlugin plugin, Long duration, int count) {
        List<Long> d = getDurations(plugin);
        // don't show this to hardcore statisticians
        for (int i = 0; i < count; i++) {
            d.add(duration / count);
        }
    }

    @Override
    public Long getAverageDuration(IngestionPlugin plugin) {
        long sum = 0l;
        List<Long> d = getDurations(plugin);
        for(Long l : d) {
            sum += l;
        }
        return sum / d.size();
    }
}
