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
package eu.europeana.uim.mintclient.service.listeners;

import java.io.IOException;

import org.jibx.runtime.IMarshallable;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.utils.MintClientUtils;


/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class UIMConsumerListener extends DefaultConsumer {

	private Channel channel; 
	
	public UIMConsumerListener(Channel channel) {
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
        
    	System.out.println("ASDF");
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
