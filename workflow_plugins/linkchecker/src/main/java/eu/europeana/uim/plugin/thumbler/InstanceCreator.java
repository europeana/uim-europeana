/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package eu.europeana.uim.plugin.thumbler;

import eu.europeana.harvester.client.HarvesterClientConfig;
import eu.europeana.harvester.db.MorphiaDataStore;

/**
 *
 * @author gmamakis
 */
public interface InstanceCreator {
    
     MorphiaDataStore getDatastore();
    
    HarvesterClientConfig getConfig();
}
