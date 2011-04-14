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
package eu.europeana.uim.repoxclient.rest.exceptions;

import eu.europeana.uim.repoxclient.jibxbindings._Error;

/**
 *  Exception thrown in case Repox returns an error message
 * @author Georgios Markakis
 */
public class RepoxException extends Exception {

	/**
	 * Default Constructor
	 */
	public RepoxException() {
		super();
	}

	/**
	 * This constructor takes as an argument a String
	 * @param message the error message
	 */
	public RepoxException(String message) {
		super(message);
	}

	/**
	 * This constructor takes as an argument an _Error object
	 * @param err
	 */
	public RepoxException(_Error err) {
		super(createRepoxFailureMessage(err));
	}


	/**
	 * Auxiliary method for extracting error information
	 * from an _Error object
	 * 
	 * @param err the Error object
	 * @return the error description
	 */
	private static String createRepoxFailureMessage(_Error err){
		
		StringBuffer sb = new StringBuffer();
		sb.append(" Error Type: ");
		sb.append(err.getType());
		sb.append(" Error Cause: ");		
		sb.append(err.getCause());
		sb.append(" Request URI: ");		
		sb.append(err.getRequestURI());		
		
		return sb.toString();
	}

}
