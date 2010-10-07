package eu.europeana.uim.store;



/*
    provider    // ref to Provider obj
    name_code   // ingestion internal shorthand for Aggregator
    name        // official name of Aggreagator (for nice reports etc)
    home_page   // homepage of Aggreagator (for nice reports etc)
    language    // Primary language for collection (used as default for all fields if not given)

 */
public interface Collection {
	
	public long getId();
	public String getName();
	public Provider getProvider();
}
