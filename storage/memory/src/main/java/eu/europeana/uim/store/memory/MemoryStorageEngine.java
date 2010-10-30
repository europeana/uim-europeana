package eu.europeana.uim.store.memory;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;
import gnu.trove.TLongArrayList;
import gnu.trove.TLongLongHashMap;
import gnu.trove.TLongLongIterator;
import gnu.trove.TLongObjectHashMap;
import gnu.trove.TLongObjectIterator;
import gnu.trove.TObjectLongHashMap;

public class MemoryStorageEngine implements StorageEngine {


	private TLongObjectHashMap<Provider> providers = new TLongObjectHashMap<Provider>();
	private TObjectLongHashMap<String> providerMnemonics = new TObjectLongHashMap<String>();

	private TLongObjectHashMap<Collection> collections = new TLongObjectHashMap<Collection>();
	private TObjectLongHashMap<String> collectionMnemonics = new TObjectLongHashMap<String>();

	private TLongObjectHashMap<Request> requests = new TLongObjectHashMap<Request>();
	private TLongObjectHashMap<Execution> executions = new TLongObjectHashMap<Execution>();

	
	private TLongLongHashMap metarequest = new TLongLongHashMap();
	private TLongObjectHashMap<MetaDataRecord<?>> metadatas = new TLongObjectHashMap<MetaDataRecord<?>>();

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
	public Provider createProvider() {
		return new MemoryProvider(providerId.getAndIncrement());
	}
	@Override
	public void updateProvider(Provider provider) throws StorageEngineException {
		if (provider.getMnemonic() == null) {
			throw new StorageEngineException("Cannot store provider without mnemonic/code.");
		}
		if (providerMnemonics.containsKey(provider.getMnemonic())) {
			long pid = providerMnemonics.get(provider.getMnemonic());
			if (pid != provider.getId()) {
				throw new StorageEngineException("Cannot store provider duplicate mnemonic/code.");
			}
		}
		providers.put(provider.getId(), provider);
		providerMnemonics.put(provider.getMnemonic(), provider.getId());
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
	public Provider findProvider(String mnemonic) {
		if (providerMnemonics.containsKey(mnemonic)) {
			long id = providerMnemonics.get(mnemonic);
			return providers.get(id);
		}
		return null;
	}




	@Override
	public Collection createCollection(Provider provider) {
		return new MemoryCollection(collectionId.getAndIncrement(), (MemoryProvider) provider);
	}
	@Override
	public void updateCollection(Collection collection) throws StorageEngineException {
		if (collection.getMnemonic() == null) {
			throw new StorageEngineException("Cannot store collection without mnemonic/code.");
		}
		if (providerMnemonics.containsKey(collection.getMnemonic())) {
			long pid = providerMnemonics.get(collection.getMnemonic());
			if (pid != collection.getId()) {
				throw new StorageEngineException("Cannot store collection duplicate mnemonic/code.");
			}
		}
		collections.put(collection.getId(), collection);
		collectionMnemonics.put(collection.getMnemonic(), collection.getId());
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
	public Collection findCollection(String mnemonic) {
		if (collectionMnemonics.containsKey(mnemonic)) {
			long id = collectionMnemonics.get(mnemonic);
			return collections.get(id);
		}
		return null;
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
	public long[] getByProvider(Provider provider, boolean recursive) {
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
	public int getTotalByProvider(Provider provider, boolean recursive) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int getTotalForAllIds() {
		// TODO Auto-generated method stub
		return 0;
	}



}
