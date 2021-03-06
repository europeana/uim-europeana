/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved 
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 *  
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under 
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of 
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under 
 *  the Licence.
 */
package eu.europeana.uim.plugins.mint.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.junit.Test;
import eu.europeana.uim.mintclient.ampq.MintAMPQClientSync;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsResponse;
import eu.europeana.uim.mintclient.jibxbindings.ImportExistsAction;
import eu.europeana.uim.mintclient.jibxbindings.ImportExistsCommand;
import eu.europeana.uim.mintclient.jibxbindings.ImportExistsResponse;
import eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsAction;
import eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsCommand;
import eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsResponse;
import eu.europeana.uim.mintclient.jibxbindings.PublicationAction;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationResponse;
import eu.europeana.uim.mintclient.jibxbindings.UserExistsAction;
import eu.europeana.uim.mintclient.jibxbindings.UserExistsCommand;
import eu.europeana.uim.mintclient.jibxbindings.UserExistsResponse;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.utils.MintClientUtils;

/**
 * Unit tests implementation for MINT client synchronous calls used by both
 * mockito based and integration tests
 * 
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since Oct 11, 2013
 */
public abstract class AbstractSyncMintTest {

	private static org.apache.log4j.Logger log = Logger.getLogger(AbstractSyncMintTest.class);
	
	private static String userId;
	private static String orgId;
	private static String importId;
	
	
	/**
	 * Protected client field initialized by subclass
	 */
	protected static MintAMPQClientSync client;
	
	
	/**
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	@Test
	public void createOrganizationTest() throws MintOSGIClientException, MintRemoteException{
		CreateOrganizationCommand command = new CreateOrganizationCommand();
		command.setCountry("es");
		command.setEnglishName("TestOrg");
		command.setName("TestOrg");
		command.setType("Archive");
		command.setUserId("1000");
		CreateOrganizationResponse resp = client.createOrganization(command);
		orgId = resp.getOrganizationId();
		CreateOrganizationAction act = new CreateOrganizationAction();
		act.setCreateOrganizationResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
		assertNotNull(resp.getOrganizationId());
	}
	
	/**
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	@Test
	public void createUserTest() throws MintOSGIClientException, MintRemoteException{
		CreateUserCommand command = new CreateUserCommand();
		command.setEmail("email");
		command.setFirstName("firstName");
		command.setLastName("lastName");
		command.setUserName("user" + (new Date()).toString());
		command.setPassword("werwer");
		command.setPhone("234234234");
		command.setOrganization(orgId);
		CreateUserResponse resp = client.createUser(command);
		userId = resp.getUserId();
		CreateUserAction act = new CreateUserAction();
		act.setCreateUserResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
		assertNotNull(resp.getUserId());
	}
	

	/**
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	@Test
	public void createImportsTest() throws MintOSGIClientException, MintRemoteException{
		CreateImportCommand command = new CreateImportCommand();
		//command.setUserId(userId);
		command.setUserId("1000");
		//command.setOrganizationId(orgId);
		command.setOrganizationId("1");
		command.setRepoxTableName("azores13");
		CreateImportResponse resp = client.createImports(command);
		importId = resp.getImportId();
		CreateImportAction act = new CreateImportAction();
		act.setCreateImportResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
		assertNotNull(resp.getImportId());
		
		
	}
	
	/**
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	@Test
	public void getImportsTest() throws MintOSGIClientException, MintRemoteException{
		GetImportsCommand command =  new GetImportsCommand();
		command.setOrganizationId(orgId);
		GetImportsResponse resp = client.getImports(command);
		GetImportsAction act = new GetImportsAction();
		act.setGetImportsResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
	}
	
	/**
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	@Test
	public void getTransformations() throws MintOSGIClientException, MintRemoteException{
		GetTransformationsCommand command = new GetTransformationsCommand();
		command.setOrganizationId(orgId);
		GetTransformationsResponse resp = client.getTransformations(command);
		GetTransformationsAction act = new GetTransformationsAction();
		act.setGetTransformationsResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
	}
	
	/**
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	@Test
	public void publishCollection() throws MintOSGIClientException, MintRemoteException{
		PublicationCommand command = new PublicationCommand();
		List<String> list =  new ArrayList<String>();
		list.add("test1");
		list.add("test2");
		command.setIncludedImportList(list );
		command.setOrganizationId(orgId);
		command.setUserId(userId);
		PublicationResponse resp = client.publishCollection(command);
		PublicationAction act = new PublicationAction();
		act.setPublicationResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void organizationExists() throws Exception{
		OrganizationExistsCommand command = new OrganizationExistsCommand();
		if(orgId == null){
			orgId = "1000";
		}
		command.setOrganizationId(orgId);
		OrganizationExistsResponse resp = client.organizationExists(command);
		OrganizationExistsAction act = new OrganizationExistsAction();
		act.setOrganizationExistsResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
		assertEquals(resp.isExists(),true);
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void userExists() throws Exception{
		UserExistsCommand command = new UserExistsCommand();
		if(userId == null){
			userId = "1000";
		}
		command.setUserId(userId);
		UserExistsResponse resp = client.userExists(command);
		UserExistsAction act = new UserExistsAction();
		act.setUserExistsResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
		assertEquals(resp.isExists(),true);
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void importExists() throws Exception{
		ImportExistsCommand command = new ImportExistsCommand();
		if(importId == null){
			importId = "1000";
		}
		command.setImportId(importId);
		ImportExistsResponse resp = client.importExists(command);
		ImportExistsAction act = new ImportExistsAction();
		act.setImportExistsResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
		assertEquals(resp.isExists(),true);
	}
	
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void organizationNotExists() throws Exception{
		OrganizationExistsCommand command = new OrganizationExistsCommand();
		command.setOrganizationId("0");
		OrganizationExistsResponse resp = client.organizationExists(command);
		OrganizationExistsAction act = new OrganizationExistsAction();
		act.setOrganizationExistsResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
		assertEquals(resp.isExists(),false);
	}
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void userNotExists() throws Exception{
		UserExistsCommand command = new UserExistsCommand();
		command.setUserId("0");
		UserExistsResponse resp = client.userExists(command);
		UserExistsAction act = new UserExistsAction();
		act.setUserExistsResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
		assertEquals(resp.isExists(),false);
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void importNotExists() throws Exception{
		ImportExistsCommand command = new ImportExistsCommand();
		command.setImportId("0");
		ImportExistsResponse resp = client.importExists(command);
		ImportExistsAction act = new ImportExistsAction();
		act.setImportExistsResponse(resp);
		log.info(MintClientUtils.unmarshallObject(act));
		assertNotNull(resp);
		assertEquals(resp.isExists(),false);
	}
	
}
