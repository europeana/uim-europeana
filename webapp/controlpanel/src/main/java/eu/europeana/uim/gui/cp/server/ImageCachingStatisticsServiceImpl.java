package eu.europeana.uim.gui.cp.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.joda.time.Interval;

import com.google.code.morphia.Datastore;

import eu.europeana.harvester.client.HarvesterClient;
import eu.europeana.harvester.client.HarvesterClientConfig;
import eu.europeana.harvester.client.HarvesterClientImpl;
import eu.europeana.harvester.domain.JobState;
import eu.europeana.harvester.domain.JobStatistics;
import eu.europeana.harvester.domain.Page;
import eu.europeana.harvester.domain.ProcessingJob;
import eu.europeana.harvester.util.pagedElements.PagedElements;
import eu.europeana.uim.gui.cp.client.services.ImageCachingStatisticsService;
import eu.europeana.uim.gui.cp.server.util.CRFStatisticsUtil;
import eu.europeana.uim.gui.cp.shared.validation.ImageCachingStatisticsDTO;
import eu.europeana.uim.gui.cp.shared.validation.ImageCachingStatisticsResultDTO;

public class ImageCachingStatisticsServiceImpl extends IntegrationServicesProviderServlet implements ImageCachingStatisticsService {

	private static final long serialVersionUID = -7834660081217510125L;

	private static Datastore datastore;
	
	private static HarvesterClient client;
	
	private static void setupMongo() {
		datastore = CRFStatisticsUtil.getMongo();
		HarvesterClientConfig config = new HarvesterClientConfig();
		client = new HarvesterClientImpl(datastore, config);
	}
	
	@Override
	public ImageCachingStatisticsResultDTO getImageCachingStatistics(int offset, int maxSize, List<String> collections, String providerId) {
		setupMongo();
		ImageCachingStatisticsResultDTO result = null;
		try {
//			List<Statistics> statisticsReports = generateStub();
			List<Statistics> statisticsReports = getStatisticsReports(offset, maxSize, collections, providerId);  
			List<ImageCachingStatisticsDTO> reportsDTO = convertStatisticsReportsToDTO(statisticsReports);
			if (maxSize > 0) {
				List<ImageCachingStatisticsDTO> statisticsReportDTOList = new ArrayList<ImageCachingStatisticsDTO>();			
				int maxval = offset + maxSize > reportsDTO.size() ? reportsDTO.size() : offset + maxSize;			
				List<ImageCachingStatisticsDTO> statisticsReportDTOSublist =  reportsDTO.subList(offset, maxval); 
				for (ImageCachingStatisticsDTO statistiscReportDTO : statisticsReportDTOSublist) {				
					statisticsReportDTOList.add(statistiscReportDTO);
				}
				result = new ImageCachingStatisticsResultDTO(statisticsReportDTOList, reportsDTO.size());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
	/**
	 * 
	 * @param collections
	 * @return lists of statistics per work-flow executionIds
	 * @throws Exception
	 */
	private static List<Statistics> getStatisticsReports(int offset, int maxSize, List<String> collections, String providerId) throws Exception {
		List<Statistics> statisticsList = new ArrayList<Statistics>();
		for (String collId : collections) {
			long time = System.currentTimeMillis();
//			List<LastSourceDocumentProcessingStatistics> stat = client.findLastSourceDocumentProcessingStatistics(collId, null, Arrays.asList(ProcessingState.values()));
			//PagedElements<ProcessingJob> jobsByCollectionAndState = client.findJobsByCollectionAndState(new HashSet<String>(Arrays.asList(collId)), new HashSet<JobState>(Arrays.asList(JobState.values())), new Page(offset, maxSize));
			Map<String, JobStatistics> map = client.findJobsByCollectionId(collId);
			
			System.out.println("*** The time elapsed for the CRF Statistics UIM page generation for collection " + collId + ": " + ((System.currentTimeMillis() - time)/1000) + " second ***");
			
			
		    for (Entry<String,JobStatistics> execId : map.entrySet()) { 
				Interval dateIntervalForProcessing = client.getDateIntervalForProcessing(execId.getKey());
				Statistics statistics = new Statistics();
				statistics.setDateCreated(new Date(dateIntervalForProcessing.getStartMillis()));
				statistics.setDateCompleted(new Date(dateIntervalForProcessing.getEndMillis()));
				statistics.setProviderId(providerId);				
				statistics.setCollectionId(collId);
				statistics.setPendingJobs(execId.getValue().getPending());
				statistics.setFailedJobs(execId.getValue().getFailed());
				statistics.setSuccessfulJobs(execId.getValue().getSuccessful());
				statistics.setTotal(execId.getValue().getPending() + execId.getValue().getFailed() + execId.getValue().getSuccessful());
			
				statistics.setExecutionId(execId.getKey());
				statisticsList.add(statistics);
			}
		}
		return statisticsList;
	}

	private static List<ImageCachingStatisticsDTO> convertStatisticsReportsToDTO(List<Statistics> statisticsReports) {
		List<ImageCachingStatisticsDTO> imageCachingStatisticsDTOList = new ArrayList<ImageCachingStatisticsDTO>();
		for (Statistics st : statisticsReports) {
			ImageCachingStatisticsDTO imageCachingStatisticsDTO = new ImageCachingStatisticsDTO();
			imageCachingStatisticsDTO.setDateCreated(formatDate(st.getDateCreated()));
			imageCachingStatisticsDTO.setDateCompleted(formatDate(st.getDateCompleted()));
			imageCachingStatisticsDTO.setCollectionId(st.getCollectionId());
//			imageCachingStatisticsDTO.setTotal(st.getTotal());
			imageCachingStatisticsDTO.setTotalJobs(st.getPendingJobs() + st.getFailedJobs() + st.getSuccessfulJobs());
			imageCachingStatisticsDTO.setPendingJobs(st.getPendingJobs());
			imageCachingStatisticsDTO.setFailedJobs(st.getFailedJobs());
			imageCachingStatisticsDTO.setSuccessfulJobs(st.getSuccessfulJobs());
			imageCachingStatisticsDTO.setExecutionId(st.getExecutionId());
			imageCachingStatisticsDTO.setProviderId(st.getProviderId());
			imageCachingStatisticsDTOList.add(imageCachingStatisticsDTO);
		}
		return imageCachingStatisticsDTOList;
	}
	
	private static class Statistics {		
		String collectionId;
		private String providerId;
		Date dateCreated;
		Date dateCompleted;
		long total;
		long pendingJobs;
		long successfulJobs;
		long failedJobs;
		String executionId;

		public String getCollectionId() {
			return collectionId;
		}
		public void setCollectionId(String collectionId) {
			this.collectionId = collectionId;
		}
		public Date getDateCreated() {
			return dateCreated;
		}
		public void setDateCreated(Date dateCreated) {
			this.dateCreated = dateCreated;
		}
		public Date getDateCompleted() {
			return dateCompleted;
		}
		public void setDateCompleted(Date dateCompleted) {
			this.dateCompleted = dateCompleted;
		}
		public long getTotal() {
			return total;
		}
		public void setTotal(long total) {
			this.total = total;
		}
		public long getPendingJobs() {
			return pendingJobs;
		}
		public void setPendingJobs(long pendingJobs) {
			this.pendingJobs = pendingJobs;
		}
		public long getSuccessfulJobs() {
			return successfulJobs;
		}
		public void setSuccessfulJobs(long successfulJobs) {
			this.successfulJobs = successfulJobs;
		}
		public long getFailedJobs() {
			return failedJobs;
		}
		public void setFailedJobs(long failedJobs) {
			this.failedJobs = failedJobs;
		}
		public String getExecutionId() {
			return executionId;
		}
		public void setExecutionId(String executionId) {
			this.executionId = executionId;
		}
		public String getProviderId() {
			return providerId;
		}
		public void setProviderId(String providerId) {
			this.providerId = providerId;
		}
	}

	//TODO
	private static List<Statistics> generateStub() {
		List<Statistics> stub = new ArrayList<Statistics>();
		Calendar cal = Calendar.getInstance();		
		for (int i = 0; i < 2; i++) {
			Statistics statistics = new Statistics();
			statistics.setCollectionId("03915_Ag_FR_MCC_MEMOIRE_SAP_IMAGE");
			cal.set(2015, 9, 18, 16, 45);
			statistics.setDateCreated(cal.getTime());
			cal.set(2015, 9, 18, 21, 01);
			statistics.setDateCompleted(cal.getTime());
//			statistics.setTotal(1000);
			statistics.setPendingJobs(100);
			statistics.setSuccessfulJobs(600);
			statistics.setFailedJobs(200);
			statistics.setExecutionId(i + 1 + "");
			stub.add(statistics);
		}
		for (int i = 0; i < 2; i++) {
			Statistics statistics = new Statistics();
			statistics.setCollectionId("03915_Ag_FR_MCC_MEMOIRE_SAP_IMAGE");
			cal.set(2015, 10, 4, 22, 00);
			statistics.setDateCreated(cal.getTime());
			cal.set(2015, 10, 6, 16, 11);
			statistics.setDateCompleted(cal.getTime());
//			statistics.setTotal(4000);
			statistics.setPendingJobs(1000);
			statistics.setSuccessfulJobs(2900);
			statistics.setFailedJobs(100);
			statistics.setExecutionId(i + 3 + "");
			stub.add(statistics);
		}
		for (int i = 0; i < 3; i++) {
			Statistics statistics = new Statistics();
			statistics.setCollectionId("90901_L_ES_BibVirtualCervantes_dc");
			cal.set(2015, 10, 2, 2, 16);
			statistics.setDateCreated(cal.getTime());
			cal.set(2015, 10, 2, 10, 17);
			statistics.setDateCompleted(cal.getTime());
//			statistics.setTotal(620);
			statistics.setPendingJobs(100);
			statistics.setSuccessfulJobs(420);
			statistics.setFailedJobs(100);
			statistics.setExecutionId(i + 5 + "");
			stub.add(statistics);
		}
		for (int i = 0; i < 3; i++) {
			Statistics statistics = new Statistics();
			statistics.setCollectionId("2022616_Ag_NO_ELocal_MediaVideo");
			cal.set(2015, 11, 16, 8, 38);
			statistics.setDateCreated(cal.getTime());
			cal.set(2015, 11, 17, 6, 00);
			statistics.setDateCompleted(cal.getTime());
//			statistics.setTotal(10800);
			statistics.setPendingJobs(0);
			statistics.setSuccessfulJobs(10000);
			statistics.setFailedJobs(800);
			statistics.setExecutionId(i + 8 + "");
			stub.add(statistics);
		}
		
		return stub;
	}
	
	/**
	 * TODO move it to a separate utility class!
	 * Utility method for date formating
	 * @param dateUpdated is long representation of date
	 * @return a string representation of date in the format 'dd-MM-yyyy'
	 */
	private static String formatDate(Date dateUpdated) {
        return (new SimpleDateFormat("dd-MM-yyyy HH:mm:ss")).format(dateUpdated);
	}
}
