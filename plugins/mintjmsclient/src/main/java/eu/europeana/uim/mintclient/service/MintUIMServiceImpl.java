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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eu.europeana.uim.mintclient.ampq.MintAMPQClientASync;
import eu.europeana.uim.mintclient.ampq.MintAMPQClientSync;
import eu.europeana.uim.mintclient.ampq.MintClientFactory;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.service.listeners.UIMConsumerListener;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.api.LoggingEngine;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.StorageEngineException;


/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class MintUIMServiceImpl implements MintUIMService {

	private static MintAMPQClientSync synchronousClient;
	private static MintAMPQClientASync asynchronousClient;
	private Registry registry;
	private Orchestrator<?> orchestrator;
	private LoggingEngine<?> logger; 
	
	/**
	 * 
	 */
	@SuppressWarnings("unchecked")
	public MintUIMServiceImpl(Registry registry,Orchestrator<?> orchestrator){
		this.registry = registry;
		this.orchestrator = orchestrator;
		this.logger = (LoggingEngine<?>) (registry!=null ? registry.getLoggingEngine(): null);
	}
	
	
	/**
	 * @param <T>
	 * @param registryref
	 * @param orchestratorref
	 */

	public static MintUIMServiceImpl  createService(Registry registryref, Orchestrator<?> orchestratorref ){

		MintClientFactory factory = new MintClientFactory();
		try {
			synchronousClient = (MintAMPQClientSync) factory.syncMode().createClient();			
			asynchronousClient = (MintAMPQClientASync) factory.asyncMode(MintUIMServiceImpl.UIMConsumerListener.class).createClient();
			return new MintUIMServiceImpl(registryref,orchestratorref);
		} catch (MintOSGIClientException e) {
			registryref.getLoggingEngine().logFailed(Level.SEVERE,
					"MintUIMServiceImpl", e,
					"Error instaniating service, client threw an exception");
		} catch (MintRemoteException e) {
			registryref.getLoggingEngine().logFailed(Level.SEVERE,
					"MintUIMServiceImpl", e,
					"Error instaniating service, remote Mint Service threw an exception");
		}
		return null;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.mintclient.service.MintUIMService#createMintOrganization
	 * (eu.europeana.uim.store.Provider)
	 */
	@Override
	public void createMintOrganization(Provider provider)
			throws MintOSGIClientException, MintRemoteException, StorageEngineException {
		CreateOrganizationCommand command = new CreateOrganizationCommand();
		command.setCountry(provider.getValue(ControlledVocabularyProxy.PROVIDERCOUNTRY));
		command.setEnglishName(provider.getName());
		command.setName(provider.getName());
		command.setType(provider.getValue(ControlledVocabularyProxy.PROVIDERTYPE));
		String userID = provider.getValue(ControlledVocabularyProxy.PROVIDERMINTUSERID);
		if(userID == null){
			throw new MintOSGIClientException("User ID value in provider cannot be null");
		}
		command.setUserId(userID);
		
		CreateOrganizationResponse resp = synchronousClient.createOrganization(command);
		provider.putValue(ControlledVocabularyProxy.MINTID, resp.getOrganizationId());
		
		registry.getStorageEngine().updateProvider(provider);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.mintclient.service.MintUIMService#createMintAuthorizedUser
	 * (eu.europeana.uim.store.Provider)
	 */
	@Override
	public void createMintAuthorizedUser(Provider provider)
			throws MintOSGIClientException, MintRemoteException {
		CreateUserCommand command = new CreateUserCommand();

		command.setEmail("email");
		command.setFirstName("firstName");
		command.setLastName("lastName");
		command.setUserName("userX");
		command.setPassword("werwer");
		command.setPhone("234234234");
		command.setOrganization(provider.getValue(ControlledVocabularyProxy.MINTID));
		synchronousClient.createUser(command);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.mintclient.service.MintUIMService#createMappingSession
	 * (eu.europeana.uim.store.Collection)
	 */
	@Override
	public void createMappingSession(Collection collection)
			throws MintOSGIClientException, MintRemoteException, StorageEngineException {
		CreateImportCommand command = new CreateImportCommand();

		Provider provider = collection.getProvider();
		command.setUserId(provider.getValue(ControlledVocabularyProxy.PROVIDERMINTUSERID));
		command.setOrganizationId(provider.getValue(ControlledVocabularyProxy.PROVIDERMINTUSERPASSWORD));
		command.setRepoxTableName(collection.getValue(ControlledVocabularyProxy.REPOXID));
		CreateImportResponse resp = synchronousClient.createImports(command);
		
		collection.putValue(ControlledVocabularyProxy.LATESTMINTMAPPINGID, resp.getImportId());
		registry.getStorageEngine().updateCollection(collection);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.mintclient.service.MintUIMService#publishCollection(
	 * eu.europeana.uim.store.Collection)
	 */
	@Override
	public void publishCollection(Collection<?> collection)
			throws MintOSGIClientException, MintRemoteException {
		
		Provider provider = collection.getProvider();
		PublicationCommand command = new PublicationCommand();
		//command.setCorrelationId("correlationId");
		List<String> list = new ArrayList<String>();
		list.add("test1");
		list.add("test2");
		command.setIncludedImportList(list);
		command.setOrganizationId("orgid");
		command.setUserId("userId");
		synchronousClient.publishCollection(command);

	}
	

    /**
     *
     * @author Georgios Markakis <gwarkx@hotmail.com>
     * @since 6 Mar 2012
     */
    public static class UIMConsumerListener extends DefaultConsumer {

		private Channel channel; 
		
		public UIMConsumerListener(Channel channel) {
			super(channel);
			// TODO Auto-generated constructor stub
		}
		
	    @Override
	    public void handleDelivery(String consumerTag,
	                               Envelope envelope,
	                               AMQP.BasicProperties properties,
	                               byte[] body)
	        throws IOException
	    {
	        String routingKey = envelope.getRoutingKey();
	        String contentType = properties.getContentType();

	        long deliveryTag = envelope.getDeliveryTag();
	        
	        System.out.println(new String(body));
	        // (process the message components here ...)

	        
	        ///channel.basicAck(deliveryTag, false);
	    }

    }
}
    
