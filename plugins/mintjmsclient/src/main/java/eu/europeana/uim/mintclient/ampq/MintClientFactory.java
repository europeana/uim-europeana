/**
 * 
 */
package eu.europeana.uim.mintclient.ampq;


import com.rabbitmq.client.DefaultConsumer;
import eu.europeana.uim.mintclient.ampq.listeners.UIMConsumerListener;
import eu.europeana.uim.mintclient.plugin.MintAMPQClient;
import eu.europeana.uim.mintclient.plugin.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.plugin.exceptions.MintRemoteException;

/**
 * 
 * @author Georgios Markakis
 *
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
		public MintAMPQClient createClient() throws MintOSGIClientException{

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
		public MintAMPQClient createClient() {
			return MintAMPQClientSyncImpl.getClient();
		}
		
		
	}
	

	
}
