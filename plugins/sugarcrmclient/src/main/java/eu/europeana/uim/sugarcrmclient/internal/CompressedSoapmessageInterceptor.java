/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.internal;

import org.springframework.ws.client.WebServiceClientException;
import org.springframework.ws.client.support.interceptor.ClientInterceptor;
import org.springframework.ws.context.MessageContext;
import org.springframework.beans.factory.InitializingBean;
/**
 * 
 * @author geomark
 */
public class CompressedSoapmessageInterceptor implements ClientInterceptor,InitializingBean {

	/* (non-Javadoc)
	 * @see org.springframework.ws.client.support.interceptor.ClientInterceptor#handleFault(org.springframework.ws.context.MessageContext)
	 */
	@Override
	public boolean handleFault(MessageContext arg0)
			throws WebServiceClientException {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see org.springframework.ws.client.support.interceptor.ClientInterceptor#handleRequest(org.springframework.ws.context.MessageContext)
	 */
	@Override
	public boolean handleRequest(MessageContext arg0)
			throws WebServiceClientException {

		
		return false;
	}

	/* (non-Javadoc)
	 * @see org.springframework.ws.client.support.interceptor.ClientInterceptor#handleResponse(org.springframework.ws.context.MessageContext)
	 */
	@Override
	public boolean handleResponse(MessageContext arg0)
			throws WebServiceClientException {

		return true;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		// TODO Auto-generated method stub
		
	}

}
