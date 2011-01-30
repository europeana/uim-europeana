package eu.europeana.uim.store.memory;

import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.UimEntity;

import java.util.Date;

public class MemoryExecution extends AbstractMemoryEntity implements Execution {

    private boolean isActive = false;
    private Date startTime, endTime;
    private UimEntity dataSet;
    private String workflowIdentifier;

    public MemoryExecution() {
		super();
	}

	public MemoryExecution(long id) {
		super(id);
	}

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public void setEndTime(Date endTime) {
        this.endTime = endTime;
    }

    public UimEntity getDataSet() {
        return dataSet;
    }

    public void setDataSet(UimEntity dataSet) {
        this.dataSet = dataSet;
    }

    @Override
    public Boolean isActive() {
        return isActive();
    }

    @Override
    public void setActive(boolean active) {
        this.isActive = active;
    }

    @Override
    public String getWorkflowName() {
        return workflowIdentifier;
    }

    @Override
    public void setWorkflowName(String identifier) {
        this.workflowIdentifier = identifier;
    }
}
