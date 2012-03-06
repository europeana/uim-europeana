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
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.Consumer;

import eu.europeana.uim.mintclient.jibxbindings.CreateImportAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationAction;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.service.listeners.UIMConsumerListener;
import eu.europeana.uim.mintclient.utils.MintClientUtils;


/**
 * A Class implementing an asynchronous client.
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class MintAMPQClientAsyncImpl extends MintAbstractAMPQClient implements MintAMPQClientASync{

	protected static Connection rabbitConnection;
	protected static Channel sendChannel;
	protected static Channel receiveChannel;
	protected static String inbound = "MintInboundQueue";
	protected static String outbound = "MintOutboundQueue";
	protected static Builder builder;
	protected static BasicProperties pros;
	private static Consumer defaultConsumer;
	private static MintAMPQClientAsyncImpl instance;
	
	/**
	 * Private constructor (can only be instantiated via Factory class)
	 */
	private MintAMPQClientAsyncImpl(){
	}
	
	
	/**
	 * Protected static factory method for creating a MINT asynchronous client.
	 * It takes a 
	 * 
	 * @param listenerClassType the listener class type to be instantiated via reflection
	 * @return an instance of this class
	 * @throws MintOSGIClientException
	 */
	protected static <T extends DefaultConsumer> MintAMPQClientASync getClient(Class<T> listenerClassType) throws MintOSGIClientException{
		
		Constructor<T> con;

			try {
				con = listenerClassType.getConstructor(Channel.class);
				defaultConsumer = (T) con.newInstance(receiveChannel);
			} catch (SecurityException e) {
				throw MintClientUtils.propagateException(e, MintOSGIClientException.class,
						"Error in instantiating session listener");

			} catch (NoSuchMethodException e) {
				throw MintClientUtils.propagateException(e, MintOSGIClientException.class,
						"Error in instantiating session listener");
			} catch (IllegalArgumentException e) {
				throw MintClientUtils.propagateException(e, MintOSGIClientException.class,
						"Error in instantiating session listener");
			} catch (InstantiationException e) {
				throw MintClientUtils.propagateException(e, MintOSGIClientException.class,
						"Error in instantiating session listener");
			} catch (IllegalAccessException e) {
				throw MintClientUtils.propagateException(e, MintOSGIClientException.class,
						"Error in instantiating session listener");
			} catch (InvocationTargetException e) {
				throw MintClientUtils.propagateException(e, MintOSGIClientException.class,
						"Error in instantiating session listener");
			}

		
		return getClient();
	}

	protected static MintAMPQClientASync getClient() throws MintOSGIClientException{
		
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
				sendChannel.queueDeclare(inbound, true, false, false, null);
				receiveChannel.queueDeclare(outbound, true, false, false, null);
				
				defaultConsumer =  defaultConsumer==null ? new UIMConsumerListener(receiveChannel) : defaultConsumer;

				
				receiveChannel.basicConsume(outbound, true, defaultConsumer);
				
				instance = new MintAMPQClientAsyncImpl();
				
			} catch (IOException e) {			
				throw MintClientUtils.propagateException(e, MintOSGIClientException.class,
						"Error in instantiating asynchronous client");
			}
		}
		return instance;
	}
	
	@Override
	public void createOrganization(CreateOrganizationCommand command) throws MintOSGIClientException, MintRemoteException {
		CreateOrganizationAction cu = new CreateOrganizationAction();
		cu.setCreateOrganizationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}

	@Override
	public void createUser(CreateUserCommand command) throws MintOSGIClientException, MintRemoteException {
		CreateUserAction cu = new CreateUserAction();
		cu.setCreateUserCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}

	
	
	@Override
	public void getImports(GetImportsCommand command) throws MintOSGIClientException, MintRemoteException {
		GetImportsAction cu = new GetImportsAction();
		cu.setGetImportsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}

	@Override
	public void createImports(CreateImportCommand command) throws MintOSGIClientException, MintRemoteException {
		CreateImportAction cu = new CreateImportAction();
		cu.setCreateImportCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}
	
	@Override
	public void getTransformations(GetTransformationsCommand command) throws MintOSGIClientException, MintRemoteException {
		GetTransformationsAction cu = new GetTransformationsAction();
		cu.setGetTransformationsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}

	@Override
	public void publishCollection(PublicationCommand command) throws MintOSGIClientException, MintRemoteException {
		PublicationAction cu = new PublicationAction();
		cu.setPublicationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}


	
	
	private void sendChunk(String correlationId,byte[] payload, boolean isLast,String queue) throws MintRemoteException, MintOSGIClientException{
		builder.deliveryMode(2);
		HashMap<String, Object> heads = new HashMap<String, Object>();
		heads.put("isLast", isLast);
		builder.headers(heads);
		BasicProperties properties =  new BasicProperties
         .Builder()
         .correlationId(correlationId)
         .replyTo(outbound)
         //.headers(message.header().properties())
         .build();
		
		try {
			sendChannel.basicPublish( "", queue, 
					properties,
			        payload);
		} catch (IOException e) {
			throw MintClientUtils.propagateException(e, MintRemoteException.class,
					"Error in in sending asynchronous chunk");
		}
	}


	


}
