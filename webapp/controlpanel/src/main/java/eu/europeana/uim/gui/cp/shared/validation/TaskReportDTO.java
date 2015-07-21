package eu.europeana.uim.gui.cp.shared.validation;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public class TaskReportDTO implements IsSerializable {
	
	/**
	 * Task report id.
	 */
	private long taskId;
	
	/**
	 * Task report query.
	 */
    private String query;
    
    /**
     * Number of records processed.
     */
    private long processed;
    
    /**
     * Total number of records.
     */
    private long total;

    /**
     * Status of a task report. Can take the following values:
     * INITIAL, PROCESSING, FINISHED, STOPPED.
     */
    private String status;
    
    /**
     * Creation date of a task report.
     */
    private String dateCreated;
    
    /**
     * Last update date of a task report
     */
    private String dateUpdated;

    public static final ProvidesKey<TaskReportDTO> KEY_PROVIDER = new ProvidesKey<TaskReportDTO>() {
        @Override
        public Object getKey(
                TaskReportDTO item) {
            return item == null
                    ? null
                    : item.getTaskId();
        }
    };

	public long getTaskId() {
		return taskId;
	}

	public void setTaskId(long taskId) {
		this.taskId = taskId;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public long getProcessed() {
		return processed;
	}

	public void setProcessed(long processed) {
		this.processed = processed;
	}

	public long getTotal() {
		return total;
	}

	public void setTotal(long total) {
		this.total = total;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		if (status != null
				&& (status.equalsIgnoreCase("initial")
				|| status.equalsIgnoreCase("processing")
				|| status.equalsIgnoreCase("stopped")
				|| status.equalsIgnoreCase("finished"))) {
			this.status = status;			
		} else {
			this.status = "INITIAL";			
		}
	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getDateUpdated() {
		return dateUpdated;
	}

	public void setDateUpdated(String dateUpdated) {
		this.dateUpdated = dateUpdated;
	}
}
