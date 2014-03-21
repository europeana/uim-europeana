package eu.europeana.uim.gui.cp.server;

import java.io.IOException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.mongodb.Mongo;

import eu.europeana.corelib.dereference.VocabularyMongoServer;
import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
import eu.europeana.corelib.dereference.impl.VocabularyMongoServerImpl;
import eu.europeana.uim.gui.cp.server.util.PropertyReader;
import eu.europeana.uim.gui.cp.server.util.UimConfigurationProperty;

public class CopyMappingServlet extends HttpServlet {
	/**
	 * Initialization method
	 */
	public void init(ServletConfig config) throws ServletException {
		super.init(config);

	}

	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {
		String selectedVocabulary = request.getParameter("selectedVocabulary");
		String vocabularyName = request.getParameter("vocabularyName");
		String vocabularyURI = request.getParameter("vocabularyURI");
		String[] vocabularyRules = request.getParameter("vocabularyRules")
				.split(" ");
		VocabularyMongoServer server = new VocabularyMongoServerImpl(
				new Mongo(
						PropertyReader
								.getProperty(UimConfigurationProperty.MONGO_HOSTURL),
						Integer.parseInt(PropertyReader
								.getProperty(UimConfigurationProperty.MONGO_HOSTPORT))),
				PropertyReader
						.getProperty(UimConfigurationProperty.MONGO_DB_VOCABULARY));
		ControlledVocabularyImpl voc = server
				.getControlledVocabularyByName(selectedVocabulary);
		ControlledVocabularyImpl copyVoc = new ControlledVocabularyImpl();
		copyVoc.setName(vocabularyName);
		copyVoc.setURI(vocabularyURI);
		copyVoc.setRules(vocabularyRules);
		copyVoc.setIterations(voc.getIterations());
		copyVoc.setElements(voc.getElements());
		server.getDatastore().save(copyVoc);
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.getWriter()
				.print("The mapping was created successfully.");
		response.flushBuffer();
	}
}
