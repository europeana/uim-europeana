/**
 * 
 */
package eu.europeana.uim.europeanaspecific.workflowstarts.util;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import org.xml.sax.helpers.DefaultHandler;


/**
 *
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since 26 Mar 2013
 *
 */
public class SaxBasedIDExtractor extends DefaultHandler{

	
	/**
	 * 
	 */
	public SaxBasedIDExtractor(){
		
	}
	
	private List<String> ids = new ArrayList<String>();
	
	/**
	 * @param xml
	 * @return
	 */
	public List<String> extractIDs(String xml){

				SAXParserFactory spf = SAXParserFactory.newInstance();
				try {
					SAXParser sp = spf.newSAXParser();
					sp.parse(new InputSource(new StringReader(xml)), this);	
				}catch(SAXException se) {
					se.printStackTrace();
				}catch(ParserConfigurationException pce) {
					pce.printStackTrace();
				}catch (IOException ie) {
					ie.printStackTrace();
				}
		
		return ids;
		
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {

        if (qName.equalsIgnoreCase("edm:Proxy") || qName.equalsIgnoreCase("ore:Proxy") || qName.equalsIgnoreCase("edm:ProvidedCHO")){
        	String value = attributes.getValue("rdf:about");
        	ids.add(value);
          }
	}
	
}
