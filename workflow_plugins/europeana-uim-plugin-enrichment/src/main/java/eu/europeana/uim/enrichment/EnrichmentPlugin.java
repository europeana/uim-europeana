package eu.europeana.uim.enrichment;

import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrException;
import org.apache.solr.common.SolrInputDocument;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;

import eu.annocultor.converters.europeana.Entity;
import eu.annocultor.converters.europeana.Field;
import eu.europeana.corelib.definitions.jibx.AgentType;
import eu.europeana.corelib.definitions.jibx.Concept;
import eu.europeana.corelib.definitions.jibx.LiteralType;
import eu.europeana.corelib.definitions.jibx.PlaceType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.jibx.ResourceOrLiteralType;
import eu.europeana.corelib.definitions.jibx.ResourceOrLiteralType.Lang;
import eu.europeana.corelib.definitions.jibx.ResourceType;
import eu.europeana.corelib.definitions.jibx.TimeSpanType;
import eu.europeana.corelib.definitions.model.EdmLabel;
import eu.europeana.corelib.definitions.solr.beans.FullBean;
import eu.europeana.corelib.dereference.impl.RdfMethod;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.server.EdmMongoServer;
import eu.europeana.corelib.solr.server.impl.EdmMongoServerImpl;
import eu.europeana.corelib.solr.utils.EseEdmMap;
import eu.europeana.corelib.solr.utils.MongoConstructor;
import eu.europeana.corelib.solr.utils.SolrConstructor;
import eu.europeana.corelib.tools.lookuptable.EuropeanaId;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;
import eu.europeana.corelib.tools.utils.HashUtils;
import eu.europeana.corelib.tools.utils.PreSipCreatorUtils;
import eu.europeana.corelib.tools.utils.SipCreatorUtils;
import eu.europeana.uim.api.AbstractIngestionPlugin;
import eu.europeana.uim.api.CorruptedMetadataRecordException;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.enrichment.service.EnrichmentService;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.EuropeanaRetrievableField;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.sugarcrm.SugarCrmRecord;
import eu.europeana.uim.sugarcrm.SugarCrmService;
//import eu.europeana.uim.enrichment.osgi.MongoConstructor;
import eu.europeana.uim.enrichment.utils.OsgiEdmMongoServer;

public class EnrichmentPlugin extends AbstractIngestionPlugin {
	private static String vocabularyDB;

	private static CommonsHttpSolrServer solrServer;

	private static String mongoDB;
	private static String mongoHost = "127.0.0.1";
	private static String mongoPort = "27017";
	private static String solrUrl;
	private static String solrCore;
	private static int recordNumber;
	private static String europeanaID;
	private static final int RETRIES = 10;
	private static String repository;
	private static SugarCrmService sugarCrmService;
	private static EnrichmentService enrichmentService;
	private static String previewsOnlyInPortal;
	private static String collections;
	private static Morphia morphia;

	public EnrichmentPlugin(String name, String description) {
		super(name, description);
		// TODO Auto-generated constructor stub
	}

	public EnrichmentPlugin() {
		super("", "");
		// TODO Auto-generated constructor stub
	}

	private static final Logger log = Logger.getLogger(EnrichmentPlugin.class
			.getName());
	/**
	 * The parameters used by this WorkflowStart
	 */
	private static final List<String> params = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

	};

	public TKey<?, ?>[] getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public TKey<?, ?>[] getOptionalFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public TKey<?, ?>[] getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public void shutdown() {
		// TODO Auto-generated method stub

	}

	public List<String> getParameters() {
		// TODO Auto-generated method stub
		return params;
	}

	public int getPreferredThreadCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	public int getMaximumThreadCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	public <I> void initialize(ExecutionContext<I> context)
			throws IngestionPluginFailedException {
		// TODO Auto-generated method stub

		try {

			solrServer = enrichmentService.getSolrServer();
			@SuppressWarnings("rawtypes")
			Collection collection = (Collection) context.getExecution()
					.getDataSet();
			String sugarCrmId = collection
					.getValue(ControlledVocabularyProxy.SUGARCRMID);
			SugarCrmRecord sugarCrmRecord = sugarCrmService
					.retrieveRecord(sugarCrmId);
			previewsOnlyInPortal = sugarCrmRecord
					.getItemValue(EuropeanaRetrievableField.PREVIEWS_ONLY_IN_PORTAL);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public <I> void completed(ExecutionContext<I> context)
			throws IngestionPluginFailedException {
		try {
			solrServer.commit();
			solrServer.optimize();

		} catch (IOException e) {
			context.getLoggingEngine().logFailed(
					Level.SEVERE,
					this,
					e,
					"Input/Output exception occured in Solr with the following message: "
							+ e.getMessage());
		} catch (SolrServerException e) {
			context.getLoggingEngine().logFailed(
					Level.SEVERE,
					this,
					e,
					"Solr server exception occured in Solr with the following message: "
							+ e.getMessage());
		}

	}

	public <I> boolean processRecord(MetaDataRecord<I> mdr,
			ExecutionContext<I> context) throws IngestionPluginFailedException,
			CorruptedMetadataRecordException {
		IBindingFactory bfact;
		MongoConstructor mongoConstructor = new MongoConstructor();

		try {
			morphia = new Morphia();
			Mongo mongo = new Mongo(mongoHost, Integer.parseInt(mongoPort));
			mongoDB = enrichmentService.getMongoDB();
			OsgiEdmMongoServer mongoServer = new OsgiEdmMongoServer(mongo,
					mongoDB, "", "");
		  

			mongoServer.createDatastore(morphia);
			bfact = BindingDirectory.getFactory(RDF.class);

			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();

			String value = mdr.getValues(
					EuropeanaModelRegistry.EDMDEREFERENCEDRECORD).get(0);
			RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));
			List<Entity> entities = enrichmentService
					.enrich(new SolrConstructor().constructSolrDocument(rdf));
			mergeEntities(rdf, entities);
			SolrInputDocument solrInputDocument = new SolrConstructor()
					.constructSolrDocument(rdf);
			FullBeanImpl fullBean = mongoConstructor.constructFullBean(rdf,
					mongoServer);
			solrInputDocument.addField(
					EdmLabel.PREVIEW_NO_DISTRIBUTE.toString(),
					previewsOnlyInPortal);
			fullBean.getAggregations()
					.get(0)
					.setEdmPreviewNoDistribute(
							Boolean.parseBoolean(previewsOnlyInPortal));
			String collectionId = (String) mdr.getCollection().getId();

			String fileName = (String) mdr.getCollection().getName();
			String hash = hashExists(collectionId, fileName, fullBean);

			if (StringUtils.isNotEmpty(hash)) {
				createLookupEntry(mongo, fullBean, hash);
			}

			fullBean.setEuropeanaCollectionName(new String[] { fileName });
			if (mongoServer.getFullBean(fullBean.getAbout()) == null) {
				
				mongoServer.getDatastore().save(fullBean);
			}

			int retries = 0;
			while (retries < RETRIES) {
				try {
					solrServer.add(solrInputDocument);
					retries++;
					recordNumber++;
					return true;
				} catch (SolrServerException e) {
					log.log(Level.WARNING, "Solr Exception occured with error "
							+ e.getMessage() + "\nRetrying");
					retries++;
				} catch (IOException e) {
					log.log(Level.WARNING, "IO Exception occured with error "
							+ e.getMessage() + "\nRetrying");
					retries++;
				} catch (SolrException e) {
					log.log(Level.WARNING, "Solr Exception occured with error "
							+ e.getMessage() + "\nRetrying");
					retries++;
				}
			}

		} catch (JiBXException e) {
			log.log(Level.WARNING,
					"JibX Exception occured with error " + e.getMessage()
							+ "\nRetrying");
		} catch (MalformedURLException e) {
			log.log(Level.WARNING,
					"Malformed URL Exception occured with error "
							+ e.getMessage() + "\nRetrying");
		} catch (InstantiationException e) {
			log.log(Level.WARNING,
					"Instantiation Exception occured with error "
							+ e.getMessage() + "\nRetrying");
		} catch (IllegalAccessException e) {
			log.log(Level.WARNING,
					"Illegal Access Exception occured with error "
							+ e.getMessage() + "\nRetrying");
		} catch (IOException e) {
			log.log(Level.WARNING,
					"IO Exception occured with error " + e.getMessage()
							+ "\nRetrying");
		} catch (Exception e) {

			log.log(Level.WARNING,
					"Generic Exception occured with error " + e.getMessage()
							+ "\nRetrying");
			e.printStackTrace();
		}
		return false;
	}

	private void mergeEntities(RDF rdf, List<Entity> entities)
			throws SecurityException, IllegalArgumentException,
			NoSuchMethodException, IllegalAccessException,
			InvocationTargetException {

		List<RDF.Choice> choices = rdf.getChoiceList();
		for (Entity entity : entities) {
			RDF.Choice choice = new RDF.Choice();
			if (StringUtils.equals(entity.getClassName(), "Concept")) {
				Concept concept = new Concept();
				List<Field> fields = entity.getFields();
				if (fields != null && fields.size() > 0) {
					for (Field field : fields) {
						if (StringUtils.equalsIgnoreCase(field.getName(),
								"skos_concept")) {
							concept.setAbout(field
									.getValues()
									.get(field.getValues().keySet().iterator()
											.next()).get(0));
						} else {
							for (Entry<String, List<String>> entry : field
									.getValues().entrySet()) {
								for (String str : entry.getValue()) {
									appendConceptValue(concept,
											field.getName(), str, "_@xml:lang",
											entry.getKey());
								}
							}
						}

					}

					choice.setConcept(concept);
					choices.add(choice);
					rdf.setChoiceList(choices);
				}
			} else if (StringUtils.equals(entity.getClassName(), "Timespan")) {

				TimeSpanType ts = new TimeSpanType();
				List<Field> fields = entity.getFields();
				if (fields != null && fields.size() > 0) {
					for (Field field : fields) {
						if (StringUtils.equalsIgnoreCase(field.getName(),
								"edm_timespan")) {
							ts.setAbout(field
									.getValues()
									.get(field.getValues().keySet().iterator()
											.next()).get(0));
						} else {
							for (Entry<String, List<String>> entry : field
									.getValues().entrySet()) {
								for (String str : entry.getValue()) {
									appendValue(TimeSpanType.class, ts,
											field.getName(), str, "_@xml:lang",
											entry.getKey());
								}
							}
						}

					}

					choice.setTimeSpan(ts);
					choices.add(choice);
					rdf.setChoiceList(choices);
				}
			} else if (StringUtils.equals(entity.getClassName(), "Agent")) {

				AgentType ts = new AgentType();
				List<Field> fields = entity.getFields();
				if (fields != null && fields.size() > 0) {
					for (Field field : fields) {
						if (StringUtils.equalsIgnoreCase(field.getName(),
								"edm_agent")) {
							ts.setAbout(field
									.getValues()
									.get(field.getValues().keySet().iterator()
											.next()).get(0));
						} else {
							for (Entry<String, List<String>> entry : field
									.getValues().entrySet()) {
								for (String str : entry.getValue()) {
									appendValue(AgentType.class, ts,
											field.getName(), str, "_@xml:lang",
											entry.getKey());
								}
							}
						}

					}

					choice.setAgent(ts);
					choices.add(choice);
					rdf.setChoiceList(choices);
				}
			} else {
				PlaceType ts = new PlaceType();
				List<Field> fields = entity.getFields();
				if (fields != null && fields.size() > 0) {
					for (Field field : fields) {
						if (StringUtils.equalsIgnoreCase(field.getName(),
								"edm_place")) {
							ts.setAbout(field
									.getValues()
									.get(field.getValues().keySet().iterator()
											.next()).get(0));
						} else {
							for (Entry<String, List<String>> entry : field
									.getValues().entrySet()) {
								for (String str : entry.getValue()) {
									appendValue(PlaceType.class, ts,
											field.getName(), str, "_@xml:lang",
											entry.getKey());
								}
							}
						}

					}

					choice.setPlace(ts);
					choices.add(choice);
					rdf.setChoiceList(choices);
				}
			}
		}

	}

	public CommonsHttpSolrServer getSolrServer() {
		return solrServer;
	}

	public void setSolrServer(CommonsHttpSolrServer solrServer) {
		EnrichmentPlugin.solrServer = solrServer;
	}

	public void setSugarCrmService(SugarCrmService sugarCrmService) {
		EnrichmentPlugin.sugarCrmService = sugarCrmService;
	}

	public String getEuropeanaID() {
		return europeanaID;
	}

	public void setEuropeanaID(String europeanaID) {
		EnrichmentPlugin.europeanaID = europeanaID;
	}

	public int getRecords() {
		return recordNumber;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		EnrichmentPlugin.repository = repository;
	}

	public void setVocabularyDB(String vocabularyDB) {
		EnrichmentPlugin.vocabularyDB = vocabularyDB;
	}

	public String getVocabularyDB() {
		return vocabularyDB;
	}

	public String getCollections() {
		return collections;
	}

	public void setCollections(String collections) {
		EnrichmentPlugin.collections = collections;
	}

	public SugarCrmService getSugarCrmService() {
		return sugarCrmService;
	}

	public String getMongoDB() {
		return mongoDB;
	}

	public void setMongoDB(String mongoDB) {
		EnrichmentPlugin.mongoDB = mongoDB;
	}

	public String getMongoHost() {
		return mongoHost;
	}

	public void setMongoHost(String mongoHost) {
		EnrichmentPlugin.mongoHost = mongoHost;
	}

	public String getMongoPort() {
		return mongoPort;
	}

	public void setMongoPort(String mongoPort) {
		EnrichmentPlugin.mongoPort = mongoPort;
	}

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		EnrichmentPlugin.solrUrl = solrUrl;
	}

	public String getSolrCore() {
		return solrCore;
	}

	public void setSolrCore(String solrCore) {
		EnrichmentPlugin.solrCore = solrCore;
	}

	public EnrichmentService getEnrichmentService() {
		return enrichmentService;
	}

	public void setEnrichmentService(EnrichmentService enrichmentService) {
		EnrichmentPlugin.enrichmentService = enrichmentService;
	}

	private void createLookupEntry(Mongo mongo, FullBean fullBean, String hash) {
		EuropeanaIdMongoServer europeanaIdMongoServer = new EuropeanaIdMongoServer(
				mongo, europeanaID);
		EuropeanaId europeanaId = europeanaIdMongoServer
				.retrieveEuropeanaIdFromOld(hash).get(0);
		europeanaId.setNewId(fullBean.getAbout());
		europeanaIdMongoServer.saveEuropeanaId(europeanaId);

	}

	private String hashExists(String collectionId, String fileName,
			FullBean fullBean) {
		SipCreatorUtils sipCreatorUtils = new SipCreatorUtils();
		sipCreatorUtils.setRepository(repository);
		if (sipCreatorUtils.getHashField(collectionId, fileName) != null) {
			return HashUtils.createHash(EseEdmMap.valueOf(
					sipCreatorUtils.getHashField(collectionId, fileName))
					.getEdmValue(fullBean));
		}
		PreSipCreatorUtils preSipCreatorUtils = new PreSipCreatorUtils();
		preSipCreatorUtils.setRepository(repository);
		if (preSipCreatorUtils.getHashField(collectionId, fileName) != null) {
			return HashUtils.createHash(EseEdmMap.valueOf(
					preSipCreatorUtils.getHashField(collectionId, fileName))
					.getEdmValue(fullBean));
		}
		return null;
	}

	private <T> T appendValue(Class<T> clazz, T obj, String edmLabel,
			String val, String edmAttr, String valAttr)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		RdfMethod RDF = null;
		for (RdfMethod rdfMethod : RdfMethod.values()) {
			if (StringUtils.equals(rdfMethod.getSolrField(), edmLabel)) {
				RDF = rdfMethod;
			}
		}

		//
		if (RDF.getMethodName().endsWith("List")) {

			Method mthd = clazz.getMethod(RDF.getMethodName());

			List lst = mthd.invoke(obj) != null ? (ArrayList) mthd.invoke(obj)
					: new ArrayList();
			if (RDF.getClazz().getSuperclass()
					.isAssignableFrom(ResourceType.class)) {

				ResourceType rs = new ResourceType();
				rs.setResource(val);
				lst.add(RDF.returnObject(RDF.getClazz(), rs));

			} else if (RDF.getClazz().getSuperclass()
					.isAssignableFrom(ResourceOrLiteralType.class)) {
				ResourceOrLiteralType rs = new ResourceOrLiteralType();
				if (isURI(val)) {
					rs.setResource(val);
				} else {
					rs.setString(val);
				}
				if (edmAttr != null
						&& StringUtils.equals(
								StringUtils.split(edmAttr, "@")[1], "xml:lang")) {
					Lang lang = new Lang();
					lang.setLang(valAttr);
					rs.setLang(lang);
				}
				lst.add(RDF.returnObject(RDF.getClazz(), rs));
			} else if (RDF.getClazz().getSuperclass()
					.isAssignableFrom(LiteralType.class)) {
				LiteralType rs = new LiteralType();
				rs.setString(val);
				if (edmAttr != null
						&& StringUtils.equals(
								StringUtils.split(edmAttr, "@")[1], "xml:lang")) {
					LiteralType.Lang lang = new LiteralType.Lang();
					lang.setLang(valAttr);
					rs.setLang(lang);
				}
				lst.add(RDF.returnObject(RDF.getClazz(), rs));
			}

			Class<?>[] cls = new Class<?>[1];
			cls[0] = List.class;
			Method method = obj.getClass()
					.getMethod(
							StringUtils.replace(RDF.getMethodName(), "get",
									"set"), cls);
			method.invoke(obj, lst);
		} else {
			if (RDF.getClazz().isAssignableFrom(ResourceType.class)) {
				ResourceType rs = new ResourceType();
				rs.setResource(val);
				Class<?>[] cls = new Class<?>[1];
				cls[0] = RDF.getClazz();
				Method method = obj.getClass().getMethod(
						StringUtils.replace(RDF.getMethodName(), "get", "set"),
						cls);
				method.invoke(obj, RDF.returnObject(RDF.getClazz(), rs));
			} else if (RDF.getClazz().isAssignableFrom(LiteralType.class)) {
				LiteralType rs = new LiteralType();
				rs.setString(val);
				if (edmAttr != null
						&& StringUtils.equals(
								StringUtils.split(edmAttr, "@")[1], "xml:lang")) {
					LiteralType.Lang lang = new LiteralType.Lang();
					lang.setLang(valAttr);
					rs.setLang(lang);
				}
				Class<?>[] cls = new Class<?>[1];
				cls[0] = RDF.getClazz();
				Method method = obj.getClass().getMethod(
						StringUtils.replace(RDF.getMethodName(), "get", "set"),
						cls);
				method.invoke(obj, RDF.returnObject(RDF.getClazz(), rs));

			} else if (RDF.getClazz().isAssignableFrom(
					ResourceOrLiteralType.class)) {
				ResourceOrLiteralType rs = new ResourceOrLiteralType();
				if (isURI(val)) {
					rs.setResource(val);
				} else {
					rs.setString(val);
				}
				if (edmAttr != null
						&& StringUtils.equals(
								StringUtils.split(edmAttr, "@")[1], "xml:lang")) {
					Lang lang = new Lang();
					lang.setLang(valAttr);
					rs.setLang(lang);
				}
				Class<?>[] cls = new Class<?>[1];
				cls[0] = clazz;
				Method method = obj.getClass().getMethod(
						StringUtils.replace(RDF.getMethodName(), "get", "set"),
						cls);
				method.invoke(obj, RDF.returnObject(RDF.getClazz(), rs));
			}
		}

		//
		return obj;
	}

	private Concept appendConceptValue(Concept concept, String edmLabel,
			String val, String edmAttr, String valAttr)
			throws SecurityException, NoSuchMethodException,
			IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
		RdfMethod RDF = null;
		for (RdfMethod rdfMethod : RdfMethod.values()) {
			if (StringUtils.equals(rdfMethod.getSolrField(), edmLabel)) {
				RDF = rdfMethod;
				break;
			}
		}
		List<Concept.Choice> lst = concept.getChoiceList() != null ? concept
				.getChoiceList() : new ArrayList<Concept.Choice>();
		if (RDF.getClazz().getSuperclass().isAssignableFrom(ResourceType.class)) {

			ResourceType obj = new ResourceType();
			obj.setResource(val);
			Class<?>[] cls = new Class<?>[1];
			cls[0] = RDF.getClazz();
			Concept.Choice choice = new Concept.Choice();
			Method method = choice.getClass()
					.getMethod(
							StringUtils.replace(RDF.getMethodName(), "get",
									"set"), cls);
			method.invoke(choice, RDF.returnObject(RDF.getClazz(), obj));
			lst.add(choice);

		} else if (RDF.getClazz().getSuperclass()
				.isAssignableFrom(ResourceOrLiteralType.class)) {

			ResourceOrLiteralType obj = new ResourceOrLiteralType();

			if (isURI(val)) {
				obj.setResource(val);
			} else {
				obj.setString(val);
			}
			if (edmAttr != null
					&& StringUtils.equals(StringUtils.split(edmAttr, "@")[1],
							"xml:lang")) {
				Lang lang = new Lang();
				lang.setLang(valAttr);
				obj.setLang(lang);
			}
			Class<?>[] cls = new Class<?>[1];
			cls[0] = RDF.getClazz();
			Concept.Choice choice = new Concept.Choice();
			Method method = choice.getClass()
					.getMethod(
							StringUtils.replace(RDF.getMethodName(), "get",
									"set"), cls);
			method.invoke(choice, RDF.returnObject(RDF.getClazz(), obj));
			lst.add(choice);

		} else if (RDF.getClazz().getSuperclass()
				.isAssignableFrom(LiteralType.class)) {
			LiteralType obj = new LiteralType();
			obj.setString(val);
			if (edmAttr != null) {
				LiteralType.Lang lang = new LiteralType.Lang();
				lang.setLang(valAttr);
				obj.setLang(lang);
			}
			Class<?>[] cls = new Class<?>[1];
			cls[0] = RDF.getClazz();
			Concept.Choice choice = new Concept.Choice();
			Method method = choice.getClass()
					.getMethod(
							StringUtils.replace(RDF.getMethodName(), "get",
									"set"), cls);
			method.invoke(choice, RDF.returnObject(RDF.getClazz(), obj));
			lst.add(choice);
		}
		concept.setChoiceList(lst);
		return concept;
	}

	private static boolean isURI(String uri) {

		try {
			new URL(uri);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}

	}
}
