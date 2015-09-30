/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.dedup.osgi.service;


import eu.europeana.corelib.tools.lookuptable.LookupResult;
import eu.europeana.corelib.definitions.jibx.RDF;


/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 27 Sep 2012
 */
public class DeduplicationResult {

	private String originalRecordID;
	
	private String derivedRecordID;
	
	private LookupResult lookupresult; 
	
	private String edm;
	
	// caches the final inmarshalled EDM (after updateInternalReferences) for later use so as to avoid inmarshalling again..
	private RDF unmarshalledEdm;
	

	
	/**
	 * 
	 */
	public DeduplicationResult() {
	}

	/**
	 * @return the originalRecordID
	 */
	public String getOriginalRecordID() {
		return originalRecordID;
	}

	/**
	 * @param originalRecordID the originalRecordID to set
	 */
	public void setOriginalRecordID(String originalRecordID) {
		this.originalRecordID = originalRecordID;
	}

	/**
	 * @return the derivedRecordID
	 */
	public String getDerivedRecordID() {
		return derivedRecordID;
	}

	/**
	 * @param derivedRecordID the derivedRecordID to set
	 */
	public void setDerivedRecordID(String derivedRecordID) {
		this.derivedRecordID = derivedRecordID;
	}

	/**
	 * @return the lookupresult
	 */
	public LookupResult getLookupresult() {
		return lookupresult;
	}

	/**
	 * @param lookupresult the lookupresult to set
	 */
	public void setLookupresult(LookupResult lookupresult) {
		this.lookupresult = lookupresult;
	}

	/**
	 * @return the edm
	 */
	public String getEdm() {
		return edm;
	}

	/**
	 * @param edm the edm to set
	 */
	public void setEdm(String edm) {
		this.edm = edm;
	}

	public RDF getUnmarshalledEdm() {
		return unmarshalledEdm;
	}

	
	public void setUnmarshalledEdm(RDF unmarshalledEdm) {
		this.unmarshalledEdm = unmarshalledEdm;
	}

}
