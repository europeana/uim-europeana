package eu.europeana.uim.store.mongo;

import com.google.code.morphia.annotations.Entity;
import com.google.code.morphia.annotations.Reference;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.UimEntity;

import java.util.Date;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
@Entity
public class MongoExecution extends AbstractMongoEntity implements Execution {

    private boolean isActive;
    private Date startTime;
    private Date endTime;
    private String workflowIdentifier;

    @Reference
    private UimEntity dataSet;

    public MongoExecution() {
    }

    public MongoExecution(long id) {
        super(id);
    }

    public Boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
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

    public String getWorkflowName() {
        return workflowIdentifier;
    }

    public void setWorkflowName(String workflow) {
        this.workflowIdentifier = workflow;
    }
}
