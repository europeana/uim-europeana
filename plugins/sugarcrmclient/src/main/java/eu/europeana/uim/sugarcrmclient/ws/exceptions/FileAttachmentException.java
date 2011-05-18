/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.ws.exceptions;

import eu.europeana.uim.sugarcrmclient.jibxbindings.ErrorValue;

/**
 * Exception thrown for File Attachment Errors
 * @author Georgios Markakis
 *
 */
public class FileAttachmentException extends GenericSugarCRMException {

	private static final long serialVersionUID = 1L;

	/**
	 * This constructor takes as an argument an ErrorValue object
	 * @param err the ErrorValue message
	 */
	public FileAttachmentException(ErrorValue err) {
		
		super(generateMessageFromObject(err));

	}
}
