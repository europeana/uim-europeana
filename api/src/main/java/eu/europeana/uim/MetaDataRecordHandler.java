package eu.europeana.uim;

import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Logger;
import java.util.Set;

import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.command.UIMConfigStorage;
import eu.europeana.uim.common.parse.RecordField;
import eu.europeana.uim.common.parse.RecordHandler;
import eu.europeana.uim.common.parse.RecordMap;
import eu.europeana.uim.store.Request;

public class MetaDataRecordHandler implements RecordHandler {
	private static final Logger log = Logger.getLogger(MetaDataRecordHandler.class.getName());
	
	private final StorageEngine storage;
	private final Request request;

	private final String recordElement;
	private int count;
	private Set<String> unique = new HashSet<String>();

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
		try {
			MetaDataRecord mdr = storage.createMetaDataRecord(request);

			for (Entry<RecordField, List<String>> entry : record.entrySet()) {
				if ("title".equals(entry.getKey().getLocal())) {
					if (entry.getKey().getLanguage() != null) {
						for (String  value : entry.getValue()) {
							mdr.addQField(MDRFieldRegistry.title, entry.getKey().getLanguage(), value);
						}
					} else {
						for (String  value : entry.getValue()) {
							mdr.setFirstField(MDRFieldRegistry.title, value);
						}
					}
				} else if ("identifier".equals(entry.getKey().getLocal())) {
					String string = entry.getValue().get(0);
					if (string != null && string.length() > 0) { 
						mdr.setIdentifier(entry.getValue().get(0));
					} else {
						mdr.setIdentifier("" + count);
					}
				}
				count++;
			}
			
			if (unique.contains(mdr.getIdentifier())) {
				log.warning("Duplicate identifier:" + mdr.getIdentifier());
			} else {
				storage.updateMetaDataRecord(mdr);
				unique.add(mdr.getIdentifier());
			}
		} catch (StorageEngineException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
