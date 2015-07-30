package eu.europeana.europeanauim.publish.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.Morphia;
import eu.europeana.corelib.edm.exceptions.MongoDBException;
import eu.europeana.corelib.mongo.server.EdmMongoServer;
import eu.europeana.corelib.mongo.server.impl.EdmMongoServerImpl;
import eu.europeana.europeanauim.publish.OsgiEdmMongoServer;
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
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;

public class PublishServiceImpl implements PublishService {
  private CloudSolrServer solrServer;
  private CloudSolrServer solrIngestionServer;
  private String zookeeperUrl;
  private String zookeeperUrlProduction;
  private static OsgiEuropeanaIdMongoServer idserver;
  private static OsgiEuropeanaIdMongoServer idserverProduction;
  private static String[] mongoHost = PropertyReader.getProperty(
      UimConfigurationProperty.MONGO_HOSTURL).split(",");
  private static String[] mongoHostProduction = PropertyReader.getProperty(
      UimConfigurationProperty.MONGO_HOSTURL_PRODUCTION).split(",");
  private static String mongoDbIngestion = PropertyReader.getProperty(
          UimConfigurationProperty.MONGO_INGESTION_DB);
  private static String mongoDbProduction = PropertyReader.getProperty(
          UimConfigurationProperty.MONGO_PRODUCTION_DB);
  @Override
  public  OsgiEdmMongoServer getMongoIngestion() {
    return mongoIngestion;
  }


  @Override
  public  OsgiEdmMongoServer getMongoProduction() {
    return mongoProduction;
  }



  private static OsgiEdmMongoServer mongoIngestion;
  private static OsgiEdmMongoServer mongoProduction;

  public PublishServiceImpl(){
    final String solrUrl =
            PropertyReader.getProperty(UimConfigurationProperty.SOLR_CLOUD_PRODUCTION_HOSTURL);
    final String solrCore =
            PropertyReader.getProperty(UimConfigurationProperty.SOLR_CLOUD_PRODUCTION_CORE);

    zookeeperUrlProduction = PropertyReader.getProperty(UimConfigurationProperty.ZOOKEEPER_PRODUCTION_HOSTURL);
    zookeeperUrl = PropertyReader.getProperty(UimConfigurationProperty.ZOOKEEPER_INGESTION_HOSTURL);

  try{

          LBHttpSolrServer lbTarget = new LBHttpSolrServer(solrUrl.split(","));
          solrServer = new CloudSolrServer(zookeeperUrlProduction, lbTarget);
          solrServer.setDefaultCollection(solrCore);
          solrServer.connect();
         // solrServer = new CloudSolrServer(new URL(solrUrl) + solrCore);
        } catch (MalformedURLException e) {
          // TODO Auto-generated catch block
          e.printStackTrace();
        }

    final String solrIngestionUrl =
            PropertyReader.getProperty(UimConfigurationProperty.SOLR_HOSTURL);
    final String solrIngestionCore =
            PropertyReader.getProperty(UimConfigurationProperty.SOLR_CORE);



    LBHttpSolrServer lbTarget = null;
    try {
      lbTarget = new LBHttpSolrServer(solrIngestionUrl.split(","));
    solrIngestionServer = new CloudSolrServer(zookeeperUrl, lbTarget);
    solrIngestionServer.setDefaultCollection(solrIngestionCore);
    solrIngestionServer.connect();
    } catch (MalformedURLException e1) {
      e1.printStackTrace();
    }

          //solrIngestionServer = new CloudSolrServer(new URL(solrIngestionUrl) + solrIngestionCore);



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
    final Mongo tgtMongo = new Mongo(addresses);

    idserver = new OsgiEuropeanaIdMongoServer((tgtMongo), "EuropeanaId");
    idserver.createDatastore();
    List<ServerAddress> addressesProduction = new ArrayList<>();
    for (String mongoStr : mongoHostProduction) {
      ServerAddress address;
      try {
        address = new ServerAddress(mongoStr, 27017);
        addressesProduction.add(address);
      } catch (UnknownHostException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }
    final Mongo tgtProductionMongo = new Mongo(addressesProduction);

    idserverProduction = new OsgiEuropeanaIdMongoServer((tgtProductionMongo), "EuropeanaId");
    idserverProduction.createDatastore();

    BlockingInitializer initializer = new BlockingInitializer() {
      @Override
      protected void initializeInternal() {
        try {
          mongoIngestion = new OsgiEdmMongoServer((tgtMongo),mongoDbIngestion,"","");
          Morphia morphia = new Morphia();
          mongoIngestion.createDatastore(morphia);
          mongoIngestion.getFullBean("test");
        } catch (MongoDBException e) {
          e.printStackTrace();
        }

      }
    };
    initializer.initialize(OsgiEdmMongoServer.class.getClassLoader());


    BlockingInitializer initializer1 = new BlockingInitializer() {
      @Override
      protected void initializeInternal() {
        try {
          mongoProduction = new OsgiEdmMongoServer((tgtProductionMongo),mongoDbProduction,"","");
          Morphia morphia = new Morphia();
          mongoProduction.createDatastore(morphia);
          mongoProduction.getFullBean("test");
        } catch (MongoDBException e) {
          e.printStackTrace();
        }
      }
    };
    initializer1.initialize(OsgiEdmMongoServer.class.getClassLoader());

  }


  @Override
  public SolrServer getSolrServer() {

    return solrServer;
  }
  @Override
  public SolrServer getSolrIngestionServer(){

    return solrIngestionServer;
  }

  @Override
  public EuropeanaIdMongoServer getEuropeanaIdMongoServer() {

    return idserver;
  }

  @Override
  public EuropeanaIdMongoServer getEuropeanaIdMongoServerProduction() {

    return idserverProduction;
  }



}
