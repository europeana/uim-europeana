/**
 * 
 */
package eu.europeana.uim.mintclient.plugin;

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

/**
 * 
 * 
 * @author geomark
 */
public interface MintAMPQClient {

	public CreateOrganizationResponse createOrganization(CreateOrganizationCommand command);
	
	public CreateUserResponse createUser(CreateUserCommand command);
	
	public GetImportsResponse getImports(GetImportsCommand command);
	
	public GetTransformationsResponse getTransformations(GetTransformationsCommand command);
	
	public PublicationResponse publishCollection(PublicationCommand command);
	
	public CreateImportResponse createImports(CreateImportCommand command);
}
