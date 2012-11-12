/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.ws.exceptions;

import eu.europeana.uim.sugar.LoginFailureException;



/**
 * Exception thrown in case of a Logout Error
 * 
 * @author Georgios Markakis
 */
public class JIXBLogoutFailureException extends LoginFailureException{

	private static final long serialVersionUID = 1L;

	/**
	 * This constructor takes as an argument an ErrorValue object
	 * @param err the ErrorValue message
	 */
	public JIXBLogoutFailureException(String err) {
		
		super(err);

	}
}
