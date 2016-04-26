/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.europeana.uim.enrichment.service.impl;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;
import com.mongodb.ServerAddress;
import eu.europeana.harvester.client.HarvesterClientConfig;

import eu.europeana.harvester.db.interfaces.SourceDocumentReferenceDao;
import eu.europeana.harvester.db.mongo.SourceDocumentReferenceDaoImpl;
import eu.europeana.harvester.domain.ProcessingJob;
import eu.europeana.harvester.domain.SourceDocumentReference;
import eu.europeana.uim.common.BlockingInitializer;
import eu.europeana.uim.enrichment.service.InstanceCreator;
import eu.europeana.uim.enrichment.utils.PropertyReader;
import eu.europeana.uim.enrichment.utils.UimConfigurationProperty;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
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
    
    public InstanceCreatorImpl(){
        
        try {
            
            String mongoHosts[] = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_HOSTURL).split(",");
            int mongoPort = Integer.parseInt(PropertyReader.getProperty(UimConfigurationProperty.CLIENT_HOSTPORT));
            String dbName = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_DB);

            String username = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_USERNAME);
            String password = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_PASSWORD);
            List<ServerAddress> addresses = new ArrayList<>();
            for(String mongoHost :mongoHosts) {
                ServerAddress address = new ServerAddress(mongoHost, mongoPort);
                addresses.add(address);
            }

            MongoClient mongo = new MongoClient(addresses);
            Morphia morphia = new Morphia();
            morphia.map(SourceDocumentReference.class);
            morphia.map(ProcessingJob.class);
            if(StringUtils.isNotEmpty(password)) {
                ds = morphia.createDatastore(mongo, dbName, username, password.toCharArray());
            } else {
                ds = morphia.createDatastore(mongo, dbName);
            }


            
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
            BlockingInitializer configInit = new BlockingInitializer() {
                @Override
                protected void initializeInternal() {
                    config = new HarvesterClientConfig();
                }
            };
            configInit.initialize(HarvesterClientConfig.class.getClassLoader());


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
}
