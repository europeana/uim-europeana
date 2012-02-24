/**
 * 
 */
package eu.europeana.uim.mintclient.ampq;

import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;

import eu.europeana.uim.mintclient.plugin.MintAMPQClient;

/**
 * 
 * @author Georgios Markakis
 *
 */
public class MintClientFactory {

	
	
	public static void defineMode(boolean isSync){
		
	}
	
	

	
	private interface ProducerProxy{
		
		public MintAMPQClient createClient();
	}
	
	
	private class ProducerProxyAsyncImpl implements ProducerProxy{
	
		ProducerProxyAsyncImpl(){
			
		}

		@Override
		public MintAMPQClient createClient() {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	}
	
	private class ProducerProxySyncImpl implements ProducerProxy{
		
		ProducerProxySyncImpl(){
			
		}

		@Override
		public MintAMPQClient createClient() {
			// TODO Auto-generated method stub
			return null;
		}
		
		
	}
	
	/**
	 * @param isAsync
	 * @param asyncConsumer
	 * @return
	 */
	public MintAMPQClient createClient(boolean isAsync,DefaultConsumer asyncConsumer){
		
		if(isAsync == true && asyncConsumer == null){
			throw new IllegalArgumentException("If isAsync id true then asyncConsumer cannot be null");
		}
		
		MintAMPQClient client = isAsync ? MintAMPQClientSyncImpl.getClient() : null; 

		return client;
	}
	
}
