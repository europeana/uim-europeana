package eu.europeana.uim.store.mongo;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.DB;
import com.mongodb.Mongo;
import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class MongoStorageEngine implements StorageEngine {

    private static final String DEFAULT_UIM_DB_NAME = "UIM";
    private Mongo mongo = null;
    private DB db = null;
    private Datastore ds = null;

    private EngineStatus status = EngineStatus.STOPPED;

    private AtomicLong providerIdCounter = null;
    private AtomicLong collectionIdCounter = null;
    private AtomicLong requestIdCounter = null;
    private AtomicLong executionIdCounter = null;

    private String dbName;

    public MongoStorageEngine(String dbName) {
        this.dbName = dbName;
    }

    public String getIdentifier() {
        return MongoStorageEngine.class.getName();
    }

    public void setConfiguration(Map<String, String> config) {
    }

    public Map<String, String> getConfiguration() {
        return null;
    }

    public void initialize() {
        try {
            if(dbName == null) {
                dbName = DEFAULT_UIM_DB_NAME;
            }
            status = EngineStatus.BOOTING;
            mongo = new Mongo();
            db = mongo.getDB(dbName);
            ds = new Morphia().createDatastore(mongo, dbName);
            status = EngineStatus.RUNNING;

            // initialize counters
            providerIdCounter = new AtomicLong(ds.find(MongoProvider.class).countAll());
            requestIdCounter = new AtomicLong(ds.find(MongoRequest.class).countAll());
            collectionIdCounter = new AtomicLong(ds.find(MongodbCollection.class).countAll());
            executionIdCounter = new AtomicLong(ds.find(MongoExecution.class).countAll());

        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        status = EngineStatus.STOPPED;
    }

    public String getDbName() {
        return dbName;
    }

    public EngineStatus getStatus() {
        return status;
    }

    public long size() {
        return 0;
    }

    public Provider createProvider() {
        Provider p = new MongoProvider(providerIdCounter.getAndIncrement());
        ds.save(p);
        return p;
    }

    public void updateProvider(Provider provider) throws StorageEngineException {
        ds.save(provider);
    }

    public Provider getProvider(long id) {
        return ds.find(MongoProvider.class).filter("lid", id).get();
    }

    public Provider findProvider(String mnemonic) {
        return ds.find(MongoProvider.class).field("mnemonic").equal(mnemonic).get();
    }

    public List<Provider> getProvider() {
        final List<Provider> res = new ArrayList<Provider>();
        for(Provider p : ds.find(MongoProvider.class).asList()) {
            res.add(p);
        }
        return res;
    }

    public Collection createCollection(Provider provider) {
        Collection c = new MongodbCollection(collectionIdCounter.getAndIncrement(), provider);
        ds.save(c);
        return c;
    }

    public void updateCollection(Collection collection) throws StorageEngineException {
        ds.save(collection);
    }

    public Collection getCollection(long id) {
        return ds.find(MongodbCollection.class).filter("lid", id).get();
    }

    public Collection findCollection(String mnemonic) {
        return ds.find(MongodbCollection.class).filter("mnemonic", mnemonic).get();
    }

    public List<Collection> getCollections(Provider provider) {
        List<Collection> res = new ArrayList<Collection>();
        for(Collection c : ds.find(MongodbCollection.class).filter("provider", provider).asList()) {
            res.add(c);
        }
        return res;
    }

    public Request createRequest(Collection collection) {
        Request r = new MongoRequest(requestIdCounter.getAndIncrement(), (MongodbCollection)collection);
        ds.save(r);
        return r;
    }

    public void updateRequest(Request request) throws StorageEngineException {
        ds.save(request);
    }

    public List<Request> getRequests(Collection collection) {
        List<Request> res = new ArrayList<Request>();
        for(Request r : ds.find(MongoRequest.class).filter("collection", collection).asList()) {
            res.add(r);
        }
        return res;
    }

    public MetaDataRecord<MDRFieldRegistry> createMetaDataRecord(Request request) {
        return null;
    }

    public void updateMetaDataRecord(MetaDataRecord<MDRFieldRegistry> record) throws StorageEngineException {
    }

    public Execution createExecution() {
        MongoExecution me = new MongoExecution(executionIdCounter.getAndIncrement());
        ds.save(me);
        return me;
    }

    public void updateExecution(Execution execution) throws StorageEngineException {
        ds.save(execution);
    }

    public List<Execution> getExecutions() {
        List<Execution> res = new ArrayList<Execution>();
        for(Execution e : ds.find(MongoExecution.class).asList()) {
            res.add(e);
        }
        return res;
    }

    public MetaDataRecord<MDRFieldRegistry>[] getMetaDataRecords(long... ids) {
        return null;
    }

    public long[] getByRequest(Request request) {
        return new long[0];
    }

    public long[] getByCollection(Collection collection) {
        return new long[0];
    }

    public long[] getByProvider(Provider provider, boolean recursive) {
        return new long[0];
    }

    public long[] getAllIds() {
        return new long[0];
    }

    public int getTotalByRequest(Request request) {
        return 0;
    }

    public int getTotalByCollection(Collection collection) {
        return 0;
    }

    public int getTotalByProvider(Provider provider, boolean recursive) {
        return 0;
    }

    public int getTotalForAllIds() {
        return 0;
    }
}
