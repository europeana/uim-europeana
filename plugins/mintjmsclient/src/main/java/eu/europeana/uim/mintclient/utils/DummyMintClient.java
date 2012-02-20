/**
 * 
 */
package eu.europeana.uim.mintclient.utils;

import java.io.IOException;
import java.util.HashMap;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.AMQP.BasicProperties.Builder;

import eu.europeana.uim.mintclient.ampq.MintAMPQClientImpl;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationResponse;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserCommand;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsResponse;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsCommand;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsResponse;
import eu.europeana.uim.mintclient.jibxbindings.PublicationAction;
import eu.europeana.uim.mintclient.jibxbindings.PublicationCommand;
import eu.europeana.uim.mintclient.jibxbindings.PublicationResponse;

/**
 * @author geomark
 *
 */
public class DummyMintClient extends MintAMPQClientImpl{
	
	public DummyMintClient(){
		super();
	}
	
	

	public void createOrganizationResponse(CreateOrganizationResponse response) {
		CreateOrganizationAction cu = new CreateOrganizationAction();
		cu.setCreateOrganizationResponse(response);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);
	}


	public void createUserResponse(CreateUserResponse response) {
		CreateUserAction cu = new CreateUserAction();
		cu.setCreateUserResponse(response);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);

	}

	

	public void getImportsResponse(GetImportsResponse response) {
		GetImportsAction cu = new GetImportsAction();
		cu.setGetImportsResponse(response);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);
	}


	public void createImportsResponse(CreateImportResponse response) {
		CreateImportAction cu = new CreateImportAction();
		cu.setCreateImportResponse(response);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);
	}
	

	public void getTransformationsResponse(GetTransformationsResponse response) {
		GetTransformationsAction cu = new GetTransformationsAction();
		cu.setGetTransformationsResponse(response);
		String cmdstring = MintClientUtils.unmarshallObject(cu, null);
		sendChunk(cmdstring.getBytes(),true);
	}


	public void publishCollectionResponse(PublicationResponse response) {
		PublicationAction cu = new PublicationAction();
		cu.setPublicationResponse(response);
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
			receiveChannel.basicPublish( "", outbound, 
			        pros,
			        payload);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
