/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.internal.helpers;

import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModules;


/**
 * @author georgiosmarkakis
 *
 */
public class JaxbContextProvider {

	
	public ClassLoader provideJaxbClassloader() {
		
		return GetAvailableModules.class.getClassLoader();
	}

	
}
