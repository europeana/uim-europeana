package eu.europeana.uim.gui.cp.client.services;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import eu.europeana.uim.gui.cp.shared.validation.FailedRecordResultDTO;

@RemoteServiceRelativePath("failedrecords")
public interface FailedRecordService extends RemoteService{

	/**
	 * Retrieval method for failed records
	 * @param collectionId The collection id to search for
	 * @param offset The start of records
	 * @param maxSize The number of records to retrieve
	 * @return Failed records
	 */
	public FailedRecordResultDTO getFailedRecords(String collectionId, int offset, int maxSize);
}
