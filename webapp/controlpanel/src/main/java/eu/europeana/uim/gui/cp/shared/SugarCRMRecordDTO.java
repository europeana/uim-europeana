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
package eu.europeana.uim.gui.cp.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * SugarCRMRecordDTO represents a client side (translatable to javascript)
 * object in GWT. It represents and contains information about a SugarCRM record.
 * 
 * @author Georgios Markakis
 */
public class SugarCRMRecordDTO implements IsSerializable,Comparable<SugarCRMRecordDTO> {


	private String id;
	private String importedIMG;
	private String name;
	private String assigned_user_name;
	private String organization_name;
	private String expected_ingestion_date;
	private String country_c;
	private String ingested_total_c;
	private String status;

	

	
	
	@Override
	public int compareTo(SugarCRMRecordDTO arg0) {

		return 0;
	}




	/**
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}


	/**
	 * @return the id
	 */
	public String getId() {
		return id;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param assigned_user_name the assigned_user_name to set
	 */
	public void setAssigned_user_name(String assigned_user_name) {
		this.assigned_user_name = assigned_user_name;
	}




	/**
	 * @return the assigned_user_name
	 */
	public String getAssigned_user_name() {
		return assigned_user_name;
	}




	/**
	 * @param organization_name the organization_name to set
	 */
	public void setOrganization_name(String organization_name) {
		this.organization_name = organization_name;
	}




	/**
	 * @return the organization_name
	 */
	public String getOrganization_name() {
		return organization_name;
	}



	/**
	 * @param expected_ingestion_date the expected_ingestion_date to set
	 */
	public void setExpected_ingestion_date(String expected_ingestion_date) {
		this.expected_ingestion_date = expected_ingestion_date;
	}




	/**
	 * @return the expected_ingestion_date
	 */
	public String getExpected_ingestion_date() {
		return expected_ingestion_date;
	}



	/**
	 * @param country_c the country_c to set
	 */
	public void setCountry_c(String country_c) {
		this.country_c = country_c;
	}




	/**
	 * @return the country_c
	 */
	public String getCountry_c() {
		return country_c;
	}




	/**
	 * @param ingested_total_c the ingested_total_c to set
	 */
	public void setIngested_total_c(String ingested_total_c) {
		this.ingested_total_c = ingested_total_c;
	}




	/**
	 * @return the ingested_total_c
	 */
	public String getIngested_total_c() {
		return ingested_total_c;
	}



	/**
	 * @param status the status to set
	 */
	public void setStatus(String status) {
		this.status = status;
	}




	/**
	 * @return the status
	 */
	public String getStatus() {
		return status;
	}


	/**
	 * @param importedIMG the importedIMG to set
	 */
	public void setImportedIMG(String importedIMG) {
		this.importedIMG = importedIMG;
	}




	/**
	 * @return the importedIMG
	 */
	public String getImportedIMG() {
		return importedIMG;
	}













}
