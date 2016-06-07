package eu.europeana.uim.plugin.solr.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	MONGO_HOSTURL("mongo.hostUrl"),
	MONGO_HOSTPORT("mongo.hostPort"),
	MONGO_DB_VOCABULARY("mongo.db.vocabulary"),
	//MONGO_AUTH_DB("mongo.auth.db"),
	MONGO_USERNAME("mongo.ingestion.username"),
	MONGO_PASSWORD("mongo.ingestion.password"),
	UIM_BCLIST_PATH("uim.bclist.path");
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
