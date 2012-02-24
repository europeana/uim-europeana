/**
 * 
 */
package eu.europeana.uim.mintclient.plugin;

import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;


/**
 * 
 * @author geomark
 *
 */
public interface MintAMPQClientASync extends MintAMPQClient {

	public void createOrganization(CreateOrganizationCommand command);
	
	public void createUser(CreateUserCommand command);
	
	public void getImports(GetImportsCommand command);
	
	public void getTransformations(GetTransformationsCommand command);
	
	public void publishCollection(PublicationCommand command);
	
	public void createImports(CreateImportCommand command);
}
