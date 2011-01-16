package eu.europeana.uim.store.memory;

import eu.europeana.uim.store.UimEntity;

public class AbstractMemoryEntity implements UimEntity {

	private long id;

	private String mnemonic;
	private String name;
	
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

	/**
	 * @return the mnemonic
	 */
	public String getMnemonic() {
		return mnemonic;
	}

	/**
	 * @param mnemonic the mnemonic to set
	 */
	public void setMnemonic(String mnemonic) {
		this.mnemonic = mnemonic;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	public String toString() {
		return getMnemonic() + "\t" + getName();
	}
	
}
