package eu.europeana.uim.gui.cp.shared.validation;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class representing a field-value(s) pair as it is stored in Mongo and Solr
 * 
 * @author Yorgos.Mamakis@kb.nl
 *
 */
public class FieldValueDTO implements IsSerializable {

	// Field name
	private String fieldName;
	// Field value
	private List<String> fieldValue;
	
	public String getFieldName() {
		return fieldName;
	}
	public void setFieldName(String fieldName) {
		this.fieldName = fieldName;
	}
	public List<String> getFieldValue() {
		return fieldValue;
	}
	public void setFieldValue(List<String> fieldValue) {
		this.fieldValue = fieldValue;
	}
	
	
}
