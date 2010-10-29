package eu.europeana.uim.file;

import java.io.InputStream;

import eu.europeana.uim.MetaDataRecordHandler;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.common.parse.RecordParser;
import eu.europeana.uim.common.parse.XMLStreamParserException;
import eu.europeana.uim.store.Request;

public class RecordLoader {

	public RecordLoader() {
		super();
	}

	
	public long[] doEseImport(InputStream data, StorageEngine storage, Request request, ProgressMonitor monitor) throws XMLStreamParserException {
		long[] ids = null;
		
		RecordParser parser = new RecordParser();
		MetaDataRecordHandler handler = new MetaDataRecordHandler(storage, request, "europeana:record");
		
		// parse the file/stream
		parser.parse(data, handler, monitor);
		ids = storage.getByRequest(request);
		return ids;
	}



}
