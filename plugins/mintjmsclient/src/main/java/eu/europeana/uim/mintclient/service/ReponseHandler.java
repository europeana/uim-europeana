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
package eu.europeana.uim.mintclient.service;

import org.jibx.runtime.IMarshallable;

import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.utils.AMPQOperations;
import eu.europeana.uim.mintclient.utils.MintClientUtils;

/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 22 Mar 2012
 */
public class ReponseHandler {

	private static Registry registry;
	private static Orchestrator<?> orchestrator;
	
	
	public ReponseHandler(Registry registry,Orchestrator<?> orchestrator){
		this.registry = registry;
		this.orchestrator = orchestrator;
	}
	
	public void handleResponse(IMarshallable response ){
    	

 	   
    	AMPQOperations responseType;
		try {
			responseType = MintClientUtils.translateAMPQOperation(response.JiBX_getName());
	    	switch(responseType){
	    	case CreateOrganizationAction:
	    		
	    	     break;
	    	case CreateUserAction:
	    		
	    	     break;	        		
	    	case CreateImportAction:
	    		break;	 
	    		
	    	case	GetImportsAction:
	    		break;	 
	    	case	GetTransformationsAction:
	    		
	    		break;	 
	    	case	PublicationAction:
	    		break;	 
	    	case	ImportExistsAction:
	    		break;	 
	    	case	UserExistsAction:
	    		break;	 
	    	case	OrganizationExistsAction:
	    		break;	 

	    	default:
	    		throw new UnsupportedOperationException("Received Message from Mint is not supported.");
	    	}
		} catch (MintOSGIClientException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}


	}
}
