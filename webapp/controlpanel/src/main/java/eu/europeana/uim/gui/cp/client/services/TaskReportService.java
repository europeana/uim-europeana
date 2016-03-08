package eu.europeana.uim.gui.cp.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import eu.europeana.uim.gui.cp.shared.validation.TaskReportResultDTO;

/**
 * 
 * @author Alena Fedasenka
 *
 */
@RemoteServiceRelativePath("taskreports")
public interface TaskReportService extends RemoteService {

	/**
	 * Retrieval method for task reports.
	 */
	public TaskReportResultDTO getTaskReports(int offset, int maxSize, boolean isActive, String filterQuery, String newTaskReportQuery, long stopTaskId);
}
