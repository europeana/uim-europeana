/**
 * 
 */
package eu.europeana.uim.mintclient.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eu.europeana.uim.mintclient.ampq.MintAMPQClientASync;
import eu.europeana.uim.mintclient.ampq.MintAMPQClientSync;
import eu.europeana.uim.mintclient.ampq.MintClientFactory;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.service.listeners.UIMConsumerListener;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.Orchestrator;

/**
 * 
 * @author Georgios Markakis
 */
public class MintUIMServiceImpl implements MintUIMService {

	private static MintAMPQClientSync synchronousClient;
	private static MintAMPQClientASync asynchronousClient;
	private static Registry registry;
	private static Orchestrator<?> orchestrator;
	
	
	public MintUIMServiceImpl(){
		 
	}
	
	
	public static void createService(Registry registryref, Orchestrator<?> orchestratorref ){

		registry = registryref;
		orchestrator = orchestratorref;
		
		
		MintClientFactory factory = new MintClientFactory();
		try {
			synchronousClient = (MintAMPQClientSync) factory.syncMode().createClient();
			
			asynchronousClient = (MintAMPQClientASync) factory.asyncMode(MintUIMServiceImpl.UIMConsumerListener.class).createClient();
		} catch (MintOSGIClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MintRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.mintclient.service.MintUIMService#createMintOrganization
	 * (eu.europeana.uim.store.Provider)
	 */
	@Override
	public void createMintOrganization(Provider<?> provider)
			throws MintOSGIClientException, MintRemoteException {
		CreateOrganizationCommand command = new CreateOrganizationCommand();
		command.setCorrelationId("correlationId");
		command.setCountry("es");
		command.setEnglishName("TestOrg");
		command.setName("TestOrg");
		command.setType("Type");
		command.setUserId("1002");
		synchronousClient.createOrganization(command);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.mintclient.service.MintUIMService#createMintAuthorizedUser
	 * (eu.europeana.uim.store.Provider)
	 */
	@Override
	public void createMintAuthorizedUser(Provider<?> provider)
			throws MintOSGIClientException, MintRemoteException {
		CreateUserCommand command = new CreateUserCommand();
		command.setCorrelationId("correlationId");
		command.setEmail("email");
		command.setFirstName("firstName");
		command.setLastName("lastName");
		command.setUserName("userX");
		command.setPassword("werwer");
		command.setPhone("234234234");
		command.setOrganization("1001");
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
	public void createMappingSession(Collection<?> collection)
			throws MintOSGIClientException, MintRemoteException {
		CreateImportCommand command = new CreateImportCommand();

		command.setCorrelationId("123");
		command.setUserId("1000");
		command.setOrganizationId("1");
		command.setRepoxTableName("azores13");
		synchronousClient.createImports(command);

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
		PublicationCommand command = new PublicationCommand();
		command.setCorrelationId("correlationId");
		List<String> list = new ArrayList();
		list.add("test1");
		list.add("test2");
		command.setIncludedImportList(list);
		command.setOrganizationId("orgid");
		command.setUserId("userId");
		synchronousClient.publishCollection(command);

	}
	

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
    
