/**
 * 
 */
package eu.europeana.uim.mintclient.ampq;

import java.io.IOException;
import java.util.HashMap;
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
import eu.europeana.uim.mintclient.jibxbindings.PublicationAction;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationResponse;
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
 * 
 * @author Georgios Markakis
 */
public class MintAMPQClientSyncImpl extends MintAbstractAMPQClient implements MintAMPQClientSync {

	protected static Connection rabbitConnection;
	protected static Channel sendChannel;
	protected static Channel receiveChannel;
	protected static String rpcQueue = "RPCQueue";
	protected static String rndReplyqueue;
	
	protected static Builder builder;
	protected static BasicProperties pros;
	private static QueueingConsumer consumer;

	private static MintAMPQClientSyncImpl instance;
	
	/**
	 * 
	 */
	private MintAMPQClientSyncImpl(){
	}
	
	

	/**
	 * @return
	 */
	protected static MintAMPQClientSync getClient() {
		
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
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	@Override
	public CreateOrganizationResponse createOrganization(CreateOrganizationCommand command) throws MintOSGIClientException, MintRemoteException {
		CreateOrganizationAction cu = new CreateOrganizationAction();
		cu.setCreateOrganizationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		CreateOrganizationAction respObj = MintClientUtils.marshallobject(resp, CreateOrganizationAction.class);
		return respObj.getCreateOrganizationResponse();
	}

	@Override
	public CreateUserResponse createUser(CreateUserCommand command) throws MintOSGIClientException, MintRemoteException {
		CreateUserAction cu = new CreateUserAction();
		cu.setCreateUserCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		CreateUserAction respObj = MintClientUtils.marshallobject(resp, CreateUserAction.class);
		return respObj.getCreateUserResponse();
	}

	
	
	@Override
	public GetImportsResponse getImports(GetImportsCommand command) throws MintOSGIClientException, MintRemoteException {
		GetImportsAction cu = new GetImportsAction();
		cu.setGetImportsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		GetImportsAction respObj = MintClientUtils.marshallobject(resp, GetImportsAction.class);
		return respObj.getGetImportsResponse();
	}

	@Override
	public CreateImportResponse createImports(CreateImportCommand command) throws MintOSGIClientException, MintRemoteException {
		CreateImportAction cu = new CreateImportAction();
		cu.setCreateImportCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		CreateImportAction respObj = MintClientUtils.marshallobject(resp, CreateImportAction.class);
		return respObj.getCreateImportResponse();
	}
	
	@Override
	public GetTransformationsResponse getTransformations(GetTransformationsCommand command) throws MintOSGIClientException, MintRemoteException {
		GetTransformationsAction cu = new GetTransformationsAction();
		cu.setGetTransformationsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);		
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		GetTransformationsAction respObj = MintClientUtils.marshallobject(resp, GetTransformationsAction.class);
		return respObj.getGetTransformationsResponse();
	}

	@Override
	public PublicationResponse publishCollection(PublicationCommand command) throws MintOSGIClientException, MintRemoteException {
		PublicationAction cu = new PublicationAction();
		cu.setPublicationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		PublicationAction respObj = MintClientUtils.marshallobject(resp, PublicationAction.class);
		return respObj.getPublicationResponse();
	}



	
	private String handleSynchronousDelivery(String correlationID) throws MintRemoteException, MintOSGIClientException{
	    while (true) {
	    	QueueingConsumer.Delivery delivery;
			try {
				delivery = consumer.nextDelivery();
		        if (delivery.getProperties().getCorrelationId().equals(correlationID)) {
		            String response = new String(delivery.getBody());
		            
		            return response;
		        }
			} catch (ShutdownSignalException e) {
				throw MintClientUtils.propagateException(e, MintRemoteException.class, "Error in handling asynchronous delivery in " + this.getClass());
			} catch (ConsumerCancelledException e) {
				throw MintClientUtils.propagateException(e, MintRemoteException.class, "Error in handling asynchronous delivery in " + this.getClass());
			} catch (InterruptedException e) {
				throw MintClientUtils.propagateException(e, MintRemoteException.class, "Error in handling asynchronous delivery in " + this.getClass());
			}           
	    }
	}
	
	
	private void sendChunk(String correlationId,byte[] payload, boolean isLast,String queue) throws MintRemoteException, MintOSGIClientException{
		builder.deliveryMode(2);
		HashMap<String, Object> heads = new HashMap<String, Object>();
		heads.put("isLast", isLast);
		builder.headers(heads);
		BasicProperties properties =  new BasicProperties
         .Builder()
         .correlationId(correlationId)
         .replyTo(rndReplyqueue)
         //.headers(message.header().properties())
         .build();
		
		try {
			sendChannel.basicPublish( "", queue, 
					properties,
			        payload);
		} catch (IOException e) {
			throw MintClientUtils.propagateException(e, MintRemoteException.class, "Error sending chunk in " + this.getClass());
		}
	}





}
