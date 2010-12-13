package eu.europeana.uim.orchestration;

import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.UimEntity;

import java.util.Date;

/**
* @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
*/
public class UIMExecution implements ActiveExecution {
    private final long id;
    private final UimEntity dataset;
    private final ProgressMonitor monitor;
    private final Workflow workflow;
    private final Date startTime;

    public UIMExecution(long id, UimEntity dataset, ProgressMonitor monitor, Workflow workflow) {
        this.id = id;
        this.dataset = dataset;
        this.monitor = monitor;
        this.workflow = workflow;
        this.startTime = new Date();
    }

    @Override
    public Date getStartTime() {
        return startTime;
    }

    @Override
    public Workflow getWorkflow() {
        return workflow;
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
