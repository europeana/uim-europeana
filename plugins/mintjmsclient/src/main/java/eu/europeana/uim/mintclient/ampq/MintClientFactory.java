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
package eu.europeana.uim.mintclient.ampq;


import com.rabbitmq.client.DefaultConsumer;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.service.exceptions.MintRemoteException;
import eu.europeana.uim.mintclient.service.listeners.UIMConsumerListener;


/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class MintClientFactory {

	
	/**
	 * @return
	 */
	public ProducerProxy syncMode(){
		return this.new ProducerProxySyncImpl();
	}
	
	/**
	 * @return
	 */
	public ProducerProxy asyncMode(){
		return this.new ProducerProxyAsyncImpl<UIMConsumerListener>();
	}
	
	
	/**
	 * @param listenerClass
	 * @return
	 */
	public <T  extends DefaultConsumer> ProducerProxy asyncMode(Class<T> listenerClass){
		return this.new ProducerProxyAsyncImpl<T>(listenerClass);
	}
	

	
	/**
	 *
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 6 Mar 2012
	 */
	public interface ProducerProxy{
		
		public MintAMPQClient createClient() throws MintOSGIClientException,MintRemoteException;
	}
	
	
	/**
	 *
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 6 Mar 2012
	 * @param <T>
	 */
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
	
	/**
	 *
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 6 Mar 2012
	 */
	public class ProducerProxySyncImpl implements ProducerProxy{
		
		@Override
		public MintAMPQClientSync createClient() {
			return MintAMPQClientSyncImpl.getClient();
		}
		
	}
	

	
}
