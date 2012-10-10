package eu.europeana.uim.gui.cp.server;

import java.util.ArrayList;
import java.util.List;

import com.mongodb.MongoException;

import eu.europeana.corelib.tools.lookuptable.FailedRecord;
import eu.europeana.corelib.tools.lookuptable.LookupState;
import eu.europeana.uim.gui.cp.client.services.FailedRecordService;
import eu.europeana.uim.gui.cp.server.engine.ExpandedOsgiEngine;
import eu.europeana.uim.gui.cp.shared.validation.FailedRecordDTO;
import eu.europeana.uim.gui.cp.shared.validation.FailedRecordResultDTO;

public class FailedRecordServiceImpl extends IntegrationServicesProviderServlet
		implements FailedRecordService {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5292609043023259706L;

	@Override
	public FailedRecordResultDTO getFailedRecords(String collectionId,
			int offset, int maxSize) {
		ExpandedOsgiEngine engine = getEngine();
		FailedRecordResultDTO result = null;
		try {

			List<FailedRecord> failedResults = engine.getDedupService()
					.getFailedRecords(collectionId);

			int max = maxSize;
			if (failedResults != null
					&& failedResults.size() - offset < maxSize) {
				max = failedResults.size() - offset;
			}

			if (max > 0) {
				List<FailedRecordDTO> failedRecordDTOList = new ArrayList<FailedRecordDTO>();
				for (FailedRecord failedRecord : failedResults.subList(offset,
						max)) {
					FailedRecordDTO failedRecordDTO = new FailedRecordDTO();
					failedRecordDTO.setCollectionId(failedRecord
							.getCollectionId());
					failedRecordDTO.setEuropeanaId(failedRecord
							.getEuropeanaId());
					failedRecordDTO.setOriginalId(failedRecord.getOriginalId());
					failedRecordDTO.setEdm(failedRecord.getXml());
					if (failedRecord.getLookupState().equals(
							LookupState.IDENTICAL)) {
						failedRecordDTO
								.setLookupState("The record was encountered twice");
					} else if (failedRecord.getLookupState().equals(
							LookupState.DUPLICATE_IDENTIFIER_ACROSS_COLLECTIONS)) {
						failedRecordDTO
								.setLookupState("The Europeana Identifier of the record was encountered twice for different records among split datasets");
					} 
					else if (failedRecord.getLookupState().equals(
							LookupState.DUPLICATE_RECORD_ACROSS_COLLECTIONS)) {
						failedRecordDTO
								.setLookupState("The record was encountered twice for different records among split datasets");
					}else {
						failedRecordDTO
								.setLookupState("The Europeana Identifier of the record was encountered twice for different records in the same dataset");
					}
					failedRecordDTOList.add(failedRecordDTO);
				}

				result = new FailedRecordResultDTO(failedRecordDTOList,
						failedResults.size());
			}
		} catch (MongoException e) {
			e.printStackTrace();
		}
		return result;
	}

}
