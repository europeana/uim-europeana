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
import java.util.Map;
import java.util.logging.Level;
import org.jibx.runtime.IMarshallable;
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
import eu.europeana.uim.mintclient.jibxbindings.ErrorResponse;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.utils.AMPQOperations;
import eu.europeana.uim.mintclient.utils.MintClientUtils;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.api.LoggingEngine;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.sugarcrm.SugarCrmService;

/**
 * Base Class for implementing the UIM Mint connectivity
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class MintUIMServiceImpl implements MintUIMService {

	private static MintAMPQClientSync synchronousClient;
	private static MintAMPQClientASync asynchronousClient;
	private static Registry registry;
	private static Orchestrator<?> orchestrator;
	private static SugarCrmService sugservice;
	private static LoggingEngine<?> logger;

	private final static String HEADERERRORMESSAGE = "hasError";
	private final static String HEADERCORRELATIONID = "correlationID";
	
	
	/**
	 * Private constructor, instantiated via private factory method
	 */
	public MintUIMServiceImpl(Registry registry, Orchestrator<?> orchestrator,
			SugarCrmService sugservice) {
		MintUIMServiceImpl.registry = registry;
		MintUIMServiceImpl.orchestrator = orchestrator;
		MintUIMServiceImpl.sugservice = sugservice;
		MintUIMServiceImpl.logger = (LoggingEngine<?>) (registry != null ? registry
				.getLoggingEngine() : null);
	}



	/**
	 * Factory method currently used for initialising the service
	 * instance (currently used by Spring).
	 * 
	 * @param registryref
	 * @param orchestratorref
	 * @param service
	 * @return
	 */
	public static MintUIMServiceImpl createService(Registry registryref,
			Orchestrator<?> orchestratorref, SugarCrmService service) {

		MintClientFactory factory = new MintClientFactory();
		try {
			synchronousClient = (MintAMPQClientSync) factory.syncMode()
					.createClient();
			asynchronousClient = (MintAMPQClientASync) factory.asyncMode(
					MintUIMServiceImpl.UIMConsumerListener.class)
					.createClient();
			return new MintUIMServiceImpl(registryref, orchestratorref, service);
		} catch (MintOSGIClientException e) {
			registryref.getLoggingEngine().logFailed(Level.SEVERE,
					"MintUIMServiceImpl", e,
					"Error instaniating service, client threw an exception");
		} catch (MintRemoteException e) {
			registryref
					.getLoggingEngine()
					.logFailed(Level.SEVERE, "MintUIMServiceImpl", e,
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
			throws MintOSGIClientException, MintRemoteException,
			StorageEngineException {
		CreateOrganizationCommand command = new CreateOrganizationCommand();
		command.setCountry(provider
				.getValue(ControlledVocabularyProxy.PROVIDERCOUNTRY));
		command.setEnglishName(provider.getName());
		command.setName(provider.getName());
		command.setType(provider
				.getValue(ControlledVocabularyProxy.PROVIDERTYPE));
		String userID = provider
				.getValue(ControlledVocabularyProxy.PROVIDERMINTUSERID);
		if (userID == null) {
			throw new MintOSGIClientException(
					"User ID value in provider cannot be null");
		}
		command.setUserId(userID);

		CreateOrganizationResponse resp = synchronousClient
				.createOrganization(command);
		provider.putValue(ControlledVocabularyProxy.MINTID,
				resp.getOrganizationId());

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

		command.setEmail(provider
				.getValue(ControlledVocabularyProxy.PROVIDERTYPE));
		command.setFirstName(provider
				.getValue(ControlledVocabularyProxy.PROVIDERMINTUSERFIRSTNAME));
		command.setLastName(provider
				.getValue(ControlledVocabularyProxy.PROVIDERMINTUSERLASTNAME));
		command.setUserName(provider
				.getValue(ControlledVocabularyProxy.PROVIDERMINTUSERID));
		command.setPassword(provider
				.getValue(ControlledVocabularyProxy.PROVIDERMINTUSERPASSWORD));
		command.setPhone(provider
				.getValue(ControlledVocabularyProxy.PROVIDERMINTPHONE));
		command.setOrganization(provider
				.getValue(ControlledVocabularyProxy.MINTID));
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
			throws MintOSGIClientException, MintRemoteException,
			StorageEngineException {
		CreateImportCommand command = new CreateImportCommand();

		Provider provider = collection.getProvider();
		command.setUserId(provider
				.getValue(ControlledVocabularyProxy.PROVIDERMINTUSERID));
		command.setOrganizationId(provider
				.getValue(ControlledVocabularyProxy.PROVIDERMINTUSERPASSWORD));
		command.setRepoxTableName(collection
				.getValue(ControlledVocabularyProxy.REPOXID));
		CreateImportResponse resp = synchronousClient.createImports(command);

		collection.putValue(ControlledVocabularyProxy.LATESTMINTMAPPINGID,
				resp.getImportId());
		registry.getStorageEngine().updateCollection(collection);

	}

	/**
	 * Public static nested class implementing a Listener for incoming messages
	 * 
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 6 Mar 2012
	 */
	public static class UIMConsumerListener extends DefaultConsumer {

		public UIMConsumerListener(Channel channel) {
			super(channel);
		}

		@Override
		public void handleDelivery(String consumerTag, Envelope envelope,
				AMQP.BasicProperties properties, byte[] body)
				throws IOException {
			String routingKey = envelope.getRoutingKey();
			String contentType = properties.getContentType();

			Map<String, Object> maprops = properties.getHeaders();

			String correlationid = (String) maprops.get(HEADERCORRELATIONID);
			boolean hasError = Boolean.parseBoolean((String) maprops
					.get(HEADERERRORMESSAGE));

			if (hasError) {
				try {
					ErrorResponse err = MintClientUtils.marshallobject(
							new String(body), ErrorResponse.class);

					// Log Error
					StringBuilder sb = new  StringBuilder();
					sb.append("Operation Name:");
					sb.append(err.getCommand());
					sb.append("Error Description:");
					sb.append(err.getErrorMessage());
					sb.append("Correlation ID:");
					sb.append(correlationid);
					
					logger.logFailed(Level.SEVERE, "RemoteServer has throw an exception:",
							new MintRemoteException(sb.toString()));

				} catch (MintOSGIClientException e) {
					logger.logFailed(Level.SEVERE, "Incoming message caused an exception to the client",e);
				}
			}

			try {
				IMarshallable response = MintClientUtils
						.unmarshallobject(new String(body));

				AMPQOperations responseType = MintClientUtils
						.translateAMPQOperation(response.JiBX_getName());

				switch (responseType) {
				case CreateOrganizationAction:

					break;
				case CreateUserAction:

					break;
				case CreateImportAction:

					break;

				case GetImportsAction:

					break;
				case GetTransformationsAction:

					break;
				case PublicationAction:

					break;
				case ImportExistsAction:

					break;
				case UserExistsAction:

					break;
				case OrganizationExistsAction:

					break;

				default:
					throw new UnsupportedOperationException(
							"Received Message from Mint is not supported.");
				}

			} catch (MintOSGIClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// (process the message components here ...)

			// /channel.basicAck(deliveryTag, false);
		}

	}
}
