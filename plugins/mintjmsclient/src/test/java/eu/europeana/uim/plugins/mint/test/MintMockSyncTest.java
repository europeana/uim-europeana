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

import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import org.apache.log4j.Logger;
import org.junit.BeforeClass;
import org.mockito.ArgumentCaptor;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import eu.europeana.uim.mintclient.ampq.MintAMPQClientSync;
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
import eu.europeana.uim.mintclient.jibxbindings.ImportExistsCommand;
import eu.europeana.uim.mintclient.jibxbindings.ImportExistsResponse;
import eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsCommand;
import eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsResponse;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationResponse;
import eu.europeana.uim.mintclient.jibxbindings.UserExistsCommand;
import eu.europeana.uim.mintclient.jibxbindings.UserExistsResponse;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;



/**
 * Mockito based unit tests for MINT synchronous calls
 * 
 * @author Georgios Markakis (gwarkx@hotmail.com)
 *
 * @since Oct 14, 2013
 */
public class MintMockSyncTest extends AbstractSyncMintTest {

	private static org.apache.log4j.Logger log = Logger.getLogger(MintMockSyncTest.class);
	
	/**
	 * Initialize client, superclass fields and mock objects behaviour
	 * 
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	@BeforeClass public static void initclient() throws MintOSGIClientException, MintRemoteException {
		client = mock(MintAMPQClientSync.class);
		
		CreateOrganizationResponse createOrganizationCommandRes = new CreateOrganizationResponse();
		createOrganizationCommandRes.setOrganizationId("mockOrgID"); 
		when(client.createOrganization((CreateOrganizationCommand) anyObject())).thenReturn(createOrganizationCommandRes);
		
		CreateUserResponse createUserCommandRes = new CreateUserResponse();
		createUserCommandRes.setUserId("mockUserID");
		when(client.createUser((CreateUserCommand) anyObject())).thenReturn(createUserCommandRes );
		
		CreateImportResponse createImportCommandRes = new CreateImportResponse();
		createImportCommandRes.setImportId("mockImportID");
		when(client.createImports((CreateImportCommand) anyObject())).thenReturn(createImportCommandRes );
		
		GetImportsResponse getImportsCommandRes = new GetImportsResponse();
		when(client.getImports((GetImportsCommand) anyObject())).thenReturn(getImportsCommandRes );
		
		GetTransformationsResponse getTransformationsCommandRes = new GetTransformationsResponse();
		when(client.getTransformations((GetTransformationsCommand) anyObject())).thenReturn(getTransformationsCommandRes );
		
		PublicationResponse publicationCommandResp = new PublicationResponse();
		publicationCommandResp.setUrl("mockurl");
		when(client.publishCollection((PublicationCommand) anyObject())).thenReturn(publicationCommandResp);
		
		
		// Define argument captors
		final ArgumentCaptor<OrganizationExistsCommand> orgexistsargument = ArgumentCaptor.forClass(OrganizationExistsCommand.class);
		final ArgumentCaptor<UserExistsCommand> userexistsargument = ArgumentCaptor.forClass(UserExistsCommand.class);
		final ArgumentCaptor<ImportExistsCommand> importexistsargument = ArgumentCaptor.forClass(ImportExistsCommand.class);
		
		when(client.organizationExists(orgexistsargument.capture())).thenAnswer(new Answer<OrganizationExistsResponse>(){
			@Override
			public OrganizationExistsResponse answer(InvocationOnMock invocation)
					throws Throwable {
				OrganizationExistsResponse resp = new OrganizationExistsResponse();
				
				if(orgexistsargument.getValue().getOrganizationId().equals("0")){
					resp.setExists(false);
				}
				else{
					
					resp.setExists(true);
				}
				return resp;
			}
		});
		
		
		when(client.importExists(importexistsargument.capture())).thenAnswer(new Answer<ImportExistsResponse>(){
			@Override
			public ImportExistsResponse answer(InvocationOnMock invocation)
					throws Throwable {
				ImportExistsResponse resp = new ImportExistsResponse();
				
				if(importexistsargument.getValue().getImportId().equals("0")){
					resp.setExists(false);
				}
				else{
					
					resp.setExists(true);
				}
				return resp;
			}
		});
		
		when(client.userExists(userexistsargument.capture())).thenAnswer(new Answer<UserExistsResponse>(){
			@Override
			public UserExistsResponse answer(InvocationOnMock invocation)
					throws Throwable {
				UserExistsResponse resp = new UserExistsResponse();
				
				if(userexistsargument.getValue().getUserId().equals("0")){
					resp.setExists(false);
				}
				else{
					
					resp.setExists(true);
				}
				return resp;
			}
		});
		
	}
}
