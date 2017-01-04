package eu.europeana.uim.gui.cp.server.util;

import com.google.code.morphia.Datastore;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import eu.europeana.harvester.client.HarvesterClient;
import eu.europeana.harvester.client.HarvesterClientConfig;
import eu.europeana.harvester.client.HarvesterClientImpl;
import eu.europeana.harvester.domain.URLSourceType;
import eu.europeana.harvester.domain.report.SubTaskState;
import eu.europeana.harvester.domain.report.SubTaskType;

import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

;

public class PdfReportGenerator {

    private static final String EDM_HAS_VIEW = "edm:hasView";

    private static final String EDM_IS_SHOWN_AT = "edm:isShownAt";

    private static final String EDM_IS_SHOWN_BY = "edm:isShownBy";

    private static final String EDM_OBJECT = "edm:object";

    private static final String PROCESSED = "processed";

    private static final String CHECKED = "checked";
    
    private static final String RESOLVING = "resolving";
    
    private static final String SUCCESSFULLY = "successfully";

    private static final Font FONT_FILE_NAME = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.ITALIC);

    private static final Font FONT_CAPTION = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD);

    private static final Font FONT_TEXT = FontFactory.getFont(FontFactory.HELVETICA, 10, Font.NORMAL);

    private static float INDENT_AFTER_IMAGE = 80f;
    
    private static float INDENT_TITLE = 60f;

    private static Datastore datastore;

    private static HarvesterClient client;

    static {
        datastore = CRFStatisticsUtil.getMongo();
        HarvesterClientConfig config = new HarvesterClientConfig();
        client = new HarvesterClientImpl(datastore, config);
//		try {
//			List<ServerAddress> addresses = new ArrayList<ServerAddress>();
//			addresses.add(new ServerAddress("mongo1.crf.europeana.eu", 27017));
//			addresses.add(new ServerAddress("mongo2.crf.europeana.eu", 27017));
//			Mongo mongo = new MongoClient(addresses);
//            Morphia morphia = new Morphia();
//            boolean auth = mongo.getDB("admin").authenticate("admin", "Nhck0zCfcu0M6kK".toCharArray());
//            if (!auth) {
//                throw new MongoException("ERROR: Couldn't authenticate the admin-user against admin-db");
//            }
//			datastore = morphia.createDatastore(mongo, "crf_harvester_second");
//		} catch (MongoException e) {
//			e.printStackTrace();
//		} catch (Exception any) {
//			any.printStackTrace();
//		}
//		HarvesterClientConfig config = new HarvesterClientConfig();
//		client = new HarvesterClientImpl(datastore, config);
    }


    /**
     * @param os
     * @param provId
     * @param colId
     */
    public static void generatePDFReport(OutputStream os, String provId, String colId, long dateStart, String recordsCount, String imagePath) {
        if (provId == null || colId == null) {
            return;
        }
        Report report = generateStatistics(provId, colId, dateStart, recordsCount);
//		Report report = Stub.getReport();
        // Landscape page layout
        // Document document = new Document(PageSize.A4.rotate(), 30f, 30f, 30f, 30f);
        Document document = new Document(PageSize.A4, 30f, 30f, 30f, 30f);
        document.addAuthor("Europeana");
        document.addCreationDate();
        document.addCreator("UIM Report Generator");
        document.addTitle("Image Caching Statistics");
        document.addSubject("Image caching statistics for a given dataset.");
        try {
            PdfWriter writer = PdfWriter.getInstance(document, os);
            document.open();

            //Document title
            Paragraph title = new Paragraph("Statistics report on link checking, technical metadata extraction and preview caching\n\n", FONT_CAPTION);
            title.setIndentationLeft(INDENT_TITLE);
			document.add(title);

            // Add Image
            String imageUrl = imagePath + "/images/EU_basic_logo_portrait_black.png";
            Image image = Image.getInstance(new URL(imageUrl));
            image.setAbsolutePosition(30f, 690f);
            image.scaleAbsolute(68f, 94f);
            document.add(image);

            // File name title
            Paragraph fileNameTitle = new Paragraph("Name of the file: ", FONT_FILE_NAME);
            fileNameTitle.add(new Chunk(getFileName(report.getCollection_name()), FONT_TEXT));
            fileNameTitle.setIndentationLeft(INDENT_AFTER_IMAGE);
            document.add(fileNameTitle);
            

            // General Information
            Phrase generalnfoPhrase = new Phrase("\nGeneral Information: ", FONT_CAPTION);
            generalnfoPhrase.add("\n");
            generalnfoPhrase.add(new Chunk("Provider name: ", FONT_TEXT));
            generalnfoPhrase.add(new Chunk(report.getProvider_name(), FONT_TEXT));
            generalnfoPhrase.add("\n");
            generalnfoPhrase.add(new Chunk("Dataset name: ", FONT_TEXT));
            generalnfoPhrase.add(new Chunk(report.getCollection_name(), FONT_TEXT));
            generalnfoPhrase.add("\n");
            generalnfoPhrase.add(new Chunk("Timeframe of processed executions: ", FONT_TEXT));
            generalnfoPhrase.add(new Chunk(report.getTime_start(), FONT_TEXT));
            generalnfoPhrase.add("\t - \t");
            generalnfoPhrase.add(new Chunk(report.getTime_end(), FONT_TEXT));
            generalnfoPhrase.add("\n");
            generalnfoPhrase.add(new Chunk("Number of active records within dataset: ", FONT_TEXT));
            generalnfoPhrase.add(new Chunk(report.getRecords_number() + "", FONT_TEXT));
            generalnfoPhrase.add("\n");
            Paragraph generalInfoTitle = new Paragraph(generalnfoPhrase);
            generalInfoTitle.setIndentationLeft(INDENT_AFTER_IMAGE);
            document.add(generalInfoTitle);

            // Link Checking
            Phrase linkCheckingPhrase = new Phrase("\nLink Checking", FONT_CAPTION);
            linkCheckingPhrase.add("\n");
            linkCheckingPhrase.add(new Chunk("Number of links checked within the dataset: ", FONT_TEXT));
            linkCheckingPhrase.add(new Chunk(report.getNumber_links_checked() + "", FONT_TEXT));
            linkCheckingPhrase.add("\n");
            linkCheckingPhrase.add(new Chunk("Number of links resolving within the dataset: ", FONT_TEXT));
            linkCheckingPhrase.add(new Chunk(report.getNumber_links_checked_successful() + "", FONT_TEXT));
            Paragraph linkChecking = new Paragraph(linkCheckingPhrase);
            document.add(linkChecking);

            // Link Checking Table
            PdfPTable linkCheckingTable = buildTable();
            addTableLine(linkCheckingTable, EDM_OBJECT, report.getEdm_object_checked() + "", CHECKED, report.getEdm_object_checked_successful() + "", RESOLVING);
            addTableLine(linkCheckingTable, EDM_IS_SHOWN_BY, report.getEdm_isshownby_checked() + "", CHECKED, report.getEdm_isshownby_checked_successful() + "", RESOLVING);
            addTableLine(linkCheckingTable, EDM_IS_SHOWN_AT, report.getEdm_isshownat_checked() + "", CHECKED, report.getEdm_isshownat_checked_successful() + "", RESOLVING);
            addTableLine(linkCheckingTable, EDM_HAS_VIEW, report.getEdm_hasview_checked() + "", CHECKED, report.getEdm_hasview_checked_successful() + "", RESOLVING);
            document.add(linkCheckingTable);

            // Metadata Extraction
            Phrase metadataExtractionPhrase = new Phrase("Technical metadata extraction", FONT_CAPTION);
            linkCheckingPhrase.add("\n");
            linkCheckingPhrase.add(new Chunk("Number of links checked within the dataset for which there was an attempt for technical metadata extraction: ", FONT_TEXT));
            linkCheckingPhrase.add(new Chunk(report.getNumber_metadata_extracted() + "", FONT_TEXT));
            linkCheckingPhrase.add("\n");
            linkCheckingPhrase.add(new Chunk("Number of links resolving within the dataset for which technical metadata extraction: ", FONT_TEXT));
            linkCheckingPhrase.add(new Chunk(report.getNumber_metadata_extracted_successul() + "", FONT_TEXT));
            Paragraph metadataExtraction = new Paragraph(metadataExtractionPhrase);
            document.add(metadataExtraction);

            // Metadata Extraction Table
            PdfPTable metadataExtractionTable = buildTable();
            addTableLine(metadataExtractionTable, EDM_IS_SHOWN_BY, report.getEdm_isshownby_processed() + "", PROCESSED, report.getEdm_isshownby_processed_successful() + "", SUCCESSFULLY + " " + PROCESSED);
            addTableLine(metadataExtractionTable, EDM_HAS_VIEW, report.getEdm_hasview_processed() + "", PROCESSED, report.getEdm_hasview_processed_successful() + "", SUCCESSFULLY + " " + PROCESSED);
            document.add(metadataExtractionTable);

            // Preview Caching
            Phrase previewCachingPhrase = new Phrase("Preview caching", FONT_CAPTION);
            previewCachingPhrase.add("\n");
            previewCachingPhrase.add(new Chunk("Number of links (edm:object, edm:IsShownBy, edm:hasView) checked within the dataset for which there was an attempt for preview caching: ", FONT_TEXT));
            previewCachingPhrase.add(new Chunk(report.getNumber_images_cached() + "", FONT_TEXT));
            previewCachingPhrase.add("\n");
            previewCachingPhrase.add(new Chunk("Number of successfully cached previews: ", FONT_TEXT));
            previewCachingPhrase.add(new Chunk(report.getNumber_images_cached_successful() + "", FONT_TEXT));
            Paragraph previewCaching = new Paragraph(previewCachingPhrase);
            document.add(previewCaching);

            document.close();
            writer.close();
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Report generateStatistics(String providerId, String collectionId, long dateStart, String recordsCount) {
        Report report = new Report();
        report.setProvider_name(providerId);
        report.setCollection_name(collectionId);

        // For link checking edm:object
        Long linkCheckObject = client.countAllTaskTypesByUrlSourceType(collectionId, URLSourceType.OBJECT);
        Long linkCheckObjectSuccess = client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.OBJECT, SubTaskType.RETRIEVE, SubTaskState.SUCCESS);

        // For link checking edm:isShownBy
        Long linkCheckIsShownBy = client.countAllTaskTypesByUrlSourceType(collectionId, URLSourceType.ISSHOWNBY);
        Long linkCheckIsShownBySuccess = client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.ISSHOWNBY, SubTaskType.RETRIEVE, SubTaskState.SUCCESS);

        // For link checking edm:isShownAt
        Long linkCheckIsShownAt = client.countAllTaskTypesByUrlSourceType(collectionId, URLSourceType.ISSHOWNAT);
        Long linkCheckIsShownAtSuccess = client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.ISSHOWNAT, SubTaskType.RETRIEVE, SubTaskState.SUCCESS);

        // For link checking edm:hasView
        Long linkCheckHasView = client.countAllTaskTypesByUrlSourceType(collectionId, URLSourceType.HASVIEW);
        Long linkCheckHasViewSuccess = client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.HASVIEW, SubTaskType.RETRIEVE, SubTaskState.SUCCESS);

        // For metadata extraction edm:isShownBy
        Long metaExtractIsShownBy = client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.ISSHOWNBY, SubTaskType.META_EXTRACTION, null);
        Long metaExtractIsShownBySuccess = client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.ISSHOWNBY, SubTaskType.META_EXTRACTION, SubTaskState.SUCCESS);

        // For metadata extraction edm:hasView
        Long metaExtractHasView = client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.HASVIEW, SubTaskType.META_EXTRACTION, null);
        Long metaExtractHasViewSuccess = client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.HASVIEW, SubTaskType.META_EXTRACTION, SubTaskState.SUCCESS);

        // For preview caching
        Long thumbnailGeneration = client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.OBJECT, SubTaskType.THUMBNAIL_GENERATION, null) +
                client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.HASVIEW, SubTaskType.THUMBNAIL_GENERATION, null) +
                client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.ISSHOWNBY, SubTaskType.THUMBNAIL_GENERATION, null);

        Long thumbnailGenerationSuccess = client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.OBJECT, SubTaskType.THUMBNAIL_GENERATION, SubTaskState.SUCCESS) +
                client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.HASVIEW, SubTaskType.THUMBNAIL_GENERATION, SubTaskState.SUCCESS) +
                client.countSubtaskStatesByUrlSourceType(collectionId, URLSourceType.ISSHOWNBY, SubTaskType.THUMBNAIL_GENERATION, SubTaskState.SUCCESS);


        // Date start
        Date start = new Date();
        start.setTime(dateStart);
        report.setTime_start(formatDate(start));
        // Date End - FIXME: hard-coded stub!
        report.setTime_end(formatDate(new Date()));

        // Amount of active records
        if (recordsCount != null) {
            try {
                report.setRecords_number(new Long(recordsCount));
            } catch (NumberFormatException e) {
                report.setRecords_number(0l);
            }
        } else {
            report.setRecords_number(0l);
        }

        report.setNumber_links_checked(linkCheckObject + linkCheckIsShownBy + linkCheckIsShownAt + linkCheckHasView);
        report.setNumber_links_checked_successful(linkCheckObjectSuccess + linkCheckIsShownBySuccess + linkCheckIsShownAtSuccess + linkCheckHasViewSuccess);

        report.setEdm_object_checked(linkCheckObject);
        report.setEdm_object_checked_successful(linkCheckObjectSuccess);
        report.setEdm_isshownby_checked(linkCheckIsShownBy);
        report.setEdm_isshownby_checked_successful(linkCheckIsShownBySuccess);
        report.setEdm_isshownat_checked(linkCheckIsShownAt);
        report.setEdm_isshownat_checked_successful(linkCheckIsShownAtSuccess);
        report.setEdm_hasview_checked(linkCheckHasView);
        report.setEdm_hasview_checked_successful(linkCheckHasViewSuccess);

        report.setNumber_metadata_extracted(metaExtractIsShownBy + metaExtractHasView);
        report.setNumber_metadata_extracted_successul(metaExtractIsShownBySuccess + metaExtractHasViewSuccess);

//		report.setEdm_object_processed(metaExtractObject);
//		report.setEdm_object_processed_successful(metaExtractObjectSuccess);
        report.setEdm_isshownby_processed(metaExtractIsShownBy);
        report.setEdm_isshownby_processed_successful(metaExtractIsShownBySuccess);
//		report.setEdm_isshownat_processed(metaExtractIsShownAt);
//		report.setEdm_isshownat_processed_successful(metaExtractIsShownAtSuccess);
        report.setEdm_hasview_processed(metaExtractHasView);
        report.setEdm_hasview_processed_successful(metaExtractHasViewSuccess);

        report.setNumber_images_cached(thumbnailGeneration);
        report.setNumber_images_cached_successful(thumbnailGenerationSuccess);

        return report;
    }

    public static String getFileName(String collectionName) {
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        return collectionName + "_" + df.format(new Date()) + "_statistics.pdf";
    }

    private static void addTableLine(PdfPTable table, String prefix, String firstNum, String firstTxt, String secondNum, String secondTxt) throws DocumentException {
        PdfPCell cell1 = new PdfPCell(new Paragraph(firstNum, FONT_TEXT));
        cell1.setPaddingLeft(10);
        cell1.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell1.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell1.setBorderColor(BaseColor.WHITE);

        PdfPCell cell2 = new PdfPCell(new Paragraph(prefix + " values " + firstTxt, FONT_TEXT));
        cell2.setPaddingLeft(10);
        cell2.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell2.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell2.setBorderColor(BaseColor.WHITE);

        PdfPCell cell3 = new PdfPCell(new Paragraph(secondNum, FONT_TEXT));
        cell3.setPaddingLeft(10);
        cell3.setHorizontalAlignment(Element.ALIGN_RIGHT);
        cell3.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell3.setBorderColor(BaseColor.WHITE);

        PdfPCell cell4 = new PdfPCell(new Paragraph(prefix + " values " + secondTxt, FONT_TEXT));
        cell4.setPaddingLeft(10);
        cell4.setHorizontalAlignment(Element.ALIGN_LEFT);
        cell4.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell4.setBorderColor(BaseColor.WHITE);

        //To avoid having the cell border and the content overlap, if you are having thick cell borders
        cell1.setUseBorderPadding(true);
        cell2.setUseBorderPadding(true);
        cell3.setUseBorderPadding(true);
        cell4.setUseBorderPadding(true);

        table.addCell(cell1);
        table.addCell(cell2);
        table.addCell(cell3);
        table.addCell(cell4);
    }

    private static PdfPTable buildTable() throws DocumentException {
        PdfPTable table = new PdfPTable(4); // 4 columns.
        table.setWidthPercentage(100); //Width 100%
        table.setSpacingBefore(10f); //Space before table
        table.setSpacingAfter(10f); //Space after table
        table.setWidths(new float[]{1f, 3f, 1f, 4f}); //Columns width ratios
        return table;
    }

    /**
     * day = date.substring(9, 11);
     * month = date.substring(12, 14);
     * year = date.substring(15);
     *
     * @param date
     * @return date for file name
     */
    public static String convertDate(String date) {
        StringBuffer dateConverted = new StringBuffer();
        dateConverted.append(date.substring(15))
                .append(date.substring(12, 14))
                .append(date.substring(9, 11));
        return dateConverted.toString();
    }
    
    private static String formatDate(Date date) {
    	SimpleDateFormat format = new SimpleDateFormat("hh:mm:ss dd-MM-yyyy");
    	return format.format(date);
    }

    private static class Report {
        private String provider_name;
        private String collection_name;
        private String time_start;
        private String time_end;
        private Long records_number;
        private Long number_links_checked;
        private Long number_links_checked_successful;
        private Long edm_object_checked;
        private Long edm_object_checked_successful;
        private Long edm_isshownby_checked;
        private Long edm_isshownby_checked_successful;
        private Long edm_isshownat_checked;
        private Long edm_isshownat_checked_successful;
        private Long edm_hasview_checked;
        private Long edm_hasview_checked_successful;
        private Long number_metadata_extracted;
        private Long number_metadata_extracted_successul;
        private Long edm_object_processed;
        private Long edm_object_processed_successful;
        private Long edm_isshownby_processed;
        private Long edm_isshownby_processed_successful;
        private Long edm_isshownat_processed;
        private Long edm_isshownat_processed_successful;
        private Long edm_hasview_processed;
        private Long edm_hasview_processed_successful;
        private Long number_images_cached;
        private Long number_images_cached_successful;

        public String getProvider_name() {
            return provider_name;
        }

        public void setProvider_name(String provider_name) {
            this.provider_name = provider_name;
        }

        public String getCollection_name() {
            return collection_name;
        }

        public void setCollection_name(String collection_name) {
            this.collection_name = collection_name;
        }

        public String getTime_start() {
            return time_start;
        }

        public void setTime_start(String time_start) {
            this.time_start = time_start;
        }

        public String getTime_end() {
            return time_end;
        }

        public void setTime_end(String time_end) {
            this.time_end = time_end;
        }

        public Long getRecords_number() {
            return records_number;
        }

        public void setRecords_number(Long records_number) {
            this.records_number = records_number;
        }

        public Long getNumber_links_checked() {
            return number_links_checked;
        }

        public void setNumber_links_checked(Long number_links_checked) {
            this.number_links_checked = number_links_checked;
        }

        public Long getNumber_links_checked_successful() {
            return number_links_checked_successful;
        }

        public void setNumber_links_checked_successful(
                Long number_links_checked_successful) {
            this.number_links_checked_successful = number_links_checked_successful;
        }

        public Long getEdm_object_checked() {
            return edm_object_checked;
        }

        public void setEdm_object_checked(Long edm_object_checked) {
            this.edm_object_checked = edm_object_checked;
        }

        public Long getEdm_object_checked_successful() {
            return edm_object_checked_successful;
        }

        public void setEdm_object_checked_successful(
                Long edm_object_checked_successful) {
            this.edm_object_checked_successful = edm_object_checked_successful;
        }

        public Long getEdm_isshownby_checked() {
            return edm_isshownby_checked;
        }

        public void setEdm_isshownby_checked(Long edm_isshownby_checked) {
            this.edm_isshownby_checked = edm_isshownby_checked;
        }

        public Long getEdm_isshownby_checked_successful() {
            return edm_isshownby_checked_successful;
        }

        public void setEdm_isshownby_checked_successful(
                Long edm_isshownby_checked_successful) {
            this.edm_isshownby_checked_successful = edm_isshownby_checked_successful;
        }

        public Long getEdm_isshownat_checked() {
            return edm_isshownat_checked;
        }

        public void setEdm_isshownat_checked(Long edm_isshownat_checked) {
            this.edm_isshownat_checked = edm_isshownat_checked;
        }

        public Long getEdm_isshownat_checked_successful() {
            return edm_isshownat_checked_successful;
        }

        public void setEdm_isshownat_checked_successful(
                Long edm_isshownat_checked_successful) {
            this.edm_isshownat_checked_successful = edm_isshownat_checked_successful;
        }

        public Long getEdm_hasview_checked() {
            return edm_hasview_checked;
        }

        public void setEdm_hasview_checked(Long edm_hasview_checked) {
            this.edm_hasview_checked = edm_hasview_checked;
        }

        public Long getEdm_hasview_checked_successful() {
            return edm_hasview_checked_successful;
        }

        public void setEdm_hasview_checked_successful(
                Long edm_hasview_checked_successful) {
            this.edm_hasview_checked_successful = edm_hasview_checked_successful;
        }

        public Long getNumber_metadata_extracted() {
            return number_metadata_extracted;
        }

        public void setNumber_metadata_extracted(Long number_metadata_extracted) {
            this.number_metadata_extracted = number_metadata_extracted;
        }

        public Long getNumber_metadata_extracted_successul() {
            return number_metadata_extracted_successul;
        }

        public void setNumber_metadata_extracted_successul(
                Long number_metadata_extracted_successul) {
            this.number_metadata_extracted_successul = number_metadata_extracted_successul;
        }

        public Long getEdm_object_processed() {
            return edm_object_processed;
        }

        public void setEdm_object_processed(Long edm_object_processed) {
            this.edm_object_processed = edm_object_processed;
        }

        public Long getEdm_object_processed_successful() {
            return edm_object_processed_successful;
        }

        public void setEdm_object_processed_successful(
                Long edm_object_processed_successful) {
            this.edm_object_processed_successful = edm_object_processed_successful;
        }

        public Long getEdm_isshownby_processed() {
            return edm_isshownby_processed;
        }

        public void setEdm_isshownby_processed(Long edm_isshownby_processed) {
            this.edm_isshownby_processed = edm_isshownby_processed;
        }

        public Long getEdm_isshownby_processed_successful() {
            return edm_isshownby_processed_successful;
        }

        public void setEdm_isshownby_processed_successful(
                Long edm_isshownby_processed_successful) {
            this.edm_isshownby_processed_successful = edm_isshownby_processed_successful;
        }

        public Long getEdm_isshownat_processed() {
            return edm_isshownat_processed;
        }

        public void setEdm_isshownat_processed(Long edm_isshownat_processed) {
            this.edm_isshownat_processed = edm_isshownat_processed;
        }

        public Long getEdm_isshownat_processed_successful() {
            return edm_isshownat_processed_successful;
        }

        public void setEdm_isshownat_processed_successful(
                Long edm_isshownat_processed_successful) {
            this.edm_isshownat_processed_successful = edm_isshownat_processed_successful;
        }

        public Long getEdm_hasview_processed() {
            return edm_hasview_processed;
        }

        public void setEdm_hasview_processed(Long edm_hasview_processed) {
            this.edm_hasview_processed = edm_hasview_processed;
        }

        public Long getEdm_hasview_processed_successful() {
            return edm_hasview_processed_successful;
        }

        public void setEdm_hasview_processed_successful(
                Long edm_hasview_processed_successful) {
            this.edm_hasview_processed_successful = edm_hasview_processed_successful;
        }

        public Long getNumber_images_cached() {
            return number_images_cached;
        }

        public void setNumber_images_cached(Long number_images_cached) {
            this.number_images_cached = number_images_cached;
        }

        public Long getNumber_images_cached_successful() {
            return number_images_cached_successful;
        }

        public void setNumber_images_cached_successful(
                Long number_images_cached_successful) {
            this.number_images_cached_successful = number_images_cached_successful;
        }

    }

    //FIXME remove stub
    private static class Stub {
        public static Report getReport() {
            Report report = new Report();
            report.setProvider_name("eu: AthenaPlus");
            report.setCollection_name("2048004_Ag_EU_AthenaPlus_SiauliuMuziejus");
            report.setTime_start("16:45:31 18-09-2015");
            report.setTime_end("21:00:39 18-09-2015");
            report.setRecords_number(10330l);

            report.setNumber_links_checked(35100l);
            report.setNumber_links_checked_successful(32000l);

            report.setEdm_object_checked(10100l);
            report.setEdm_object_checked_successful(10000l);
            report.setEdm_isshownby_checked(5000l);
            report.setEdm_isshownby_checked_successful(4000l);
            report.setEdm_isshownat_checked(10000l);
            report.setEdm_isshownat_checked_successful(9000l);
            report.setEdm_hasview_checked(10000l);
            report.setEdm_hasview_checked_successful(9000l);

            report.setNumber_metadata_extracted(32000l);
            report.setNumber_metadata_extracted_successul(15000l);

            report.setEdm_object_processed(1000l);
            report.setEdm_object_processed_successful(5000l);
            report.setEdm_isshownby_processed(10000l);
            report.setEdm_isshownby_processed_successful(5000l);
            report.setEdm_isshownat_processed(10000l);
            report.setEdm_isshownat_processed_successful(4000l);
            report.setEdm_hasview_processed(2000l);
            report.setEdm_hasview_processed_successful(1000l);

            report.setNumber_images_cached(15000l);
            report.setNumber_images_cached_successful(9000l);
            return report;
        }
    }
}
