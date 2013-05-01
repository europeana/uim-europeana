/* ReportDownloadServlet.java - created on Jul 15, 2011, Copyright (c) 2011 The European Library, all rights reserved */
package eu.europeana.uim.gui.cp.server;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import eu.europeana.uim.gui.cp.server.engine.Engine;

/**
 * Servlet for report download
 * 
 * @author Rene Wiermer (rene.wiermer@kb.nl)
 * @date Jul 15, 2011
 */
public class ReportDownloadServlet extends HttpServlet {
    final Engine                             engine;

    private static final Map<String, String> fileTypes = new HashMap<String, String>();
    static {
        fileTypes.put("pdf", "application/pdf");
        fileTypes.put("xls", "application/vnd.ms-excel");
        fileTypes.put("html", "text/html");
        
    }

    final static int                         BUFSIZE   = 4096;

    /**
     * Creates a new instance of this class.
     */
    public ReportDownloadServlet() {
        this.engine = Engine.getInstance();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String type = request.getParameter("type");
        if (type == null || !fileTypes.containsKey(type)) {
            response.sendError(404, "No or wrong type parameter given (filetype)");
        }

        String executionid = request.getParameter("executionid");
        if (executionid == null) {
            response.sendError(404, "No executionid parameter given");
        }

        String reportDesign = request.getParameter("report");
        if (reportDesign == null) {
            response.sendError(404, "No report design given");
        }
        response.setContentType(fileTypes.get(type));


        doDownload(request, response, reportDesign, executionid, type);
    }

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
    protected void doDownload(HttpServletRequest req, HttpServletResponse resp,String reportDesign, 
    		String executionid,String type) throws IOException {

        resp.setContentType("text/html"); 
        resp.setHeader("Cache-Control", "no-cache"); 
        resp.setDateHeader("Expires", 0);  
        resp.setHeader("Pragma", "No-cache");

        PrintWriter out = resp.getWriter();
        
        out.write("<iframe src=\"http://127.0.0.1:8181/gui/EuropeanaIngestionControlPanel/downloadPDF?");        
        out.write("report=" + reportDesign);
        out.write("&executionid=" + executionid);
        out.write("&type=" + type + "\"");
        out.write(" style=\"width:1200px; height:700px;\" frameborder=\"0\"></iframe>" );
        
        out.flush();
        out.close();
    }
}