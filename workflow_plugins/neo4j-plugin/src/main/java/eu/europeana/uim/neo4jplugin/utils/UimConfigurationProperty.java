package eu.europeana.uim.neo4jplugin.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	
        NEO4JPATH("neo4j.path"),
        NEO4JINDEX("neo4j.index");
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
