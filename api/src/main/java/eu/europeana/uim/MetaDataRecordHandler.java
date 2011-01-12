package eu.europeana.uim;

import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.common.parse.RecordField;
import eu.europeana.uim.common.parse.RecordHandler;
import eu.europeana.uim.common.parse.RecordMap;
import eu.europeana.uim.store.Request;

import java.util.List;
import java.util.Map.Entry;

public class MetaDataRecordHandler implements RecordHandler {

	private final StorageEngine storage;
	private final Request request;
	
	private final String recordElement;
	
	public MetaDataRecordHandler(StorageEngine storage, Request request, String recordElement) {
		super();
		this.storage = storage;
		this.request = request;
		this.recordElement = recordElement;
	}

	
	@Override
	public String getRecordElement() {
		return recordElement;
	}


	@Override
	public void record(RecordMap record) {
		MetaDataRecord<MDRFieldRegistry> mdr = storage.createMetaDataRecord(request);
		
		for (Entry<RecordField, List<String>> entry : record.entrySet()) {
			if ("title".equals(entry.getKey().getLocal())) {
				if (entry.getKey().getLanguage() != null) {
					for (String  value : entry.getValue()) {
						mdr.setQField(MDRFieldRegistry.title, entry.getKey().getLanguage(), value);
					}
				} else {
					for (String  value : entry.getValue()) {
						mdr.setFirstField(MDRFieldRegistry.title, value);
					}
				}
			}
		}
		
		try {
			storage.updateMetaDataRecord(mdr);
		} catch (StorageEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
