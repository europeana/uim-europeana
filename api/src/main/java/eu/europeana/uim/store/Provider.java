package eu.europeana.uim.store;

import java.util.List;

/*
    aggregator // ref to Aggregator obj
    name_code       // ingestion internal shorthand for Aggregator
    name            // official name of Aggreagator (for nice reports etc)
    home_page       // homepage of Aggreagator (for nice reports etc)
    country         // Country for Provider
    content_type    // enum PROVT_MUSEUM, PROVT_ARCHIVE, PROVT_LIBRARY, PROVT_AUDIO_VIS_ARCH, PROVT_AGGREGATOR

 */
public class Provider {
    // Document object, ie nested hashlist

	private List<Collection> collections;
	private List<Request> request;
	
}
