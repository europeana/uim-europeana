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
import eu.europeana.harvester.domain.ProcessingJobRetrieveSubTaskState;
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
//	
//	private static final String EDM_OBJECT = "edm:object";
//	private static final String EDM_ISSHOWNBY = "edm:isShownBy";
//	private static final String EDM_ISSHOWNAT = "edm:isShownAt";
//	private static final String EDM_HASVIEW = "emd:hasView";

    private static Datastore datastore;

    private static HarvesterClient client;

    private static OutputStreamWriter writer;

    private static Statistics statistics;

    static {
        datastore = CRFStatisticsUtil.getMongo();
        HarvesterClientConfig config = new HarvesterClientConfig();
        client = new HarvesterClientImpl(datastore, config);
    }


    public static void generateReport(OutputStream os, String executionId, String collectionId) {
        statistics = generateStatistics(executionId, collectionId);
//		statistics = Stub.getStatistics();
        writer = new OutputStreamWriter(os);
        try {
            writer.write(COLLECTION_NAME);
            writer.write(collectionId + "\n");
            writer.write("\n");

            writeStatisticsItem(StatisticsType.LINK_CHECKING, URLSourceType.OBJECT);
            writeStatisticsItem(StatisticsType.LINK_CHECKING, URLSourceType.ISSHOWNBY);
            writeStatisticsItem(StatisticsType.LINK_CHECKING, URLSourceType.ISSHOWNAT);
            writeStatisticsItem(StatisticsType.LINK_CHECKING, URLSourceType.HASVIEW);

            writeStatisticsItem(StatisticsType.LINK_CACHING, URLSourceType.OBJECT);
            writeStatisticsItem(StatisticsType.LINK_CACHING, URLSourceType.ISSHOWNBY);
            writeStatisticsItem(StatisticsType.LINK_CACHING, URLSourceType.HASVIEW);

            writeStatisticsItem(StatisticsType.METADATA_EXTRACTION, URLSourceType.OBJECT);
            writeStatisticsItem(StatisticsType.METADATA_EXTRACTION, URLSourceType.ISSHOWNBY);
            writeStatisticsItem(StatisticsType.METADATA_EXTRACTION, URLSourceType.ISSHOWNAT);
            writeStatisticsItem(StatisticsType.METADATA_EXTRACTION, URLSourceType.HASVIEW);


            writeStatisticsItem(StatisticsType.COLOR_EXTRACTION, URLSourceType.ISSHOWNBY);
            writeStatisticsItem(StatisticsType.COLOR_EXTRACTION, URLSourceType.HASVIEW);

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
     * @param executionId
     * @param collectionId
     * @return
     */
    private static Statistics generateStatistics(String executionId, String collectionId) {
        Statistics statistics = new Statistics();
        List<LastSourceDocumentProcessingStatistics> findLastSourceDocumentProcessingStatistics =
                client.findLastSourceDocumentProcessingStatistics(collectionId, executionId, Arrays.asList(ProcessingState.ERROR, ProcessingState.FAILED));
        Map<String, LastSourceDocumentProcessingStatistics> linkCheckStats = new HashMap<String, LastSourceDocumentProcessingStatistics>();
        Map<String, LastSourceDocumentProcessingStatistics> retrieveStateStats = new HashMap<String, LastSourceDocumentProcessingStatistics>();
        Map<String, LastSourceDocumentProcessingStatistics> metaExtractStats = new HashMap<String, LastSourceDocumentProcessingStatistics>();
        Map<String, LastSourceDocumentProcessingStatistics> previewCachingStats = new HashMap<String, LastSourceDocumentProcessingStatistics>();
        Map<String, LastSourceDocumentProcessingStatistics> colorExtractionStats = new HashMap<String, LastSourceDocumentProcessingStatistics>();
        for (LastSourceDocumentProcessingStatistics st : findLastSourceDocumentProcessingStatistics) {
            ProcessingJobSubTaskStats processingJobSubTaskStats = st.getProcessingJobSubTaskStats();

            // link check and retrieve state
            ProcessingJobRetrieveSubTaskState retrieveState = processingJobSubTaskStats.getRetrieveState();
            if (retrieveState == ProcessingJobRetrieveSubTaskState.FAILED || retrieveState == ProcessingJobRetrieveSubTaskState.ERROR) {
                if (st.getHttpResponseCode() == 200) {
                    retrieveStateStats.put(st.getSourceDocumentReferenceId(), st);
                } else {
                    linkCheckStats.put(st.getSourceDocumentReferenceId(), st);
                }
            }
            // metadata extraction
            ProcessingJobSubTaskState metaExtractionState = processingJobSubTaskStats.getMetaExtractionState();
            if (metaExtractionState == ProcessingJobSubTaskState.FAILED || metaExtractionState == ProcessingJobSubTaskState.ERROR) {
                metaExtractStats.put(st.getSourceDocumentReferenceId(), st);
            }
            // preview caching
            ProcessingJobSubTaskState thumbGenerationState = processingJobSubTaskStats.getThumbnailGenerationState();
            if (thumbGenerationState == ProcessingJobSubTaskState.FAILED || thumbGenerationState == ProcessingJobSubTaskState.ERROR) {
                previewCachingStats.put(st.getSourceDocumentReferenceId(), st);
            }

            //color extraction
            ProcessingJobSubTaskState colorExtactionState = processingJobSubTaskStats.getColorExtractionState();
            if (colorExtactionState == ProcessingJobSubTaskState.FAILED || colorExtactionState == ProcessingJobSubTaskState.ERROR) {
                colorExtractionStats.put(st.getSourceDocumentReferenceId(), st);
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
            Integer httpResponseCode = stats.getHttpResponseCode();
            linkCheckItem.addStatisticsEntryByURLType(stats.getUrlSourceType(), new StatisticsEntry("ERROR " + (httpResponseCode == -1 ? "408" : httpResponseCode + ""), linkChecUris.get(id)));
        }
        statistics.addStatisticsItemByType(StatisticsType.LINK_CHECKING, linkCheckItem);

        // retrieve state
        Map<String, String> retrieveStateUris = new HashMap<String, String>();
        List<SourceDocumentReference> retrieveStateSourceDocRefById = client.retrieveSourceDocumentReferencesByIds(new ArrayList<String>(retrieveStateStats.keySet()));
        for (SourceDocumentReference reference : retrieveStateSourceDocRefById) {
            retrieveStateUris.put(reference.getId(), reference.getUrl());
        }
        StatisticsItem retrieveStateItem = new StatisticsItem();
        for (String id : retrieveStateStats.keySet()) {
            LastSourceDocumentProcessingStatistics stats = retrieveStateStats.get(id);
            String retrieveStateLog = stats.getProcessingJobSubTaskStats().getRetrieveLog();
            retrieveStateItem.addStatisticsEntryByURLType(stats.getUrlSourceType(), new StatisticsEntry("ERROR: " + (retrieveStateLog == null ? "" : retrieveStateLog), retrieveStateUris.get(id)));
        }
        statistics.addStatisticsItemByType(StatisticsType.LINK_CACHING, retrieveStateItem);

        // meatdata extraction
        Map<String, String> metaExtractUris = new HashMap<String, String>();
        List<SourceDocumentReference> metaExtrSourceDocRefById = client.retrieveSourceDocumentReferencesByIds(new ArrayList<String>(metaExtractStats.keySet()));
        for (SourceDocumentReference reference : metaExtrSourceDocRefById) {
            metaExtractUris.put(reference.getId(), reference.getUrl());
        }
        StatisticsItem metaExtractionItem = new StatisticsItem();
        for (String id : metaExtractStats.keySet()) {
            LastSourceDocumentProcessingStatistics stats = metaExtractStats.get(id);
            String metaExtractionLog = stats.getProcessingJobSubTaskStats().getMetaExtractionLog();
            String errorCode = stats.getHttpResponseCode() == 200 ? "ERROR" : "ERROR " + stats.getHttpResponseCode() + " : " + (metaExtractionLog == null ? "" : metaExtractionLog);
            metaExtractionItem.addStatisticsEntryByURLType(stats.getUrlSourceType(), new StatisticsEntry(errorCode, metaExtractUris.get(id)));
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
            String thumbnailGenerationLog = stats.getProcessingJobSubTaskStats().getThumbnailGenerationLog();
            String errorCode = stats.getHttpResponseCode() == 200 ? "ERROR" : "ERROR " + stats.getHttpResponseCode() + " : " + (thumbnailGenerationLog == null ? "" : thumbnailGenerationLog);
            thumbnailGenerationItem.addStatisticsEntryByURLType(stats.getUrlSourceType(), new StatisticsEntry(errorCode, previewCachingUris.get(id)));
        }
        statistics.addStatisticsItemByType(StatisticsType.PREVIEW_CACHING, thumbnailGenerationItem);


        // color extraction
        Map<String, String> colorExtractionUris = new HashMap<String, String>();
        List<SourceDocumentReference> colorExtractionSourceDocRefById = client.retrieveSourceDocumentReferencesByIds(new ArrayList<String>(colorExtractionStats.keySet()));
        for (SourceDocumentReference reference : colorExtractionSourceDocRefById) {
            colorExtractionUris.put(reference.getId(), reference.getUrl());
        }
        StatisticsItem colorExtractionItem = new StatisticsItem();
        for (String id : colorExtractionStats.keySet()) {
            LastSourceDocumentProcessingStatistics stats = colorExtractionStats.get(id);
            String colorExtractionLog = stats.getProcessingJobSubTaskStats().getColorExtractionLog();
            String errorCode = "ERROR: Color extraction failed. " + (colorExtractionLog == null ? "" : colorExtractionLog);
            colorExtractionItem.addStatisticsEntryByURLType(stats.getUrlSourceType(), new StatisticsEntry(errorCode, colorExtractionUris.get(id)));
        }
        statistics.addStatisticsItemByType(StatisticsType.COLOR_EXTRACTION, colorExtractionItem);
        return statistics;
    }

    public static String getFileName(String collectionId) {
        return collectionId + "_error_log.txt";
    }

    private static class Statistics {
        private String collection_name;

        private final List<StatisticsItem> link_check = new ArrayList<StatisticsItem>();
        private final List<StatisticsItem> retrieve_state = new ArrayList<StatisticsItem>();
        private final List<StatisticsItem> metadata_extract = new ArrayList<StatisticsItem>();
        private final List<StatisticsItem> preview_cache = new ArrayList<StatisticsItem>();
        private final List<StatisticsItem> color_extraction = new ArrayList<StatisticsItem>();

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
                case LINK_CACHING:
                    retrieve_state.add(item);
                    break;
                case METADATA_EXTRACTION:
                    metadata_extract.add(item);
                    break;
                case PREVIEW_CACHING:
                    preview_cache.add(item);
                    break;
                case COLOR_EXTRACTION:
                    color_extraction.add(item);
                    break;
            }
        }

        public List<StatisticsItem> getStatisticsItemsByType(StatisticsType type) {
            switch (type) {
                case LINK_CHECKING:
                    return link_check;
                case LINK_CACHING:
                    return retrieve_state;
                case METADATA_EXTRACTION:
                    return metadata_extract;
                case PREVIEW_CACHING:
                    return preview_cache;
                case COLOR_EXTRACTION:
                    return color_extraction;
            }
            return null;
        }
    }

    private static class StatisticsItem {

        private List<StatisticsEntry> errorCodesAndUrisForObject = new ArrayList<StatisticsEntry>();

        private List<StatisticsEntry> errorCodesAndUrisForIsShownBy = new ArrayList<StatisticsEntry>();

        private List<StatisticsEntry> errorCodesAndUrisForIsShownAt = new ArrayList<StatisticsEntry>();

        private List<StatisticsEntry> errorCodesAndUrisForHasView = new ArrayList<StatisticsEntry>();

        public void addStatisticsEntryByURLType(URLSourceType type, StatisticsEntry entry) {
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
            return null;
        }
    }

    private static class StatisticsEntry implements Entry<String, String> {
        String errorCode;
        String url;

        public StatisticsEntry(String errorCode, String url) {
            this.errorCode = errorCode;
            this.url = url;
        }

        @Override
        public String getKey() {
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
        LINK_CACHING("Link caching /"),
        METADATA_EXTRACTION("Metadata extraction / "),
        PREVIEW_CACHING("Preview caching / "),
        COLOR_EXTRACTION("Color extraction / ");

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
                        item.addStatisticsEntryByURLType(urlType, new StatisticsEntry("400" + i, "http://link.sample.com/" + stType.name() + "/" + urlType.name() + "_" + i));
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
