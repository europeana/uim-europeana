/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.europeana.uim.plugin.thumbler;

import com.google.code.morphia.Datastore;
import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;
import eu.europeana.harvester.client.HarvesterClient;
import eu.europeana.harvester.client.HarvesterClientConfig;


/**
 *
 * @author gmamakis
 */
public interface InstanceCreator {
    
    Datastore getDatastore();
    
    HarvesterClientConfig getConfig();

    CollectionMongoServer getCollectionMongoServer();

    HarvesterClient getClient();
}
