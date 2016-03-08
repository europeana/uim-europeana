/* ReportDownloadServlet.java - created on Jul 15, 2011, Copyright (c) 2011 The European Library, all rights reserved */
package eu.europeana.uim.gui.cp.server;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.europeana.uim.gui.cp.server.util.CsvReportGenerator;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public class CSVReportGenerationServlet extends HttpServlet {
    
	private static final long serialVersionUID = 1L;

	 /**
     * Initialization method
     */
	@Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);

    }
   
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {    	resp.setContentType("application/txt");  
    	resp.setHeader("Content-Disposition", "attachment; filename=\"" + CsvReportGenerator.getFileName() + "\"");
//    	resp.setHeader("Content-Disposition", "attachment; filename=\"" + "TEST_CRF.pdf" + "\"");
    	resp.setHeader("Cache-Control", "no-cache"); 
    	resp.setDateHeader("Expires", 0);  
    	resp.setHeader("Pragma", "No-cache");
		try {
			System.out.println("*** Generating CSV... ***");
			ServletOutputStream op = resp.getOutputStream();
			CsvReportGenerator.generateReport(op);
			op.flush();
			op.close();
			System.out.println("*** CSV error log file was successfully generated! ***");
		} catch (Exception e) {
			System.out.println("*** Failure of CSV generation! ***");
			e.printStackTrace();
		}
    }
}