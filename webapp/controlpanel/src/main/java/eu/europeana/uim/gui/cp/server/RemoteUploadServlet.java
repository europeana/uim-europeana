package eu.europeana.uim.gui.cp.server;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.disk.DiskFileItemFactory;

/**
 * Serlvet for uploading files from URLs
 * 
 * @author Yorgos.Mamakis@ kb.nl
 */
public class RemoteUploadServlet extends HttpServlet {

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
	 * Left here for debugging reasons
	 */
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		doPost(request, response);
	}

	/**
	 * Hadling POST methods. A file is retrieved from a URL and saved in the
	 * local repository
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

		try {
			/*
			 * Parse the request
			 */

			String remoteLocation = request.getParameter("url");
			String name = request.getParameter("vocabularyName");
			if (isUrl(remoteLocation)) {
				URLConnection urlConnection = new URL(remoteLocation)
						.openConnection();
				int in = 0;

				InputStream inputStream = urlConnection.getInputStream();
				OutputStream fileOutputStream = new BufferedOutputStream(
						new FileOutputStream(DESTINATION_DIR_PATH + "/" + name));
				byte[] buffer = new byte[1024];
				while ((in = inputStream.read(buffer)) != -1) {
					fileOutputStream.write(buffer, 0, in);
				}
				inputStream.close();
				fileOutputStream.close();

			}
		} catch (Exception ex) {
			ex.printStackTrace();
			response.setStatus(HttpServletResponse.SC_SERVICE_UNAVAILABLE);
			response.getWriter().print("The file was not created");
			response.flushBuffer();
		}

	}

	private boolean isUrl(String location) {
		try {
			new URL(location);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
}
