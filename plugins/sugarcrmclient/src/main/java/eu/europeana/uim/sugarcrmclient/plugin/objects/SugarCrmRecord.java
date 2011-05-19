/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.plugin.objects;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.europeana.uim.sugarcrmclient.plugin.objects.data.SugarCrmField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.UpdatableField;

/**
 * 
 * 
 * @author Georgios Markakis
 */
public class SugarCrmRecord {
	
	private Element record;
	
	private SugarCrmRecord(){
	}
	
	public static SugarCrmRecord getInstance(Element el){
		SugarCrmRecord obj = new SugarCrmRecord();
		obj.setRecord(el);
		return obj;
	}

	
	public void setItemValue(UpdatableField field){
		modifyElement(field.getFieldId(), record);
	}
	
	public String getItemValue(SugarCrmField field){
		return extractFromElement(field.getFieldId(), record);
	}
	
	
	private void modifyElement(String value, Element el){
		NodeList nl =el.getElementsByTagName(value);
		
		if(nl.getLength() != 0){
			 nl.item(0).setTextContent(value);
		}	
	}
	
	private String extractFromElement(String value, Element el){
		NodeList nl =el.getElementsByTagName("item");
		
		for (int i =0; i<nl.getLength(); i++){
			Node nd = nl.item(i);
			String textcontent = nd.getChildNodes().item(0).getTextContent(); 
			if (value.equals(textcontent)){
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
