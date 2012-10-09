package eu.europeana.uim.gui.cp.client.services;

import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.europeana.uim.gui.cp.shared.validation.FailedRecordResultDTO;

public interface FailedRecordServiceAsync {

	public void getFailedRecords(String collectionId, int offset, int maxSize, AsyncCallback<FailedRecordResultDTO> results);
}
