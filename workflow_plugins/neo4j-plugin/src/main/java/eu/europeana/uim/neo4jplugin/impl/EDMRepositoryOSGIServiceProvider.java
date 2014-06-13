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

	private  GraphConstructorSpring graphconstructor;
	
	public GraphConstructorSpring getGraphconstructor() {
		return graphconstructor;
	}

	public void setGraphconstructor(GraphConstructorSpring graphconstructor) {
		this.graphconstructor = graphconstructor;
	}

	public EDMRepositoryOSGIServiceProvider(){

		
		BlockingInitializer initializer = new BlockingInitializer() {
	            @Override
	            public void initializeInternal() {
	                try {
	                    status = STATUS_BOOTING;
	                    graphconstructor= new GraphConstructorSpring();
	                    status = STATUS_INITIALIZED;
	                } catch (Throwable t) {
                            t.printStackTrace();
	                    status = STATUS_FAILED;
	                }
	            }
	        };
	        
	        initializer.initialize(GraphConstructorSpring.class.getClassLoader());
	        
            
            if(graphconstructor ==  null){
            	System.out.println("Initialization failed!!!");
            }
            else{
            	System.out.println("Initialization successful!!!");
            }
	}
	
	
}
