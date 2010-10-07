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


	long[] getBatchByRequest(Request request);
	long[] getBatchByCollection(Collection collection);
	long[] getBatchByProvider(Provider provider);
	long[] getBatchByAggregator(Aggregator aggregator);
	long[] getBatchForAllIds();

    int getTotalByRequest(Request request);
    int getTotalByCollection(Collection collection);
    int getTotalByProvider(Provider provider);
    int getTotalByAggregator(Aggregator aggregator);
    int getTotalForAllIds();

}
