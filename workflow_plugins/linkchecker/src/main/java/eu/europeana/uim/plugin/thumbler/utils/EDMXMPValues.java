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
package eu.europeana.uim.plugin.thumbler.utils;


/**
 * @author Georgios Markakis <gwarkx@hotmail.com>
 *
 * 11 Jul 2012
 */
public enum  EDMXMPValues {
	
	dc_title("xmp-dc:Title"),// dc:title (with xml:lang attribute set to "x-default") from dc:title
	// dc:rights from edm:rights
	dc_rights("xmp-dc:Rights"),
	// cc:attributionName from dc:creator
	cc_attributionName("xmp-cc:attributionName"),
	// dc:rights from edm:rights
	edm_rights("xmp-dc:Rights"),
	// edm:dataProvider from europeana:dataProvider
	edm_dataProvider("xmp-edm:DataProvider"),
	// edm:provider from europeana:provider
	edm_provider("xmp-edm:Provider"),
	// xmpRights:Marked from europeana:rights: "False" if europeana:rights
	// is http://creativecommons.org/publicdomain/mark/1.0/ or
	// http://creativecommons.org/publicdomain/zero/1.0/, "True" otherwise.
	xmpRights_Marked("xmp-xmpRights:Marked"),
	// xmpRights:WebStatement from europeana:isShownAt
	xmpRights_WebStatement("xmp-xmpRights:WebStatement"),
	// cc:morePermissions from europeana:isShownAt (as a value for the
	// rdf:resource attribute)
	cc_morePermissions("xmp-cc:morePermissions"),
	// xmpMM:OriginalDocumentID from europeana:object
	xmpMM_OriginalDocumentID("xmp-xmpMM:OriginalDocumentID"),
	// cc:useGuidelines with http://www.europeana.eu/rights/pd-usage-guide/
	// (as a value for the rdf:resource attribute) if europeana:rights is
	// http://creativecommons.org/publicdomain/mark/1.0/ or
	// http://creativecommons.org/publicdomain/zero/
	cc_useGuidelines("xmp-cc:useGuidelines");
	
	
	private String fieldId;
	
	EDMXMPValues(String id) {
		this.setFieldId(id);
	}

	public String getFieldId() {
		return fieldId;
	}

	public void setFieldId(String fieldId) {
		this.fieldId = fieldId;
	}


}
