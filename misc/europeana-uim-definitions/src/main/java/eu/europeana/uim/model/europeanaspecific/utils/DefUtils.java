/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.model.europeanaspecific.utils;

import java.io.StringReader;
import java.io.StringWriter;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

/**
 * 
 * @author Georgios Markakis
 */
public class DefUtils {

	
	
	/**
	 * Private auxiliary method that performs the unmarshalling of a JIBX object
	 * 
	 * @param jibxObject
	 * @return
	 * @throws JiBXException
	 */
	public static String marshallObject(Object jibxObject) throws JiBXException{
		IBindingFactory context;
		context = BindingDirectory.getFactory(jibxObject.getClass());

		IMarshallingContext mctx = context.createMarshallingContext();
		mctx.setIndent(2);
		StringWriter stringWriter = new StringWriter();
		mctx.setOutput(stringWriter);
		mctx.marshalDocument(jibxObject);		
		
		String xmlContents = stringWriter.toString();
		
		return xmlContents;
	}
	
	
	 /**
	 * @param <T>
	 * @param xml
	 * @param type
	 * @return
	 * @throws JiBXException
	 */
	public static <T> T unmarshallObject(String xml,T type) throws JiBXException{
		 IBindingFactory bfactory = BindingDirectory.getFactory(type.getClass());
		 IUnmarshallingContext context = bfactory.createUnmarshallingContext();
		 
		 StringReader reader = new StringReader(xml);

		 @SuppressWarnings("unchecked")
		T object = (T) context.unmarshalDocument(reader);
		 
		 return object;
	 }
	
	
}
