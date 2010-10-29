package eu.europeana.uim.api;

import java.util.List;

import eu.europeana.uim.FieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.store.Aggregator;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;


public interface StorageEngine {

	public String getIdentifier();
	public long size();
	
	Aggregator createAggregator();
	void updateAggregator(Aggregator aggregator);
	Aggregator getAggregator(long id);
	List<Aggregator> getAggregators();

	Provider createProvider(Aggregator aggregator);
	void updateProvider(Provider provider);
	Provider getProvider(long id);
	List<Provider> getProvider();

	Collection createCollection(Provider provider);
	void updateCollection(Collection collection);
	Collection getCollection(long id);
	List<Collection> getCollections(Provider provider);

	Request createRequest(Collection collection);
	void updateRequest(Request request);
	List<Request> getRequests(Collection collection);

	MetaDataRecord<FieldRegistry> createMetaDataRecord(Request request);
	void updateMetaDataRecord(MetaDataRecord<FieldRegistry> record);
	
	Execution createExecution();
	void updateExecution(Execution execution);
	List<Execution> getExecutions();

	MetaDataRecord<FieldRegistry>[] getMetaDataRecords(long...ids);
	

	long[] getByRequest(Request request);
	long[] getByCollection(Collection collection);
	long[] getByProvider(Provider provider);
	long[] getByAggregator(Aggregator aggregator);
	long[] getAllIds();

    int getTotalByRequest(Request request);
    int getTotalByCollection(Collection collection);
    int getTotalByProvider(Provider provider);
    int getTotalByAggregator(Aggregator aggregator);
    int getTotalForAllIds();

}
