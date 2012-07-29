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
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.gui.cp.server.engine.ExpandedOsgiEngine;
import eu.europeana.uim.model.europeana.EuropeanaLink;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.store.MetaDataRecord;

/**
 * @author Georgios Markakis <gwarkx@hotmail.com>
 *
 * 27 Jul 2012
 */
public class MongoImageViewServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
    private  ExpandedOsgiEngine        engine;
	private static ThumbnailService thumbnailHandler;
	
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    	
		 PrintWriter out = response.getWriter();
		 response.setContentType("text/html");
		 
    	String recid = request.getParameter("recordID");
    	 StorageEngine<String> storagenegine = (StorageEngine<String>) engine.getRegistry().getStorageEngine();
    	
    	 try {
			MetaDataRecord<String> record = storagenegine.getMetaDataRecord(recid);
			
			
			EuropeanaLink value = record.getValues(EuropeanaModelRegistry.EUROPEANALINK)
					.get(0);

			String url = value.getUrl();
			ImageCache imgcache = thumbnailHandler.findByOriginalUrl(url);
			
			if(imgcache == null){
				out.write("<h1>No images were cached for record with id " + recid+ "</h1>");
				return;
			}
			
			
			 Map<String, Image> imgs = imgcache.getImages();
			 
			 Image big = imgs.get("LARGE");
			 
			 Image small = imgs.get("MEDIUM");
			 
			 Image tiny = imgs.get("TINY");
			 
			 String bigbase64 =  new sun.misc.BASE64Encoder().encode(big.getImage());
			 
			 String smallbase64 =  new sun.misc.BASE64Encoder().encode(small.getImage());
			 
			 String tinybase64 =  new sun.misc.BASE64Encoder().encode(tiny.getImage());
			 
			 

			 out.write("<h1>Cached Images for record" + recid + "</h1>");
			 out.write("</hr>");
			 out.write("<h3>Large thumbnail: </h3>");
			 out.write("</br>");
			 out.write("<img src=\"data:image/jpeg;base64," + bigbase64 + "\" />");
			 out.write("</hr>");
			 out.write("<h3>Medium thumbnail: </h3>");
			 out.write("<img src=\"data:image/jpeg;base64," + smallbase64 + "\" />");
			 out.write("</hr>");
			 out.write("<h3>Tiny thumbnail: </h3>");
			 out.write("<img src=\"data:image/jpeg;base64," + tinybase64 + "\" />");
			 out.write("</hr>");
			
		} catch (StorageEngineException e) {
			e.printStackTrace();
		} catch (DatabaseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
    }
    
	/**
	 * Initialization method
	 */
	public void init(ServletConfig config) throws ServletException {
		Morphia mor = new Morphia();
		Mongo mongo;
		try {
			mongo = new Mongo();
			String dbName = "imageUIMDB";
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
