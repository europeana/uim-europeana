package eu.europeana.uim.store.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.store.Aggregator;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.store.StorageEngine;
import gnu.trove.TLongArrayList;
import gnu.trove.TLongLongHashMap;
import gnu.trove.TLongLongIterator;
import gnu.trove.TLongObjectHashMap;
import gnu.trove.TLongObjectIterator;

public class MemoryStorageEngine implements StorageEngine {


	private TLongObjectHashMap<Aggregator> aggregators = new TLongObjectHashMap<Aggregator>();
	private TLongObjectHashMap<Provider> providers = new TLongObjectHashMap<Provider>();
	private TLongObjectHashMap<Collection> collections = new TLongObjectHashMap<Collection>();
	private TLongObjectHashMap<Request> requests = new TLongObjectHashMap<Request>();
	private TLongObjectHashMap<Execution> executions = new TLongObjectHashMap<Execution>();

	private TLongLongHashMap metarequest = new TLongLongHashMap();
	private TLongObjectHashMap<MetaDataRecord<?>> metadatas = new TLongObjectHashMap<MetaDataRecord<?>>();

	
	private AtomicLong aggregatorId= new AtomicLong();
	private AtomicLong providerId= new AtomicLong();
	private AtomicLong collectionId= new AtomicLong();
	private AtomicLong requestId= new AtomicLong();
	private AtomicLong executionId= new AtomicLong();

	private AtomicLong mdrId= new AtomicLong();

	public static final String IDENTIFIER = MemoryStorageEngine.class.getSimpleName();
	
	public String getIdentifier(){
		return IDENTIFIER;
	}
	

	@Override
	public long size() {
		return metadatas.size();
	}


	@SuppressWarnings("unchecked")
	@Override
	public MetaDataRecord<FieldRegistry>[] getMetaDataRecords(long... ids) {
		ArrayList<MetaDataRecord<FieldRegistry>> result = new ArrayList<MetaDataRecord<FieldRegistry>>(ids.length);
		for (long id : ids) {
			result.add((MetaDataRecord<FieldRegistry>) metadatas.get(id));
		}
		return result.toArray(new MetaDataRecord[result.size()]);
	}


	@Override
	public Aggregator createAggregator() {
		return new MemoryAggregator(aggregatorId.getAndIncrement());
	}
	@Override
	public void updateAggregator(Aggregator aggregator) {
		aggregators.put(aggregator.getId(), aggregator);
	}
	@Override
	public List<Aggregator> getAggregators() {
		ArrayList<Aggregator> result = new ArrayList<Aggregator>();
		TLongObjectIterator<Aggregator> iterator = aggregators.iterator();
		while (iterator.hasNext()) {
			iterator.advance();
			result.add(iterator.value());
		}
		return result;
	}
	@Override
	public Aggregator getAggregator(long id) {
		return aggregators.get(id);
	}




	@Override
	public Provider createProvider(Aggregator aggregator) {
		return new MemoryProvider(providerId.getAndIncrement(), (MemoryAggregator) aggregator);
	}
	@Override
	public void updateProvider(Provider provider) {
		providers.put(provider.getId(), provider);
	}
	@Override
	public List<Provider> getProvider() {
		ArrayList<Provider> result = new ArrayList<Provider>();
		TLongObjectIterator<Provider> iterator = providers.iterator();
		while (iterator.hasNext()) {
			iterator.advance();
			result.add(iterator.value());
		}
		return result;
	}
	@Override
	public Provider getProvider(long id) {
		return providers.get(id);
	}




	@Override
	public Collection createCollection(Provider provider) {
		return new MemoryCollection(collectionId.getAndIncrement(), (MemoryProvider) provider);
	}
	@Override
	public void updateCollection(Collection collection) {
		collections.put(collection.getId(), collection);
	}
	@Override
	public List<Collection> getCollections(Provider provider) {
		ArrayList<Collection> result = new ArrayList<Collection>();
		TLongObjectIterator<Collection> iterator = collections.iterator();
		while (iterator.hasNext()) {
			iterator.advance();
			Collection collection = iterator.value();
			if (collection.getProvider().equals(provider)) {
				result.add(collection);
			}
		}
		return result;
	}
	@Override
	public Collection getCollection(long id) {
		return collections.get(id);
	}
	
	
	
	@Override
	public Request createRequest(Collection collection) {
		return new MemoryRequest(requestId.getAndIncrement(), (MemoryCollection) collection);
	}
	@Override
	public void updateRequest(Request request) {
		requests.put(request.getId(), request);
	}
	@Override
	public List<Request> getRequests(Collection collection) {
		ArrayList<Request> result = new ArrayList<Request>();
		TLongObjectIterator<Request> iterator = requests.iterator();
		while (iterator.hasNext()) {
			iterator.advance();
			Request request = iterator.value();
			if (request.getCollection().equals(collection)) {
				result.add(request);
			}
		}
		return result;
	}
	
	
	
	

	@Override
	public MetaDataRecord<FieldRegistry> createMetaDataRecord(Request request) {
		MetaDataRecord<FieldRegistry> mdr = new MetaDataRecord<FieldRegistry>(mdrId.getAndIncrement());
		mdr.setRequest(request);
		return mdr;
	}
	@Override
	public void updateMetaDataRecord(MetaDataRecord<FieldRegistry> record) {
		metadatas.put(record.getId(), record);
		metarequest.put(record.getId(), record.getRequest().getId());
	}


	@Override
	public Execution createExecution() {
		return new MemoryExecution(executionId.getAndIncrement());
	}
	@Override
	public void updateExecution(Execution execution) {
		executions.put(execution.getId(), execution);
	}
	@Override
	public List<Execution> getExecutions() {
		ArrayList<Execution> result = new ArrayList<Execution>();
		TLongObjectIterator<Execution> iterator = executions.iterator();
		while (iterator.hasNext()) {
			iterator.advance();
			result.add(iterator.value());
		}
		return result;
	}
	
	
	
	
	@Override
	public long[] getByRequest(Request request) {
		TLongArrayList result = new TLongArrayList();
		TLongLongIterator iterator = metarequest.iterator();
		while(iterator.hasNext()) {
			iterator.advance();
			if (iterator.value() == request.getId()) {
				result.add(iterator.key());
			}
		}
		return result.toNativeArray();
	}
	
	@Override
	public long[] getByCollection(Collection collection) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long[] getByProvider(Provider provider) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long[] getByAggregator(Aggregator aggregator) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public long[] getAllIds() {
		return metadatas.keys();
	}


	@Override
	public int getTotalByRequest(Request request) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getTotalByCollection(Collection collection) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getTotalByProvider(Provider provider) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getTotalByAggregator(Aggregator aggregator) {
		// TODO Auto-generated method stub
		return 0;
	}


	@Override
	public int getTotalForAllIds() {
		// TODO Auto-generated method stub
		return 0;
	}



}
