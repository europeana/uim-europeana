/**
 * 
 */
package eu.europeana.uim.mintclient.plugin;

import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;

/**
 * @author geomark
 *
 */
public interface MintUIMService {

	public void createMintAuthorizedUser(Provider<?> provider);
	
	public void createMintOrganization(Provider<?> provider);
	
	public void createMappingSession(Collection<?> collection);
	
	public void publishCollection(Collection<?> collection);
	
}
