/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.repoxclient.rest;

import org.springframework.web.client.RestTemplate;

/**
 * Class implementing REST functionality for accessing the REPOX
 * repository.
 *  
 * @author Georgios Markakis
 */
public class RepoxRestClient {

	private RestTemplate restTemplate;

	private String defaultURI;
	
	
	/**
	 * @param <T>
	 * @param <S>
	 * @param wsOperation
	 * @return
	 */
	private <T,S> S invokRestTemplate( T wsOperation, Class<S> responseClass){

		@SuppressWarnings("unchecked")
		S restResponse = (S)restTemplate.getForObject("http://url/myService/{param}", responseClass, "myParameterWord");       //.marshalSendAndReceive(wsOperation);
		
		return restResponse;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	//Getters & Setters
		
	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setDefaultURI(String defaultURI) {
		this.defaultURI = defaultURI;
	}

	public String getDefaultURI() {
		return defaultURI;
	}
	
	
	
	
	
}
