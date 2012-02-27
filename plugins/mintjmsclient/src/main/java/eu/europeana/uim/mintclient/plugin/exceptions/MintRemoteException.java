/**
 * 
 */
package eu.europeana.uim.mintclient.plugin.exceptions;

/**
 * @author geomark
 *
 */
public class MintRemoteException extends MintGenericException {


	private static final long serialVersionUID = 1L;

    /**
     * This constructor takes as an argument a String
     * 
     * @param message
     *            the error message
     */
    public MintRemoteException(String message) {
        super(message);
    }
}
