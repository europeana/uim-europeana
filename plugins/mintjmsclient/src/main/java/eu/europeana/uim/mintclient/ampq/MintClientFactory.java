/**
 * 
 */
package eu.europeana.uim.mintclient.ampq;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;

import eu.europeana.uim.mintclient.ampq.listeners.UIMConsumerListener;
import eu.europeana.uim.mintclient.plugin.MintAMPQClient;

/**
 * 
 * @author Georgios Markakis
 *
 */
public class MintClientFactory {

	
	
	
	public <T  extends DefaultConsumer> ProducerProxy defineMode(boolean isSync,Class<T> listenerClass) {
		if(isSync==true && listenerClass!= null){
			throw new IllegalArgumentException("Synchronous client does not support Listener Classes");
		}
		else if(isSync==true && listenerClass == null){
			return this.new ProducerProxySyncImpl();
		}
		else if(isSync==false && listenerClass == null){
			return this.new ProducerProxyAsyncImpl<UIMConsumerListener>();
		}
		else if(isSync==false && listenerClass != null){
			return this.new ProducerProxyAsyncImpl<T>(listenerClass);
		}
		else{
			throw new UnsupportedOperationException("Proxy was not properly initialized");
		}

	}

	
	private interface ProducerProxy{
		
		public MintAMPQClient createClient();
	}
	
	
	private class ProducerProxyAsyncImpl <T> implements ProducerProxy{
		private Class<T> listenerClass;
		
		ProducerProxyAsyncImpl(){
			
		}

		ProducerProxyAsyncImpl(Class<T> listenerClass){
			this.listenerClass = listenerClass;
		}
		
		@Override
		public MintAMPQClient createClient() {

			return null;
		}
		
		
	}
	
	private class ProducerProxySyncImpl implements ProducerProxy{
		
		@Override
		public MintAMPQClient createClient() {
			return MintAMPQClientSyncImpl.getClient();
		}
		
		
	}
	

	
}
