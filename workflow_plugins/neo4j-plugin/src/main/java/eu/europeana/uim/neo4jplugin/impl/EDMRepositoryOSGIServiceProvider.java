/**
 * 
 */
package eu.europeana.uim.neo4jplugin.impl;

import eu.europeana.uim.common.BlockingInitializer;

/**
 * @author geomark
 *
 */
public class EDMRepositoryOSGIServiceProvider {

	private  GraphConstructor graphconstructor;
	
	public GraphConstructor getGraphconstructor() {
		return graphconstructor;
	}

	public void setGraphconstructor(GraphConstructor graphconstructor) {
		this.graphconstructor = graphconstructor;
	}

	public EDMRepositoryOSGIServiceProvider(){

		
		BlockingInitializer initializer = new BlockingInitializer() {
	            @Override
	            public void initializeInternal() {
	                try {
	                    status = STATUS_BOOTING;
	                    graphconstructor= new GraphConstructor();
	                    status = STATUS_INITIALIZED;
	                } catch (Throwable t) {
                            t.printStackTrace();
	                    status = STATUS_FAILED;
	                }
	            }
	        };
	        
	        initializer.initialize(GraphConstructor.class.getClassLoader());
	        
            
            if(graphconstructor ==  null){
            	System.out.println("Initialization failed!!!");
            }
            else{
            	System.out.println("Initialization successful!!!");
            }
	}
	
	
}
