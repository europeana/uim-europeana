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
package eu.europeana.uim.mintclient.ampq;

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
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;


/**
 * Interface for a synchronous AMPQ Client
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public interface MintAMPQClientSync extends MintAMPQClient {

	/**
	 * Send a message that creates an Organization entity
	 * 
	 * @param command the AMPQ message payload
	 * @return the payload of the AMPQ message response
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	public CreateOrganizationResponse createOrganization(CreateOrganizationCommand command) throws MintOSGIClientException, MintRemoteException;
	
	/**
	 * Send a message that creates a User entity
	 * 
	 * @param command the AMPQ message payload
	 * @return the payload of the AMPQ message response
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	public CreateUserResponse createUser(CreateUserCommand command) throws MintOSGIClientException, MintRemoteException;
	
	/**
	 * Send a message that gets the specific imports on a given Organization
	 * 
	 * @param command the AMPQ message payload
	 * @return the payload of the AMPQ message response
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	public GetImportsResponse getImports(GetImportsCommand command) throws MintOSGIClientException, MintRemoteException;
	
	/**
	 * Send a message that gets the transformations from a given Organization
	 * 
	 * @param command the AMPQ message payload
	 * @return the payload of the AMPQ message response
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	public GetTransformationsResponse getTransformations(GetTransformationsCommand command) throws MintOSGIClientException, MintRemoteException;
	
	/**
	 * Publishes the given collection by applying a specific transformation
	 * 
	 * @param command the AMPQ message payload
	 * @return the payload of the AMPQ message response
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	public PublicationResponse publishCollection(PublicationCommand command) throws MintOSGIClientException, MintRemoteException;
	
	/**
	 * Send a message that creates a mapping session by importing from Repox
	 * 
	 * @param command the AMPQ message payload
	 * @return the payload of the AMPQ message response
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	public CreateImportResponse createImports(CreateImportCommand command) throws MintOSGIClientException, MintRemoteException;
}
