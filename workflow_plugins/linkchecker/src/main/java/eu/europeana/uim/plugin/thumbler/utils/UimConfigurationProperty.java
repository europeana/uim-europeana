package eu.europeana.uim.plugin.thumbler.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	CLIENT_HOSTURL("harvester.hostUrl"),
	CLIENT_HOSTPORT("harvester.hostPort"),
        CLIENT_DB("harvester.db")
        ;
	
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
