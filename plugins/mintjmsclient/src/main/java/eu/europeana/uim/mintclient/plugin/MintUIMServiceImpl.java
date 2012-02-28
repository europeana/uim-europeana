/**
 * 
 */
package eu.europeana.uim.mintclient.plugin;

import java.util.ArrayList;
import java.util.List;


import eu.europeana.uim.mintclient.ampq.MintAMPQClientSync;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;

/**
 * 
 * @author Georgios Markakis
 */
public class MintUIMServiceImpl implements MintUIMService {
private MintAMPQClientSync client;


	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.plugin.MintUIMService#createMintAuthorizedUser(eu.europeana.uim.store.Provider)
	 */
	@Override
	public void createMintAuthorizedUser(Provider<?> provider) throws MintOSGIClientException, MintRemoteException {
		CreateUserCommand command = new CreateUserCommand();
		command.setCorrelationId("correlationId");
		command.setEmail("email");
		command.setFirstName("firstName");
		command.setLastName("lastName");
		command.setUserName("userX");
		command.setPassword("werwer");
		command.setPhone("234234234");
		command.setOrganization("1001");
		client.createUser(command);

	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.plugin.MintUIMService#createMintOrganization(eu.europeana.uim.store.Provider)
	 */
	@Override
	public void createMintOrganization(Provider<?> provider) throws MintOSGIClientException, MintRemoteException {
		CreateOrganizationCommand command = new CreateOrganizationCommand();
		command.setCorrelationId("correlationId");
		command.setCountry("es");
		command.setEnglishName("TestOrg");
		command.setName("TestOrg");
		command.setType("Type");
		command.setUserId("1002");
		client.createOrganization(command);

	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.plugin.MintUIMService#createMappingSession(eu.europeana.uim.store.Collection)
	 */
	@Override
	public void createMappingSession(Collection<?> collection) throws MintOSGIClientException, MintRemoteException {
		CreateImportCommand command = new CreateImportCommand();
		
		command.setCorrelationId("123");
		command.setUserId("1000");
		command.setOrganizationId("1");
		command.setJdbcRepoxURL("jdbc:postgresql://localhost:5432/repox");
		command.setRepoxUserName("postgres");
		command.setRepoxUserPassword("raistlin");
		command.setRepoxTableName("azores13");
		client.createImports(command);

	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.plugin.MintUIMService#publishCollection(eu.europeana.uim.store.Collection)
	 */
	@Override
	public void publishCollection(Collection<?> collection) throws MintOSGIClientException, MintRemoteException {
		PublicationCommand command = new PublicationCommand();
		command.setCorrelationId("correlationId");
		List<String> list =  new ArrayList();
		list.add("test1");
		list.add("test2");
		command.setIncludedImportList(list );
		command.setOrganizationId("orgid");
		command.setUserId("userId");
		client.publishCollection(command);

	}



}
