package eu.europeana.uim.gui.cp.shared.validation;

import java.util.List;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * Class representing the result of fields from Solr or Mongo
 * @author gmamakis
 *
 */
public class EdmFieldRecordDTO implements IsSerializable
{
	List<FieldValueDTO> fieldValue;

	public List<FieldValueDTO> getFieldValue() {
		return fieldValue;
	}

	public void setFieldValue(List<FieldValueDTO> fieldValue) {
		this.fieldValue = fieldValue;
	}
	
}
