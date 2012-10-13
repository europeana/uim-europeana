/* ReportUtils.java - created on Jul 19, 2011, Copyright (c) 2011 The European Library, all rights reserved */
package eu.europeana.uim.gui.cp.server;

import  org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import eu.europeana.uim.api.ResourceEngine;
import eu.europeana.uim.gui.cp.server.engine.Engine;
import eu.europeana.uim.gui.cp.server.util.PropertyReader;
import eu.europeana.uim.gui.cp.server.util.UimConfigurationProperty;

/**
 * 
 * 
 * @author Rene Wiermer (rene.wiermer@kb.nl)
 * @date Jul 19, 2011
 */
public class ReportUtils {
    final static String         BIRT_URL               = PropertyReader.getProperty(UimConfigurationProperty.BIRT_URL);
    final static String         REPORT_DOWNLOAD_PREFIX = "EuropeanaIngestionControlPanel/reportDownload";

    private final static Logger log                    = Logger.getLogger(ReportUtils.class.getName());

    /**
     * Returns the URL to download the file from the download servlet
     * 
     * @param reportDesign
     *            the report design
     * @param executionID
     *            execution id
     * @param outputFormat
     *            the output format ("pdf","xls")
     * @return the download url
     */
    public static String generateDownloadURL(String reportDesign, String executionID,
            String outputFormat) {
        return REPORT_DOWNLOAD_PREFIX + "?report=" + reportDesign + "&executionid=" + executionID +
               "&type=" + outputFormat;
    }

    /**
     * Returns the URL for the BIRT report to be downloaded
     * 
     * @param reportDesign
     * @param executionID
     * @param outputFormat
     * @return the Birt report download URL.
     */
    public static String generateBirtURL(String reportDesign, String executionID,
            String outputFormat) {
        return BIRT_URL + "/frameset?__report=reports%2F" + reportDesign + "&execution=" +
               executionID + "&__format=" + outputFormat + "&__locale=en";
    }

    /**
     * Generate the specified report in all formats for this executionID
     * 
     * @param reportDesign
     * @param executionID
     * @param formats
     * @return True iff the generation was succesfull
     */
    public static Boolean generateReport(String reportDesign, String executionID, String[] formats) {
        Engine engine = Engine.getInstance();
        ResourceEngine resourceEngine = engine.getRegistry().getResourceEngine();

        if (resourceEngine == null) {
            log.warning("Could not get resource engine while generating reports. Saving reports to temporary directory!");
        }

        for (String format : formats) {
            try {
                URL downloadURL = new URL(generateBirtURL(reportDesign, executionID, format));
                
                File downloadToFile = getReportFile(resourceEngine, reportDesign, executionID,
                        format);
                log.info("Start downloading report in format " + format + " from " + downloadURL +
                         " to " + downloadToFile);
                try {
                    FileUtils.copyURLToFile(downloadURL, downloadToFile);
                } catch (IOException e) {
                    log.warning("Error downloading report from " + downloadURL + e);
                    // writing failed. Server down ? I/O error in the target ?
                    return false;
                }
                log.info("Finished donwload successfully");
            } catch (MalformedURLException e) {
                throw new RuntimeException("Exception while generating download URL for report.", e);
            }
        }
        return true;
    }



	/**
     * Get back the reference to the report file
     * 
     * @param reportDesign
     * @param executionID
     * @param format
     * @return the file reference to the local file
     */
    public static File getReportFile(String reportDesign, String executionID, String format) {
        Engine engine = Engine.getInstance();
        ResourceEngine resourceEngine = engine.getRegistry().getResourceEngine();
        return getReportFile(resourceEngine, reportDesign, executionID, format);
    }

    /**
     * @param engine
     * @param reportDesign
     * @param executionID
     * @param format
     * @return
     */
    private static File getReportFile(ResourceEngine engine, String reportDesign,
            String executionID, String format) {
        File reportDir;

        if (engine == null) {
            reportDir = new File(System.getProperty("java.io.tmpdir"));
            // throw new RuntimeException("Could not get resource engine while generating reports");
        } else {
            reportDir = new File(engine.getWorkingDirectory(), "reports");
        }

        if (!reportDir.exists() && !reportDir.mkdirs()) { throw new RuntimeException(
                "Could not create reports directory: " + reportDir); }

        return new File(reportDir, reportDesign + "." + executionID + "." + format);
    }
}