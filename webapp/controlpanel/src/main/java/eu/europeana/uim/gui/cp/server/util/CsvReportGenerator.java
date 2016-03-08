package eu.europeana.uim.gui.cp.server.util;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;

import eu.europeana.harvester.client.HarvesterClient;
import eu.europeana.harvester.client.HarvesterClientConfig;
import eu.europeana.harvester.client.HarvesterClientImpl;
import eu.europeana.harvester.domain.DocumentReferenceTaskType;
import eu.europeana.harvester.domain.LastSourceDocumentProcessingStatistics;
import eu.europeana.harvester.domain.ProcessingJobSubTaskState;
import eu.europeana.harvester.domain.ProcessingJobSubTaskStats;
import eu.europeana.harvester.domain.ProcessingState;
import eu.europeana.harvester.domain.SourceDocumentReference;
import eu.europeana.harvester.domain.URLSourceType;

public class CsvReportGenerator {
	
	private static final String COLLECTION_NAME = "Collection name: ";
	
//	private static final String LINK_CHECKING = "Link checking / ";
//	private static final String METADATA_EXTRACTION = "Metadata extraction / ";
//	private static final String PREVIEW_CACHING = "Preview caching / ";
	
	private static final String EDM_OBJECT = "edm:object";
	private static final String EDM_ISSHOWNBY = "edm:isShownBy";
	private static final String EDM_ISSHOWNAT = "edm:isShownAt";
	private static final String EDM_HASVIEW = "emd:hasView";
	
    private static Datastore datastore;
	
	private static HarvesterClient client;

	private static OutputStreamWriter writer;

	private static Statistics statistics;
	
	static {
		try {
			//connect to Mongo
			//TODO need host, port and DB name
			Mongo mongo = new MongoClient("", 0);
			Morphia morphia = new Morphia();
			datastore = morphia.createDatastore(mongo, "europeana");
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (Exception any) {
			any.printStackTrace();
		}
		HarvesterClientConfig config = new HarvesterClientConfig();
		client = new HarvesterClientImpl(datastore, config);
	}

	
	public static void generateReport (OutputStream os) {
		//FIXME remove stub, use generateStatistics() instead!
		statistics = Stub.getStatistics();		
		writer = new OutputStreamWriter(os);
		try {
			writer.write(COLLECTION_NAME);
			writer.write(Stub.collection_name + "\n");
			writer.write("\n");
			///////////////////////////////////////////////////
			writeStatisticsItem(StatisticsType.LINK_CHECKING, URLSourceType.OBJECT);
			writeStatisticsItem(StatisticsType.LINK_CHECKING, URLSourceType.ISSHOWNBY);
			writeStatisticsItem(StatisticsType.LINK_CHECKING, URLSourceType.ISSHOWNAT);
			writeStatisticsItem(StatisticsType.LINK_CHECKING, URLSourceType.HASVIEW);
			
			writeStatisticsItem(StatisticsType.METADATA_EXTRACTION, URLSourceType.OBJECT);
			writeStatisticsItem(StatisticsType.METADATA_EXTRACTION, URLSourceType.ISSHOWNBY);
			writeStatisticsItem(StatisticsType.METADATA_EXTRACTION, URLSourceType.ISSHOWNAT);
			writeStatisticsItem(StatisticsType.METADATA_EXTRACTION, URLSourceType.HASVIEW);
			
			writeStatisticsItem(StatisticsType.PREVIEW_CACHING, URLSourceType.OBJECT);
			writeStatisticsItem(StatisticsType.PREVIEW_CACHING, URLSourceType.ISSHOWNBY);			
			writer.flush();
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private static void writeStatisticsItem(StatisticsType stType, URLSourceType urlType) throws IOException {
		writer.write(stType.getTitle() + urlType.name() + "\n");
		for (StatisticsItem item : statistics.getStatisticsItemsByType(stType)) {
			for (StatisticsEntry entry : item.getStatisticsEntriesByUrlType(urlType)) {
				writer.write(entry.getKey() + " : " + entry.getValue() + "\n");
			}
		}
		writer.write("\n");
	}
	
	/**
	 * 
	 * @param executionId
	 * @param collectionId
	 * @return
	 */
	private static Statistics generateStatistics(String executionId, String collectionId) {
		Statistics statistics = new Statistics();

		// For link checking
		List<LastSourceDocumentProcessingStatistics> findLastSourceDocumentProcessingStatistics = 
				client.findLastSourceDocumentProcessingStatistics(collectionId, executionId, Arrays.asList(ProcessingState.ERROR, ProcessingState.FAILED));		
		
		Map<String, LastSourceDocumentProcessingStatistics> linkCheckStats = new HashMap<String, LastSourceDocumentProcessingStatistics>();		
		Map<String, LastSourceDocumentProcessingStatistics> metaExtractStats = new HashMap<String, LastSourceDocumentProcessingStatistics>();
		Map<String, LastSourceDocumentProcessingStatistics> previewCachingStats = new HashMap<String, LastSourceDocumentProcessingStatistics>();
		
		for(LastSourceDocumentProcessingStatistics st : findLastSourceDocumentProcessingStatistics) {
			// link check
			if (st.getTaskType() == DocumentReferenceTaskType.CHECK_LINK) {
				linkCheckStats.put(st.getSourceDocumentReferenceId(), st);									
			}
			
			ProcessingJobSubTaskStats processingJobSubTaskStats = st.getProcessingJobSubTaskStats();
			// metadata extraction
			ProcessingJobSubTaskState metaExtractionState = processingJobSubTaskStats.getMetaExtractionState();
			if (metaExtractionState == ProcessingJobSubTaskState.FAILED || metaExtractionState == ProcessingJobSubTaskState.ERROR) {
				metaExtractStats.put(st.getSourceDocumentReferenceId(), st);
			}
			// preview caching
			ProcessingJobSubTaskState thumbGenerationState = processingJobSubTaskStats.getThumbnailGenerationState();
			if (thumbGenerationState == ProcessingJobSubTaskState.FAILED || metaExtractionState == ProcessingJobSubTaskState.ERROR) {
				previewCachingStats.put(st.getSourceDocumentReferenceId(), st);
			}
 		}
		
		// link check
		Map<String, String> linkChecUris = new HashMap<String, String>(); 
		List<SourceDocumentReference> linkCheckSourceDocRefById = client.retrieveSourceDocumentReferencesByIds(new ArrayList<String>(linkCheckStats.keySet()));
		for (SourceDocumentReference reference : linkCheckSourceDocRefById) {
			linkChecUris.put(reference.getId(), reference.getUrl());
		}
		StatisticsItem linkCheckItem = new StatisticsItem();
		for (String id : linkCheckStats.keySet()) {
			LastSourceDocumentProcessingStatistics stats = linkCheckStats.get(id);
			linkCheckItem.addStatisticsEntryByURLtype(stats.getUrlSourceType(), new StatisticsEntry(stats.getHttpResponseCode(), linkChecUris.get(id)));
		}
		statistics.addStatisticsItemByType(StatisticsType.LINK_CHECKING, linkCheckItem);
		
		// meatdata extraction
		Map<String, String> metaExtractUris = new HashMap<String, String>(); 
		List<SourceDocumentReference> metaExtrSourceDocRefById = client.retrieveSourceDocumentReferencesByIds(new ArrayList<String>(metaExtractStats.keySet()));
		for (SourceDocumentReference reference : metaExtrSourceDocRefById) {
			metaExtractUris.put(reference.getId(), reference.getUrl());
		}
		StatisticsItem metaExtractionItem = new StatisticsItem();
		for (String id : metaExtractStats.keySet()) {
			LastSourceDocumentProcessingStatistics stats = metaExtractStats.get(id);
			metaExtractionItem.addStatisticsEntryByURLtype(stats.getUrlSourceType(), new StatisticsEntry(stats.getHttpResponseCode(), metaExtractUris.get(id)));
		}
		statistics.addStatisticsItemByType(StatisticsType.METADATA_EXTRACTION, metaExtractionItem);
		
		// preview caching
		Map<String, String> previewCachingUris = new HashMap<String, String>(); 
		List<SourceDocumentReference> thumbGenerationSourceDocRefById = client.retrieveSourceDocumentReferencesByIds(new ArrayList<String>(previewCachingStats.keySet()));
		for (SourceDocumentReference reference : thumbGenerationSourceDocRefById) {
			previewCachingUris.put(reference.getId(), reference.getUrl());
		}
		StatisticsItem thumbnailGenerationItem = new StatisticsItem();
		for (String id : previewCachingStats.keySet()) {
			LastSourceDocumentProcessingStatistics stats = previewCachingStats.get(id);
			thumbnailGenerationItem.addStatisticsEntryByURLtype(stats.getUrlSourceType(), new StatisticsEntry(stats.getHttpResponseCode(), previewCachingUris.get(id)));
		}
		statistics.addStatisticsItemByType(StatisticsType.PREVIEW_CACHING, thumbnailGenerationItem);
		
		return statistics;
	}
	 
	 public static String getFileName() {
		 return Stub.collection_name + "_error_log.txt";
	 }
	
	 private static class Statistics {
		private String collection_name;

		private final List<StatisticsItem>  link_check = new ArrayList<StatisticsItem>();
		private final List<StatisticsItem>  metadata_extract = new ArrayList<StatisticsItem>();
		private final List<StatisticsItem>  preview_cache = new ArrayList<StatisticsItem>();
		 
		public String getCollection_name() {
			return collection_name;
		}
		
		public void setCollection_name(String collection_name) {
			this.collection_name = collection_name;
		}
		
		public void addStatisticsItemByType(StatisticsType type, StatisticsItem item) {
			switch (type) {
			case LINK_CHECKING:
				link_check.add(item);
				break;
			case METADATA_EXTRACTION:
				metadata_extract.add(item);
				break;
			case PREVIEW_CACHING:
				preview_cache.add(item);
				break;
			}
		}
		
		public List<StatisticsItem> getStatisticsItemsByType(StatisticsType type) {
			switch (type) {
			case LINK_CHECKING:
				return link_check;
			case METADATA_EXTRACTION:
				return metadata_extract;
			case PREVIEW_CACHING:
				return preview_cache;
			}
			return null;
		}
	 }
	 
	 private static class StatisticsItem {
	 
		 private List<StatisticsEntry> errorCodesAndUrisForObject = new ArrayList<StatisticsEntry>();
		 
		 private List<StatisticsEntry> errorCodesAndUrisForIsShownBy = new ArrayList<StatisticsEntry>();
		 
		 private List<StatisticsEntry> errorCodesAndUrisForIsShownAt = new ArrayList<StatisticsEntry>();
		 
		 private List<StatisticsEntry> errorCodesAndUrisForHasView = new ArrayList<StatisticsEntry>();

		 public void addStatisticsEntryByURLtype(URLSourceType type, StatisticsEntry entry) {
			 switch (type) {
			 case OBJECT:
				 errorCodesAndUrisForObject.add(entry);
				 break;
			 case ISSHOWNBY:
				 errorCodesAndUrisForIsShownBy.add(entry);
				 break;
			 case ISSHOWNAT:
				 errorCodesAndUrisForIsShownAt.add(entry);
				 break;
			 case HASVIEW:
				 errorCodesAndUrisForHasView.add(entry);
				 break;
			 }
		 }
		 
		 public List<StatisticsEntry> getStatisticsEntriesByUrlType(URLSourceType type) {
			 switch (type) {
			 case OBJECT:
				 return errorCodesAndUrisForObject;
			 case ISSHOWNBY:
				 return errorCodesAndUrisForIsShownBy;
			 case ISSHOWNAT:
				 return errorCodesAndUrisForIsShownAt;
			 case HASVIEW:
				 return errorCodesAndUrisForHasView;
			 }
			 return  null;
		 }
	 }
	 
	 private static class StatisticsEntry implements Entry<Integer, String> {
		Integer errorCode;
		String url;

		public StatisticsEntry(Integer errorCode, String url) {
			this.errorCode = errorCode;
			this.url = url;
		}
		
		@Override
		public Integer getKey() {
			return errorCode;
		}

		@Override
		public String getValue() {
			return url;
		}

		@Override
		public String setValue(String value) {
			url = value;
			return url;
		}
	 }
	 
	 private enum StatisticsType {
		 LINK_CHECKING("Link checking / "), 
		 METADATA_EXTRACTION("Metadata extraction / "), 
		 PREVIEW_CACHING("Preview caching / ");
		 
		 private String title;
		 
		 StatisticsType(String title) {
			this.title = title;
		 }
		 
		 public String getTitle() {
			 return this.title;
		 }
	 }
	 
	 
	//FIXME remove stub
	private static class Stub {
		private static Statistics statistics;		
		private static final String collection_name = "2048004_Ag_EU_AthenaPlus_SiauliuMuziejus";
		
		static {
			statistics = new Statistics();
			statistics.setCollection_name(collection_name);
			
			for (StatisticsType stType : StatisticsType.values()) {
				StatisticsItem item = new StatisticsItem();
				for (URLSourceType urlType : URLSourceType.values()) {
					for (int i = 0; i < 10; i++) {
						item.addStatisticsEntryByURLtype(urlType, new StatisticsEntry(400 + i, "http://link.sample.com/" + stType.name() + "/" + urlType.name() + "_"+ i));
					}
					statistics.addStatisticsItemByType(stType, item);
				}
			}
		}
		
		public static Statistics getStatistics() {
			return statistics;
		}
	}
}
