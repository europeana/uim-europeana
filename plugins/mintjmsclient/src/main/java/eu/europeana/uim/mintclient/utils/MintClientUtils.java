/**
 * 
 */
package eu.europeana.uim.mintclient.utils;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.util.HashMap;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import eu.europeana.uim.mintclient.service.exceptions.MintGenericException;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;

/**
 * 
 * @author Georgios Markakis
 */
public class MintClientUtils {

	public static synchronized String unmarshallObject(Object jibxObject) throws MintOSGIClientException {
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
			throw propagateException(e, MintOSGIClientException.class,"Error in Jibx unmarshalling");
		}

	}
	
	@SuppressWarnings("unchecked")
	public static synchronized <T> T marshallobject(String str2marshall, Class<T> type) throws MintOSGIClientException{
		IBindingFactory context;
		
		try {
			context = BindingDirectory.getFactory(type);
			IUnmarshallingContext umctx = context.createUnmarshallingContext();
			T jibxObject =(T) umctx.unmarshalDocument(new StringReader(str2marshall));
			return jibxObject;
		} catch (JiBXException e) {
			throw propagateException(e, MintOSGIClientException.class,"Error in Jibx marshalling.");
		}
	}
	
	
	
	
	public static synchronized <T extends MintGenericException> T propagateException(Exception e, Class<T> toconvert,String ... extra) throws MintOSGIClientException{
	
		StringBuilder clientErrorMsg = new StringBuilder();
		clientErrorMsg.append(e.getClass());
		clientErrorMsg.append(":");
		clientErrorMsg.append(e.getMessage());
		
		for(int i=0 ; i < extra.length; i++){
			clientErrorMsg.append(" ");
			clientErrorMsg.append(extra[i]);
		}
		
		
		Constructor<T> con;
		try {
			
			con = toconvert.getConstructor(String.class);
			T instance = (T) con.newInstance(clientErrorMsg.toString());
			return instance;
			
		} catch (Exception ex) {

			throw new MintOSGIClientException("Error in propagating exception.");
		}
		
	}



}
