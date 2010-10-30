package eu.europeana.uim.store.memory;

import java.util.Date;

import eu.europeana.uim.store.Request;

public class MemoryRequest extends AbstractMemoryEntity implements Request {

	private MemoryCollection collection;
	private Date date;
	
	public MemoryRequest(MemoryCollection collection) {
		super();
		this.collection = collection;
	}

	public MemoryRequest(long id, MemoryCollection collection) {
		super(id);
		this.collection = collection;
	}

	

	public MemoryCollection getCollection() {
		return collection;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
