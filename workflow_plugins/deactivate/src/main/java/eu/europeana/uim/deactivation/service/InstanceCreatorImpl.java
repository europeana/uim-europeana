/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.europeana.uim.deactivation.service;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.MongoClient;
import eu.europeana.europeanauim.utils.PropertyReader;
import eu.europeana.europeanauim.utils.UimConfigurationProperty;
import eu.europeana.harvester.client.HarvesterClientConfig;

import eu.europeana.harvester.domain.ProcessingJob;
import eu.europeana.harvester.domain.SourceDocumentReference;
import eu.europeana.uim.common.BlockingInitializer;
import org.apache.commons.lang.StringUtils;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gmamakis
 */
public class InstanceCreatorImpl implements InstanceCreator {
    static Datastore ds;
    static HarvesterClientConfig config;
    
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
