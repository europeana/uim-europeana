package eu.europeana.uim.store;

import java.util.List;
import java.util.Map;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;

public abstract class StorageEngineAdapter implements StorageEngine {

	@Override
	public String getIdentifier() {
		return null;
	}
	

	@Override
	public void initialize() {
	}


	@Override
	public void setConfiguration(Map<String, String> config) {
	}


	@Override
	public Map<String, String> getConfiguration() {
		return null;
	}


	@Override
	public EngineStatus getStatus() {
		return null;
	}

	@Override
	public long size() {
		return 0;
	}

	@Override
	public Provider createProvider() {
		return null;
	}

	@Override
	public void updateProvider(Provider provider) throws StorageEngineException {
		

	}

	@Override
	public Provider getProvider(long id) {
		
		return null;
	}

	@Override
	public Provider findProvider(String mnemonic) {
		
		return null;
	}

	@Override
	public List<Provider> getProvider() {
		
		return null;
	}

	@Override
	public Collection createCollection(Provider provider) {
		
		return null;
	}

	@Override
	public void updateCollection(Collection collection)
			throws StorageEngineException {
		

	}

	@Override
	public Collection getCollection(long id) {
		
		return null;
	}

	@Override
	public Collection findCollection(String mnemonic) {
		
		return null;
	}

	@Override
	public List<Collection> getCollections(Provider provider) {
		
		return null;
	}

	@Override
	public Request createRequest(Collection collection) {
		
		return null;
	}

	@Override
	public void updateRequest(Request request) throws StorageEngineException {
		

	}

	@Override
	public List<Request> getRequests(Collection collection) {
		
		return null;
	}

	@Override
	public MetaDataRecord<FieldRegistry> createMetaDataRecord(Request request) {
		
		return null;
	}

	@Override
	public void updateMetaDataRecord(MetaDataRecord<FieldRegistry> record)
			throws StorageEngineException {
		

	}

	@Override
	public Execution createExecution() {
		
		return null;
	}

	@Override
	public void updateExecution(Execution execution)
			throws StorageEngineException {
		

	}

	@Override
	public List<Execution> getExecutions() {
		
		return null;
	}

	@Override
	public MetaDataRecord<FieldRegistry>[] getMetaDataRecords(long... ids) {
		
		return null;
	}

	@Override
	public long[] getByRequest(Request request) {
		
		return null;
	}

	@Override
	public long[] getByCollection(Collection collection) {
		
		return null;
	}

	@Override
	public long[] getByProvider(Provider provider, boolean recursive) {
		
		return null;
	}

	@Override
	public long[] getAllIds() {
		
		return null;
	}

	@Override
	public int getTotalByRequest(Request request) {
		
		return 0;
	}

	@Override
	public int getTotalByCollection(Collection collection) {
		
		return 0;
	}

	@Override
	public int getTotalByProvider(Provider provider, boolean recursive) {
		
		return 0;
	}

	@Override
	public int getTotalForAllIds() {
		
		return 0;
	}

}
