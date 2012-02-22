/**
 * 
 */
package eu.europeana.uim.mintclient.ampq;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import eu.europeana.uim.mintclient.ampq.listeners.UIMConsumerFactory;
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
import eu.europeana.uim.mintclient.plugin.MintAMPQClient;
import eu.europeana.uim.mintclient.utils.MintClientUtils;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConsumerCancelledException;
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;
import com.rabbitmq.client.QueueingConsumer;
import com.rabbitmq.client.ShutdownSignalException;

/**
 * 
 * @author Georgios Markakis
 */
public class MintAMPQClientImpl implements MintAMPQClient {

	protected static Connection rabbitConnection;
	protected static Channel sendChannel;
	protected static Channel receiveChannel;
	protected static String inbound = "MintInboundQueue";
	protected static String outbound = "MintOutboundQueue";
	protected static String rpcQueue = "RPCQueue";
	protected static String rndReplyqueue;
	
	protected Builder builder;
	protected BasicProperties pros;
	private QueueingConsumer consumer;

	
	public MintAMPQClientImpl(){
		ConnectionFactory factory = new ConnectionFactory();
		builder = new Builder();

		factory.setHost("panic.image.ntua.gr");
		factory.setUsername("guest");
		factory.setPassword("guest");
		try {
			rabbitConnection = factory.newConnection();
			sendChannel = rabbitConnection.createChannel();
			receiveChannel = rabbitConnection.createChannel();
			rndReplyqueue = receiveChannel.queueDeclare().getQueue();
			//sendChannel.queueDeclare(inbound, true, false, false, null);
			sendChannel.queueDeclare(rpcQueue, true, false, false, null);
			receiveChannel.queueDeclare(outbound, true, false, false, null);
			consumer = new QueueingConsumer(receiveChannel);
			receiveChannel.basicConsume(rndReplyqueue, true, consumer);
			//receiveChannel.basicConsume(outbound, true, new UIMConsumerFactory(receiveChannel));
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public CreateOrganizationResponse createOrganization(CreateOrganizationCommand command) {
		CreateOrganizationAction cu = new CreateOrganizationAction();
		cu.setCreateOrganizationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		
		CreateOrganizationResponse respObj = new CreateOrganizationResponse();
		MintClientUtils.marshallobject(resp, respObj);
		
		return respObj;
	}

	@Override
	public CreateUserResponse createUser(CreateUserCommand command) {
		CreateUserAction cu = new CreateUserAction();
		cu.setCreateUserCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		CreateUserResponse respObj = new CreateUserResponse();
		MintClientUtils.marshallobject(resp, respObj);
		
		return respObj;
	}

	
	
	@Override
	public GetImportsResponse getImports(GetImportsCommand command) {
		GetImportsAction cu = new GetImportsAction();
		cu.setGetImportsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		GetImportsResponse respObj = new GetImportsResponse();
		MintClientUtils.marshallobject(resp, respObj);
		
		return respObj;
	}

	@Override
	public CreateImportResponse createImports(CreateImportCommand command) {
		CreateImportAction cu = new CreateImportAction();
		cu.setCreateImportCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		CreateImportResponse respObj = new CreateImportResponse();
		MintClientUtils.marshallobject(resp, respObj);
		
		return respObj;
	}
	
	@Override
	public GetTransformationsResponse getTransformations(GetTransformationsCommand command) {
		GetTransformationsAction cu = new GetTransformationsAction();
		cu.setGetTransformationsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		GetTransformationsResponse respObj = new GetTransformationsResponse();
		MintClientUtils.marshallobject(resp, respObj);
		
		return respObj;
	}

	@Override
	public PublicationResponse publishCollection(PublicationCommand command) {
		PublicationAction cu = new PublicationAction();
		cu.setPublicationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,rpcQueue);
		
		String resp = handleSynchronousDelivery(command.getCorrelationId());
		PublicationResponse respObj = new PublicationResponse();
		MintClientUtils.marshallobject(resp, respObj);
		
		return respObj;
	}


	protected Connection getConnection(){
		return this.rabbitConnection;
	}

	
	
	
	private String handleSynchronousDelivery(String correlationID){
	    while (true) {
	    	QueueingConsumer.Delivery delivery;
			try {
				delivery = consumer.nextDelivery();
		        if (delivery.getProperties().getCorrelationId().equals(correlationID)) {
		            String response = new String(delivery.getBody());
		            
		            return response;
		        }
			} catch (ShutdownSignalException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ConsumerCancelledException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}           
	    }
	}
	
	
	private void sendChunk(String correlationId,byte[] payload, boolean isLast,String queue){
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
			e.printStackTrace();
		}
	}
}
