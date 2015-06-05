package eu.europeana.uim.gui.cp.server.util;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	MONGO_HOSTURL("mongo.hostUrl"),
	MONGO_HOSTPORT("mongo.hostPort"),
	MONGO_USERNAME("mongo.username"),
    MONGO_PASSWORD("mongo.password"),
	MONGO_DB_COLLECTIONS("mongo.db.collections"),
	MONGO_DB_VOCABULARY("mongo.db.vocabulary"),
	MONGO_DB_EUROPEANA("mongo.db.europeana"),
	MONGO_DB_IMAGE("mongo.db.image"),
	UIM_STORAGE_LOCATION("uim.storage.location"),
	UIM_REPOSITORY("uim.repository"),
	SOLR_CORE("solr.core"),
	SOLR_HOSTURL("solr.hostUrl"),
	BIRT_URL("birt.url"),
	LOCAL_SERVER("local.server"),
	LOCAL_PORT("local.port"),
	MINT_URI("mint.hostUrl"),
	PORTAL_URI("europeana.portal.uri"),
	TESTPORTAL_URI("europeana.testportal.uri");
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
