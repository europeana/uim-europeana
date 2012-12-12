/* LinkcheckServer.java - created on Jul 15, 2011, Copyright (c) 2011 The European Library, all rights reserved */
package eu.europeana.uim.plugin.thumbler.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.List;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.theeuropeanlibrary.collections.guarded.Guarded;
import org.theeuropeanlibrary.model.common.Link;
import org.theeuropeanlibrary.uim.check.weblink.http.AbstractWeblinkServer;
import org.theeuropeanlibrary.uim.check.weblink.http.GuardedMetaDataRecordUrl;
import org.theeuropeanlibrary.uim.check.weblink.http.HttpClientSetup;
import org.theeuropeanlibrary.uim.check.weblink.http.Submission;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.europeana.corelib.db.dao.impl.NosqlDaoImpl;
import eu.europeana.corelib.db.entity.nosql.ImageCache;
import eu.europeana.corelib.db.exception.DatabaseException;
import eu.europeana.corelib.db.service.ThumbnailService;
import eu.europeana.corelib.db.service.impl.ThumbnailServiceImpl;
import eu.europeana.corelib.definitions.jibx.Aggregation;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.uim.model.europeana.EuropeanaLink;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.plugin.thumbler.utils.ImageMagickUtils;
import eu.europeana.uim.plugin.thumbler.utils.PropertyReader;
import eu.europeana.uim.plugin.thumbler.utils.UimConfigurationProperty;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.MetaDataRecord.QualifiedValue;
import eu.europeana.uim.store.UimDataSet;

/**
 * HTTP Link checker with internal thread pool using the @see
 * {@link HttpClientSetup} to check the status of links. Initially we try the
 * HEAD method which would be optimal but not widely supported.
 * 
 * If the HEAD method is not supported by the webserver we do use as fallback
 * forth on the GET method.
 * 
 * 
 * @author Andreas Juffinger (andreas.juffinger@kb.nl)
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since Jul 15, 2011
 */
public class EuropeanaWeblinkThumbler extends AbstractWeblinkServer {
	private static final Logger log = Logger
			.getLogger(EuropeanaWeblinkThumbler.class.getName());

	private static EuropeanaWeblinkThumbler instance;

	private static ThumbnailService thumbnailHandler;

	private static IBindingFactory bfact;

	private static IUnmarshallingContext uctx;

	private static final String STORAGELOCATION = PropertyReader
			.getProperty(UimConfigurationProperty.UIM_STORAGE_LOCATION);

	
	//Static inializer block for Morphia
	static{
		
		Morphia mor = new Morphia();
		Mongo mongo;
		try {
			bfact = BindingDirectory.getFactory(RDF.class);
			uctx = bfact.createUnmarshallingContext();
			
			
			mongo = new Mongo(
					PropertyReader
							.getProperty(UimConfigurationProperty.MONGO_HOSTURL),
					Integer.parseInt(PropertyReader
							.getProperty(UimConfigurationProperty.MONGO_HOSTPORT)));
			System.out.println(PropertyReader
							.getProperty(UimConfigurationProperty.MONGO_HOSTURL));
			System.out.println(Integer.parseInt(PropertyReader
							.getProperty(UimConfigurationProperty.MONGO_HOSTPORT)));
			String dbName = PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_IMAGE);

			Datastore store = mor.createDatastore(mongo, dbName);

			@SuppressWarnings({ "unchecked", "rawtypes" })
			NosqlDaoImpl morphiaDAOImpl = new NosqlDaoImpl(ImageCache.class,
					store);

			ThumbnailServiceImpl thumbnailService = new ThumbnailServiceImpl();
			thumbnailService.setDao(morphiaDAOImpl);

			thumbnailHandler = thumbnailService;



		} catch (UnknownHostException e) {
			log.severe(e.getMessage());
		} catch (MongoException e) {
			log.severe(e.getMessage());
		} catch (JiBXException e) {
			log.severe(e.getMessage());
		}
		
	}
	
	/**
	 * Private singleton constructor.
	 */
	private EuropeanaWeblinkThumbler() {

	}

	/**
	 * @return the singleton thumbler instance
	 */
	public static EuropeanaWeblinkThumbler getShared() {
		if (instance == null) {
			instance = new EuropeanaWeblinkThumbler();
		}
		return instance;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.theeuropeanlibrary.uim.check.weblink.http.AbstractWeblinkServer#shutdown
	 * ()
	 */
	@Override
	public void shutdown() {
		super.shutdown();
		instance = null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.theeuropeanlibrary.uim.check.weblink.http.AbstractWeblinkServer#
	 * createTask(org.theeuropeanlibrary.collections.guarded.Guarded)
	 */
	@Override
	public Runnable createTask(Guarded guarded) {
		return new CacheTask((GuardedMetaDataRecordUrl<?>) guarded);
	}

	/**
	 * @author Andreas Juffinger (andreas.juffinger@kb.nl)
	 * @since Jul 15, 2011
	 */
	private class CacheTask implements Runnable {
		private final GuardedMetaDataRecordUrl<?> guarded;

		public CacheTask(GuardedMetaDataRecordUrl<?> guarded) {
			this.guarded = guarded;
		}

		@Override
		public void run() {
			if (guarded != null) {
				store();
			}
		}

		/**
		 * Intitializes the entire store operation
		 * 
		 * @return file with downloaded content
		 */
		protected void store() {

			try {

				UimDataSet<?> dataset = guarded.getExecution().getDataSet();
				MetaDataRecord<?> mdr = guarded.getMetaDataRecord();
				QualifiedValue<String> value = mdr.getFirstQualifiedValue(EuropeanaModelRegistry.EDMRECORD);
				StringReader reader = new StringReader(value.getValue());
				RDF edmRecord = (RDF) uctx.unmarshalDocument(reader);
				String objectIDURI = extractOrigDocumentId(edmRecord);
				Link offeredlink = guarded.getLink();

				List<QualifiedValue<EuropeanaLink>> europeanalinks = mdr
						.getQualifiedValues(EuropeanaModelRegistry.EUROPEANALINK);

				for (QualifiedValue<EuropeanaLink> eulink : europeanalinks) {
					if (offeredlink.getUrl().equals(eulink.getValue().getUrl())
							&& eulink.getValue().isCacheable()) {
						storefileInMongo(offeredlink.getUrl(), dataset,
								edmRecord);
					}
				}

			} catch (Throwable t) {
				Submission submission = getSubmission(guarded.getExecution());

				synchronized (submission) {
					submission.incrStatus(1);
					submission.incrExceptions();
					submission.removeRemaining(guarded);
				}

				log.info("Failed to store and process file locally"
						+ guarded.getUrl() + ">");
				guarded.processed(1, t.getMessage());
				log.severe(t.getClass().getName());
			}

		}

		/**
		 * @param resource
		 * @param dataset
		 * @param edmRecord
		 * @throws IOException
		 * @throws DatabaseException
		 */
		private void storefileInMongo(String resource, UimDataSet<?> dataset,
				RDF edmRecord) throws IOException, DatabaseException {

			File img = retrieveFile(resource);
			File convimg = ImageMagickUtils.convert(img);
			BufferedImage buff = ImageIO.read(convimg);
			Collection coll = (Collection) dataset;
			thumbnailHandler.storeThumbnail(resource, (String) coll.getId(),
					buff, resource, edmRecord);
		}

		/**
		 * Retrieves a file given a URI
		 * 
		 * @param url
		 * @return
		 */
		private File retrieveFile(String url) {

			Submission submission = getSubmission(guarded.getExecution());
			File target = null;
			int status = 0;
			try {
				String name = guarded.getUrl().getFile();
				
				target = new File(STORAGELOCATION
						+ URLEncoder.encode(name, "UTF-8"));
				FileUtils.copyURLToFile(new URL(url), target, 10000000,
						1000000000);

				guarded.processed(status,
						"filename: " + target.getAbsolutePath());

				synchronized (submission) {
					submission.incrStatus(status);
					submission.incrProcessed();
					submission.removeRemaining(guarded);
				}

				return target;

			} catch (Throwable t) {

				log.info("Failed to retrieve url: <" + guarded.getUrl() + ">");

			}

			return target;
		}

		/**
		 * Extracts the original document ID from the EDM document
		 * 
		 * @param edmRecord
		 * @return
		 */
		private String extractOrigDocumentId(RDF edmRecord) {
			List<Aggregation> elements = edmRecord.getAggregationList();
			for (Aggregation aggregation : elements) {
				// Deal with Aggregation elements
					if (aggregation.getObject() != null) {
						return aggregation.getObject().getResource();
					} else {
						return "";
					}
			}
			return null;

		}
	}

}
