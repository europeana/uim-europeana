/* ReportService.java - created on Jul 15, 2011, Copyright (c) 2011 The European Library, all rights reserved */
package eu.europeana.uim.gui.cp.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import eu.europeana.uim.gui.cp.shared.ExecutionDTO;

/**
 * This service communicates back to the BIRT engine and the Reporting download servlet to generate
 * and get the reports from the engine
 * 
 * @author Rene Wiermer (rene.wiermer@kb.nl)
 * @date Jul 15, 2011
 */
@RemoteServiceRelativePath("reporting")
public interface ReportingService extends RemoteService {
    /**
     * Generate Reports for the execution
     * 
     * @param executionID
     * @param report
     * @param filetypes
     * @return true, if the creation was successful, false, if not
     * 
     */
    Boolean generateReport(String executionID, String report, String[] filetypes);

    /**
     * Check, if the the report is already generated and is ready for download. Return the URL to
     * start download from.
     * 
     * @param reportDesign
     *            the name of the report design to check
     * @param exeuctionID
     *            the ID of the execution
     * @param filetype
     *            the filetype to be expected ("pdf","xls"...)
     * @return the URL to download from, null if the report does not exists
     */
    String getReport(String reportDesign, String exeuctionID, String filetype);

    /**
     * @param workflows
     *            the list of workflows to be considered. Null means all.
     * @param design
     * @param filetypes
     * @return list of completed executions from a list of specific workflows
     */
    List<ExecutionDTO> getPastExecutions(String[] workflows, String design, String[] filetypes);
}