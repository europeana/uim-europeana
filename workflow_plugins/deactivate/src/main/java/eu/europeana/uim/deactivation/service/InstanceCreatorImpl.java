/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.europeana.uim.deactivation.service;

import eu.europeana.europeanauim.utils.PropertyReader;
import eu.europeana.europeanauim.utils.UimConfigurationProperty;
import eu.europeana.harvester.client.HarvesterClientConfig;
import eu.europeana.harvester.db.MorphiaDataStore;
import eu.europeana.harvester.domain.ProcessingJob;
import eu.europeana.harvester.domain.SourceDocumentReference;
import eu.europeana.uim.common.BlockingInitializer;


import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author gmamakis
 */
public class InstanceCreatorImpl implements InstanceCreator {
    static MorphiaDataStore ds;
    static HarvesterClientConfig config;
    
    public InstanceCreatorImpl(){
        
        try {
            
            String mongoHost = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_HOSTURL);
            int mongoPort = Integer.parseInt(PropertyReader.getProperty(UimConfigurationProperty.CLIENT_HOSTPORT));
            String dbName = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_DB);
            
            ds = new MorphiaDataStore(mongoHost, mongoPort, dbName);
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
        } catch (IOException ex) {
            Logger.getLogger(InstanceCreatorImpl.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    
    @Override
    public MorphiaDataStore getDatastore(){
        return ds;
    }
    
    @Override
    public HarvesterClientConfig getConfig(){
        return config;
    }
}
