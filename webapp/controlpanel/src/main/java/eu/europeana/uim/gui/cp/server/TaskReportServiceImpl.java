package eu.europeana.uim.gui.cp.server;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.google.code.morphia.query.Query;
import com.google.code.morphia.query.UpdateOperations;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.europeana.reindexing.common.Status;
import eu.europeana.reindexing.common.TaskReport;
import eu.europeana.uim.gui.cp.client.services.TaskReportService;
import eu.europeana.uim.gui.cp.shared.validation.TaskReportDTO;
import eu.europeana.uim.gui.cp.shared.validation.TaskReportResultDTO;
import org.apache.commons.lang.StringUtils;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public class TaskReportServiceImpl extends IntegrationServicesProviderServlet implements TaskReportService {

	private static final long serialVersionUID = -33869393639533715L;
	private static Datastore datastore;

	static {
		try {
			//connect to Mongo
			Mongo mongo = MongoProvider.getReindexingMongo();
			Morphia morphia = new Morphia();
			morphia.map(TaskReport.class);
			datastore = morphia.createDatastore(mongo, "taskreports");
	        datastore.ensureIndexes();
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (Exception any) {
			any.printStackTrace();
		}
	}
	
	@Override
	public TaskReportResultDTO getTaskReports(int offset, int maxSize, boolean showActiveOnly, String filterQuery, String newTaskReportQuery, long stopTaskId) {
		//FIXME remove stub and retrieve real task reports here!
		//List<TaskReportDTO> reportsDTO = stub(isActive, filterQuery, newTaskReportQuery, stopTaskId);
		List<TaskReport> reports = getTaskReportsFromMongo(showActiveOnly, filterQuery, newTaskReportQuery, stopTaskId);
		List<TaskReportDTO> reportsDTO = convertTaskReportsToDTO(reports);
		
		//create TaskReportResultDTO with provided offset and max value of the results
		TaskReportResultDTO result = null;
		if (maxSize > 0) {
			List<TaskReportDTO> taskReportDTOList = new ArrayList<TaskReportDTO>();			
			int maxval = offset + maxSize > reportsDTO.size() ? reportsDTO.size() : offset + maxSize;			
			List<TaskReportDTO> taskReportSublist =  reportsDTO.subList(offset, maxval); 
			for (TaskReportDTO taskReportDTO : taskReportSublist) {				
				taskReportDTOList.add(taskReportDTO);
			}
			result = new TaskReportResultDTO(taskReportDTOList, reportsDTO.size());
		}
		return result;
	}
	
	/**
	 * 
	 * @return task reports (full list or only unfinished ones).
	 */
	private List<TaskReport> getTaskReportsFromMongo(boolean showActiveOnly, String filterQuery, String newTaskReportQuery, long stopTaskId) {
		//first stop the task report
        if (stopTaskId > 0) {
        	Query<TaskReport> query = datastore.createQuery(TaskReport.class).field("taskId").equal(stopTaskId);
        	UpdateOperations<TaskReport> stopTaskReportOperation = datastore.createUpdateOperations(TaskReport.class).set("status", Status.STOPPED);
        	datastore.update(query, stopTaskReportOperation);     	
        }
        
        //add new task report if there is one
        if (newTaskReportQuery != null && !newTaskReportQuery.isEmpty()) {
        	datastore.save(createNewTaskReport(newTaskReportQuery));
        }
        //return all task reports
        if ((filterQuery == null || filterQuery.isEmpty()) && !showActiveOnly) {
        	return  datastore.find(TaskReport.class).asList();
        //return only unfinished task reports
        } else if ((filterQuery == null || filterQuery.isEmpty()) && showActiveOnly) {        	
        	return datastore.find(TaskReport.class).field("status").notIn(Arrays.asList(Status.FINISHED, Status.STOPPED)).asList();
        //return all filtered by query 'filterQuery' task reports
        } else if (filterQuery != null && !filterQuery.isEmpty() && !showActiveOnly) {
        	return datastore.find(TaskReport.class).filter("query", filterQuery).asList();
        //return only unfinished filtered by query 'filterQuery' task reports
        } else {
        	return datastore.find(TaskReport.class).filter("query", filterQuery).field("status").notIn(Arrays.asList(Status.FINISHED, Status.STOPPED)).asList();
        }
	}
	
	/**
	 * 
	 * @param taskReports is a list of TaskReport
	 * @return list of TaskReportDTO
	 */
	private List<TaskReportDTO> convertTaskReportsToDTO(List<TaskReport> taskReports) {
		List<TaskReportDTO> taskReportsDTO = new ArrayList<TaskReportDTO>();
		for (TaskReport report : taskReports) {
			TaskReportDTO reportDTO = new TaskReportDTO();
			reportDTO.setTaskId(report.getTaskId());
			reportDTO.setStatus(StringUtils.upperCase(report.getStatus().name()));
			reportDTO.setDateCreated(formatDate(report.getDateCreated()));
			reportDTO.setDateUpdated(formatDate(report.getDateUpdated()));
			reportDTO.setProcessed(report.getProcessed());
			reportDTO.setTotal(report.getTotal());
			reportDTO.setQuery(report.getQuery());
			taskReportsDTO.add(reportDTO);
		}
		return taskReportsDTO;
	}
	
	/**
	 * 
	 * @param newTaskReportQuery
	 * @return new TaskReport with the given query 'newTaskReportQuery'
	 */
	private TaskReport createNewTaskReport(String newTaskReportQuery) {
    	TaskReport newTaskReport = new TaskReport();
    	long date = System.currentTimeMillis();
    	newTaskReport.setTaskId(date);
    	newTaskReport.setStatus(Status.INITIAL);
    	newTaskReport.setDateCreated(date);
    	newTaskReport.setDateUpdated(date);
    	newTaskReport.setProcessed(0);
    	newTaskReport.setQuery(newTaskReportQuery);
    	return newTaskReport;
	}
	
	/**
	 * Utility method for date formating
	 * @param dateUpdated is long representation of date
	 * @return a string representation of date in the format 'dd-MM-yyyy'
	 */
	private static String formatDate(long dateUpdated) {
        return (new SimpleDateFormat("HH:mm:ss dd-MM-yyyy")).format(new Date(dateUpdated));
	}

//	//stub for testing!
//	private List<TaskReportDTO> stub(boolean isActive, String filterQuery, String newTaskReportQuery, long stopTaskId) {
//		List<TaskReportDTO> reports = new ArrayList<TaskReportDTO>();
//		TaskReportDTO report;
//		for (int i = 0 ; i < 10; i++) {
//			report = new TaskReportDTO();
//			report.setTaskId(i);
//			report.setStatus(i == stopTaskId ? "STOPPED" : (i % 2 == 0) ? "INITIAL" : "PROCESSING");
//			report.setDateCreated("29-11-2014");
//			report.setDateUpdated("29-11-2014");
//			report.setProcessed(10 + i);
//			report.setTotal(100);
//			report.setQuery((i % 2 == 0) ? "stub query" : "*:*");
//			reports.add(report);
//		}
//		for (int i = 10 ; i < 30; i++) {
//			report = new TaskReportDTO();
//			report.setTaskId(i);
//			report.setStatus(i == stopTaskId ? "STOPPED" : i < 10 ? ((i % 2 == 0) ? "INITIAL" : "FINISHED") : ((i % 2 == 0) ? "FINISHED" : "INITIAL"));
//			report.setDateCreated("29-10-2002");
//			report.setDateUpdated("29-10-2002");
//			report.setProcessed(10 + i);
//			report.setTotal(100);
//			report.setQuery((i % 2 == 0) ? "*:*" : "bla-bla");
//			reports.add(report);
//		}
//		List<TaskReportDTO> reportsToDisplay = null;
//		if (newTaskReportQuery != null && !newTaskReportQuery.isEmpty()) {
//			TaskReportDTO newReport = new TaskReportDTO();
//			newReport = new TaskReportDTO();
//			newReport.setTaskId(System.currentTimeMillis());
//			newReport.setStatus("INITIAL");
//			newReport.setDateCreated("14-07-2015");
//			newReport.setDateUpdated("14-07-2015");
//			newReport.setProcessed(0);
//			newReport.setQuery(newTaskReportQuery);
//			reports.add(0,newReport);
//		}
//		if (filterQuery == null || filterQuery.isEmpty()) {
//			reportsToDisplay = reports;
//		}  else {
//			reportsToDisplay = new ArrayList<TaskReportDTO>();
//			for (TaskReportDTO r : reports) {
//				if (r.getQuery() != null && r.getQuery().equals(filterQuery)) {
//					reportsToDisplay.add(r);
//				}
//			}
//		}
//		if (isActive) {
//			List<TaskReportDTO> unfinishedReports = new ArrayList<TaskReportDTO>();
//			for (TaskReportDTO r : reportsToDisplay) {
//				if (!r.getStatus().equalsIgnoreCase("FINISHED") && !r.getStatus().equalsIgnoreCase("STOPPED")) {
//					unfinishedReports.add(r);
//				}
//			}
//			return unfinishedReports;
//		} else {
//			return reportsToDisplay;
//		}
//	}
}
