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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.concurrent.TimeoutException;
import org.junit.BeforeClass;
import eu.europeana.uim.mintclient.ampq.MintAMPQClientASync;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsResponse;
import eu.europeana.uim.mintclient.jibxbindings.PublicationAction;
import eu.europeana.uim.mintclient.jibxbindings.PublicationResponse;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;

/**
 * Mockito based unit tests for MINT asynchronous calls
 * @author Georgios Markakis (gwarkx@hotmail.com)
 *
 * @since Oct 14, 2013
 */
public class MintMockAsyncTest extends AbstractAsyncMintTest {

	/**
	 * Initialize client, superclass fields and mock objects behaviour
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	@BeforeClass public static void initclient() throws MintOSGIClientException, MintRemoteException {
		client = mock(MintAMPQClientASync.class);
		listener = mock(TestListener.class);

		try {
			CreateImportAction createImportResponsevalue = new CreateImportAction();
			when(listener.getCreateImportResponse()).thenReturn(createImportResponsevalue);
			
			CreateOrganizationAction createCreateOrganizationResponseValue = new CreateOrganizationAction();
			CreateOrganizationResponse createOrganizationResponse = new CreateOrganizationResponse();
			createOrganizationResponse.setOrganizationId("mockorgid");
			createCreateOrganizationResponseValue.setCreateOrganizationResponse(createOrganizationResponse );
			when(listener.getCreateOrganizationResponse()).thenReturn(createCreateOrganizationResponseValue);
						
			CreateUserAction createUserResponseValue = new CreateUserAction();
			CreateUserResponse createUserResponse = new CreateUserResponse();
			createUserResponse.setUserId("mockuserid");
			createUserResponseValue.setCreateUserResponse(createUserResponse );
			when(listener.getCreateUserResponse()).thenReturn(createUserResponseValue );
						
			PublicationAction publicationActionResponseValue = new PublicationAction();
			PublicationResponse publicationResponse = new PublicationResponse();
			publicationResponse.setUrl("dummyuri");
			publicationActionResponseValue.setPublicationResponse(publicationResponse );
			when(listener.getPublicationActionResponse()).thenReturn(publicationActionResponseValue);
						
			GetTransformationsAction transformationsResponseValue = new GetTransformationsAction();
			GetTransformationsResponse getTransformationsResponse = new GetTransformationsResponse();
			transformationsResponseValue.setGetTransformationsResponse(getTransformationsResponse );
			when(listener.getTransformationsResponse()).thenReturn(transformationsResponseValue );
						
			GetImportsAction importsResponsevalue = new GetImportsAction();
			GetImportsResponse getImportsResponse = new GetImportsResponse();
			importsResponsevalue.setGetImportsResponse(getImportsResponse );
			when(listener.getImportsResponse()).thenReturn(importsResponsevalue );
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (TimeoutException e) {
			e.printStackTrace();
		}
	}
}
