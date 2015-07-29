package eu.europeana.europeanauim.publish.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	
	SOLR_HOSTURL("solr.hostUrl"),
	SOLR_CORE("solr.core"),
	SOLR_CLOUD_PRODUCTION_HOSTURL("cloud.solr.hostUrl"),
	SOLR_CLOUD_PRODUCTION_CORE("cloud.solr.core"),
	NEO4JPRODUCTIONPATH("neo4j.production.path"),
    NEO4JPRODUCTIONINDEX("neo4j.production.index"),
    MONGO_HOSTURL("mongo.hostUrl"),
	MONGO_HOSTURL_PRODUCTION("mongo.hostUrl.production"), MONGO_INGESTION_DB("mongo.db.europeana"), MONGO_PRODUCTION_DB("mongo.db.europeana.production"), ZOOKEEPER_PRODUCTION_HOSTURL("zookeeper.production.host"), ZOOKEEPER_INGESTION_HOSTURL("zookeeper.host");
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
