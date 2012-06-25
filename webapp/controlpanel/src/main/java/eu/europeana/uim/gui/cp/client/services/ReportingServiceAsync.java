/* ReportService.java - created on Jul 15, 2011, Copyright (c) 2011 The European Library, all rights reserved */
package eu.europeana.uim.gui.cp.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.europeana.uim.gui.cp.shared.ExecutionDTO;

/**
 * The asynchronous interface for the reporting service
 * 
 * 
 * @author Rene Wiermer (rene.wiermer@kb.nl)
 * @date Jul 15, 2011
 */
public interface ReportingServiceAsync {
    /**
     * @param reportDesign
     * @param exeuctionID
     * @param filetype
     * @param async
     */
    void getReport(String reportDesign, String exeuctionID, String filetype,
            AsyncCallback<String> async);

    /**
     * @param executionID
     * @param report
     * @param filetypes
     * @param reports
     * @param callback
     */
    void generateReport(String executionID, String report, String[] filetypes,
            AsyncCallback<Boolean> callback);

    /**
     * Returns list of completed executions for a list of workflows
     * 
     * @param workflows
     *            the list of workflows, null means all.
     * @param design
     * @param filetypes
     * @param async
     */
    void getPastExecutions(String[] workflows, String design, String[] filetypes,
            AsyncCallback<List<ExecutionDTO>> async);
}