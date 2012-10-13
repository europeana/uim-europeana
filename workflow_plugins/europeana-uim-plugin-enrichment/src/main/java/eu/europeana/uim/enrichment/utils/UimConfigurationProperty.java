package eu.europeana.uim.enrichment.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	MONGO_HOSTURL("mongo.hostUrl"),
	MONGO_HOSTPORT("mongo.hostPort"),
	MONGO_DB_EUROPEANA("mongo.db.europeana"),
	SOLR_HOSTURL("solr.hostUrl"),
	SOLR_CORE("solr.core"),
	MONGO_DB_COLLECTIONS("mongo.db.collections"),
	MONGO_DB_EUROPEANA_ID("mongo.db.europeanaId"),
	UIM_REPOSITORY("uim.repository");
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
