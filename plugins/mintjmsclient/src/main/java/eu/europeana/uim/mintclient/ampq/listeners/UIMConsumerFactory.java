/**
 * 
 */
package eu.europeana.uim.mintclient.ampq.listeners;

import java.io.IOException;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

/**
 * @author geomark
 *
 */
public class UIMConsumerFactory extends DefaultConsumer {

	private Channel channel; 
	
	public UIMConsumerFactory(Channel channel) {
		super(channel);
		this.channel = channel;
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

        channel.basicAck(deliveryTag, false);
    }

}
