/**
 * 
 */
package eu.europeana.uim.mintclient.ampq;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;

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
import com.rabbitmq.client.MessageProperties;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;


/**
 * 
 * @author Georgios Markakis
 */
public final class MintAMPQClientImpl implements MintAMPQClient {

	private static Connection rabbitConnection;
	private static Channel sendChannel;
	private static Channel receiveChannel;
	private static String inbound = "MintInboundQueue";
	private static String outbound = "MintOutboundQueue";
	private Builder builder;
	private BasicProperties pros;

	
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
			sendChannel.queueDeclare(inbound, true, false, false, null);
			receiveChannel.queueDeclare(outbound, true, false, false, null);
			receiveChannel.basicConsume(outbound, true, new UIMConsumerFactory(receiveChannel));
		} catch (IOException e) {			
			e.printStackTrace();
		}
	}
	
	
	
	@Override
	public void createOrganization(
		CreateOrganizationCommand command) {
		CreateOrganizationAction cu = new CreateOrganizationAction();
		cu.setCreateOrganizationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);
	}

	@Override
	public void createUser(CreateUserCommand command) {
		CreateUserAction cu = new CreateUserAction();
		cu.setCreateUserCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);

	}

	
	@Override
	public void getImports(GetImportsCommand command) {
		GetImportsAction cu = new GetImportsAction();
		cu.setGetImportsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);
	}

	@Override
	public void createImports(CreateImportCommand command) {
		CreateImportAction cu = new CreateImportAction();
		cu.setCreateImportCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);
	}
	
	@Override
	public void getTransformations(GetTransformationsCommand command) {
		GetTransformationsAction cu = new GetTransformationsAction();
		cu.setGetTransformationsCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);
	}

	@Override
	public void publishCollection(PublicationCommand command) {
		PublicationAction cu = new PublicationAction();
		cu.setPublicationCommand(command);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);
	}



	
	private void sendChunk(byte[] payload, boolean isLast){
		builder.deliveryMode(2);
		HashMap<String, Object> heads = new HashMap<String, Object>();
		heads.put("isLast", isLast);
		builder.headers(heads);
		pros = builder.build();
		
		try {
			sendChannel.basicPublish( "", inbound, 
			        pros,
			        payload);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
