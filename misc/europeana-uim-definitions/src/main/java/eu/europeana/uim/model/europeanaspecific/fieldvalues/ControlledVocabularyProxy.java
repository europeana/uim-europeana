/**

 * 
 */
package eu.europeana.uim.model.europeanaspecific.fieldvalues;

import eu.europeana.uim.sugarcrm.model.SugarCrmField;
import eu.europeana.uim.store.ControlledVocabularyKeyValue;

/**
 * 
 * @author geomark
 */
public enum ControlledVocabularyProxy implements ControlledVocabularyKeyValue{
  REPOXID("repoxID"),
  SUGARCRMID("sugarCRMID"),
  AMOUNT(EuropeanaUpdatableField.AMOUNT),
	TOTAL_INGESTED(EuropeanaUpdatableField.TOTAL_INGESTED),
	INGESTED_SOUND(EuropeanaUpdatableField.INGESTED_SOUND),
	INGESTED_VIDEO(EuropeanaUpdatableField.INGESTED_VIDEO),
	INGESTED_TEXT(EuropeanaUpdatableField.INGESTED_TEXT),
	INGESTED_IMAGE(EuropeanaUpdatableField.INGESTED_IMAGE),
	ADDRESS(EuropeanaUpdatableField.ADDRESS),
	PORT(EuropeanaUpdatableField.PORT),
	DATABASE(EuropeanaUpdatableField.DATABASE),
	USER(EuropeanaUpdatableField.USER),
	PASSWORD(EuropeanaUpdatableField.PASSWORD),
	RECORD_SYNTAX(EuropeanaUpdatableField.RECORD_SYNTAX),
	CHARSET(EuropeanaUpdatableField.CHARSET),
	Z3950METHOD(EuropeanaUpdatableField.Z3950METHOD),
	FILEPATH(EuropeanaUpdatableField.FILEPATH),
	MAXIMUMID(EuropeanaUpdatableField.MAXIMUMID),
	EARLIEST_TIMESTAMP(EuropeanaUpdatableField.EARLIEST_TIMESTAMP),
	FTPPATH(EuropeanaUpdatableField.FTPPATH),
	ISOFORMAT(EuropeanaUpdatableField.ISOFORMAT),
	SERVER(EuropeanaUpdatableField.SERVER),
	HTTPURL(EuropeanaUpdatableField.HTTPURL),
	FOLDER(EuropeanaUpdatableField.FOLDER),
	NEXT_STEP(EuropeanaUpdatableField.NEXT_STEP),
	STATUS(EuropeanaUpdatableField.STATUS),
	TYPE(EuropeanaUpdatableField.TYPE) ;
	
  
    private String id;
  
	
	ControlledVocabularyProxy(String id){
		this.id = id;
	}
	
	
	ControlledVocabularyProxy(SugarCrmField id){
		this.id = id.getQualifiedFieldId();
	}


	@Override
	public String getFieldId() {
		return id;
	}
}
