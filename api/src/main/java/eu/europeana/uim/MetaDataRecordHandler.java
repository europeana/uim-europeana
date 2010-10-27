package eu.europeana.uim;

import java.util.Map.Entry;

import eu.europeana.uim.common.parse.RecordField;
import eu.europeana.uim.common.parse.RecordHandler;
import eu.europeana.uim.common.parse.RecordMap;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.StorageEngine;

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
		MetaDataRecord<FieldRegistry> mdr = storage.createMetaDataRecord(request);
		
		for (Entry<RecordField, String> entry : record.entrySet()) {
			if ("title".equals(entry.getKey().getLocal())) {
				if (entry.getKey().getLanguage() != null) {
					mdr.setQField(FieldRegistry.title, entry.getKey().getLanguage(), entry.getValue());
				} else {
					mdr.setField(FieldRegistry.title, entry.getValue());
				}
			}
		}
		storage.updateMetaDataRecord(mdr);
	}

}
