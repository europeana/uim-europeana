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


import eu.europeana.uim.repoxclient.jibxbindings.DataSources;
import eu.europeana.uim.repoxclient.jibxbindings.Response;
import eu.europeana.uim.repoxclient.rest.exceptions.RepoxException;;

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
	 * Auxiliary method for invoking a REST operation
	 * 
	 * @param <S> the return type
	 * @param wsOperation
	 * @return
	 */
	private <S> S invokRestTemplate( String restOperation,String param, Class<S> responseClass){

		StringBuffer operation = new StringBuffer();
		operation.append(defaultURI);
		operation.append("?operation={param}");
		
		String restResponseObj = restTemplate.getForObject(operation.toString(), String.class, restOperation);		
		S restResponse = (S)restTemplate.getForObject(operation.toString(), responseClass, restOperation);      
		
		return restResponse;
	}
	

	
	/**
	 * 
	 * @return DataSources the available datasources
	 * @throws RepoxException
	 */
	public DataSources retrieveDataSources() throws RepoxException{
		
		Response resp = invokRestTemplate("listDataSources","listDataSources",Response.class);
		
		if(resp.getDataSources() == null){
			if(resp.getError() != null){
				throw new RepoxException(resp.getError());
			}
			else
			{
				throw new RepoxException("Unidentified Repox Error");
			}
		}
		else{
			
			return resp.getDataSources();
		}
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
