package eu.europeana.uim.gui.gwt.server;

import com.google.gwt.user.client.rpc.IsSerializable;
import eu.europeana.uim.common.ProgressMonitor;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class UIMProgressMonitor implements ProgressMonitor, IsSerializable {

    private String name;
    private int total;
    private int status;
    private boolean done;
    private boolean cancelled;

    @Override
    public void beginTask(String task, int work) {
        this.name = task;
        this.total = work;
        this.status = 0;
    }

    @Override
    public void worked(int work) {
        if(status + work > total) {
            status = total;
            done();
        } else {
            this.status = status + work;
        }
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

    public String getName() {
        return name;
    }

    public int getTotal() {
        return total;
    }

    public int getStatus() {
        return status;
    }

    public boolean isDone() {
        return done;
    }
}
