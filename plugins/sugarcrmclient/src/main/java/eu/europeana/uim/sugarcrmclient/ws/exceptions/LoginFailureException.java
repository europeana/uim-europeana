/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.ws.exceptions;


import eu.europeana.uim.sugarcrmclient.jibxbindings.LoginResponse;

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
	 * This constructor takes as an argument a String
	 * @param message the error message
	 */
	public LoginFailureException(String message) {
		super(message);
	}
	
	
	/**
	 * This constructor takes as an argument an LoginResponse object
	 * @param err
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
