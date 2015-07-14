/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.europeana.uim.plugin.thumbler.impl;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;
import eu.europeana.corelib.lookup.impl.CollectionMongoServerImpl;
import eu.europeana.corelib.storage.MongoServer;
import eu.europeana.corelib.tools.lookuptable.Collection;
import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;
import eu.europeana.harvester.client.HarvesterClientConfig;
import eu.europeana.harvester.db.MorphiaDataStore;
import eu.europeana.harvester.domain.ProcessingJob;
import eu.europeana.harvester.domain.SourceDocumentReference;
import eu.europeana.uim.common.BlockingInitializer;
import eu.europeana.uim.plugin.thumbler.InstanceCreator;
import eu.europeana.uim.plugin.thumbler.utils.PropertyReader;
import eu.europeana.uim.plugin.thumbler.utils.UimConfigurationProperty;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gmamakis
 */
public class InstanceCreatorImpl implements InstanceCreator {
    private static Datastore ds;
    private static HarvesterClientConfig config;
    private static CollectionMongoServer collectionMongoServer;
    private static String mongoDB = PropertyReader
            .getProperty(UimConfigurationProperty.MONGO_DB_COLLECTIONS);
    private static String[] mongoCollectionHost = PropertyReader.getProperty(
            UimConfigurationProperty.MONGO_HOSTURL).split(",");
    private static String mongoPort = PropertyReader
            .getProperty(UimConfigurationProperty.MONGO_HOSTPORT);
    public InstanceCreatorImpl(){
        
        try {

            String mongoHost = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_HOSTURL);
            int mongoPort = Integer.parseInt(PropertyReader.getProperty(UimConfigurationProperty.CLIENT_HOSTPORT));
            String dbName = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_DB);

            String username = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_USERNAME);
            String password = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_PASSWORD);
            MongoClient mongo = new MongoClient(mongoHost, mongoPort);
            Morphia morphia = new Morphia();
            if(StringUtils.isNotEmpty(password)) {
                ds = morphia.createDatastore(mongo, dbName, username, password.toCharArray());
            } else {
                ds = morphia.createDatastore(mongo, dbName);
            }

            config = new HarvesterClientConfig();

            BlockingInitializer sdr = new BlockingInitializer() {

                @Override
                protected void initializeInternal() {
                    SourceDocumentReference sdrRef = new SourceDocumentReference();
                }
            };
            sdr.initialize(SourceDocumentReference.class.getClassLoader());
            BlockingInitializer pj = new BlockingInitializer() {

                @Override
                protected void initializeInternal() {
                    ProcessingJob pJob = new ProcessingJob();
                }
            };
            pj.initialize(ProcessingJob.class.getClassLoader());

            BlockingInitializer initializer = new BlockingInitializer() {

                @Override
                protected void initializeInternal() {
                    try {
                        Morphia morphia = new Morphia();
                        morphia.map(Collection.class);
                        List<ServerAddress> addresses = new ArrayList<>();
                        for (String mongoStr : mongoCollectionHost) {
                            ServerAddress address = new ServerAddress(mongoStr, 27017);
                            addresses.add(address);
                        }
                        Mongo tgtMongo = new Mongo(addresses);
                        Datastore datastore =
                                morphia.createDatastore(tgtMongo,
                                        "collections");
                        collectionMongoServer = new CollectionMongoServerImpl();
                        datastore.ensureIndexes();
                        collectionMongoServer.setDatastore(datastore);
                        BlockingInitializer colInitializer = new BlockingInitializer() {

                            @Override
                            protected void initializeInternal() {
                                Collection col = new Collection();
                                collectionMongoServer.findOldCollectionId("test");
                            }
                        };
                        colInitializer.initialize(Collection.class.getClassLoader());
                    } catch (NumberFormatException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (MongoException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                }
            };

            initializer.initialize(CollectionMongoServerImpl.class.getClassLoader());
        } catch (IOException ex) {
            Logger.getLogger(InstanceCreatorImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    @Override
    public Datastore getDatastore(){
        return ds;
    }
    
    @Override
    public HarvesterClientConfig getConfig(){
        return config;
    }

    @Override
    public CollectionMongoServer getCollectionMongoServer() {


        return collectionMongoServer;
    }
}
