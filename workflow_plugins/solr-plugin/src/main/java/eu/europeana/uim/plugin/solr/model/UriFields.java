package eu.europeana.uim.plugin.solr.model;

/**
 * Enumeration holding the fields that should be checked for normalization
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UriFields {

	EDM_DATAPROVIDER("edm:dataProvider"),
	EDM_PROVIDER("edm:provider"),
	DC_CREATOR("dc:creator"),
	DC_CONTRIBUTOR("dc:contributor"),
	DC_DATE("dc:date"),
	DC_DESCRIPTION("dc:description"),
	DC_PUBLISHER("dc:publisher"),
	DC_RELATION("dc:relation"),
	DC_SUBJECT("dc:subject"),
	DC_TYPE("dc:type"),
	DCTERMS_CREATED("dcterms:created"),
	DCTERMS_EXTENT("dcterms:extent"),
	DCTERMS_ISSUED("dcterms:issued"),
	DCTERMS_PROVENANCE("dcterms:provenance"),
	DCTERMS_REPLACES("dcterms:replaces"),
	DCTERMS_SPATIAL("dcterms:spatial"),
	DCTERMS_TABLEOFCONTENTS("dcterms:tableOfContents"),
	DCTERMS_TEMPORAL("dcterms:temporal"),
	EDM_BROADER("edm:broader"),
	EDM_CURRENTLOCATION("edm:currentLocation"),
	DCTERMS_ISPARTOF("dcterms:isPartOf");
	
	private String uriFields;
	
	private UriFields(String uriFields){
		this.uriFields = uriFields;
	}
	
	public String toString(){
		return this.uriFields;
	}
	
}
