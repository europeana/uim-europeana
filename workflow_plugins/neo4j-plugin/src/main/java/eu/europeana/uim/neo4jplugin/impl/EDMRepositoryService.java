/**
 * 
 */
package eu.europeana.uim.neo4jplugin.impl;


import org.neo4j.rest.graphdb.RestGraphDatabase;
import eu.europeana.uim.neo4jplugin.utils.PropertyReader;
import eu.europeana.uim.neo4jplugin.utils.UimConfigurationProperty;


/**
 * @author geomark
 *
 */

public class EDMRepositoryService {
	
	private RestGraphDatabase dbservice;
	private String index;

	public EDMRepositoryService(){
		System.setProperty("org.neo4j.rest.batch_transaction", "true");
		dbservice = new RestGraphDatabase(PropertyReader
					.getProperty(UimConfigurationProperty.NEO4JPATH));
		index = PropertyReader
					.getProperty(UimConfigurationProperty.NEO4JINDEX);
                        


	}
	
	public RestGraphDatabase getGraphDatabaseService(){
		return this.dbservice;
	}
	
        public String getIndex(){
            return this.index;
        }
}
