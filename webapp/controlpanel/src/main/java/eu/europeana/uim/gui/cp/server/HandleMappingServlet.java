package eu.europeana.uim.gui.cp.server;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.UnknownHostException;
import java.util.List;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.europeana.corelib.dereference.VocabularyMongoServer;
import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
import eu.europeana.corelib.dereference.impl.VocabularyMongoServerImpl;
import eu.europeana.uim.gui.cp.server.util.PropertyReader;
import eu.europeana.uim.gui.cp.server.util.UimConfigurationProperty;

public class HandleMappingServlet extends HttpServlet {
	VocabularyMongoServer server;

	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		try {
			server = new VocabularyMongoServerImpl(
			    MongoProvider.getMongo(),
					PropertyReader
							.getProperty(UimConfigurationProperty.MONGO_DB_VOCABULARY));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
		/*
		 * Set the size threshold, above which content will be stored on disk.
		 */
		fileItemFactory.setSizeThreshold(1024 * 1024); // 1 MB
		/*
		 * Set the temporary directory to store the uploaded files of size above
		 * threshold.
		 */
		fileItemFactory.setRepository(new File(""));

		ServletFileUpload uploadHandler = new ServletFileUpload(fileItemFactory);
		try {

			@SuppressWarnings("unchecked")
			List<FileItem> items = uploadHandler.parseRequest(request);
			for (FileItem item : items) {
				if (!item.isFormField()) {
					InputStream is = item.getInputStream();
					System.out.println(is.available());
					if (is.available() > 0) {
						ObjectInputStream ois = new ObjectInputStream(is);

						ControlledVocabularyImpl voc = (ControlledVocabularyImpl) ois
								.readObject();

						server.getDatastore().save(voc);
						response.setStatus(HttpServletResponse.SC_CREATED);
						response.getWriter().print(
								"The mapping was uploaded successfully.");
						response.flushBuffer();
						is.close();
					}
				}
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

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) {
		String vocabularyName = request.getParameter("vocabularyName");
		ControlledVocabularyImpl voc = server
				.getControlledVocabularyByName(vocabularyName);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();

		try {
			response.setStatus(HttpServletResponse.SC_OK);
			response.setContentType("application/octet-stream");
			response.setHeader("Content-Disposition", "attachment; filename="
					+ vocabularyName + ".ser");
			ObjectOutputStream out = new ObjectOutputStream(baos);
			out.writeObject(voc);
			ServletOutputStream sout = response.getOutputStream();
			byte[] b = baos.toByteArray();
			sout.write(b);
			sout.close();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
