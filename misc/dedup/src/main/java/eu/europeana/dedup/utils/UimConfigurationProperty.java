package eu.europeana.dedup.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	MONGO_HOSTURL("mongo.hostUrl"),
	MONGO_HOSTPORT("mongo.hostPort"),
	MONGO_IDREGISTRY_HOST("mongo.idregistry.hostUrl"),
	MONGO_IDREGISTRY_PORT("mongo.idregistry.port"),
	MONGO_INGESTION_USERNAME("mongo.ingestion.username"),
	MONGO_INGESTION_PASSWORD("mongo.ingestion.password"),
	MONGO_DB_EUROPEANAIDREGISTRY("mongo.db.europeanaidregistry"),
	MONGO_DB_VOCABULARY("mongo.db.vocabulary");
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
