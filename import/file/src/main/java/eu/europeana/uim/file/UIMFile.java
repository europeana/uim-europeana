package eu.europeana.uim.file;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;

import org.apache.felix.gogo.commands.Argument;
import org.apache.felix.gogo.commands.Command;
import org.apache.felix.karaf.shell.console.OsgiCommandSupport;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.UIMRegistry;
import eu.europeana.uim.file.ese.ESEParser;
import eu.europeana.uim.store.Aggregator;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.StorageEngine;


@Command(scope = "uim", name = "file", description="Load file.")
public class UIMFile extends OsgiCommandSupport {

	@Argument(name = "filename", index = 0)
	private String filename;

	@Argument(name = "format", index = 1)
	private String format;

	@Argument(name = "target", required = false, index = 2)
	private long target = -1;


	public UIMFile() {

	}

	protected Object doExecute() throws Exception {
		if (filename == null) {
			System.out.println("Filename must be specified. uim:file filename format target");
			return null;
		} 
		if (format == null) {
			System.out.println("Format must be specified. uim:file filename format target");
			return null;
		}

		System.out.println("Loading File: Load <" + filename + "> as <" + format + "> into collection <" + (target==-1?"NEW":target) + ">");

		StorageEngine storage = UIMRegistry.getInstance().getFirstStorage();
		Collection collection = storage.getCollection(target);
		if (collection == null) {
			if (target < 0) {
				Aggregator aggregator = storage.createAggregator();
				storage.updateAggregator(aggregator);
				
				Provider provider = storage.createProvider(aggregator);
				storage.updateProvider(provider);
				
				collection = storage.createCollection(provider);
				storage.updateCollection(collection);
				
				System.out.println("Created aggregator, provider, collection: " + aggregator.getId() + ", " + provider.getId() + ", " + collection.getId());
			} else {
				System.out.println("Collection <" + target + "> does not exist.");
				return null;
			}
		}

		File file = new File(filename);
		if (file.exists()) {
			ESEParser parser = new ESEParser();
			List<HashMap<String,Object>> list = parser.importXml(new FileInputStream(file));
			
			Request request = storage.createRequest(collection);
			storage.updateRequest(request);
			
			for (HashMap<String, Object> record : list) {
				MetaDataRecord<FieldRegistry> mdr = storage.createMetaDataRecord(request);
				mdr.setField(FieldRegistry.field0, (String)record.get("title"));
				storage.updateMetaDataRecord(mdr);
			}
			
			long[] ids = storage.getByRequest(request);
			StringBuilder builder = new StringBuilder();
			for (long id : ids) {
				if (builder.length() > 0) {
					builder.append(", ");
				}
				builder.append(id);
			}
			System.out.println("Loaded " + list.size() + " records in request: <" + request.getId() + "> (" + builder.toString() + ")");
		} else {
			System.out.println("File: <" + file.getAbsolutePath() + "> not found.");
		}
		
		return null;
	}

}
