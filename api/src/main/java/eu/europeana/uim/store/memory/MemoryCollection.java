package eu.europeana.uim.store.memory;

import eu.europeana.uim.store.Collection;

public class MemoryCollection extends AbstractMemoryEntity implements Collection {

	private MemoryProvider provider;
	private String name;
	
	public MemoryCollection(MemoryProvider provider) {
		super();
		this.provider = provider;
	}

	public MemoryCollection(long id, MemoryProvider provider) {
		super(id);
		this.provider = provider;
	}
	

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public MemoryProvider getProvider() {
		return provider;
	}

	
	

	
}
