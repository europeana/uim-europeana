/**
 * 
 */
package eu.europeana.uim.mintclient.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * 
 * @author geomark
 */
public class MintClientUtils {

	public static synchronized String unmarshallObject(Object jibxObject,
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
	
	@SuppressWarnings("unchecked")
	public static synchronized <T> void marshallobject(String str2marshall, T jibxObject){
		IBindingFactory context;
		
		try {
			context = BindingDirectory.getFactory(jibxObject.getClass());
			IUnmarshallingContext umctx = context.createUnmarshallingContext();
			jibxObject =(T) umctx.unmarshalDocument(new StringReader(str2marshall));
		} catch (JiBXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	


}
