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
import org.theeuropeanlibrary.model.common.qualifier.LinkStatus;

import eu.europeana.uim.api.LoggingEngine;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.corelib.definitions.jibx.Aggregation;
import eu.europeana.corelib.definitions.jibx.HasView;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.uim.model.europeana.EuropeanaLink;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
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
	 * Returns the next batch of metadata records to the orchestrator
	 * 
	 * @param <I> the type of the Metadata Record ID
	 * @param batchSize
	 *            number of loaded records
	 * @param save
	 *            Should they be saved to the index?
	 * @return list of loaded Metadata records
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

				
				MetaDataRecord<I> mdr = storage.getMetaDataRecord(uuid);

				if (mdr == null) {
					mdr = storage.createMetaDataRecord(request.getCollection(),
							uuid.toString());
				}
				else{
					//Remove the previous ingestion date
					mdr.deleteValues(EuropeanaModelRegistry.UIMINGESTIONDATE);
					//Remove the previous instances of EDM Records
					mdr.deleteValues(EuropeanaModelRegistry.EDMRECORD);
					//Remove the previous registered links
					mdr.deleteValues(EuropeanaModelRegistry.EUROPEANALINK);
				}

				mdr.addValue(EuropeanaModelRegistry.UIMINGESTIONDATE,
						new Date().toString());
				
				mdr.addValue(EuropeanaModelRegistry.EDMRECORD, rdfstring);

				//Add Links to be checked values here 
				///addLinkcheckingValues(validedmrecord,mdr);
				
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
	 * Extracts the locations of links from the imported JIBX representation of EDM
	 * and registers the appropriate values
	 * 
	 * @param validedmrecord
	 */
	private <I> void addLinkcheckingValues(RDF validedmrecord,MetaDataRecord<I> mdr ){
		
		Aggregation aggregation = validedmrecord.getChoiceList().get(0).getAggregation();

		if(aggregation != null){
			
			List<HasView> has_views = aggregation.getHasViewList();
			
			for(HasView view : has_views){
				String resource = view.getResource();
				EuropeanaLink link = new EuropeanaLink();
				link.setCacheable(true);
				link.setLinkStatus(LinkStatus.NOT_CHECKED);
				link.setUrl(resource);
				mdr.addValue(EuropeanaModelRegistry.EUROPEANALINK, link);
			}
		}
		
		List<HasView> edm_has_view = validedmrecord.getChoiceList().get(0).getAggregation().getHasViewList();
		
		for(HasView view : edm_has_view){
			String hasView = view.getResource();
			EuropeanaLink link = new EuropeanaLink();
			link.setCacheable(false);
			link.setLinkStatus(LinkStatus.NOT_CHECKED);
			link.setUrl(hasView);
			mdr.addValue(EuropeanaModelRegistry.EUROPEANALINK, link);
		}
		
		if(aggregation.getIsShownAt() != null){
			String isShownAt = aggregation.getIsShownAt().getResource();
			EuropeanaLink link = new EuropeanaLink();
			link.setCacheable(false);
			link.setLinkStatus(LinkStatus.NOT_CHECKED);
			link.setUrl(isShownAt);
			mdr.addValue(EuropeanaModelRegistry.EUROPEANALINK, link);
		}
		
		if(aggregation.getIsShownBy() != null){
			String isShownBy = aggregation.getIsShownBy().getResource();
			EuropeanaLink link = new EuropeanaLink();
			link.setCacheable(false);
			link.setLinkStatus(LinkStatus.NOT_CHECKED);
			link.setUrl(isShownBy);
			mdr.addValue(EuropeanaModelRegistry.EUROPEANALINK, link);
		}
		
		if(aggregation.getObject() != null){
			String theObject = aggregation.getObject().getResource();
			EuropeanaLink link = new EuropeanaLink();
			link.setCacheable(false);
			link.setLinkStatus(LinkStatus.NOT_CHECKED);
			link.setUrl(theObject);
			mdr.addValue(EuropeanaModelRegistry.EUROPEANALINK, link);
		}
		
		
		if(validedmrecord.getChoiceList().get(0).getWebResource() != null){
		   String about = validedmrecord.getChoiceList().get(0).getWebResource().getAbout();
			EuropeanaLink link = new EuropeanaLink();
			link.setCacheable(false);
			link.setLinkStatus(LinkStatus.NOT_CHECKED);
			link.setUrl(about);
			mdr.addValue(EuropeanaModelRegistry.EUROPEANALINK, link);
		}
		
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
