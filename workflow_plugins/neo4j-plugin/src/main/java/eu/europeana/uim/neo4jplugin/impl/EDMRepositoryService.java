/**
 * 
 */
package eu.europeana.uim.neo4jplugin.impl;


import org.neo4j.graphdb.DynamicLabel;
import org.neo4j.graphdb.Label;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.springframework.data.neo4j.support.GraphDatabaseFactoryBean;
import org.springframework.data.neo4j.support.Neo4jTemplate;


/**
 * @author geomark
 *
 */

public class EDMRepositoryService {
	
	private  EDMRepository edmrepository;
	private Neo4jTemplate template;
	RestGraphDatabase dbservice ;

	public EDMRepositoryService(){
             
		dbservice = new RestGraphDatabase("http://localhost:7474/db/data/");
                
                dbservice.createNode(DynamicLabel.label("name"));
//		Map2StringConverterFactory fac1 = new Map2StringConverterFactory();
//		String2MapConverterFactory fac2 = new String2MapConverterFactory();
//		GenericConversionService conversionservice = new GenericConversionService();
//		conversionservice.addConverterFactory(fac1);
//		conversionservice.addConverterFactory(fac2);
//		dbservice.setConversionService(conversionservice);
		//template = new Neo4jTemplate(dbservice);
                
//		GenericConversionService service = (GenericConversionService) template.getConversionService();	
                
//		service.addConverterFactory(fac1);
//		service.addConverterFactory(fac2);		
		//edmrepository = new EDMRepository(template);

	}
	
	/**
	 * @return
	 */
//	public EDMRepository getEdmrepository() {
//		return edmrepository;
//	}
//
//	/**
//	 * @param edmrepository
//	 */
//	public void setEdmrepository(EDMRepository edmrepository) {
//		this.edmrepository = edmrepository;
//	}
//	
//	/**
//	 * @return
//	 */
//	public Neo4jTemplate getTemplate() {
//		return template;
//	}
//
//	/**
//	 * @param template
//	 */
//	public void setTemplate(Neo4jTemplate template) {
//		this.template = template;
//	}
	
        public RestGraphDatabase getDb(){
            return this.dbservice;
        }
}
