package eu.europeana.dedup.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.List;

import junit.framework.Assert;

import org.apache.commons.io.FileUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.Test;
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

import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.dedup.osgi.service.exceptions.DeduplicationException;
import eu.europeana.dedup.utils.Decoupler;

/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 27 Sep 2012
 */
public class DecouplerTest {

	/**
	 * 
	 */
	public DecouplerTest() {
		// TODO Auto-generated constructor stub
	}

	@Test
	public void testDecoupling() throws JiBXException, DeduplicationException, IOException{
		File file = new File("src/test/resources/EDM_test_record-update.xml");
		//File file = new File("src/test/resources/test.xml");
		String rdf = FileUtils.readFileToString(file);
		
		List<RDF> entitylist = new Decoupler().decouple(rdf);
		
		Assert.assertEquals("", 6, entitylist.size());
		
		for(RDF rdfval :entitylist){
			String out = unmarshallObject(rdfval);
			System.out.println(out);
		}
		
	}
	

	private static String unmarshallObject(Object jibxObject) throws JiBXException{
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
	
}
