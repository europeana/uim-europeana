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

package eu.europeana.uim.europeanaspecific.workflowstarts.httpzip;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import org.jibx.runtime.JiBXException;
import eu.europeana.uim.api.LoggingEngine;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.uim.model.europeanaspecific.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeanaspecific.utils.DefUtils;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.Request;

/**
 * This class uses the private zipiterator in order to batch load records into
 * the storage engine
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 5 Mar 2012
 */
public class ZipLoader {

	@SuppressWarnings("rawtypes")
	private StorageEngine storage;

	private LoggingEngine<?> loggingEngine;
	@SuppressWarnings("rawtypes")
	private Request request;
	@SuppressWarnings("unused")
	private ProgressMonitor monitor;
	private Iterator<String> zipiterator;

	private int totalProgress = 0;
	private int expectedRecords = 0;

	/**
	 * Default constructor for this class
	 * 
	 * @param expectedRecords
	 *            The number of expected records
	 * @param zipiterator
	 *            An iterator over the parsed files
	 * @param storage
	 *            A reference to the storage engine
	 * @param request
	 *            A reference to the current request
	 * @param monitor
	 *            A reference to the current monitor
	 * @param loggingEngine
	 *            A reference to the logging engine
	 */
	public <I> ZipLoader(int expectedRecords, Iterator<String> zipiterator,
			StorageEngine<I> storage, Request<I> request,
			ProgressMonitor monitor, LoggingEngine<I> loggingEngine) {
		super();
		this.expectedRecords = expectedRecords;
		this.zipiterator = zipiterator;
		this.storage = storage;
		this.request = request;
		this.monitor = monitor;
		this.loggingEngine = loggingEngine;
	}

	/**
	 * @param <I>
	 * @param batchSize
	 *            number of loaded records
	 * @param save
	 *            Should they be saved to the index?
	 * @return list of loaded MARC records
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public synchronized <I> List<MetaDataRecord<I>> doNext(int batchSize,
			boolean save) {
		List<MetaDataRecord<I>> result = new ArrayList<MetaDataRecord<I>>();
		int progress = 0;

		while (zipiterator.hasNext()) {

			if (progress >= batchSize) {
				break;
			}

			String rdfstring = zipiterator.next();

			RDF validedmrecord;
			try {
				validedmrecord = DefUtils
						.unmarshallObject(rdfstring, RDF.class);
				I uuid = (I) validedmrecord.getChoiceList().get(0)
						.getProvidedCHO().getAbout();

				//loggingEngine.log(Level.INFO, "ZipLoader",
				//		"Added record with id", uuid.toString(),
				//		" in collection ", request.getCollection()
				//				.getMnemonic());

				MetaDataRecord<I> mdr = storage.getMetaDataRecord(uuid);

				if (mdr == null) {
					mdr = storage.createMetaDataRecord(request.getCollection(),
							uuid.toString());
				}

				mdr.addValue(EuropeanaModelRegistry.UIMINGESTIONDATE,
						new Date().toString());
				mdr.addValue(EuropeanaModelRegistry.EDMRECORD, rdfstring);

				storage.updateMetaDataRecord(mdr);

				result.add(mdr);
				storage.addRequestRecord(request, mdr);

				progress++;
				totalProgress++;
			} catch (JiBXException e) {

				if (loggingEngine != null) {

					loggingEngine.logFailed(Level.SEVERE, "ZipLoader", e,
							"Error unmarshalling xml for object ");
				}
			} catch (StorageEngineException e) {
				if (loggingEngine != null) {
					loggingEngine.logFailed(Level.SEVERE, "ZipLoader", e,
							"Error storing object ");
				}
			}
		}
		return result;
	}

	/**
	 * Returns the loading state
	 * 
	 * @return the loading state
	 */
	public boolean isFinished() {
		return !zipiterator.hasNext();
	}

	/**
	 * Returns the expected records
	 * 
	 * @return the expected records
	 */
	public int getExpectedRecordCount() {

		return this.expectedRecords;
	}

	/**
	 * Finalizes the current object and make its fields eligible for garbage
	 * collection
	 */
	public void close() {
		this.expectedRecords = 0;
		this.zipiterator = null;
		this.storage = null;
		this.request = null;
		this.monitor = null;
		this.loggingEngine = null;
	}

}
