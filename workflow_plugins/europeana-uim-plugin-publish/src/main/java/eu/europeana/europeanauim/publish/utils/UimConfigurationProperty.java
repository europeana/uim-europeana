package eu.europeana.europeanauim.publish.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	
	SOLR_HOSTURL("solr.hostUrl"),
	SOLR_CORE("solr.core");
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
