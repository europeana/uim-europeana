package eu.europeana.uim.file;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.Registry;
import eu.europeana.uim.common.ese.ESEParser;
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
import java.io.InputStream;
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

        if (!readArguments(arguments)) return null;

        File file = new File(filename);
        if (file.exists()) {
            InputStream f = new FileInputStream(file);
            long[] ids = readIds(f, format, target);
        } else {
            System.out.println("File: <" + file.getAbsolutePath() + "> not found.");
        }

        return null;
    }

    public long[] getTestIds() {

        long[] res = null;
        InputStream f = getClass().getResourceAsStream("/readingeurope.xml");

        try {
            res = readIds(f, "ese", target);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return res;
    }

    private long[] readIds(InputStream data, String format, long target) throws Exception {

        System.out.println("Loading File: Load <" + filename + "> as <" + format + "> into collection <" + (target == -1 ? "NEW" : target) + ">");

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

        long[] ids = null;
        ESEParser parser = new ESEParser();
        List<HashMap<String, Object>> list = parser.importXml(data);

        Request request = storage.createRequest(collection);
        storage.updateRequest(request);

        for (HashMap<String, Object> record : list) {
            MetaDataRecord<FieldRegistry> mdr = storage.createMetaDataRecord(request);
            mdr.setField(FieldRegistry.field0, (String) record.get("title"));
            storage.updateMetaDataRecord(mdr);
        }
        ids = storage.getByRequest(request);
        StringBuilder builder = new StringBuilder();
        for (long id : ids) {
            if (builder.length() > 0) {
                builder.append(", ");
            }
            builder.append(id);
        }
        System.out.println("Loaded " + list.size() + " records in request: <" + request.getId() + "> (" + builder.toString() + ")");

        return ids;

    }

    private boolean readArguments(List<Object> arguments) {
        if (arguments.size() < 2) {
            System.out.println("Filename and format must be specified. uim:file filename format target");
            return false;
        }

        filename = arguments.get(0).toString();
        if (filename == null) {
            System.out.println("Filename must be specified. uim:file filename format target");
            return false;
        }

        format = arguments.get(1).toString();
        if (format == null) {
            System.out.println("Format must be specified. uim:file filename format target");
            return false;
        }

        if (arguments.size() > 2) {
            target = Long.parseLong(arguments.get(2).toString());
        }
        return true;
    }

}
