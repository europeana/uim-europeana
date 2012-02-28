/**
 * 
 */
package eu.europeana.uim.mintclient.plugin;

import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;

/**
 * @author geomark
 *
 */
public interface MintUIMService {

	public void createMintAuthorizedUser(Provider<?> provider) throws MintOSGIClientException, MintRemoteException;
	
	public void createMintOrganization(Provider<?> provider) throws MintOSGIClientException, MintRemoteException;
	
	public void createMappingSession(Collection<?> collection) throws MintOSGIClientException, MintRemoteException;
	
	public void publishCollection(Collection<?> collection) throws MintOSGIClientException, MintRemoteException;
	
}
