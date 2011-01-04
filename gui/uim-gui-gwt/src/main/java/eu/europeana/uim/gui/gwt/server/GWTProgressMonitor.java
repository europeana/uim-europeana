package eu.europeana.uim.gui.gwt.server;

import com.google.gwt.user.client.rpc.IsSerializable;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.gui.gwt.shared.Execution;

/**
 * GWT implementation of a ProgressMonitor. Since we display things on the client and the monitor is on the server,
 * we have to pass through an intermediary model (the Execution). We need to poll it from the client, this is why
 * we update it here. (Once WebSockets are standard, we'll be able to use those).
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class GWTProgressMonitor implements ProgressMonitor, IsSerializable {

    private String name;
    private int total;
    private int status;
    private boolean done;
    private boolean cancelled;
    private Execution execution;

    public GWTProgressMonitor() {
    }

    public GWTProgressMonitor(Execution execution) {
        this.execution = execution;
    }

    @Override
    public void beginTask(String task, int work) {
        this.name = task;
        this.total = work;
        this.status = 0;
        execution.setProgress(0);
        execution.setTotal(work);
        execution.setName(task);
    }

    @Override
    public void worked(int work) {
        if(status + work > total) {
            status = total;
            done();
        } else {
            this.status = status + work;
        }
        execution.setProgress(status);
    }

    @Override
    public void done() {
        this.done = true;
    }

    @Override
    public void subTask(String subtask) {
    }

    @Override
    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    @Override
    public boolean isCancelled() {
        return cancelled;
    }

    public boolean isDone() {
        return done;
    }

    public void setExecution(Execution execution) {
        this.execution = execution;
    }

    public String getName() {
        return name;
    }
}
