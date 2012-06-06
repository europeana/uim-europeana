package eu.europeana.uim.gui.cp.server;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
/**
 * Servlet with upload capabilities for local files
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public class UploadServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8392834299256226392L;

	private static String TMP_DIR_PATH = "/tmp";
	private File tmpDir;
	private static String DESTINATION_DIR_PATH = "/export/repository";
	private File destinationDir;

	/**
	 * Initialization method
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

	}

	/**
	 * Method for handling GET requests. Left here for debugging reasons, 
	 * will be removed in the final version as FileUploadServlet does NOT support GET requiests
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Method for handling POST requests. It uses two repositories (a temp and a produxation)
	 */
	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		tmpDir = new File(TMP_DIR_PATH);
		if (!tmpDir.isDirectory()) {
			throw new ServletException(TMP_DIR_PATH + " is not a directory");
		}

		destinationDir = new File(DESTINATION_DIR_PATH);
		if (!destinationDir.isDirectory()) {
			throw new ServletException(DESTINATION_DIR_PATH
					+ " is not a directory");
		}

		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		/*
		 * Set the size threshold, above which content will be stored on disk.
		 */
		fileItemFactory.setSizeThreshold(1024 * 1024); // 1 MB
		/*
		 * Set the temporary directory to store the uploaded files of size above
		 * threshold.
		 */
		fileItemFactory.setRepository(tmpDir);

		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		try {
			
			
			@SuppressWarnings("unchecked")
			List<FileItem> items = uploadHandler.parseRequest(request);

			for (FileItem item : items) {
				if (item.isFormField())
					continue;


				

				File file = new File(destinationDir, item.getName());
				file.createNewFile();
				item.write(file);
				response.setStatus(HttpServletResponse.SC_CREATED);
				response.getWriter()
						.print("The file was created successfully.");
				response.flushBuffer();
				
			}
		} catch (FileUploadException ex) {
			ex.printStackTrace();
			response.setStatus(HttpServletResponse.SC_UNSUPPORTED_MEDIA_TYPE);
			response.getWriter().print("The file was not created");
			response.flushBuffer();
		} catch (Exception ex) {
			ex.printStackTrace();
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			response.getWriter().print("The file was not created");
			response.flushBuffer();
		}

	}

}
