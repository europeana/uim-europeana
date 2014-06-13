/**
 * 
 */
package eu.europeana.uim.neo4jplugin.impl;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.mapdb.HTreeMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 */
public class XMLRefParser extends DefaultHandler{

	private String currentType;
	private String currentID;

	private  HTreeMap<String, Map<String,String>> map;
	
	public XMLRefParser(HTreeMap<String, Map<String,String>> map){
		this.map = map;
	}
	
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#startElement(java.lang.String,
	 * java.lang.String, java.lang.String, org.xml.sax.Attributes)
	 */
	@Override
	public void startElement(String namespaceURI, String sName, // simple name
			String qName, // qualified name
			Attributes attrs) throws SAXException {

		//Collect the attribute references here
		if (qName.equalsIgnoreCase("edm:Proxy")
				|| qName.equalsIgnoreCase("ore:Proxy")

				|| qName.equalsIgnoreCase("edm:ProvidedCHO")
				|| qName.equalsIgnoreCase("edm:WebResource")
				|| qName.equalsIgnoreCase("edm:Agent")
				|| qName.equalsIgnoreCase("edm:Place")
				|| qName.equalsIgnoreCase("edm:TimeSpan")
				|| qName.equalsIgnoreCase("skos:Concept")
				|| qName.equalsIgnoreCase("ore:Aggregation")
				|| qName.equalsIgnoreCase("edm:EuropeanaAggregation")
								/**/) {

			currentID = processEntityID(attrs.getValue("rdf:about"));
			currentType = qName;

		}

		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware

		if (attrs != null) {

			Map<String,String> referencelist = map.get(currentID);
			if (referencelist == null) {
				referencelist = new HashMap<String,String>();
				
				if(currentID != null){
					map.put(currentID, referencelist);
				}
				
			}

			for (int i = 0; i < attrs.getLength(); i++) {
				String aName = attrs.getQName(i); //getLocalName(i); // Attr name

				//System.out.println("aName:" + aName);
				if (aName.equals("rdf:resource")) {
					//System.out.println("Found resource:" + processEntityID(attrs.getValue(i)));
					referencelist.put(processEntityID(attrs.getValue(i)),eName);
				}

				if ("".equals(aName))
					aName = attrs.getQName(i);

			}
			
			if(currentID != null){
				map.put(currentID, referencelist);
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.xml.sax.helpers.DefaultHandler#endElement(java.lang.String,
	 * java.lang.String, java.lang.String)
	 */
	@Override
	public void endElement(String namespaceURI, String sName, // simple name
			String qName // qualified name
	) throws SAXException {
		String eName = sName; // element name
		if ("".equals(eName))
			eName = qName; // not namespace-aware

		if (qName.equalsIgnoreCase("edm:Proxy")
				|| qName.equalsIgnoreCase("ore:Proxy")
				|| qName.equalsIgnoreCase("edm:ProvidedCHO")
				|| qName.equalsIgnoreCase("edm:WebResource")
				|| qName.equalsIgnoreCase("edm:Agent")
				|| qName.equalsIgnoreCase("edm:Place")
				|| qName.equalsIgnoreCase("edm:TimeSpan")
				|| qName.equalsIgnoreCase("skos:Concept")
				|| qName.equalsIgnoreCase("ore:Aggregation")
				|| qName.equalsIgnoreCase("edm:EuropeanaAggregation")
				) {

			System.out.println(currentID);
			System.out.println(currentType);
			
			Map<String,String> referencelist = map.get(currentID);
			
			Iterator<String> it = referencelist.keySet().iterator();
			
			while(it.hasNext()){
				String key = it.next();
				String value = referencelist.get(key);
				//System.out.println(key);
				//System.out.println(value);
			}

			//saveNeo4jBean(bean);
			

		}
	}
	
	
	
	private String processEntityID(String id){
		
		String prid = id.replace("http://data.europeana.eu", "");
		return prid;
	}
}
