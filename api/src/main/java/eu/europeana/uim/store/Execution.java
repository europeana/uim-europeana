package eu.europeana.uim.store;

import java.util.Date;

public interface Execution extends UimEntity {

    Boolean isActive();
    void setActive(boolean active);

    Date getStartTime();
    void setStartTime(Date start);

    Date getEndTime();
    void setEndTime(Date end);

    UimEntity getDataSet();
    void setDataSet(UimEntity entity);

    String getWorkflowIdentifier();
    void setWorkflowIdentifier(String identifier);

}
