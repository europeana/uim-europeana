package eu.europeana.uim.store;

import java.util.List;


public interface StorageEngine {

	Aggregator createAggregator();
	void updateAggregator(Aggregator aggregator);
	List<Aggregator> getAggregators();
	
	Provider createProvider(Aggregator aggregator);
	void updateProvider(Provider provider);
	List<Provider> getProvider();
	
	Collection createCollection(Provider provider);
	void updateCollection(Collection collection);
	List<Collection> getCollections(Provider provider);

	Request createRequest(Collection collection);
	void updateRequest(Request request);
	List<Request> getRequests(Collection collection);

	Execution createExecution();
	void updateExecution(Execution execution);
	List<Execution> getExecutions();
	
	
	long[] getByRequest(Request request);
	long[] getByCollection(Collection collection);
	long[] getByProvider(Provider provider);
	long[] getByAggregator(Aggregator aggregator);
	long[] getAllIds();
	
}
