/**
 * 
 */
package eu.europeana.uim.plugins.mint.test;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.europeana.uim.mintclient.ampq.MintClientFactory;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.plugin.MintAMPQClientASync;
import eu.europeana.uim.mintclient.plugin.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.plugin.exceptions.MintRemoteException;


/**
 * 
 * @author Georgios Markakis
 */
public class MintSendAsyncTest {
	private static MintAMPQClientASync client;
	
	@BeforeClass public static void initclient() throws MintOSGIClientException, MintRemoteException {
		MintClientFactory factory = new MintClientFactory();
		client = (MintAMPQClientASync) factory.asyncMode().createClient(); 
	}
	    
	@AfterClass public static void tearDown() {
	   client = null;
	   System.gc();
	}
	
	
	
	@Test
	public void createOrganizationTest() throws Exception{
		CreateOrganizationCommand command = new CreateOrganizationCommand();
		
		command.setCorrelationId("correlationId");
		command.setCountry("es");
		command.setEnglishName("TestOrg");
		command.setName("TestOrg");
		command.setType("Type");
		command.setUserId("1002");
		client.createOrganization(command);
	}
	
	@Test
	public void createUserTest() throws Exception{
		CreateUserCommand command = new CreateUserCommand();
		command.setCorrelationId("correlationId");
		command.setEmail("email");
		command.setFirstName("firstName");
		command.setLastName("lastName");
		command.setUserName("user" + (new Date()).toString());
		command.setPassword("werwer");
		command.setPhone("234234234");
		command.setOrganization("1001");
		client.createUser(command);
	}
	

	@Test
	public void createImportsTest() throws Exception{
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
	
	@Test
	public void getImportsTest() throws Exception{
		GetImportsCommand command =  new GetImportsCommand();
		command.setCorrelationId("provid");
		command.setOrganizationId("1002");
		client.getImports(command);
	}
	
	@Test
	public void getTransformations() throws Exception{
		GetTransformationsCommand command = new GetTransformationsCommand();
		command.setCorrelationId("correlationId");
		command.setOrganizationId("1002");
		client.getTransformations(command);
	}
	
	@Test
	public void publishCollection() throws Exception{
		PublicationCommand command = new PublicationCommand();
		command.setCorrelationId("correlationId");
		List<String> list =  new ArrayList<String>();
		list.add("test1");
		list.add("test2");
		command.setIncludedImportList(list );
		command.setOrganizationId("orgid");
		command.setUserId("userId");
		client.publishCollection(command);
	}
}
