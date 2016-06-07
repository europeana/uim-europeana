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
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
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
import eu.europeana.uim.mintclient.jibxbindings.ImportExistsAction;
import eu.europeana.uim.mintclient.jibxbindings.ImportExistsCommand;
import eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsAction;
import eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationAction;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.jibxbindings.UserExistsAction;
import eu.europeana.uim.mintclient.jibxbindings.UserExistsCommand;
import eu.europeana.uim.mintclient.service.MintUIMServiceImpl;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.utils.MintClientUtils;


/**
 * A Singleton Class implementing an asynchronous client.
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public final class MintAMPQClientAsyncImpl extends MintAbstractAMPQClient implements MintAMPQClientASync{


	public static Consumer defaultConsumer;
	private static MintAMPQClientAsyncImpl instance;
	
	/**
	 * Private constructor (can only be instantiated via Factory class)
	 */
	private MintAMPQClientAsyncImpl(){
	}
	
	
	/**
	 * Protected overriden static factory method for creating a MINT asynchronous client.
	 * It takes a Class type argument to indicate the listener class that will be used
	 * to monitor the specific session, and then instantiates this class via reflection.
	 * Only public and public static nested classes can by used as listeners. 
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

	/**
	 * Protected overridden static factory method for creating a MINT asynchronous client.
	 * Since this is a singleton class , this method will prohibit from instantiating the
	 * same object twice.
	 * 
	 * @return an instance of this class
	 * @throws MintOSGIClientException
	 */
	protected static MintAMPQClientASync getClient() throws MintOSGIClientException{
		
		if(instance != null && receiveChannel!=null && receiveChannel.isOpen()){
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
				defaultConsumer =  defaultConsumer==null ? new MintUIMServiceImpl.UIMConsumerListener(receiveChannel) : defaultConsumer;
				receiveChannel.basicConsume(outbound, true, defaultConsumer);
				instance = new MintAMPQClientAsyncImpl();
				
			} catch (IOException e) {			
				throw MintClientUtils.propagateException(e, MintOSGIClientException.class,
						"Error in instantiating asynchronous client");
			}
		}
		return instance;
	}
	
	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientASync#createOrganization(eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand)
	 */
	@Override
	public void createOrganization(CreateOrganizationCommand command,String providerID) throws MintOSGIClientException, MintRemoteException {
	  if(!receiveChannel.isOpen()) {
		  try {
			  receiveChannel.basicConsume(outbound, true, defaultConsumer);
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
		String correlationID = MintClientUtils.createCorrelationId(providerID);
		CreateOrganizationAction cu = new CreateOrganizationAction();		
		cu.setCreateOrganizationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationID,cmdstring.getBytes(),true,inbound,outbound);
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientASync#createUser(eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand)
	 */
	@Override
	public void createUser(CreateUserCommand command,String providerID) throws MintOSGIClientException, MintRemoteException {
	  if(!receiveChannel.isOpen()) {
		  try {
			  receiveChannel.basicConsume(outbound, true, defaultConsumer);
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
		String correlationID = MintClientUtils.createCorrelationId(providerID);
		CreateUserAction cu = new CreateUserAction();
		cu.setCreateUserCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationID,cmdstring.getBytes(),true,inbound,outbound);
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientASync#getImports(eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand)
	 */
	@Override
	public void getImports(GetImportsCommand command,String providerID) throws MintOSGIClientException, MintRemoteException {
	  if(!receiveChannel.isOpen()) {
		  try {
			  receiveChannel.basicConsume(outbound, true, defaultConsumer);
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
		String correlationID = MintClientUtils.createCorrelationId(providerID);
		GetImportsAction cu = new GetImportsAction();
		cu.setGetImportsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationID,cmdstring.getBytes(),true,inbound,outbound);
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientASync#createImports(eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand)
	 */
	@Override
	public void createImports(CreateImportCommand command,String collectionID) throws MintOSGIClientException, MintRemoteException {
	  if(!receiveChannel.isOpen()) {
		  try {
			  receiveChannel.basicConsume(outbound, true, defaultConsumer);
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
		String correlationID = MintClientUtils.createCorrelationId(collectionID);
		CreateImportAction cu = new CreateImportAction();
		cu.setCreateImportCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationID,cmdstring.getBytes(),true,inbound,outbound);
	}
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientASync#getTransformations(eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand)
	 */
	@Override
	public void getTransformations(GetTransformationsCommand command,String providerID) throws MintOSGIClientException, MintRemoteException {
	  if(!receiveChannel.isOpen()) {
		  try {
			  receiveChannel.basicConsume(outbound, true, defaultConsumer);
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
		String correlationID = MintClientUtils.createCorrelationId(providerID);
		GetTransformationsAction cu = new GetTransformationsAction();
		cu.setGetTransformationsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationID,cmdstring.getBytes(),true,inbound,outbound);
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClientASync#publishCollection(eu.europeana.uim.mintclient.jibxbindings.PublicationCommand)
	 */
	@Override
	public void publishCollection(PublicationCommand command,String providerID) throws MintOSGIClientException, MintRemoteException {
	  if(!receiveChannel.isOpen()) {
		  try {
			  receiveChannel.basicConsume(outbound, true, defaultConsumer);
		  } catch (IOException e) {
			  e.printStackTrace();
		  }
	  }
		String correlationID = MintClientUtils.createCorrelationId(providerID);
		PublicationAction cu = new PublicationAction();
		cu.setPublicationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(correlationID,cmdstring.getBytes(),true,inbound,outbound);
	}


	@Override
	public  Consumer getConsumer() {
		return defaultConsumer;
	}

}
