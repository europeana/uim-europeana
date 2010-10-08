package eu.europeana.uim.store.memory;

import eu.europeana.uim.store.Provider;

public class MemoryProvider extends AbstractMemoryEntity implements Provider {

	private MemoryAggregator aggregator;
	
	private String name;
	
	
	public MemoryProvider(MemoryAggregator aggregator) {
		super();
		this.aggregator = aggregator;
	}

	public MemoryProvider(long id, MemoryAggregator aggregator) {
		super(id);
		this.aggregator = aggregator;
	}
	
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MemoryAggregator getAggregator() {
		return aggregator;
	}

	
	

}
