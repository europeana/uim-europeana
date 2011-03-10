/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.ws.exceptions;


import eu.europeana.uim.sugarcrmclient.jaxbbindings.LoginResponse;

/**
 * Exception thrown in case of Authentication Error
 * 
 * @author Georgios Markakis
 */
public class LoginFailureException extends Exception {

	private static final long serialVersionUID = 1L;


	/**
	 * Default constructor
	 */
	public LoginFailureException() {
		super();
	}

	/**
	 * 
	 * @param arg0
	 */
	public LoginFailureException(String message) {
		super(message);
	}
	
	
	/**
	 * 
	 * @param arg0
	 */
	public LoginFailureException(LoginResponse response) {
		super(createloginfailureMessage( response));
	}
	
	
	
	
	
	private static String createloginfailureMessage(LoginResponse response){
		
		StringBuffer sb = new StringBuffer();
		sb.append("Error Number: ");
		sb.append(response.getReturn().getError().getNumber());
		sb.append(" Error Name: ");
		sb.append(response.getReturn().getError().getName());
		sb.append(" Error Description: ");
		sb.append(response.getReturn().getError().getDescription());
		
		return sb.toString();
		
	}


}
