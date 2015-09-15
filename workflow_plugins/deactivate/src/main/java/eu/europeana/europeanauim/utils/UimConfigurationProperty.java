package eu.europeana.europeanauim.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	MONGO_HOSTURL("mongo.hostUrl"),
	MONGO_HOSTURL_PRODUCTION("mongo.hostUrl.production"),
	MONGO_HOSTPORT("mongo.hostPort"),
	MONGO_USERNAME("mongo.ingestion.username"),
	MONGO_PASSWORD("mongo.ingestion.password"),
	MONGO_PRODUCTION_USERNAME("mongo.production.username"),
	MONGO_PRODUCTION_PASSWORD("mongo.production.password"),
	MONGO_DB_EUROPEANA("mongo.db.europeana"),
	MONGO_DB_EUROPEANA_PRODUCTION("mongo.db.europeana.production"),
	ZOOKEEPER_HOSTURL("zookeeper.host"),
    ZOOKEEPER_HOSTURLPRODUCTION("zookeeper.production.host"),
    CLOUD_SOLR_HOSTURL("cloud.solr.hostUrl"),
    CLOUD_PRODUCTION_SOLR_HOSTURL("cloud.solr.hostUrl"),
    CLOUD_SOLR_CORE("cloud.solr.core"),
	SOLR_HOSTURL("solr.hostUrl"),
	SOLR_CORE("solr.core"),
	SOLR_CORE_SUGGESTIONS("solr.core.suggestions"),
	MONGO_DB_COLLECTIONS("mongo.db.collections"),
	MONGO_DB_EUROPEANA_ID("mongo.db.europeanaId"),
	UIM_REPOSITORY("uim.repository"),
    NEO4JPATH("neo4j.path"),
    NEO4JPATHPRODUCTION("neo4j.production.path"),
    NEO4JINDEX("neo4j.index"),
    NEO4JINDEXPRODUCTION("neo4j.production.index"),
	CLIENT_HOSTURL("harvester.hostUrl"),
	CLIENT_HOSTPORT("harvester.hostPort"),
	CLIENT_DB("harvester.db"),
	CLIENT_USERNAME("harvester.username"),
	CLIENT_PASSWORD("harvester.password");
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
