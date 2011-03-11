/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.internal.helpers;

import eu.europeana.uim.sugarcrmclient.jaxbbindings.ObjectFactory;


/**
 * @author georgiosmarkakis
 *
 */
public class JaxbContextProvider {

	
	public ClassLoader provideJaxbClassloader() {
		
		return ObjectFactory.class.getClassLoader();
	}

	
}
