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
package eu.europeana.uim.mintclient.utils;

/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 22 Mar 2012
 */
public enum AMPQOperations {

	CreateOrganizationAction("eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationAction"),
	CreateUserAction("eu.europeana.uim.mintclient.jibxbindings.CreateUserAction"),
	CreateImportAction("eu.europeana.uim.mintclient.jibxbindings.CreateImportAction"),
	GetImportsAction("eu.europeana.uim.mintclient.jibxbindings.GetImportsAction"),
	GetTransformationsAction("eu.europeana.uim.mintclient.jibxbindings.GetTransformationsAction"),
	PublicationAction("eu.europeana.uim.mintclient.jibxbindings.PublicationAction"),
	ImportExistsAction("eu.europeana.uim.mintclient.jibxbindings.ImportExistsAction"),
	UserExistsAction("eu.europeana.uim.mintclient.jibxbindings.UserExistsAction"),
	OrganizationExistsAction("eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsAction")
	;

	
	private final String sysId;
	
	 AMPQOperations(String sysId){
		this.sysId = sysId;
	}

	/**
	 * @return the sysId
	 */
	public String getSysId() {
		return sysId;
	}
}
