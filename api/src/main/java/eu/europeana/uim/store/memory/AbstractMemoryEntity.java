package eu.europeana.uim.store.memory;

public class AbstractMemoryEntity {

	private long id;
	
	public AbstractMemoryEntity(){
	}
	
	public AbstractMemoryEntity(long id) {
		this.id = id;
	}

	
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	
	
	
}
