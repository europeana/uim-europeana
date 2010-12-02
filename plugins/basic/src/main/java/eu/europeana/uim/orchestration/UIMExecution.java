package eu.europeana.uim.orchestration;

import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.UimEntity;

/**
* @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
*/
public class UIMExecution implements ActiveExecution {
    private final long id;
    private final UimEntity dataset;
    private final ProgressMonitor monitor;

    public UIMExecution(long id, UimEntity dataset, ProgressMonitor monitor) {
        this.id = id;
        this.dataset = dataset;
        this.monitor = monitor;
    }

    public UimEntity getDataSet() {
        return dataset;
    }

    public ProgressMonitor getMonitor() {
        return monitor;
    }

    @Override
    public long getId() {
        return id;
    }

}
