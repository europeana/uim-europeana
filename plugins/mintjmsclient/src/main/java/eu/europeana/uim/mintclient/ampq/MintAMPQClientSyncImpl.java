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

import java.io.IOException;
import java.util.Date;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportAction;
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
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;


/**
 * A Singleton Class implementing a asynchronous client.
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public final class MintAMPQClientSyncImpl extends MintAbstractAMPQClient implements MintAMPQClientSync {

	private static QueueingConsumer consumer;
	private static MintAMPQClientSyncImpl instance;
	

	/**
	 * Private constructor (can only be instantiated via Factory class)
	 */
	private MintAMPQClientSyncImpl(){
	}
	
	
	/**
	 * Protected overridden static factory method for creating a MINT asynchronous client.
	 * Since this is a singleton class , this method will prohibit from instantiating the
	 * same object twice.
	 * 
	 * @return an instance of this class
	 * @throws MintOSGIClientException
	 */
	protected static MintAMPQClientSync getClient() throws MintOSGIClientException {
		
		if(instance != null){
			return instance;
		}
		else{
			ConnectionFactory factory = new ConnectionFactory();
			builder = new Builder();

			factory.setHost(getHost());
			factory.setUsername(getUsername());
			factory.setPassword(getPassword());
			try {
				rabbitConnection = factory.newConnection();
				sendChannel = rabbitConnection.createChannel();
				receiveChannel = rabbitConnection.createChannel();
				rndReplyqueue = receiveChannel.queueDeclare().getQueue();
				sendChannel.queueDeclare(rpcQueue, true, false, false, null);
				consumer = new QueueingConsumer(receiveChannel);
				receiveChannel.basicConsume(rndReplyqueue, true, consumer);
				instance = new MintAMPQClientSyncImpl();
				
			} catch (IOException e) {			
				throw MintClientUtils.propagateException(e, MintOSGIClientException.class,
						"Error in instantiating synchronous client");
			}
		}
		return instance;
	}
	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientSync#createOrganization(eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand)
	 */
	@Override
	public CreateOrganizationResponse createOrganization(CreateOrganizationCommand command) throws MintOSGIClientException, MintRemoteException {
		String correlationId = new Date().toString();	
		command.setCorrelationId(correlationId);
		CreateOrganizationAction cu = new CreateOrganizationAction();
		cu.setCreateOrganizationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationId,cmdstring.getBytes(),true,rpcQueue,rndReplyqueue);
		String resp = handleSynchronousDelivery(correlationId);
		CreateOrganizationAction respObj = MintClientUtils.marshallobject(resp, CreateOrganizationAction.class);
		return respObj.getCreateOrganizationResponse();
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientSync#createUser(eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand)
	 */
	@Override
	public CreateUserResponse createUser(CreateUserCommand command) throws MintOSGIClientException, MintRemoteException {
		String correlationId = new Date().toString();	
		command.setCorrelationId(correlationId);
		CreateUserAction cu = new CreateUserAction();
		cu.setCreateUserCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationId,cmdstring.getBytes(),true,rpcQueue,rndReplyqueue);
		String resp = handleSynchronousDelivery(correlationId);
		CreateUserAction respObj = MintClientUtils.marshallobject(resp, CreateUserAction.class);
		return respObj.getCreateUserResponse();
	}

	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientSync#getImports(eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand)
	 */
	@Override
	public GetImportsResponse getImports(GetImportsCommand command) throws MintOSGIClientException, MintRemoteException {
		String correlationId = new Date().toString();	
		command.setCorrelationId(correlationId);
		GetImportsAction cu = new GetImportsAction();
		cu.setGetImportsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationId,cmdstring.getBytes(),true,rpcQueue,rndReplyqueue);
		String resp = handleSynchronousDelivery(correlationId);
		GetImportsAction respObj = MintClientUtils.marshallobject(resp, GetImportsAction.class);
		return respObj.getGetImportsResponse();
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientSync#createImports(eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand)
	 */
	@Override
	public CreateImportResponse createImports(CreateImportCommand command) throws MintOSGIClientException, MintRemoteException {
		String correlationId = new Date().toString();	
		command.setCorrelationId(correlationId);
		CreateImportAction cu = new CreateImportAction();
		cu.setCreateImportCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationId,cmdstring.getBytes(),true,rpcQueue,rndReplyqueue);
		String resp = handleSynchronousDelivery(correlationId);
		CreateImportAction respObj = MintClientUtils.marshallobject(resp, CreateImportAction.class);
		return respObj.getCreateImportResponse();
	}
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientSync#getTransformations(eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand)
	 */
	@Override
	public GetTransformationsResponse getTransformations(GetTransformationsCommand command) throws MintOSGIClientException, MintRemoteException {
		String correlationId = new Date().toString();	
		command.setCorrelationId(correlationId);
		GetTransformationsAction cu = new GetTransformationsAction();
		cu.setGetTransformationsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationId,cmdstring.getBytes(),true,rpcQueue,rndReplyqueue);		
		String resp = handleSynchronousDelivery(correlationId);
		GetTransformationsAction respObj = MintClientUtils.marshallobject(resp, GetTransformationsAction.class);
		return respObj.getGetTransformationsResponse();
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientSync#publishCollection(eu.europeana.uim.mintclient.jibxbindings.PublicationCommand)
	 */
	@Override
	public PublicationResponse publishCollection(PublicationCommand command) throws MintOSGIClientException, MintRemoteException {
		String correlationId = new Date().toString();	
		command.setCorrelationId(correlationId);
		PublicationAction cu = new PublicationAction();
		cu.setPublicationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationId,cmdstring.getBytes(),true,rpcQueue,rndReplyqueue);
		String resp = handleSynchronousDelivery(correlationId);
		PublicationAction respObj = MintClientUtils.marshallobject(resp, PublicationAction.class);
		return respObj.getPublicationResponse();
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientSync#organizationExists(eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsCommand)
	 */
	@Override
	public OrganizationExistsResponse organizationExists(
			OrganizationExistsCommand command) throws MintOSGIClientException,
			MintRemoteException {
		String corrId = new Date().toString();
		OrganizationExistsAction action  = new OrganizationExistsAction();
		action.setOrganizationExistsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(action);
		sendChunk(corrId,cmdstring.getBytes(),true,rpcQueue,rndReplyqueue);
		String resp = handleSynchronousDelivery(corrId);
		OrganizationExistsAction respObj =  MintClientUtils.marshallobject(resp, OrganizationExistsAction.class);
		return respObj.getOrganizationExistsResponse();
	}


	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientSync#userExists(eu.europeana.uim.mintclient.jibxbindings.UserExistsCommand)
	 */
	@Override
	public UserExistsResponse userExists(UserExistsCommand command)
			throws MintOSGIClientException, MintRemoteException {
		String corrId = new Date().toString();	
		UserExistsAction action = new UserExistsAction();
		action.setUserExistsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(action);
		sendChunk(null,cmdstring.getBytes(),true,rpcQueue,rndReplyqueue);
		String resp = handleSynchronousDelivery(corrId);
		UserExistsAction respObj =  MintClientUtils.marshallobject(resp, UserExistsAction.class);
		return respObj.getUserExistsResponse();
	}


	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientSync#importExists(eu.europeana.uim.mintclient.jibxbindings.ImportExistsCommand)
	 */
	@Override
	public ImportExistsResponse importExists(ImportExistsCommand command)
			throws MintOSGIClientException, MintRemoteException {
		String corrId = new Date().toString();	
		ImportExistsAction action = new ImportExistsAction();
		action.setImportExistsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(action);
		sendChunk(null,cmdstring.getBytes(),true,rpcQueue,rndReplyqueue);
		String resp = handleSynchronousDelivery(corrId);
		ImportExistsAction respObj =  MintClientUtils.marshallobject(resp, ImportExistsAction.class);
		return respObj.getImportExistsResponse();
	}

	/**
	 * Handles a synchronous delivery by the client
	 * @param correlationID
	 * @return the XML string
	 * 
	 * @throws MintRemoteException
	 * @throws MintOSGIClientException
	 */
	private String handleSynchronousDelivery(String correlationID) throws MintRemoteException, MintOSGIClientException{
	    while (true) {
	    	QueueingConsumer.Delivery delivery;
			try {
				delivery = consumer.nextDelivery(10000);
				if(delivery == null ){
					throw new MintRemoteException("Response from remote client timed out");
				}
				else if (delivery.getProperties().getCorrelationId().equals(correlationID)) {
					
		            String response = new String(delivery.getBody());
		            receiveChannel.basicAck(delivery.getEnvelope().getDeliveryTag(), false);
		            return response;
		        }
			} catch (ShutdownSignalException e) {
				throw MintClientUtils.propagateException(e, MintRemoteException.class, "Error in handling synchronous delivery in " + this.getClass());
			} catch (ConsumerCancelledException e) {
				throw MintClientUtils.propagateException(e, MintRemoteException.class, "Error in handling synchronous delivery in " + this.getClass());
			} catch (InterruptedException e) {
				throw MintClientUtils.propagateException(e, MintRemoteException.class, "Error in handling synchronous delivery in " + this.getClass());
			} catch (IOException e) {
				throw MintClientUtils.propagateException(e, MintRemoteException.class, "Error in handling synchronous delivery in " + this.getClass());
			}           
	    }
	}



	
}
