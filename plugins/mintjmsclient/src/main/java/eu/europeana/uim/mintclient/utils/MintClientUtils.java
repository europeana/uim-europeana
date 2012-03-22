/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.mintclient.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.jibx.runtime.IMarshallable; 
import eu.europeana.uim.mintclient.service.exceptions.MintGenericException;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;

/**
 * Utilities Class for the Mint Client
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class MintClientUtils {

	/**
	 * Unmarshall a JibX object.
	 * 
	 * @param jibxObject
	 *            the JIBX object
	 * @return the XML String
	 * @throws MintOSGIClientException
	 */
	public static synchronized String unmarshallObject(Object jibxObject)
			throws MintOSGIClientException {
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
			throw propagateException(e, MintOSGIClientException.class,
					"Error in Jibx unmarshalling");
		}

	}

	/**
	 * Marhalls a String to a JIBX object
	 * 
	 * @param str2marshall
	 *            the String to marshall
	 * @param type
	 *            the class of the produced JIBX Object
	 * @return the marshalled object
	 * @throws MintOSGIClientException
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> T marshallobject(String str2marshall,
			Class<T> type) throws MintOSGIClientException {
		IBindingFactory context;

		try {
			context = BindingDirectory.getFactory(type);
			IUnmarshallingContext umctx = context.createUnmarshallingContext();
			T jibxObject = (T) umctx.unmarshalDocument(new StringReader(
					str2marshall));
			return jibxObject;
		} catch (JiBXException e) {
			throw propagateException(e, MintOSGIClientException.class,
					"Error in Jibx marshalling.");
		}
	}

	
	

	/**
	 * @param str2marshall
	 * @return
	 * @throws MintOSGIClientException
	 */
	public static synchronized  IMarshallable unmarshallobject(String str2marshall) throws MintOSGIClientException {
		IBindingFactory context;

		try {
			String cdmType = "eu.europeana.uim.mintclient.jibxbindings." +
					str2marshall.split("<")[2].split(">")[0] + 
					"Command";
			
			
			context = BindingDirectory.getFactory(Class.forName(cdmType).newInstance().getClass());
			IUnmarshallingContext umctx = context.createUnmarshallingContext();
			IMarshallable jibxObject = (IMarshallable) umctx.unmarshalDocument(new StringReader(
					str2marshall));
			return jibxObject;
		} catch (JiBXException e) {
			throw propagateException(e, MintOSGIClientException.class,
					"Error in Jibx marshalling.");
		} catch (InstantiationException e) {
			throw propagateException(e, MintOSGIClientException.class,
					"Error in Instantiating Class via reflection from received message.");
		} catch (IllegalAccessException e) {
			throw propagateException(e, MintOSGIClientException.class,
					"Error in Instantiating Class via reflection from received message.");
		} catch (ClassNotFoundException e) {
			throw propagateException(e, MintOSGIClientException.class,
					"Error in Instantiating Class via reflection from received message.");
		}

	}

	
	
	/**
	 * Wraps the contents of a thrown exception to a mint exception type.
	 * The latter is instantiated via reflection.
	 * 
	 * @param e
	 *            the original exception
	 * @param toconvert
	 *            the mint-specific exception type to be returned
	 * @param extra
	 *            an array of information to be included in the exception
	 *            message
	 * @return the mint-specific exception
	 * @throws MintOSGIClientException
	 */
	public static synchronized <T extends MintGenericException> T propagateException(
			Exception e, Class<T> toconvert, String... extra)
			throws MintOSGIClientException {
		StringBuilder clientErrorMsg = new StringBuilder();
		clientErrorMsg.append(e.getClass());
		clientErrorMsg.append(":");
		clientErrorMsg.append(e.getMessage());

		for (int i = 0; i < extra.length; i++) {
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
