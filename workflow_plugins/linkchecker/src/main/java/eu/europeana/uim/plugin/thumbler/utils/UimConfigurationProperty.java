package eu.europeana.uim.plugin.thumbler.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	MONGO_HOSTURL("mongo.hostUrl"),
	MONGO_HOSTPORT("mongo.hostPort"),
	MONGO_DB_IMAGE("mongo.db.image"),
	UIM_STORAGE_LOCATION("uim.storage.location"),
	IMAGEMAGICK_PATH("imagemagick.path");
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
