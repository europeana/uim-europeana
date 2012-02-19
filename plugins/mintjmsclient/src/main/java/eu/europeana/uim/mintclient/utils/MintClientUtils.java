/**
 * 
 */
package eu.europeana.uim.mintclient.utils;

import java.io.StringWriter;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * 
 * @author geomark
 */
public class MintClientUtils {

	public static String unmarshallObject(Object jibxObject,
			org.apache.log4j.Logger LOGGER) {
		IBindingFactory context;

		try {
			context = BindingDirectory.getFactory(jibxObject.getClass());

			IMarshallingContext mctx = context.createMarshallingContext();
			mctx.setIndent(2);
			StringWriter stringWriter = new StringWriter();
			mctx.setOutput(stringWriter);
			mctx.marshalDocument(jibxObject);
			StringBuffer sb = new StringBuffer(stringWriter.toString());
			return sb.toString();

		} catch (JiBXException e) {
			e.printStackTrace();
			return null;
		}
	}

}
