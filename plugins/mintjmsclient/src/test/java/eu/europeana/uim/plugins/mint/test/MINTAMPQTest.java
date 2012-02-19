/**
 * 
 */
package eu.europeana.uim.plugins.mint.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import eu.europeana.uim.mintclient.ampq.MintAMPQClientImpl;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;

import eu.europeana.uim.mintclient.plugin.MintAMPQClient;
/**
 * @author geomark
 *
 */
public class MINTAMPQTest {

	@Test
	public void createOrganizationTest(){
		MintAMPQClient client = new MintAMPQClientImpl();
		CreateOrganizationCommand command = new CreateOrganizationCommand();
		
		command.setCorrelationId("ProviderMenmonic");
		command.setCountry("es");
		command.setEnglishName("?");
		command.setName("?");
		command.setType("Type");
		command.setUserId("userid");
		client.createOrganization(command);
	}
	
	
	
	@Test
	public void createUserTest(){
		MintAMPQClient client = new MintAMPQClientImpl();
		CreateUserCommand command = new CreateUserCommand();
		command.setCorrelationId("correlationId");
		command.setEmail("email");
		command.setFirstName("firstName");
		command.setLastName("lastName");
		command.setUserName("user");
		command.setPassword("werwer");
		command.setPhone("234234234");
		client.createUser(command);
	}
	
	@Test
	public void createImportsTest(){
		MintAMPQClient client = new MintAMPQClientImpl();
		CreateImportCommand command = new CreateImportCommand();
		command.setCorrelationId("CollectionId");
		command.setJdbcRepoxURL("jdbcRepoxURL");
		command.setRepoxTableName("menmonic");
		command.setUserId("userId");
		client.createImports(command);
		
	}
	
	@Test
	public void getImportsTest(){
		MintAMPQClient client = new MintAMPQClientImpl();
		GetImportsCommand command =  new GetImportsCommand();
		command.setCorrelationId("provid");
		command.setOrganizationId("orgid");
		client.getImports(command);
	}
	
	@Test
	public void getTransformations(){
		MintAMPQClient client = new MintAMPQClientImpl();
		GetTransformationsCommand command = new GetTransformationsCommand();
		command.setCorrelationId("correlationId");
		command.setOrganizationId("orgid");
		client.getTransformations(command);
	}
	
	@Test
	public void publishCollection(){
		MintAMPQClient client = new MintAMPQClientImpl();
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
