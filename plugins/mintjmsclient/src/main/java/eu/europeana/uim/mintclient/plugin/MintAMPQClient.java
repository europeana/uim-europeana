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

	public void createOrganization(CreateOrganizationCommand command);
	
	public void createUser(CreateUserCommand command);
	
	public void getImports(GetImportsCommand command);
	
	public void getTransformations(GetTransformationsCommand command);
	
	public void publishCollection(PublicationCommand command);
	
	public void createImports(CreateImportCommand command);
}
