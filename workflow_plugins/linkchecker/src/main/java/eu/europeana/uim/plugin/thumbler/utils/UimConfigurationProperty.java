package eu.europeana.uim.plugin.thumbler.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	CLIENT_HOSTURL("harvester.hostUrl"),
	CLIENT_HOSTPORT("harvester.hostPort"),
	CLIENT_USERNAME("harvester.username"),
	CLIENT_PASSWORD("harvester.password"),
        CLIENT_DB("harvester.db"),
	MONGO_HOSTURL("mongo.hostUrl"),
	MONGO_HOSTPORT("mongo.hostPort"),
	MONGO_DB_COLLECTIONS("mongo.db.collections")
        ;
	
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
