package eu.europeana.uim.deactivation.service;

import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;


public interface DeactivationService {

  // public HttpSolrServer getSolrServer();
  public CloudSolrServer getCloudSolrServer();

  public CloudSolrServer getProductionCloudSolrServer();

  public ExtendedEdmMongoServer getMongoServer();
  
  public ExtendedEdmMongoServer getProductionMongoServer();

  public CollectionMongoServer getCollectionMongoServer();

  public RestGraphDatabase getGraphDb();
  
  public RestGraphDatabase getGraphDbProduction();

  public String getNeo4jIndex();
  
  public String getNeo4jIndexProduction();

  public void initialize();

}
