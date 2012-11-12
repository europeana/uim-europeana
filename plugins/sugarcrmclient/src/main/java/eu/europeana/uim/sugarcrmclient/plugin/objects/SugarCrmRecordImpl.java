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
package eu.europeana.uim.sugarcrmclient.plugin.objects;


import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import eu.europeana.uim.sugar.SugarCrmRecord;
import eu.europeana.uim.sugar.model.SugarCrmField;
import eu.europeana.uim.sugar.model.UpdatableField;

/**
 * This class essentially acts as a wrapper from the response
 * result of a SugarCRM query. The response is a DOM Element. 
 * Auxiliary methods are provided in order to manipulate the
 * query results.
 * 
 * @author Georgios Markakis
 */
public class SugarCrmRecordImpl implements SugarCrmRecord {
	
	//The wrapped result element
	private Element record;
	
	
	/**
	 * Private constructor
	 */
	private SugarCrmRecordImpl(){
	}
	
	
	/**
	 * Static factory method
	 * 
	 * @param el a DOM element
	 * @return a SugarCrmRecordImpl object
	 */
	public static SugarCrmRecordImpl getInstance(Element el){
		SugarCrmRecordImpl obj = new SugarCrmRecordImpl();
		obj.setRecord(el);
		return obj;
	}	
	/**
	 * Updates 
	 * 
	 * @param field
	 */
	@Override
    public void setItemValue(UpdatableField field,String value){
		modifyElement(field.getFieldId(), value, record);
	}
	
	
	/**
	 * @param field
	 * @return
	 */
	@Override
    public String getItemValue(SugarCrmField field){
		return extractFromElement(field.getFieldId(), record);
	}
	
	
	private void modifyElement(String field, String value, Element el){
		NodeList nl =el.getElementsByTagName("item");
		  
		  boolean found=false;
		  for (int i =0; i<nl.getLength(); i++){
	            Node nd = nl.item(i);
	            String textcontent = nd.getChildNodes().item(0).getTextContent(); 
	            if (field.equals(textcontent)){
	                nd.getChildNodes().item(1).setTextContent(value);
	                found=true;
	            }   
	        }
		  
		  if (!found) {
		     //TODO: create a new node   
		  }
		  
	}
	
	private String extractFromElement(String field, Element el){
		NodeList nl =el.getElementsByTagName("item");
		
		for (int i =0; i<nl.getLength(); i++){
			Node nd = nl.item(i);
			String textcontent = nd.getChildNodes().item(0).getTextContent(); 
			if (field.equals(textcontent)){
				return nd.getChildNodes().item(1).getTextContent();
			}
			
		}
		return null;
	}
	
	
	
	/**
	 * @param record the record to set
	 */
	public void setRecord(Element record) {
		this.record = record;
	}

	/**
	 * @return the record
	 */
	public Element getRecord() {
		return record;
	}
	
}
