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
 * Factory Class for instantiating syncronous & asynchronous clients for the
 * OSGI plugin.
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class MintClientFactory {

	/**
	 * Creates a proxy that provides synchronous clients
	 * 
	 * @return a proxy
	 */
	public ProducerProxy syncMode() {
		return this.new ProducerProxySyncImpl();
	}

	/**
	 * Creates a proxy that provides asynchronous clients
	 * 
	 * @return a proxy
	 */
	public ProducerProxy asyncMode() {
		return this.new ProducerProxyAsyncImpl<UIMConsumerListener>();
	}

	/**
	 * Creates a proxy that provides asynchronous clients and assigns a listener
	 * to the clients to be generated
	 * 
	 * @param listenerClass
	 * @return a proxy
	 */
	public <T extends DefaultConsumer> ProducerProxy asyncMode(
			Class<T> listenerClass) {
		return this.new ProducerProxyAsyncImpl<T>(listenerClass);
	}

	/**
	 * A public inner interface for a ProducerProxy
	 * 
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 6 Mar 2012
	 */
	public interface ProducerProxy {

		/**
		 * Instantiates a single instance of a client
		 * 
		 * @return
		 * @throws MintOSGIClientException
		 * @throws MintRemoteException
		 */
		public MintAMPQClient createClient() throws MintOSGIClientException,
				MintRemoteException;
	}

	/**
	 * A public inner class that instantiates an asynchronous client
	 * 
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 6 Mar 2012
	 * @param <T>
	 */
	public class ProducerProxyAsyncImpl<T extends DefaultConsumer> implements
			ProducerProxy {
		private Class<T> listenerClass;

		/**
		 * Default Constructor
		 */
		ProducerProxyAsyncImpl() {

		}

		/**
		 * Constructor that takes a listener as an argument
		 * 
		 * @param listenerClass
		 */
		ProducerProxyAsyncImpl(Class<T> listenerClass) {
			this.listenerClass = listenerClass;
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * eu.europeana.uim.mintclient.ampq.MintClientFactory.ProducerProxy#
		 * createClient()
		 */
		@Override
		public MintAMPQClientASync createClient()
				throws MintOSGIClientException {

			if (listenerClass == null) {
				return MintAMPQClientAsyncImpl.getClient();
			} else {
				return MintAMPQClientAsyncImpl.getClient(listenerClass);
			}

		}

	}

	/**
	 * A public inner class that instantiates a synchronous client
	 * 
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 6 Mar 2012
	 */
	public class ProducerProxySyncImpl implements ProducerProxy {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * eu.europeana.uim.mintclient.ampq.MintClientFactory.ProducerProxy#
		 * createClient()
		 */
		@Override
		public MintAMPQClientSync createClient() throws MintOSGIClientException {
			return MintAMPQClientSyncImpl.getClient();
		}

	}

}
