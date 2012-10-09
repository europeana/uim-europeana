package eu.europeana.uim.gui.cp.shared.validation;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

public class FailedRecordResultDTO implements IsSerializable {

	private int numberRecords;
	
	private List<FailedRecordDTO> records;
	
	public FailedRecordResultDTO(){
		super();
	}

	public FailedRecordResultDTO(List<FailedRecordDTO> records, int number){
		this.records = records;
		this.numberRecords= number;
	}
	public int getNumberRecords() {
		return numberRecords;
	}

	public void setNumberRecords(int numberRecords) {
		this.numberRecords = numberRecords;
	}

	public List<FailedRecordDTO> getRecords() {
		return records;
	}

	public void setRecords(List<FailedRecordDTO> records) {
		this.records = records;
	}
	
}
