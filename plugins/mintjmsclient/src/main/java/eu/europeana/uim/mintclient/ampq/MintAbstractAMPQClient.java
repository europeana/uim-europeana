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
import java.util.HashMap;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.utils.MintClientUtils;



/**
 * Abstract class for an AMPQ client
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public abstract class MintAbstractAMPQClient implements MintAMPQClient{

	protected static  String username;
	protected static String password;
	protected static String host;
	
	protected final static String ADMINUSERID = "1000";
	
	protected static Connection rabbitConnection;
	protected static Channel sendChannel;
	protected static Channel receiveChannel;
	protected static String inbound;
	protected static String outbound;
	
	protected static String rpcQueue;
	protected static String rndReplyqueue;	
	
	
	protected static Builder builder;
	protected static BasicProperties pros;
	
	
	/**
	 * Default Constructor
	 */
	protected MintAbstractAMPQClient(){
	}

	/**
	 * @return the username for the AMPQ broker
	 */
	public static String getUsername() {
		return username;
	}


	/**
	 * @return the password for the AMPQ broker
	 */
	public static String getPassword() {
		return password;
	}


	/**
	 * @return the hostname where the AMPQ broker resides
	 */
	public static String getHost() {
		return host;
	}
	
	/**
	 * Sends a message to the specific queue
	 * 
	 * @param correlationId
	 * @param payload
	 * @param isLast
	 * @param queue
	 * @throws MintRemoteException
	 * @throws MintOSGIClientException
	 */
	protected void sendChunk(String correlationId,byte[] payload, boolean isLast,String sendqueue,String receivequeue) throws MintRemoteException, MintOSGIClientException{
		builder.deliveryMode(2);
		HashMap<String, Object> heads = new HashMap<String, Object>();
		heads.put("isLast", isLast);
		builder.headers(heads);
		BasicProperties properties =  new BasicProperties
         .Builder()
         .correlationId(correlationId)
         .replyTo(receivequeue)
         //.headers(message.header().properties())
         .build();
		
		try {
			sendChannel.basicPublish( "", sendqueue, 
					properties,
			        payload);
		} catch (IOException e) {
			throw MintClientUtils.propagateException(e, MintRemoteException.class,
					"Error in in sending asynchronous chunk");
		}
	}
	
	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.mintclient.ampq.MintAMPQClient#closeConnection()
	 */
	@Override
	public void closeConnection() throws IOException {
		rabbitConnection.close();	
	}

}
