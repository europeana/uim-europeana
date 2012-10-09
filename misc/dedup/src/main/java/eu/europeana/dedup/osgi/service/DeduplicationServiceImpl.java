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

import java.io.StringWriter;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.JiBXException;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import eu.europeana.corelib.definitions.jibx.ProxyType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.jibx.RDF.Choice;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdRegistryMongoServer;
import eu.europeana.corelib.tools.lookuptable.FailedRecord;
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
	private static final Logger log = Logger
			.getLogger(DeduplicationServiceImpl.class.getName());

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
						mongoserver = new EuropeanaIdRegistryMongoServer(mongo,
								"EuropeanaIdRegistry");

						boolean test = mongoserver.oldIdExists("something");
						log.log(java.util.logging.Level.INFO, "OK");
						status = STATUS_INITIALIZED;
					} catch (Throwable t) {
						log.log(java.util.logging.Level.SEVERE,
								"Failed to initialize Deduplication Service.",
								t);
						status = STATUS_FAILED;
					}
				}
			};
			initializer.initialize(EuropeanaIdRegistryMongoServer.class
					.getClassLoader());

		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.dedup.osgi.service.DeduplicationService#deduplicateRecord
	 * (java.lang.String, java.lang.String)
	 */
	@Override
	public List<DeduplicationResult> deduplicateRecord(String collectionID,
			String sessionid, String edmRecord) throws DeduplicationException {

		List<DeduplicationResult> deduplist = new ArrayList<DeduplicationResult>();

		List<RDF> decoupledResults = Decoupler.getInstance()
				.decouple(edmRecord);
		for (RDF result : decoupledResults) {

			DeduplicationResult dedupres = new DeduplicationResult();
			try {
				dedupres.setEdm(unmarshall(result));
			} catch (JiBXException e) {
				throw new DeduplicationException(
						"Unmarshalling of new deduplicated record failed", e);
			}

			List<Choice> choicelist = result.getChoiceList();

			String nonUUID = null;

			for (Choice choice : choicelist) {
				if (choice.ifProxy()) {
					ProxyType proxy = choice.getProxy();
					nonUUID = proxy.getAbout();
					break;
				}
			}

			LookupResult lookup = mongoserver.lookupUiniqueId(nonUUID,
					collectionID, edmRecord, sessionid);
			dedupres.setLookupresult(lookup);
			dedupres.setDerivedRecordID(lookup.getEuropeanaID());
			deduplist.add(dedupres);
		}

		return deduplist;
	}

	private String unmarshall(RDF edm) throws JiBXException {

		IBindingFactory bfact = BindingDirectory.getFactory(RDF.class);
		IMarshallingContext mctx = bfact.createMarshallingContext();
		mctx.setIndent(2);
		StringWriter stringWriter = new StringWriter();
		mctx.setOutput(stringWriter);
		mctx.marshalDocument(edm);
		String edmstring = stringWriter.toString();

		return edmstring;

	}

	@Override
	public List<FailedRecord> getFailedRecords(String collectionId) {
		return mongoserver.getDatastore().find(FailedRecord.class)
				.filter("collectionId", collectionId).asList();
	}

}
