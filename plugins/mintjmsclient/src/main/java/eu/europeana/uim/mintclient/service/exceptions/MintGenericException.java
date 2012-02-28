/**
 * 
 */
package eu.europeana.uim.mintclient.service.exceptions;

/**
 * @author geomark
 *
 */
public abstract class MintGenericException extends Exception {

	private static final long serialVersionUID = 1L;

	/**
     * This constructor takes as an argument a String
     * 
     * @param message
     *            the error message
     */
    public MintGenericException(String message) {
        super(message);
    }


}
