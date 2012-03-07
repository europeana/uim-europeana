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
package eu.europeana.uim.mintclient.service;

import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;


/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 7 Mar 2012
 */
public interface MintUIMService {

	/**
	 * @param provider
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	public void createMintAuthorizedUser(Provider<?> provider) throws MintOSGIClientException, MintRemoteException,StorageEngineException;
	
	/**
	 * @param provider
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	public void createMintOrganization(Provider<?> provider) throws MintOSGIClientException, MintRemoteException,StorageEngineException;
	
	/**
	 * @param collection
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	public void createMappingSession(Collection<?> collection) throws MintOSGIClientException, MintRemoteException,StorageEngineException;
	
	/**
	 * @param collection
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	public void publishCollection(Collection<?> collection) throws MintOSGIClientException, MintRemoteException,StorageEngineException;


	
}
