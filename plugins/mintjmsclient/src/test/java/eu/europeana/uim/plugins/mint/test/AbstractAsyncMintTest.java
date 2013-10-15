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

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.apache.log4j.Logger;
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
import eu.europeana.uim.mintclient.utils.MintClientUtils;

/**
 * Unit tests implementation for MINT client asynchronous calls used by both
 * mockito based and integration tests
 * 
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since Oct 11, 2013
 */
public abstract class AbstractAsyncMintTest {

	/**
	 * Protected client field initialized by subclass
	 */
	protected static MintAMPQClientASync client;

	/**
	 * Protected listener field initialized by subclass
	 */
	protected static TestListener listener;
	
	private final static String provId = "099";
	private final static String colId= "09911";
	private static Logger log = Logger.getLogger(AbstractAsyncMintTest.class);
    private static int blockingdelay = 2000;
    
    
	/**
	 * Create the client on test initialization
	 * 
	 * @throws MintOSGIClientException
	 * @throws MintRemoteException
	 */

	@BeforeClass public static void initclient() throws MintOSGIClientException, MintRemoteException {
		MintClientFactory factory = new MintClientFactory();
		String proplocation = AbstractAsyncMintTest.class.getProtectionDomain().getCodeSource().getLocation() + "mintTest.properties";
		String truncated = proplocation.replace("file:", "");
		client = (MintAMPQClientASync) factory.asyncMode(TestListener.class).createClient(truncated);
		listener = (TestListener) client.getConsumer();
		log.info("Initialized test context & created Asynchronous Client");
	}
	    
	/**
	 * Closes the current connection used for tests, nullifies the client
	 * and forces garbage collection.
	 * 
	 * @throws IOException 
	 */

	@AfterClass public static void tearDown() throws IOException {
	   client.closeConnection();	
	   client = null;
	   System.gc();
	   log.info("Destroyed Asynchronous Client after test completion");
	}
	
	
	
	/**
	 * @throws Exception
	 */

	@Test
	public void createOrganizationTest() throws Exception{
		log.info("CreateOrganizationTest:Sending request");
		CreateOrganizationCommand command = new CreateOrganizationCommand();
		command.setCountry("es");
		command.setEnglishName("TestOrg");
		command.setName("TestOrg");
		command.setType("Type");
		command.setUserId("1000");
		client.createOrganization(command,provId);
		//Wait for async response
		CreateOrganizationAction resp = listener.getCreateOrganizationResponse();
		assertNotNull(resp.getCreateOrganizationResponse());
		assertNotNull(resp.getCreateOrganizationResponse().getOrganizationId());
		assertNull(resp.getError());
	}
	
	/**
	 * @throws Exception
	 */

	@Test
	public void createUserTest() throws Exception{
		log.info("CreateUserTest : Sending request");
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
		CreateUserAction resp = listener.getCreateUserResponse();
		assertNotNull(resp.getCreateUserResponse());
		assertNotNull(resp.getCreateUserResponse().getUserId());
		assertNull(resp.getError());
	}
	

	/**
	 * @throws Exception
	 */

	@Test
	public void createImportsTest() throws Exception{
		log.info("CreateImportsTest : Sending request");
		CreateImportCommand command = new CreateImportCommand();
		command.setUserId("1000");
		command.setOrganizationId("1");
		command.setRepoxTableName("azores13");
		client.createImports(command,colId);
		//Wait for async response
		CreateImportAction resp = listener.getCreateImportResponse();
		assertNotNull(resp);
	}
	
	/**
	 * @throws Exception
	 */

	@Test
	public void getImportsTest() throws Exception{
		log.info("GetImportsTest : Sending request");
		GetImportsCommand command =  new GetImportsCommand();
		command.setOrganizationId("1002");
		client.getImports(command,provId);
		//Wait for async response
		GetImportsAction resp = listener.getImportsResponse();
		assertNotNull(resp.getGetImportsResponse());
		assertNotNull(resp.getGetImportsResponse().getImportIdList());
		assertNull(resp.getError());
	}
	
	/**
	 * @throws Exception
	 */

	@Test
	public void getTransformations() throws Exception{
		log.info("GetTransformationsTest : Sending request");
		GetTransformationsCommand command = new GetTransformationsCommand();
		command.setOrganizationId("1002");
		client.getTransformations(command,provId);
		//Wait for async response
		GetTransformationsAction resp = listener.getTransformationsResponse();
		assertNotNull(resp.getGetTransformationsResponse());
		assertNotNull(resp.getGetTransformationsResponse().getTransformationIdList());
		assertNull(resp.getError());
	}
	
	/**
	 * @throws Exception
	 */

	@Test
	public void publishCollection() throws Exception{
		log.info("PublishCollectionTest : Sending request");
		PublicationCommand command = new PublicationCommand();
		List<String> list =  new ArrayList<String>();
		list.add("test1");
		list.add("test2");
		command.setIncludedImportList(list );
		command.setOrganizationId("orgid");
		command.setUserId("userId");
		client.publishCollection(command,colId);
		//Wait for async response
		PublicationAction resp = listener.getPublicationActionResponse();
		assertNotNull(resp.getPublicationResponse());
		assertNotNull(resp.getPublicationResponse().getIncludedImportList());
		assertNotNull(resp.getPublicationResponse().getUrl());
		assertNull(resp.getError());

	}


	/**
	 * A test listener implementation exclusively used for unit testing.
	 * 
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 26 Mar 2012
	 * @see ReponseHandler
	 */
	public static class TestListener extends DefaultConsumer {
		
		private static CreateOrganizationAction orgaction;
		private static CreateUserAction useraction;
		private static CreateImportAction crimportaction;
		private static GetImportsAction  getimportaction;
		private static GetTransformationsAction gettransformationsaction;
		private static PublicationAction publicationaction;
		
		private static Logger log = Logger.getLogger(TestListener.class);
		private Channel channel; 
		
		public TestListener(Channel channel) {
			super(channel);
			this.channel = channel;
			
		}
		
		/**
		 * @return
		 * @throws InterruptedException
		 * @throws TimeoutException
		 */
		public CreateOrganizationAction getCreateOrganizationResponse() throws InterruptedException, TimeoutException{
			Thread.sleep(blockingdelay);
			check(orgaction);
			return orgaction;
		}
		
		/**
		 * @return
		 * @throws InterruptedException
		 * @throws TimeoutException
		 */
		public CreateUserAction getCreateUserResponse() throws InterruptedException, TimeoutException{
			Thread.sleep(blockingdelay);
			check(useraction);
			return useraction;
		}
		
		/**
		 * @return
		 * @throws InterruptedException
		 * @throws TimeoutException
		 */
		public CreateImportAction getCreateImportResponse() throws InterruptedException, TimeoutException{
			Thread.sleep(blockingdelay);
			check(crimportaction);
			return crimportaction;
		}
		
		/**
		 * @return
		 * @throws InterruptedException
		 * @throws TimeoutException
		 */
		public GetImportsAction getImportsResponse() throws InterruptedException, TimeoutException{
			Thread.sleep(blockingdelay);
			check(getimportaction);
			return getimportaction;
		}
		
		/**
		 * @return
		 * @throws InterruptedException
		 * @throws TimeoutException
		 */
		public GetTransformationsAction getTransformationsResponse() throws InterruptedException, TimeoutException{
			Thread.sleep(blockingdelay);
			check(gettransformationsaction);
			return gettransformationsaction;
		}
		
		/**
		 * @return
		 * @throws InterruptedException
		 * @throws TimeoutException
		 */
		public PublicationAction getPublicationActionResponse() throws InterruptedException, TimeoutException{
			Thread.sleep(blockingdelay);
			check(publicationaction);
			return publicationaction;
		}
		

		/**
		 * Checks if the field is initialized, throws a timeout exception otherwise
		 * @param obj
		 * @throws TimeoutException
		 */
		private static void check(Object obj) throws TimeoutException{
			if(obj == null){
				throw new TimeoutException();
			}
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

	    	IMarshallable response;
			try {
				response = MintClientUtils.unmarshallobject(new String(body));
				log.info("For operation name: " + response.JiBX_getName());
				
			 	   if(response instanceof CreateOrganizationAction){
			 		  orgaction = (CreateOrganizationAction) response;
					}
					else if(response instanceof CreateUserAction){
						useraction =  (CreateUserAction) response;
					}
					else if(response instanceof CreateImportAction){
						crimportaction = (CreateImportAction) response;
					}
					else if(response instanceof GetImportsAction){
						getimportaction = (GetImportsAction) response;
					}
					else if(response instanceof GetTransformationsAction){
						gettransformationsaction = (GetTransformationsAction) response;
					}
					else if(response instanceof PublicationAction){
						publicationaction = (PublicationAction) response;
					}				
		
			} catch (MintOSGIClientException e) {
				log.error("An exception has occured: ", e);				
			}
	    }

	}
    
    
    
	
}
