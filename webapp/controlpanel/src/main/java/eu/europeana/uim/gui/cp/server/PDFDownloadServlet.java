/**
 * 
 */
package eu.europeana.uim.gui.cp.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since 1 May 2013
 *
 */
public class PDFDownloadServlet extends ReportDownloadServlet{

	private static final long serialVersionUID = 1L;

	/**
     * Sends a file to the ServletResponse output stream. Typically you want the browser to receive
     * a different name than the name the file has been saved in your local database, since your
     * local names need to be unique.
     * 
     * @param req
     *            The request
     * @param resp
     *            The response
     * @param filename
     *            The name of the file you want to download.
     * @param original_filename
     *            The name the browser should receive.
     */
	@Override
    protected void doDownload(HttpServletRequest req, HttpServletResponse resp,String reportDesign, 
    		String executionid,String type) throws IOException {

        File file = ReportUtils.getReportFile(reportDesign, executionid, type);

        if (!file.exists()) {
            resp.sendError(
                    404,
                    "Report does not exists. Generate first ? Meant to get:" +
                            file.getAbsolutePath());
        }
        // just reuuse the name from the storage
        String filenameToSave = file.getName();
        
        int length = 0;
        ServletOutputStream op = resp.getOutputStream();

        resp.setContentType(type);  
        resp.setContentLength((int)file.length());
        resp.setHeader("Content-Disposition", "inline;  filename=\"" + filenameToSave + "\"");
        resp.setHeader("Cache-Control", "no-cache"); 
        resp.setDateHeader("Expires", 0);  
        resp.setHeader("Pragma", "No-cache");

        //
        // Stream to the requester.
        //
        byte[] bbuf = new byte[BUFSIZE];
        DataInputStream in = new DataInputStream(new FileInputStream(file));

        while (((length = in.read(bbuf)) != -1)) {
            op.write(bbuf, 0, length);
        }

        in.close();
        op.flush();
        op.close();
    }
	
}
