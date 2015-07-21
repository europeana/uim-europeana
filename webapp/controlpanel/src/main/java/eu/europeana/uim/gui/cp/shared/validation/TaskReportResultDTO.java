package eu.europeana.uim.gui.cp.shared.validation;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public class TaskReportResultDTO implements IsSerializable {

	private int numberReports;
	
	private List<TaskReportDTO> reports;
	
	public TaskReportResultDTO() {
		super();
	}

	public TaskReportResultDTO(List<TaskReportDTO> reports, int number){
		this.reports = reports;
		this.numberReports = number;
	}
	
	public int getNumberRecords() {
		return numberReports;
	}

	public void setNumberRecords(int numberRecords) {
		this.numberReports = numberRecords;
	}

	public List<TaskReportDTO> getReports() {
		return reports;
	}

	public void setReports(List<TaskReportDTO> reports) {
		this.reports = reports;
	}	
}
