/**
 * 
 */
package eu.europeana.uim.mintclient.ampq;

import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportResponse;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;

/**
 * 
 * 
 * @author geomark
 */
public interface MintAMPQClientSync extends MintAMPQClient {

	public CreateOrganizationResponse createOrganization(CreateOrganizationCommand command) throws MintOSGIClientException, MintRemoteException;
	
	public CreateUserResponse createUser(CreateUserCommand command) throws MintOSGIClientException, MintRemoteException;
	
	public GetImportsResponse getImports(GetImportsCommand command) throws MintOSGIClientException, MintRemoteException;
	
	public GetTransformationsResponse getTransformations(GetTransformationsCommand command) throws MintOSGIClientException, MintRemoteException;
	
	public PublicationResponse publishCollection(PublicationCommand command) throws MintOSGIClientException, MintRemoteException;
	
	public CreateImportResponse createImports(CreateImportCommand command) throws MintOSGIClientException, MintRemoteException;
}
