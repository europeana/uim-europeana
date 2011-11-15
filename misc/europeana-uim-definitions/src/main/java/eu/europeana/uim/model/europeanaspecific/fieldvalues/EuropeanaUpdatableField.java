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
package eu.europeana.uim.model.europeanaspecific.fieldvalues;

import eu.europeana.uim.store.ControlledVocabularyKeyValue;
import eu.europeana.uim.sugarcrm.model.UpdatableField;


/**
 *  This enumeration indicates the Fields that can be updated
 *  by the SugarCRM client. 
 *  
 * @author Georgios Markakis
 * @author Yorgos Mamakis
 */
public enum EuropeanaUpdatableField implements UpdatableField{
	AMOUNT("amount","amount","Amount"),
	TOTAL_INGESTED("ingested_total_c","ingested_total_c","Ingested Total"),
	INGESTED_SOUND("ingested_sound_c","ingested_sound_c","Ingested Sound"),
	INGESTED_VIDEO("ingested_video_c","ingested_video_c","Ingested Video"),
	INGESTED_TEXT("ingested_text_c","ingested_text_c","Ingested Text"),
	INGESTED_IMAGE("ingested_image_c","ingested_image_c","Ingested Images"),
	ADDRESS("address_c", "address_c", "Address"),
	PORT("port_c","port_c", "Port"),
	DATABASE("database_c","database_c", "Database"),
	USER("user_c","user_c","User"),
	PASSWORD("password_c","password_c", "Password"),
	RECORD_SYNTAX("record_syntax_c","record_syntax_c", "Record Syntax"),
	CHARSET("charset_c","charset_c", "Charset"),
	Z3950METHOD("z39_50_method_c","z39_50_method_c", "Z3950 Method"),
	FILEPATH("filepath_c","filepath_c","File Path"),
	MAXIMUMID("maximumid_c", "maximumid_c", "Maximum ID"),
	EARLIEST_TIMESTAMP("earliest_timestamp_c","earliest_timestamp_c", "Earliest Timestamp"),
	FTPPATH("ftppath_c","ftppath_c", "FTP Path"),
	ISOFORMAT("isoformat_c","isoformat_c", "ISO Format"),
	SERVER("server_c","server_c", "Server"),
	HTTPURL("http_url_c","http_url_c", "HTTP URL"),
	FOLDER("folder_c","folder_c","Folder"),
	NEXT_STEP("next_step","next_step","Next Step"),
	STATUS("sales_stage","sales_stage","sales_stage"),
	TYPE("opportunity_type","opportunity_type","Type"),
	;
	
	private final String fieldId;
	private final String qualifiedFieldId;
	private final String description;	
	
	EuropeanaUpdatableField(String fieldId,String qualifiedFieldId, String description ){
		this.fieldId = fieldId;
		this.qualifiedFieldId=qualifiedFieldId;
		this.description = description;
	}

	@Override
    public String getFieldId() {
		return fieldId;
	}

	@Override
    public String getDescription() {
		return description;
	}

    @Override
    public String getQualifiedFieldId() {
      return qualifiedFieldId;
    }
}
