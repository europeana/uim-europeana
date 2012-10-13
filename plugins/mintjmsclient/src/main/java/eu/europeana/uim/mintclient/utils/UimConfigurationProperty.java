package eu.europeana.uim.mintclient.utils;

/**
 * UIM configuration enumeration
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum UimConfigurationProperty {

	AMPQ_USERNAME("ampq.username"),
	AMPQ_PASSWORD("ampq.password"),
	AMPQ_HOST("ampq.host"),
	AMPQ_INBOUNDQUEUE("ampq.inboundqueue"),
	AMPQ_OUTBOUNDQUEUE("ampq.outboundqueue"),
	AMPQ_RPCQUEUE("ampq.rpcqueue");
	
	String field;
	private UimConfigurationProperty(String field){
		this.field = field;
	}
	
	@Override
	public String toString(){
		return this.field;
	}
}
