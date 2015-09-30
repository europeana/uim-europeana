package eu.europeana.uim.deactivation.service;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.neo4j.rest.graphdb.RestGraphDatabase;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

import eu.europeana.corelib.edm.exceptions.MongoDBException;
import eu.europeana.corelib.lookup.impl.CollectionMongoServerImpl;
import eu.europeana.corelib.mongo.server.EdmMongoServer;
import eu.europeana.corelib.neo4j.entity.Neo4jBean;
import eu.europeana.corelib.tools.lookuptable.Collection;
import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;
import eu.europeana.europeanauim.utils.PropertyReader;
import eu.europeana.europeanauim.utils.UimConfigurationProperty;
import eu.europeana.uim.common.BlockingInitializer;

public class DeactivationServiceImpl implements DeactivationService {

    //  private static HttpSolrServer solrServer;
    private static CloudSolrServer cloudSolrServer;
    private static CloudSolrServer cloudSolrProductionServer;
    private static String[] cloudSolrUrl = PropertyReader.getProperty(
            UimConfigurationProperty.CLOUD_SOLR_HOSTURL).split(",");
    private static String[] cloudSolrUrlProduction = PropertyReader.getProperty(
            UimConfigurationProperty.CLOUD_PRODUCTION_SOLR_HOSTURL).split(",");
    private static String cloudSolrCore = PropertyReader
            .getProperty(UimConfigurationProperty.CLOUD_SOLR_CORE);
    private static String zookeeperUrl = PropertyReader
            .getProperty(UimConfigurationProperty.ZOOKEEPER_HOSTURL);
    private static String zookeeperUrlProduction = PropertyReader
            .getProperty(UimConfigurationProperty.ZOOKEEPER_HOSTURLPRODUCTION);

    private static String mongoDB = PropertyReader
            .getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANA);
    private static String[] mongoHost = PropertyReader.getProperty(
            UimConfigurationProperty.MONGO_HOSTURL).split(",");
    private static String mongoPort = PropertyReader
            .getProperty(UimConfigurationProperty.MONGO_HOSTPORT);

    private static String mongoDBProduction = PropertyReader
            .getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANA_PRODUCTION);
    private static String[] mongoHostProduction = PropertyReader.getProperty(
            UimConfigurationProperty.MONGO_HOSTURL_PRODUCTION).split(",");

    private static ExtendedEdmMongoServer mongoServer;
    private static ExtendedEdmMongoServer mongoServerProduction;
    private static RestGraphDatabase graphDb;
    private static RestGraphDatabase graphDbProduction;
    //  private static String mongoDB;
//  private static String mongoHost;
//  private static String mongoPort;
//  private static String solrUrl;
//  private static String solrCore;
    private static String index = PropertyReader.getProperty(UimConfigurationProperty.NEO4JINDEX);
    private static String indexProduction = PropertyReader.getProperty(UimConfigurationProperty.NEO4JINDEXPRODUCTION);
    private static String neo4jPath = PropertyReader.getProperty(UimConfigurationProperty.NEO4JPATH);
    private static String neo4jPathProduction = PropertyReader.getProperty(UimConfigurationProperty.NEO4JPATHPRODUCTION);
    private static CollectionMongoServer collectionMongoServer;
    private static String usernameIngestion = PropertyReader.getProperty(UimConfigurationProperty.MONGO_USERNAME);
    private static String passwordIngestion = PropertyReader.getProperty(UimConfigurationProperty.MONGO_PASSWORD);
    private static String usernameProduction = PropertyReader.getProperty(UimConfigurationProperty.MONGO_PRODUCTION_USERNAME);
    private static String passwordProduction = PropertyReader.getProperty(UimConfigurationProperty.MONGO_PRODUCTION_PASSWORD);

    public DeactivationServiceImpl() {

    }

//  public HttpSolrServer getSolrServer() {
//    return solrServer;
//  }

    @Override
    public CloudSolrServer getCloudSolrServer() {
        return cloudSolrServer;
    }

    @Override
    public CloudSolrServer getProductionCloudSolrServer() {
        return cloudSolrProductionServer;
    }

    public ExtendedEdmMongoServer getMongoServer() {
        return mongoServer;
    }

    public ExtendedEdmMongoServer getProductionMongoServer() {
        return mongoServerProduction;
    }

    public void initialize() {
        try {
//      mongoDB = PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANA);
//      mongoHost = PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTURL);
//      mongoPort = PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTPORT);
//      solrUrl = PropertyReader.getProperty(UimConfigurationProperty.SOLR_HOSTURL);
//      solrCore = PropertyReader.getProperty(UimConfigurationProperty.SOLR_CORE); 

            graphDb =
                    new RestGraphDatabase(neo4jPath);
            graphDbProduction =
                    new RestGraphDatabase(neo4jPathProduction);
            BlockingInitializer solrInit = new BlockingInitializer() {

                @Override
                protected void initializeInternal() {
                    try {
                        LBHttpSolrServer lbTarget = new LBHttpSolrServer(cloudSolrUrl);
                        cloudSolrServer = new CloudSolrServer(zookeeperUrl, lbTarget);
                        cloudSolrServer.setDefaultCollection(cloudSolrCore);
                        cloudSolrServer.connect();
                        LBHttpSolrServer lbTargetProduction = new LBHttpSolrServer(cloudSolrUrlProduction);
                        cloudSolrProductionServer = new CloudSolrServer(zookeeperUrlProduction, lbTargetProduction);
                        cloudSolrProductionServer.setDefaultCollection(cloudSolrCore);
                        cloudSolrProductionServer.connect();
//            solrServer = new HttpSolrServer(new URL(solrUrl) + solrCore);
                    } catch (MalformedURLException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            solrInit.initialize(CloudSolrServer.class.getClassLoader());

            BlockingInitializer initializer = new BlockingInitializer() {

                @Override
                protected void initializeInternal() {
                    try {
                        List<ServerAddress> addresses = new ArrayList<ServerAddress>();
//            String[] mongoHost =
//                PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTURL).split(",");
                        for (String mongoStr : mongoHost) {
                            ServerAddress address = null;
                            try {
                                address = new ServerAddress(mongoStr, Integer.parseInt(mongoPort));
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            addresses.add(address);
                        }
                        Mongo tgtMongo = new Mongo(addresses);
                        mongoServer = new ExtendedEdmMongoServer(tgtMongo, mongoDB, usernameIngestion, passwordIngestion);
                        mongoServer.createDatastore(new Morphia());
                        System.out.println("Replica set status ingestion: " + mongoServer.getDatastore().getMongo().getReplicaSetStatus().toString());
                        mongoServer.getFullBean("test");


                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (MongoDBException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (MongoException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            };
            initializer.initialize(EdmMongoServer.class.getClassLoader());

            BlockingInitializer initializer1 = new BlockingInitializer() {
                @Override
                protected void initializeInternal() {
                    try {
                        List<ServerAddress> addressesProduction = new ArrayList<ServerAddress>();
                        for (String mongoStr : mongoHostProduction) {
                            ServerAddress addressProduction = null;
                            try {
                                addressProduction = new ServerAddress(mongoStr, Integer.parseInt(mongoPort));
                            } catch (UnknownHostException e) {
                                e.printStackTrace();
                            }
                            addressesProduction.add(addressProduction);
                        }
                        Mongo tgtMongoProduction = new Mongo(addressesProduction);
                        mongoServerProduction = new ExtendedEdmMongoServer(tgtMongoProduction, mongoDBProduction, usernameProduction, passwordProduction);
                        mongoServerProduction.createDatastore(new Morphia());
                        System.out.println("Replica set status production: " + mongoServerProduction.getDatastore().getMongo().getReplicaSetStatus().toString());
                        mongoServerProduction.getFullBean("test");

                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (MongoDBException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (MongoException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
            };
            initializer1.initialize(EdmMongoServer.class.getClassLoader());
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        BlockingInitializer colInitializer = new BlockingInitializer() {

            @Override
            protected void initializeInternal() {
                try {
                    List<ServerAddress> addresses = new ArrayList<ServerAddress>();
//          String[] mongoHost =
//              PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTURL).split(",");
                    for (String mongoStr : mongoHost) {
                        ServerAddress address = null;
                        try {
                            address = new ServerAddress(mongoStr, 27017);
                        } catch (UnknownHostException e) {
                            e.printStackTrace();
                        }
                        addresses.add(address);
                    }
                    Mongo tgtMongo = new Mongo(addresses);
                    if (StringUtils.isNotBlank(usernameIngestion)) {
                        collectionMongoServer = new CollectionMongoServerImpl(tgtMongo, PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_COLLECTIONS), usernameIngestion, passwordIngestion);
                    } else {
                        collectionMongoServer = new CollectionMongoServerImpl(tgtMongo, PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_COLLECTIONS));
                    }
                    collectionMongoServer.findOldCollectionId("test");
                } catch (NumberFormatException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (MongoException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

            }
        };
        colInitializer.initialize(Collection.class.getClassLoader());
    }

    @Override
    public CollectionMongoServer getCollectionMongoServer() {
        return collectionMongoServer;
    }

    @Override
    public RestGraphDatabase getGraphDb() {
        return graphDb;
    }

    @Override
    public RestGraphDatabase getGraphDbProduction() {
        return graphDbProduction;
    }

    @Override
    public String getNeo4jIndex() {
        return index;
    }

    @Override
    public String getNeo4jIndexProduction() {
        return indexProduction;
    }
}
