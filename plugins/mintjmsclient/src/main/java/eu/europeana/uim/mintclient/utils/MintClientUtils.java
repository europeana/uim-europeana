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
import eu.europeana.uim.mintclient.service.exceptions.MintGenericException;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;

/**
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
 */
public class MintClientUtils {

	/**
	 * @param jibxObject
	 * @return
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
	 * @param str2marshall
	 * @param type
	 * @return
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
	 * @param e
	 * @param toconvert
	 * @param extra
	 * @return
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
