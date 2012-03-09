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
package eu.europeana.uim.repoxclient.utils;

/**
 * Repox DataSet Types (Provider Attributes)
 * 
 * @author Georgios Markakis
 */

public enum DataSetType {
	MUSEUM("Museum/Gallery"),
	ARCHIVE("Archive"),
	LIBRARY("Library"),
	AUDIO_VISUAL_ARCHIVE("Audio Visual"),
	RESEARCH_EDUCATIONAL("Research and Educational"),
	CROSS_SECTOR("Cross-sector portal"),
	PUBLISHER("Publisher"),
	PRIVATE("Private"),
	AGGREGATOR("Aggregator"),
	UNKNOWN("Other/None");
	
	private String sugarName;
	
	DataSetType(String sugarName){
	   this.sugarName = sugarName;
	}

	/**
	 * @param sugarName the sugarName to set
	 */
	public void setSugarName(String sugarName) {
		this.sugarName = sugarName;
	}

	/**
	 * @return the sugarName
	 */
	public String getSugarName() {
		return sugarName;
	}
	

}
