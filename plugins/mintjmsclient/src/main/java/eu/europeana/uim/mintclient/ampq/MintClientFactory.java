/**
 * 
 */
package eu.europeana.uim.mintclient.ampq;


import com.rabbitmq.client.DefaultConsumer;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.service.listeners.UIMConsumerListener;

/**
 * 
 * 
 * @author Georgios Markakis
 */
public class MintClientFactory {

	
	public ProducerProxy syncMode(){
		return this.new ProducerProxySyncImpl();
	}
	
	public ProducerProxy asyncMode(){
		return this.new ProducerProxyAsyncImpl<UIMConsumerListener>();
	}
	
	public <T  extends DefaultConsumer> ProducerProxy asyncMode(Class<T> listenerClass){
		return this.new ProducerProxyAsyncImpl<T>(listenerClass);
	}
	

	
	public interface ProducerProxy{
		
		public MintAMPQClient createClient() throws MintOSGIClientException,MintRemoteException;
	}
	
	
	public class ProducerProxyAsyncImpl <T extends DefaultConsumer> implements ProducerProxy{
		private Class<T> listenerClass;
		
		ProducerProxyAsyncImpl(){
			
		}

		ProducerProxyAsyncImpl(Class<T> listenerClass){
			this.listenerClass = listenerClass;
		}
		
		@Override
		public MintAMPQClientASync createClient() throws MintOSGIClientException{
 
			if(listenerClass == null){
				return MintAMPQClientAsyncImpl.getClient();
			}
			else{
				return MintAMPQClientAsyncImpl.getClient(listenerClass);
			}
			
		}
		
		
	}
	
	public class ProducerProxySyncImpl implements ProducerProxy{
		
		@Override
		public MintAMPQClientSync createClient() {
			return MintAMPQClientSyncImpl.getClient();
		}
		
	}
	

	
}
