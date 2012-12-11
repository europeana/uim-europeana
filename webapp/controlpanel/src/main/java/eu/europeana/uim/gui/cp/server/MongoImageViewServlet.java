/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.gui.cp.server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;
import eu.europeana.corelib.db.entity.nosql.Image;
import eu.europeana.corelib.db.entity.nosql.ImageCache;
import eu.europeana.corelib.db.exception.DatabaseException;
import eu.europeana.corelib.db.service.ThumbnailService;
import eu.europeana.corelib.db.service.impl.ThumbnailServiceImpl;
import eu.europeana.corelib.definitions.model.ThumbSize;
import eu.europeana.uim.storage.StorageEngine;
import eu.europeana.uim.storage.StorageEngineException;
import eu.europeana.uim.gui.cp.server.engine.ExpandedOsgiEngine;
import eu.europeana.uim.gui.cp.server.util.PropertyReader;
import eu.europeana.uim.gui.cp.server.util.UimConfigurationProperty;
import eu.europeana.uim.model.europeana.EuropeanaLink;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.store.MetaDataRecord;

/**
 * Servlet that communicates with the underlying Mongo Image Storage Repository
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * 
 * @since 27 Jul 2012
 */
public class MongoImageViewServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private ExpandedOsgiEngine engine;
	private ThumbSize size;
	private static ThumbnailService thumbnailHandler;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest
	 * , javax.servlet.http.HttpServletResponse)
	 */
	@SuppressWarnings("restriction")
	@Override
	protected void doGet(HttpServletRequest request,
			HttpServletResponse response) throws ServletException, IOException {

		PrintWriter out = response.getWriter();
		response.setContentType("text/html");

		String recid = request.getParameter("recordID");

		@SuppressWarnings("unchecked")
		StorageEngine<String> storagenegine = (StorageEngine<String>) engine
				.getRegistry().getStorageEngine();

		try {
			MetaDataRecord<String> record = storagenegine
					.getMetaDataRecord(recid);

			List<EuropeanaLink> values = record
					.getValues(EuropeanaModelRegistry.EUROPEANALINK);

			out.write("<h1>Cached Images for record" + recid + "</h1>");

			for (EuropeanaLink value : values) {
				//if (value.isCacheable()) {

					String url = value.getUrl();

					ImageCache imgcache = thumbnailHandler
							.findByOriginalUrl(url);

					if (imgcache == null) {
						out.write("<h2>No images were cached for link with id"
								+ url + "</h2>");

					} else {

						out.write("<h2> Cached images for link " + url
								+ "</h2>");
						Map<String, Image> imgs = imgcache.getImages();

						Image big = imgs.get("LARGE");

						Image small = imgs.get("MEDIUM");

						Image tiny = imgs.get("TINY");

						String bigbase64 = new sun.misc.BASE64Encoder()
								.encode(big.getImage());
						String smallbase64 = new sun.misc.BASE64Encoder()
								.encode(small.getImage());
						String tinybase64 = new sun.misc.BASE64Encoder()
								.encode(tiny.getImage());

						String bigxmpData = thumbnailHandler.extractXMPInfo(
								imgcache.getObjectId(), imgcache.getImageId(),
								ThumbSize.LARGE);
						String mediumxmpData = thumbnailHandler.extractXMPInfo(
								imgcache.getObjectId(), imgcache.getImageId(),
								ThumbSize.MEDIUM);
						String smallxmpData = thumbnailHandler.extractXMPInfo(
								imgcache.getObjectId(), imgcache.getImageId(),
								ThumbSize.TINY);

						bigxmpData = bigxmpData.replaceAll("<", "&lt;")
								.replaceAll(">", "&gt;");
						mediumxmpData = mediumxmpData.replaceAll("<", "&lt;")
								.replaceAll(">", "&gt;");
						smallxmpData = smallxmpData.replaceAll("<", "&lt;")
								.replaceAll(">", "&gt;");

						out.write("</hr>");
						out.write("<table border=\"1\">");
						out.write("<tr><th><b>Thumbnail</b></th><th><b>Embedded XMP Metadata</b></th></tr>");
						out.write("<tr>");
						out.write("<td>");
						out.write("<h3>Large thumbnail: </h3>");
						out.write("<img src=\"data:image/jpeg;base64,"
								+ bigbase64 + "\" />");
						out.write("</td>");
						out.write("<td>");

						if (bigxmpData != null) {
							out.write("<pre>");
							out.write(bigxmpData);
							out.write("</pre>");
						} else {
							out.write("<b>NO METADATA</b>");
						}
						out.write("</td>");
						out.write("</tr>");
						out.write("<tr>");
						out.write("<td>");
						out.write("<h3>Medium thumbnail: </h3>");
						out.write("<img src=\"data:image/jpeg;base64,"
								+ smallbase64 + "\" />");
						out.write("</td>");
						out.write("<td>");
						if (mediumxmpData != null) {
							out.write("<pre>");
							out.write(mediumxmpData);
							out.write("</pre>");
						} else {
							out.write("<b>NO METADATA</b>");
						}
						out.write("</td>");
						out.write("</tr>");
						out.write("<tr>");
						out.write("<td>");
						out.write("<h3>Tiny thumbnail: </h3>");
						out.write("<img src=\"data:image/jpeg;base64,"
								+ tinybase64 + "\" />");
						out.write("</td>");
						out.write("<td>");
						if (smallxmpData != null) {
							out.write("<pre>");
							out.write(smallxmpData);
							out.write("</pre>");
						} else {
							out.write("<b>NO METADATA</b>");
						}
						out.write("</td>");
						out.write("</tr>");

						out.write("</table>");

					}
				}
			//}

		} catch (StorageEngineException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Servlet Initialization method
	 */
	public void init(ServletConfig config) throws ServletException {
		Morphia mor = new Morphia();
		Mongo mongo;
		try {
			mongo = new Mongo(
					PropertyReader
							.getProperty(UimConfigurationProperty.MONGO_HOSTURL),
					Integer.parseInt(PropertyReader
							.getProperty(UimConfigurationProperty.MONGO_HOSTPORT)));
			String dbName = PropertyReader
					.getProperty(UimConfigurationProperty.MONGO_DB_IMAGE);
			Datastore store = mor.createDatastore(mongo, dbName);

			@SuppressWarnings("unchecked")
			NosqlDaoImpl morphiaDAOImpl = new NosqlDaoImpl(ImageCache.class,
					store);

			ThumbnailServiceImpl thumbnailService = new ThumbnailServiceImpl();
			thumbnailService.setDao(morphiaDAOImpl);

			thumbnailHandler = thumbnailService;
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.engine = ExpandedOsgiEngine.getInstance();
		super.init(config);

	}
}
