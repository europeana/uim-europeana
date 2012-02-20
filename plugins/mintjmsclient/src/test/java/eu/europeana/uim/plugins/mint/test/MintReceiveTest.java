/**
 * 
 */
package eu.europeana.uim.plugins.mint.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import eu.europeana.uim.mintclient.ampq.MintAMPQClientImpl;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsResponse;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationResponse;
import eu.europeana.uim.mintclient.jibxbindings.PublicationResponse.IncludedImport;
import eu.europeana.uim.mintclient.plugin.MintAMPQClient;
import eu.europeana.uim.mintclient.utils.DummyMintClient;

/**
 * @author geomark
 *
 */
public class MintReceiveTest {
	private static DummyMintClient mintsender;
	
	@BeforeClass public static void initclient() {
		mintsender = new DummyMintClient();
	}
	    
	@AfterClass public static void tearDown() {
	   mintsender = null;
	   System.gc();
	}
	
	@Test
	public void OrganizationCreationResponseTest(){
		CreateOrganizationResponse response = new CreateOrganizationResponse();
		response.setCorrelationId("correlationId");
		response.setOrganizationId("organizationId");
		mintsender.createOrganizationResponse(response);
	}
	
	
	
	@Test
	public void CreateUserResponseTest(){
		CreateUserResponse response = new CreateUserResponse();
		response.setCorrelationId("correlationId");
		response.setUserId("userId");
		mintsender.createUserResponse(response);
	}
	
	@Test
	public void createImportsTest(){
		CreateImportResponse response = new CreateImportResponse();
		response.setCorrelationId("CollectionId");
		response.setImportId("importId");

		mintsender.createImportsResponse(response);
		
	}
	
	@Test
	public void getImportsTest(){
		GetImportsResponse response =  new GetImportsResponse();
		response.setCorrelationId("provid");
		List<String> list = new ArrayList();
		list.add("ex1");
		list.add("ex2");
		response.setImportIdList(list );
		
		mintsender.getImportsResponse(response);
	}
	
	@Test
	public void getTransformations(){
		GetTransformationsResponse response = new GetTransformationsResponse();
		response.setCorrelationId("correlationId");
		List<String> list = new ArrayList();
		list.add("ex1");
		list.add("ex2");
		response.setTransformationIdList(list);
		mintsender.getTransformationsResponse(response);
	}
	
	@Test
	public void publishCollection(){
		PublicationResponse response = new PublicationResponse();
		response.setCorrelationId("correlationId");


		List<IncludedImport> list = new ArrayList();
		
		IncludedImport imp1 = new IncludedImport();
		imp1.setBoolean(true);
		imp1.setImportId("importId");
		list.add(imp1);
		
		response.setIncludedImportList(list );
		response.setUrl("url");
		
		mintsender.publishCollectionResponse(response);
	}
}
