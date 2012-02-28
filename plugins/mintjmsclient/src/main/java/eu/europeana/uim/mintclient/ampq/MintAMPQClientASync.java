/**
 * 
 */
package eu.europeana.uim.mintclient.ampq;

import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;


/**
 * 
 * @author Georgios Markakis
 *
 */
public interface MintAMPQClientASync extends MintAMPQClient {

	public void createOrganization(CreateOrganizationCommand command) throws MintOSGIClientException, MintRemoteException;
	
	public void createUser(CreateUserCommand command) throws MintOSGIClientException, MintRemoteException;
	
	public void getImports(GetImportsCommand command) throws MintOSGIClientException, MintRemoteException;
	
	public void getTransformations(GetTransformationsCommand command) throws MintOSGIClientException, MintRemoteException;
	
	public void publishCollection(PublicationCommand command) throws MintOSGIClientException, MintRemoteException;
	
	public void createImports(CreateImportCommand command) throws MintOSGIClientException, MintRemoteException;
}
