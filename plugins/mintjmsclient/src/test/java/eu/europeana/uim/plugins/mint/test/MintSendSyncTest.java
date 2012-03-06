/**
 * 
 */
package eu.europeana.uim.plugins.mint.test;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.europeana.uim.mintclient.ampq.MintAMPQClientSync;
import eu.europeana.uim.mintclient.ampq.MintAMPQClientSyncImpl;
import eu.europeana.uim.mintclient.ampq.MintClientFactory;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsResponse;
import eu.europeana.uim.mintclient.jibxbindings.PublicationAction;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationResponse;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.utils.MintClientUtils;


import org.apache.log4j.Logger;

/**
 * 
 * @author Georgios Markakis
 */
public class MintSendSyncTest {
	private static org.apache.log4j.Logger log = Logger.getLogger(MintSendSyncTest.class);
	
	private static MintAMPQClientSync client;
	
	@BeforeClass public static void initclient() throws MintOSGIClientException, MintRemoteException {
		MintClientFactory factory = new MintClientFactory();
		client = (MintAMPQClientSync) factory.syncMode().createClient();  
	}
	    
	@AfterClass public static void tearDown() {
	   client = null;
	   System.gc();
	}
	
	
	
	@Test
	public void createOrganizationTest() throws MintOSGIClientException, MintRemoteException{
		CreateOrganizationCommand command = new CreateOrganizationCommand();
		command.setCorrelationId("correlationId");
		command.setCountry("es");
		command.setEnglishName("TestOrg");
		command.setName("TestOrg");
		command.setType("Type");
		command.setUserId("1002");
		CreateOrganizationResponse resp = client.createOrganization(command);
		CreateOrganizationAction act = new CreateOrganizationAction();
		act.setCreateOrganizationResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
	}
	
	@Test
	public void createUserTest() throws MintOSGIClientException, MintRemoteException{
		CreateUserCommand command = new CreateUserCommand();
		command.setCorrelationId("correlationId");
		command.setEmail("email");
		command.setFirstName("firstName");
		command.setLastName("lastName");
		command.setUserName("user" + (new Date()).toString());
		command.setPassword("werwer");
		command.setPhone("234234234");
		command.setOrganization("1001");
		CreateUserResponse resp = client.createUser(command);
		CreateUserAction act = new CreateUserAction();
		act.setCreateUserResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
	}
	

	@Test
	public void createImportsTest() throws MintOSGIClientException, MintRemoteException{
		CreateImportCommand command = new CreateImportCommand();
		
		command.setCorrelationId("123");
		command.setUserId("1000");
		command.setOrganizationId("1");
		command.setRepoxTableName("azores13");
		CreateImportResponse resp = client.createImports(command);
		CreateImportAction act = new CreateImportAction();
		act.setCreateImportResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
		
	}
	
	@Test
	public void getImportsTest() throws MintOSGIClientException, MintRemoteException{
		GetImportsCommand command =  new GetImportsCommand();
		command.setCorrelationId("provid");
		command.setOrganizationId("1002");
		GetImportsResponse resp = client.getImports(command);
		GetImportsAction act = new GetImportsAction();
		act.setGetImportsResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
	}
	
	@Test
	public void getTransformations() throws MintOSGIClientException, MintRemoteException{
		GetTransformationsCommand command = new GetTransformationsCommand();
		command.setCorrelationId("correlationId");
		command.setOrganizationId("1002");
		GetTransformationsResponse resp = client.getTransformations(command);
		GetTransformationsAction act = new GetTransformationsAction();
		act.setGetTransformationsResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
	}
	
	@Test
	public void publishCollection() throws MintOSGIClientException, MintRemoteException{
		PublicationCommand command = new PublicationCommand();
		command.setCorrelationId("correlationId");
		List<String> list =  new ArrayList<String>();
		list.add("test1");
		list.add("test2");
		command.setIncludedImportList(list );
		command.setOrganizationId("orgid");
		command.setUserId("userId");
		PublicationResponse resp = client.publishCollection(command);
		PublicationAction act = new PublicationAction();
		act.setPublicationResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
	}
}
