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

		File file1 = new File("src/test/resources/Item_27253459.xml");
		String rdf = FileUtils.readFileToString(file1);
		
		List<RDF> entitylist1 = new Decoupler().decouple(rdf);	
		
		for(RDF rdfval :entitylist1){
			String out = unmarshallObject(rdfval);
			System.out.println(out);
		}
		
		File file2 = new File("src/test/resources/EDM_test_record-update.xml");
		
		String rdf2 = FileUtils.readFileToString(file2);
		
		List<RDF> entitylist2 = new Decoupler().decouple(rdf2);	
		
		for(RDF rdfval :entitylist2){
			String out = unmarshallObject(rdfval);
			System.out.println(out);
		}
		
		Assert.assertEquals("",6, entitylist2.size());
		Assert.assertEquals("",346, entitylist1.size());
		
		System.out.println(entitylist1.size());
		System.out.println(entitylist2.size());
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
