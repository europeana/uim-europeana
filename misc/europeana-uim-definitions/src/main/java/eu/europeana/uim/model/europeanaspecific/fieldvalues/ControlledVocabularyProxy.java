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

import eu.europeana.uim.sugarcrm.model.SugarCrmField;
import eu.europeana.uim.store.ControlledVocabularyKeyValue;

/**
 * 
 * @author Georgios Markakis
 */
public enum ControlledVocabularyProxy implements ControlledVocabularyKeyValue {
	//System Specific
	REPOXID("repoxID"), 
	SUGARCRMID("sugarCRMID"),
	MINTID("mintID"),
	
	
	
	//Provider Specific (Existing only on Provider level)
	PROVIDERDESCRIPTION("providerDescription"),
	PROVIDERWEBSITE("providerWebsite"),
	PROVIDERTYPE("providerType"),
	PROVIDERCOUNTRY("providerCountry"),
	PROVIDERDEASENT("providerdeaSent"),
	PROVIDERDEASIGNED("providerdeaSigned"),
	//Provider Mint Specific Fields
	PROVIDERMINTUSERID("mintUserID"),
	PROVIDERMINTUSERPASSWORD("mintUserPWD"),
	PROVIDERMINTUSERFIRSTNAME("mintFirstName"),
	PROVIDERMINTUSERLASTNAME("mintLastName"),
	PROVIDERMINTPHONE("mintPhone"),
	//Collection Feilds
	MINTPUBLICATIONLOCATION("mintPubLoc"),
	LATESTMINTMAPPINGID("latestMintMapID"),
	
	//Ingestion Results
	
	
	AMOUNT(EuropeanaUpdatableField.AMOUNT), 
	TOTAL_INGESTED(EuropeanaUpdatableField.TOTAL_INGESTED), 
	INGESTED_SOUND(EuropeanaUpdatableField.INGESTED_SOUND), 
	INGESTED_VIDEO(EuropeanaUpdatableField.INGESTED_VIDEO), 
	INGESTED_TEXT(EuropeanaUpdatableField.INGESTED_TEXT), 
	INGESTED_IMAGE(EuropeanaUpdatableField.INGESTED_IMAGE), 
	PLANNED_TOTAL(EuropeanaUpdatableField.PLANNED_TOTAL),
	PLANNED_SOUND(EuropeanaUpdatableField.PLANNED_SOUND),
	PLANNED_VIDEO(EuropeanaUpdatableField.PLANNED_VIDEO),
	PLANNED_TEXT(EuropeanaUpdatableField.PLANNED_TEXT),
	PLANNED_IMAGE(EuropeanaUpdatableField.PLANNED_IMAGE),
	
	//HARVESTING INFO
	HARVEST_URL(EuropeanaUpdatableField.HARVEST_URL),
	SETSPEC(EuropeanaUpdatableField.SETSPEC),
	METADATA_FORMAT(EuropeanaUpdatableField.METADATA_FORMAT),
	METADATA_SCHEMA(EuropeanaUpdatableField.METADATA_SCHEMA),
	METADATA_NAMESPACE(EuropeanaUpdatableField.METADATA_NAMESPACE),
	HARVESTING_TYPE(EuropeanaRetrievableField.HARVESTING_TYPE),
	Z3950ADDRESS(EuropeanaUpdatableField.Z3950ADDRESS), 
	Z3950PORT(EuropeanaUpdatableField.Z3950PORT), 
	Z3950DATABASE(EuropeanaUpdatableField.Z3950DATABASE), 
	FTP_Z3950_USER(EuropeanaUpdatableField.FTP_Z3950_USER), 
	FTP_Z3950_PASSWORD(EuropeanaUpdatableField.FTP_Z3950_PASSWORD), 
	Z3950RECORD_SYNTAX(EuropeanaUpdatableField.Z3950RECORD_SYNTAX), 
	Z3950CHARSET(EuropeanaUpdatableField.Z3950CHARSET), 
	Z3950METHOD(EuropeanaUpdatableField.Z3950METHOD), 
	Z3950FILEPATH(EuropeanaUpdatableField.Z3950FILEPATH), 
	Z3950MAXIMUMID(EuropeanaUpdatableField.Z3950MAXIMUMID), 
	Z3950EARLIEST_TIMESTAMP(EuropeanaUpdatableField.Z3950EARLIEST_TIMESTAMP), 
	FTPPATH(EuropeanaUpdatableField.FTPPATH), 
	FTP_HTTP_ISOFORMAT(EuropeanaUpdatableField.FTP_HTTP_ISOFORMAT), 
	FTPSERVER(EuropeanaUpdatableField.FTPSERVER), 
	HTTPURL(EuropeanaUpdatableField.HTTPURL), 
	FOLDER(EuropeanaUpdatableField.FOLDER), 
	DATASET_COUNTRY(EuropeanaRetrievableField.DATASET_COUNTRY),
	STATUS(EuropeanaUpdatableField.STATUS), 
	NAME(EuropeanaRetrievableField.NAME),
	DATE_ENTERED(EuropeanaRetrievableField.DATE_ENTERED),
	CREATED_BY_USER(EuropeanaRetrievableField.CREATED_BY_USER),
	DESCRIPTION(EuropeanaRetrievableField.DESCRIPTION),
	DELETED(EuropeanaUpdatableField.DELETED),
	EXPECTED_INGESTION_DATE(EuropeanaRetrievableField.EXPECTED_INGESTION_DATE),
	ACRONYM(EuropeanaRetrievableField.ACRONYM),
	IDENTIFIER(EuropeanaRetrievableField.IDENTIFIER),
	ENABLED(EuropeanaUpdatableField.ENABLED),
	DATE_OF_REPLICATION(EuropeanaUpdatableField.DATE_OF_REPLICATION),
	PREVIEWS_ONLY_IN_PORTAL(EuropeanaRetrievableField.PREVIEWS_ONLY_IN_PORTAL);

	;

	private String fieldId;

	ControlledVocabularyProxy(String id) {
		this.fieldId = id;
	}

	ControlledVocabularyProxy(SugarCrmField id) {
		this.fieldId = id.getQualifiedFieldId();
	}

	@Override
	public String getFieldId() {
		return fieldId;
	}
}
