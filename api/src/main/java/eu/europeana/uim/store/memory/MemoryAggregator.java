package eu.europeana.uim.store.memory;

import eu.europeana.uim.store.Aggregator;


public class MemoryAggregator extends AbstractMemoryEntity implements Aggregator {

	private String name;
	
	public MemoryAggregator() {
		super();
	}

	public MemoryAggregator(long id) {
		super(id);
	}

	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	
	
}
