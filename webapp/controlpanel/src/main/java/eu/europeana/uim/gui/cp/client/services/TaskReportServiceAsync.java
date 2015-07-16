package eu.europeana.uim.gui.cp.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.europeana.uim.gui.cp.shared.validation.TaskReportResultDTO;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public interface TaskReportServiceAsync {

	public void getTaskReports(int offset, int maxSize, boolean isActive, String query, String newTaskReportQuery, long stopTaskId, AsyncCallback<TaskReportResultDTO> reports);
}
