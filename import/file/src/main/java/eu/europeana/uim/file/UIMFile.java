package eu.europeana.uim.file;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.Registry;
import eu.europeana.uim.file.ese.ESEParser;
import eu.europeana.uim.store.Aggregator;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.StorageEngine;
import org.osgi.service.command.CommandSession;
import org.osgi.service.command.Function;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.List;


public class UIMFile implements Function {

	private String filename;

	private String format;

	private long target = -1;

    private Registry registry;

    @Autowired
	public UIMFile(Registry registry) {
        this.registry = registry;

	}

    @Override
    public Object execute(CommandSession commandSession, List<Object> arguments) throws Exception {

        if(arguments.size() < 2) {
            System.out.println("Filename and format must be specified. uim:file filename format target");
            return null;
        }

        filename = arguments.get(0).toString();
        if (filename == null) {
			System.out.println("Filename must be specified. uim:file filename format target");
			return null;
		} 

        format = arguments.get(1).toString();
        if (format == null) {
			System.out.println("Format must be specified. uim:file filename format target");
			return null;
		}

        if(arguments.size() > 2) {
            target = Long.parseLong(arguments.get(2).toString());
        }

		System.out.println("Loading File: Load <" + filename + "> as <" + format + "> into collection <" + (target==-1?"NEW":target) + ">");

		StorageEngine storage = registry.getFirstStorage();
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
