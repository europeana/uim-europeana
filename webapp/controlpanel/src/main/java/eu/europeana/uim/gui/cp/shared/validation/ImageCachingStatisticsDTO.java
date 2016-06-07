package eu.europeana.uim.gui.cp.shared.validation;

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public class ImageCachingStatisticsDTO implements IsSerializable {
	
	/**
	 * Task report id.
	 */
	private String executionId;
    
    /**
     * Number of failed jobs.
     */
    private long failedJobs;
    
    /**
     * Number of pending jobs.
     */
    private long pendingJobs;
	
    /**
     * Number of successful jobs.
     */
    private long successfulJobs;
    
    /**
     * Total number of jobs per collection.
     */
    private long totalJobs;
//    
//    /**
//     * Total number of records per executionId.
//     */
//    private long total;
    
    /**
     * Creation date of the job
     */
    private String dateCreated;
    
    /**
     * Completion date of the job
     */
    private String dateCompleted;
    
    private String collectionId;
    
    private String providerId;

    public static final ProvidesKey<ImageCachingStatisticsDTO> KEY_PROVIDER = new ProvidesKey<ImageCachingStatisticsDTO>() {
        @Override
        public Object getKey(
                ImageCachingStatisticsDTO item) {
            return item == null
                    ? null
                    : item.getExecutionId();
        }
    };

	public String getExecutionId() {
		return executionId;
	}

	public void setExecutionId(String executionId) {
		this.executionId = executionId;
	}	
	
	public long getFailedJobs() {
		return failedJobs;
	}

	public void setFailedJobs(long failedJobs) {
		this.failedJobs = failedJobs;
	}

	public long getPendingJobs() {
		return pendingJobs;
	}

	public void setPendingJobs(long pendingJobs) {
		this.pendingJobs = pendingJobs;
	}

	public long getSuccessfulJobs() {
		return successfulJobs;
	}

	public void setSuccessfulJobs(long successfulJobs) {
		this.successfulJobs = successfulJobs;
	}

	public long getTotalJobs() {
		return totalJobs;
	}

	public void setTotalJobs(long totalJobs) {
		this.totalJobs = totalJobs;
	}
//
//	public long getTotal() {
//		return total;
//	}
//
//	public void setTotal(long total) {
//		this.total = total;
//	}

	public String getDateCreated() {
		return dateCreated;
	}

	public void setDateCreated(String dateCreated) {
		this.dateCreated = dateCreated;
	}

	public String getDateCompleted() {
		return dateCompleted;
	}

	public void setDateCompleted(String dateCompleted) {
		this.dateCompleted = dateCompleted;
	}

	public String getCollectionId() {
		return collectionId;
	}

	public void setCollectionId(String collectionId) {
		this.collectionId = collectionId;
	}

	public String getProviderId() {
		return providerId;
	}

	public void setProviderId(String providerId) {
		this.providerId = providerId;
	}
	
	@Override
	public String toString() {
		return "collection id: \"" + this.getCollectionId() + 
				"\" | provider id: \"" + this.getProviderId() +
				"\" | date creation: \"" + this.getDateCreated() +
				"\" | date completion: \"" + this.getDateCompleted() +
				"\" | execution id: \"" + this.getExecutionId() +
				"\" | failed jobs: \"" + this.getFailedJobs() +
				"\" | pending jobs: \"" + this.getPendingJobs() +
				"\" | successful jobs: \"" + this.getSuccessfulJobs() +
				"\" | total jobs: \"" + this.getTotalJobs();
//		"\" | total: \"" + this.getTotal();
	}
}
