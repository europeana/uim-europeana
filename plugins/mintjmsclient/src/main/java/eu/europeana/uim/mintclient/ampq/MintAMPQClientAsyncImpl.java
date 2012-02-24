/**
 * 
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

import eu.europeana.uim.mintclient.ampq.listeners.UIMConsumerListener;
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
import eu.europeana.uim.mintclient.plugin.MintAMPQClient;
import eu.europeana.uim.mintclient.plugin.MintAMPQClientASync;
import eu.europeana.uim.mintclient.utils.MintClientUtils;

/**
 * 
 * @author geomark
 *
 */
public class MintAMPQClientAsyncImpl implements MintAMPQClientASync{

	protected static Connection rabbitConnection;
	protected static Channel sendChannel;
	protected static Channel receiveChannel;
	protected static String inbound = "MintInboundQueue";
	protected static String outbound = "MintOutboundQueue";
	protected static Builder builder;
	protected static BasicProperties pros;


	private static Consumer defaultConsumer;
	private static MintAMPQClientAsyncImpl instance;
	
	private MintAMPQClientAsyncImpl(){

	}
	
	
	protected static <T extends DefaultConsumer> MintAMPQClient getClient(Class<T> listenerClassType) {
		
		Constructor<T> con;

			try {
				con = listenerClassType.getConstructor(listenerClassType);
				defaultConsumer = (T) con.newInstance(receiveChannel);
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		
		return getClient();
	}

	protected static MintAMPQClient getClient() {
		
		if(instance != null){
			return instance;
		}
		else{
			ConnectionFactory factory = new ConnectionFactory();
			builder = new Builder();

			factory.setHost("panic.image.ntua.gr");
			factory.setUsername("guest");
			factory.setPassword("guest");
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
				e.printStackTrace();
			}
		}
		return instance;
	}
	
	@Override
	public void createOrganization(CreateOrganizationCommand command) {
		CreateOrganizationAction cu = new CreateOrganizationAction();
		cu.setCreateOrganizationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}

	@Override
	public void createUser(CreateUserCommand command) {
		CreateUserAction cu = new CreateUserAction();
		cu.setCreateUserCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}

	
	
	@Override
	public void getImports(GetImportsCommand command) {
		GetImportsAction cu = new GetImportsAction();
		cu.setGetImportsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}

	@Override
	public void createImports(CreateImportCommand command) {
		CreateImportAction cu = new CreateImportAction();
		cu.setCreateImportCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}
	
	@Override
	public void getTransformations(GetTransformationsCommand command) {
		GetTransformationsAction cu = new GetTransformationsAction();
		cu.setGetTransformationsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}

	@Override
	public void publishCollection(PublicationCommand command) {
		PublicationAction cu = new PublicationAction();
		cu.setPublicationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(command.getCorrelationId(),cmdstring.getBytes(),true,inbound);
	}


	
	
	private void sendChunk(String correlationId,byte[] payload, boolean isLast,String queue){
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
			e.printStackTrace();
		}
	}




}
