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
package eu.europeana.dedup.osgi.service;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import eu.europeana.corelib.definitions.jibx.ProxyType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.jibx.RDF.Choice;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdRegistryMongoServer;
import eu.europeana.corelib.tools.lookuptable.LookupResult;
import eu.europeana.dedup.osgi.service.exceptions.DeduplicationException;
import eu.europeana.dedup.utils.Decoupler;
import eu.europeana.uim.common.BlockingInitializer;


/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 27 Sep 2012
 */
public class DeduplicationServiceImpl implements DeduplicationService {

    /**
     * Set the Logging variable to use logging within this class
     */
    private static final Logger           log       = Logger.getLogger(DeduplicationServiceImpl.class.getName());
	
	EuropeanaIdRegistryMongoServer mongoserver;
	
	
	/**
	 * 
	 */
	public DeduplicationServiceImpl() {
		
		try {
			final Mongo mongo = new Mongo();
			
	        BlockingInitializer initializer = new BlockingInitializer() {
	            @Override
	            public void initializeInternal() {
	                try {
	                    status = STATUS_BOOTING;
	                    mongoserver = new EuropeanaIdRegistryMongoServer(mongo, "EuropeanaIdRegistry");

	                    boolean test = mongoserver.oldIdExists("something");
	                    log.log(java.util.logging.Level.INFO,"OK");
	                    status = STATUS_INITIALIZED;
	                } catch (Throwable t) {
	                    log.log(java.util.logging.Level.SEVERE,
	                            "Failed to initialize Deduplication Service.", t);
	                    status = STATUS_FAILED;
	                }
	            }
	        };
	        initializer.initialize(EuropeanaIdRegistryMongoServer.class.getClassLoader());
			
			
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.dedup.osgi.service.DeduplicationService#deduplicateRecord(java.lang.String, java.lang.String)
	 */
	@Override
	public List<DeduplicationResult> deduplicateRecord(String collectionID,String sessionid,String edmRecord) throws DeduplicationException {
		
		List<DeduplicationResult> deduplist = new ArrayList<DeduplicationResult>();
		
		List<RDF> decoupledResults = Decoupler.getInstance().decouple(edmRecord);
		for(RDF result : decoupledResults){

			DeduplicationResult dedupres = new DeduplicationResult();
			dedupres.setEdm(result);
			
			
			List<Choice> choicelist = result.getChoiceList();
		
			String nonUUID = null;
			
			
			for(Choice choice : choicelist){
				if(choice.ifProxy()){
					
					ProxyType proxy = choice.getProxy();
					
					nonUUID = proxy.getAbout();
					dedupres.setDerivedRecordID(nonUUID);
					
					break;
				}
			}

			LookupResult lookup = mongoserver.lookupUiniqueId(nonUUID, collectionID, edmRecord, sessionid);
			dedupres.setLookupresult(lookup);
			deduplist.add(dedupres);
		}
		
		
		return deduplist;
	}

}
