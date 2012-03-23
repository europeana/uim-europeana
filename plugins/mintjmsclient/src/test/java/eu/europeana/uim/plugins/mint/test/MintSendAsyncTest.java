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
package eu.europeana.uim.plugins.mint.test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jibx.runtime.IMarshallable;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eu.europeana.uim.mintclient.ampq.MintAMPQClientASync;
import eu.europeana.uim.mintclient.ampq.MintClientFactory;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.ImportExistsCommand;
import eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.jibxbindings.UserExistsCommand;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.utils.MintClientUtils;



/**
 * Unit Tests for the asynchronous AMPQ client
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class MintSendAsyncTest {
	private static MintAMPQClientASync client;
	private final static String provId = "099";
	private final static String colId= "09911";
	
	/**
	 * Create the client on test initialization
	 * 
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */
	@BeforeClass public static void initclient() throws MintOSGIClientException, MintRemoteException {
		MintClientFactory factory = new MintClientFactory();
		client = (MintAMPQClientASync) factory.asyncMode(TestListener.class).createClient(); 
	}
	    
	/**
	 * Deallocate resources on system shutdown
	 */
	@AfterClass public static void tearDown() {
	   client = null;
	   System.gc();
	}
	
	
	
	/**
	 * @throws Exception
	 */
	@Test
	public void createOrganizationTest() throws Exception{
		CreateOrganizationCommand command = new CreateOrganizationCommand();
		command.setCountry("es");
		command.setEnglishName("TestOrg");
		command.setName("TestOrg");
		command.setType("Type");
		command.setUserId("1002");
		client.createOrganization(command,provId);
		
		//Wait for async response
		Thread.sleep(10000);

	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void createUserTest() throws Exception{
		CreateUserCommand command = new CreateUserCommand();
		command.setEmail("email");
		command.setFirstName("firstName");
		command.setLastName("lastName");
		command.setUserName("user" + (new Date()).toString());
		command.setPassword("werwer");
		command.setPhone("234234234");
		command.setOrganization("1001");
		client.createUser(command,provId);
		
		//Wait for async response
		Thread.sleep(10000);
	}
	

	/**
	 * @throws Exception
	 */
	@Test
	public void createImportsTest() throws Exception{
		CreateImportCommand command = new CreateImportCommand();
		command.setUserId("1000");
		command.setOrganizationId("1");
		command.setRepoxTableName("azores13");
		client.createImports(command,colId);
		
		//Wait for async response
		Thread.sleep(10000);
		
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void getImportsTest() throws Exception{
		GetImportsCommand command =  new GetImportsCommand();
		command.setOrganizationId("1002");
		client.getImports(command,provId);
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void getTransformations() throws Exception{
		GetTransformationsCommand command = new GetTransformationsCommand();
		command.setOrganizationId("1002");
		client.getTransformations(command,provId);
		
		//Wait for async response
		Thread.sleep(10000);
	}
	
	/**
	 * @throws Exception
	 */
	@Test
	public void publishCollection() throws Exception{
		PublicationCommand command = new PublicationCommand();
		List<String> list =  new ArrayList<String>();
		list.add("test1");
		list.add("test2");
		command.setIncludedImportList(list );
		command.setOrganizationId("orgid");
		command.setUserId("userId");
		client.publishCollection(command,colId);
		
		//Wait for async response
		Thread.sleep(10000);
	}
	

	public static class TestListener extends DefaultConsumer {

		private Channel channel; 
		
		public TestListener(Channel channel) {
			super(channel);
			this.channel = channel;
			
		}
		
	    /* (non-Javadoc)
	     * @see com.rabbitmq.client.DefaultConsumer#handleDelivery(java.lang.String, com.rabbitmq.client.Envelope, com.rabbitmq.client.AMQP.BasicProperties, byte[])
	     */
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
	        
	    	System.out.println("ASDFX");
	    	System.out.println(new String(body));
	    	IMarshallable type;
			try {
				type = MintClientUtils.unmarshallobject(new String(body));
		    	System.out.println(type.JiBX_getName());
		    	System.out.println("XXX");
			} catch (MintOSGIClientException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }

	}
	
}
