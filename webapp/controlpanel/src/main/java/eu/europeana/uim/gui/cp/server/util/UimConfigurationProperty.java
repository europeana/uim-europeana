package eu.europeana.uim.gui.cp.server.util;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	MONGO_HOSTURL("mongo.hostUrl"),
	MONGO_HOSTPORT("mongo.hostPort"),
	MONGO_USERNAME("mongo.ingestion.username"),
    MONGO_PASSWORD("mongo.ingestion.password"),
	MONGO_PRODUCTION_USERNAME("mongo.production.username"),
	MONGO_PRODUCTION_PASSWORD("mongo.production.password"),
	MONGO_DB_COLLECTIONS("mongo.db.collections"),
	MONGO_DB_VOCABULARY("mongo.db.vocabulary"),
	MONGO_DB_EUROPEANA("mongo.db.europeana"),
	MONGO_DB_IMAGE("mongo.db.image"),
	ZOOKEEPER_HOSTURL("zookeeper.host"),
	CLOUD_SOLR_HOSTURL("cloud.solr.hostUrl"),
	CLOUD_SOLR_CORE("cloud.solr.core"),
	UIM_STORAGE_LOCATION("uim.storage.location"),
	UIM_REPOSITORY("uim.repository"),
	SOLR_CORE("solr.core"),
	SOLR_HOSTURL("solr.hostUrl"),
	BIRT_URL("birt.url"),
	LOCAL_SERVER("local.server"),
	LOCAL_PORT("local.port"),
	MINT_URI("mint.hostUrl"),
	PORTAL_URI("europeana.portal.uri"),
	TESTPORTAL_URI("europeana.testportal.uri"),
	CLIENT_HOSTURL("harvester.hostUrl"),
	CLIENT_HOSTPORT("harvester.hostPort"),
	CLIENT_DB("harvester.db"),
	CLIENT_USERNAME("harvester.username"),
	CLIENT_PASSWORD("harvester.password"),
	MONGO_REINDEXING_HOST("mongo.reindexing.host"),
	MONGO_REINDEXING_PORT("mongo.reindexing.port");

	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
