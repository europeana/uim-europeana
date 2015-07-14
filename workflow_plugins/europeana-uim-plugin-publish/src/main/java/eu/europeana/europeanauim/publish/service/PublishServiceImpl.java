package eu.europeana.europeanauim.publish.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import com.mongodb.Mongo;
import com.mongodb.ServerAddress;

import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;
import eu.europeana.europeanauim.publish.utils.OsgiEuropeanaIdMongoServer;
import eu.europeana.europeanauim.publish.utils.PropertyReader;
import eu.europeana.europeanauim.publish.utils.UimConfigurationProperty;
import eu.europeana.uim.common.BlockingInitializer;

public class PublishServiceImpl implements PublishService {
  private SolrServer solrServer;
  private SolrServer solrIngestionServer;
  private static OsgiEuropeanaIdMongoServer idserver;
  private static OsgiEuropeanaIdMongoServer idserverProduction;
  private static String[] mongoHost = PropertyReader.getProperty(
      UimConfigurationProperty.MONGO_HOSTURL_PRODUCTION).split(",");
  private static String[] mongoHostProduction = PropertyReader.getProperty(
      UimConfigurationProperty.MONGO_HOSTURL_PRODUCTION).split(",");

  @Override
  public SolrServer getSolrServer() {
    final String solrUrl =
        PropertyReader.getProperty(UimConfigurationProperty.SOLR_CLOUD_PRODUCTION_HOSTURL);
    final String solrCore =
        PropertyReader.getProperty(UimConfigurationProperty.SOLR_CLOUD_PRODUCTION_CORE);


    BlockingInitializer solrInit = new BlockingInitializer() {

      @Override
      protected void initializeInternal() {
        try {
          solrServer = new CloudSolrServer(new URL(solrUrl) + solrCore);
        } catch (MalformedURLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    };
    solrInit.initialize(CloudSolrServer.class.getClassLoader());
    return solrServer;
  }
  @Override
  public SolrServer getSolrIngestionServer(){
    final String solrUrl =
            PropertyReader.getProperty(UimConfigurationProperty.SOLR_HOSTURL);
    final String solrCore =
            PropertyReader.getProperty(UimConfigurationProperty.SOLR_CORE);


    BlockingInitializer solrInit = new BlockingInitializer() {

      @Override
      protected void initializeInternal() {
        try {
          solrIngestionServer = new CloudSolrServer(new URL(solrUrl) + solrCore);
        } catch (MalformedURLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }
      }
    };
    solrInit.initialize(CloudSolrServer.class.getClassLoader());
    return solrIngestionServer;
  }

  @Override
  public EuropeanaIdMongoServer getEuropeanaIdMongoServer() {
    List<ServerAddress> addresses = new ArrayList<>();
    for (String mongoStr : mongoHost) {
      ServerAddress address;
      try {
        address = new ServerAddress(mongoStr, 27017);
        addresses.add(address);
      } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    Mongo tgtMongo = new Mongo(addresses);

    idserver = new OsgiEuropeanaIdMongoServer((tgtMongo), "EuropeanaId");
    idserver.createDatastore();
    return idserver;
  }

  @Override
  public EuropeanaIdMongoServer getEuropeanaIdMongoServerProduction() {
    List<ServerAddress> addresses = new ArrayList<>();
    for (String mongoStr : mongoHostProduction) {
      ServerAddress address;
      try {
        address = new ServerAddress(mongoStr, 27017);
        addresses.add(address);
      } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    Mongo tgtMongo = new Mongo(addresses);

    idserverProduction = new OsgiEuropeanaIdMongoServer((tgtMongo), "EuropeanaId");
    idserverProduction.createDatastore();
    return idserverProduction;
  }



}
