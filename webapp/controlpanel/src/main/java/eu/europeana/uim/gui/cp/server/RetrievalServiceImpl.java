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


import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.theeuropeanlibrary.model.common.qualifier.Status;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

import com.mongodb.Mongo;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

import eu.europeana.corelib.definitions.jibx.ProxyType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.model.EdmLabel;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.entity.AgentImpl;
import eu.europeana.corelib.solr.entity.AggregationImpl;
import eu.europeana.corelib.solr.entity.ConceptImpl;
import eu.europeana.corelib.solr.entity.EuropeanaAggregationImpl;
import eu.europeana.corelib.solr.entity.LicenseImpl;
import eu.europeana.corelib.solr.entity.PlaceImpl;
import eu.europeana.corelib.solr.entity.ProxyImpl;
import eu.europeana.corelib.solr.entity.TimespanImpl;
import eu.europeana.corelib.solr.entity.WebResourceImpl;
import eu.europeana.corelib.edm.exceptions.MongoDBException;
import eu.europeana.corelib.mongo.server.impl.EdmMongoServerImpl;
import eu.europeana.uim.common.BlockingInitializer;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.gui.cp.client.services.RetrievalService;
import eu.europeana.uim.gui.cp.server.util.PropertyReader;
import eu.europeana.uim.gui.cp.server.util.UimConfigurationProperty;
import eu.europeana.uim.gui.cp.shared.validation.EdmFieldRecordDTO;
import eu.europeana.uim.gui.cp.shared.validation.EdmRecordDTO;
import eu.europeana.uim.gui.cp.shared.validation.FieldValueDTO;
import eu.europeana.uim.gui.cp.shared.validation.LinkDTO;
import eu.europeana.uim.gui.cp.shared.validation.LinksResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.MetaDataRecordDTO;
import eu.europeana.uim.gui.cp.shared.validation.MetaDataResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.NGramResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.SearchResultDTO;
import eu.europeana.uim.model.europeana.EuropeanaLink;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.storage.StorageEngine;
import eu.europeana.uim.storage.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.MetaDataRecord.QualifiedValue;

/**
 * Implementation of the service for retrieval functionality (records from
 * repository, record content, search functionality).
 * 
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @since May 11, 2011
 */
@SuppressWarnings("unchecked")
public class RetrievalServiceImpl extends AbstractOSGIRemoteServiceServlet
		implements RetrievalService {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final static Logger log = Logger
			.getLogger(RetrievalServiceImpl.class.getName());

	private static HttpSolrServer solrServer;
	private static EdmMongoServerImpl mongoServer;

    // /** String PORTAL_SINGLE_RECORD_URL */
    private static  String PORTAL_SINGLE_RECORD_URL = "http://www.europeana.eu/portal/record/";
    private static  String PORTAL_PREVIEW_URL;
    private static  String REPOSITORY_PREVIEW_URL;
	
    private static IUnmarshallingContext uctx;

    private static DocumentBuilder db; 
    
	/**
	 * Creates a new instance of this class.
	 */
	public RetrievalServiceImpl() {

		super();

		PORTAL_SINGLE_RECORD_URL = PropertyReader.getProperty(UimConfigurationProperty.PORTAL_URI);
		
		PORTAL_PREVIEW_URL = PropertyReader.getProperty(UimConfigurationProperty.TESTPORTAL_URI);
		
		try {
			
			IBindingFactory bfact = BindingDirectory.getFactory(RDF.class);

			uctx = bfact
					.createUnmarshallingContext();
			
			DocumentBuilderFactory dbf = DocumentBuilderFactory
					.newInstance();
			db = dbf.newDocumentBuilder();
			
			
			
			BlockingInitializer initializer = new BlockingInitializer() {
				
				@Override
				protected void initializeInternal() {
					solrServer = new HttpSolrServer(
							PropertyReader
									.getProperty(UimConfigurationProperty.SOLR_HOSTURL)
									+ PropertyReader
											.getProperty(UimConfigurationProperty.SOLR_CORE));
					
				}
			};
			initializer.initialize(HttpSolrServer.class.getClassLoader());
			
			BlockingInitializer mongoInitializer = new BlockingInitializer() {
				
				@Override
				protected void initializeInternal() {
					try {
					  List addresses = new ArrayList();
                      String[] mongoHost = PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTURL).split(",");
                      String mongoPort = PropertyReader
                          .getProperty(UimConfigurationProperty.MONGO_HOSTPORT);
                      for (String mongoStr : mongoHost) {
                          ServerAddress address = new ServerAddress(mongoStr, Integer.parseInt(mongoPort));
                          addresses.add(address);
                      }
                      String uname =
                          PropertyReader.getProperty(UimConfigurationProperty.MONGO_USERNAME) != null ? PropertyReader
                              .getProperty(UimConfigurationProperty.MONGO_USERNAME) : "";
                      String pass =
                          PropertyReader.getProperty(UimConfigurationProperty.MONGO_PASSWORD) != null ? PropertyReader
                              .getProperty(UimConfigurationProperty.MONGO_PASSWORD) : "";
                      Mongo tgtMongo = new Mongo(addresses);
						mongoServer = new EdmMongoServerImpl(tgtMongo, PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANA), uname, pass);
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MongoDBException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (UnknownHostException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (MongoException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			};
			mongoInitializer.initialize(EdmMongoServerImpl.class.getClassLoader());
		} catch (Exception e) {
			log.log(Level.SEVERE, e.getMessage());
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.gui.cp.client.services.RetrievalService#
	 * getRecordsForCollection(java.lang.String, int, int, java.lang.String)
	 */
	@Override
	public MetaDataResultDTO getRecordsForCollection(String collectionId,
			int offset, int maxSize, String constraint) {
		StorageEngine<String> storage = (StorageEngine<String>) getEngine()
				.getRegistry().getStorageEngine();
		if (storage == null) {
			log.log(Level.SEVERE, "Storage connection is null!");
			return null;
		}

		String recordId = null;
		if (!constraint.equals("")) {
			recordId = constraint;
		}

		int maxNumber = 0;
		List<MetaDataRecord<String>> metaDataRecords = new ArrayList<MetaDataRecord<String>>();
		if (recordId == null) {
			Collection<String> collection = null;
			try {
				collection = storage.getCollection(collectionId);
			} catch (Throwable t) {
				log.log(Level.WARNING, "Could not retrieve collection for id '"
						+ collectionId + "'!", t);
			}

			if (collection != null) {
				String[] recordIds = null;
				try {
					recordIds = storage.getByCollection(collection);
					maxNumber = recordIds.length;
				} catch (Throwable t) {
					log.log(Level.WARNING,
							"Could not retrieve records for collection '"
									+ collectionId + "'!", t);
					maxNumber = -1;
				}

				if (recordIds != null && recordIds.length > 0) {
					Arrays.sort(recordIds);
					String[] subset = Arrays.copyOfRange(recordIds,
							Math.min(offset, recordIds.length - 1),
							Math.min(offset + maxSize, recordIds.length));

					try {
						metaDataRecords = storage.getMetaDataRecords(Arrays
								.asList(subset));
					} catch (Throwable t) {
						log.log(Level.WARNING,
								"Could not retrieve records for Ids '"
										+ Arrays.toString(subset) + "'!", t);
						maxNumber = -1;
					}
				}
			}
		} else {
			metaDataRecords = new ArrayList<MetaDataRecord<String>>();
			try {
				metaDataRecords.add(storage.getMetaDataRecord(recordId));
				maxNumber = 1;
			} catch (Throwable t) {
				log.log(Level.WARNING, "Could not retrieve record with Id '"
						+ recordId + "'!", t);
			}
		}


		List<MetaDataRecordDTO> results = new ArrayList<MetaDataRecordDTO>();
		for (MetaDataRecord<String> metaDataRecord : metaDataRecords) {
			if (metaDataRecord != null && metaDataRecord.getId() != null) {
				MetaDataRecordDTO record = new MetaDataRecordDTO();
				record.setId(metaDataRecord.getId());

				try {
					Status status = metaDataRecord
							.getFirstValue(EuropeanaModelRegistry.STATUS);
					
					if(status != null && status.equals(Status.DELETED)){
						record.setDeleted(true);
					}
					
					
					String creationDate = metaDataRecord
							.getFirstValue(EuropeanaModelRegistry.UIMINGESTIONDATE);
					
					record.setImportuimdate(creationDate);
					
					String updateDate = metaDataRecord
							.getFirstValue(EuropeanaModelRegistry.UIMUPDATEDDATE);
					
					record.setUpdateuimdate(updateDate); 
					
					Long initialsaveDate = metaDataRecord
							.getFirstValue(EuropeanaModelRegistry.INITIALSAVE);
					
					if(initialsaveDate != null){
						Date tmpdate = new Date(initialsaveDate);
						record.setFirstingestiondate(tmpdate.toString());
					}
					
					Long updatesaveDate = metaDataRecord
							.getFirstValue(EuropeanaModelRegistry.UPDATEDSAVE);
					
					if(updatesaveDate != null){
						Date tmpdate = new Date(updatesaveDate);
						record.setIngestionupdatedate(tmpdate.toString());
					}
					
					String edmxml = metaDataRecord
							.getFirstValue(EuropeanaModelRegistry.EDMRECORD);

					if (edmxml != null) {
						RDF rdf = (RDF) uctx
								.unmarshalDocument(new StringReader(edmxml));

								ProxyType cho = rdf.getProxyList().get(0);

								List<eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice> dctypelist = cho
										.getChoiceList();
								if (dctypelist != null) {
									for (eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice dcchoice : dctypelist) {

										if (dcchoice.ifTitle()) {
											record.setTitle(dcchoice.getTitle()
													.getString());
										}
									}
								}
					}

				} catch (JiBXException e) {

					e.printStackTrace();
				}

				results.add(record);
			}
		}
		
		
		int deleted_records = 0;
		int active_records = maxNumber;
		
		Collection<String> collection;
		try {
			collection = storage.getCollection(collectionId);
			deleted_records = collection.getValue("Deleted") == null? 0 :  Integer.parseInt(collection.getValue("Deleted"));
		    active_records = maxNumber - deleted_records;
		} catch (StorageEngineException e) {
			e.printStackTrace();
		}
		

		MetaDataResultDTO response =  new MetaDataResultDTO(results, maxNumber,active_records ,deleted_records);
		
		return response;
	}

	@Override
	public String getRawRecord(String recordId) {
		String res = "";

		StorageEngine<String> storage = (StorageEngine<String>) getEngine()
				.getRegistry().getStorageEngine();
		if (storage == null) {
			log.log(Level.SEVERE, "Storage connection is null!");
		} else {
			MetaDataRecord<String> metaDataRecord = null;
			try {
				metaDataRecord = storage.getMetaDataRecord(recordId);
			} catch (Throwable t) {
				log.log(Level.WARNING, "Could not retrieve record for id '"
						+ recordId + "'!", t);
			}

			if (metaDataRecord == null) {
				log.log(Level.WARNING, "Could not find record with id '"
						+ recordId + "'!");
			} else {

			}
		}

		return res;
	}

	/**
	 * @param recordId
	 * @param edmrecord
	 * @return
	 */
	private String getXml(String recordId,
			TKey<EuropeanaModelRegistry, String> edmrecord) {
		String res = "";

		StorageEngine<String> storage = (StorageEngine<String>) getEngine()
				.getRegistry().getStorageEngine();
		if (storage == null) {
			log.log(Level.SEVERE, "Storage connection is null!");
		} else {
			MetaDataRecord<String> metaDataRecord = null;
			try {
				metaDataRecord = storage.getMetaDataRecord(recordId);
			} catch (Throwable t) {
				log.log(Level.WARNING, "Could not retrieve record for id '"
						+ recordId + "'!", t);
			}

			if (metaDataRecord == null) {
				log.log(Level.WARNING, "Could not find record with id '"
						+ recordId + "'!");
			} else {

				res = metaDataRecord.getFirstValue(edmrecord);

				try {
					InputSource is = new InputSource(new StringReader(res));
					synchronized(db){
						Document doc = db.parse(is);
						res = getFormattedXml(doc);
					}
					
					
				} catch (Throwable t) {
					log.log(Level.WARNING, "Could not format xml for '"
							+ recordId + "'!", t);

				}

			}
		}

		return res;
	}

	@Override
	public String getXmlRecord(String recordId) {
		return getXml(recordId, EuropeanaModelRegistry.EDMRECORD);

	}

	@Override
	public String getDereferencedRecord(String recordId) {
		return getXml(recordId, EuropeanaModelRegistry.EDMDEREFERENCEDRECORD);
	}



	@Override
	public SearchResultDTO searchIndex(String searchQuery, int offset,
			int maxSize, String[] facets) {

		throw new UnsupportedOperationException("Not Implemented Yet");

	}

	@Override
	public EdmRecordDTO getSearchRecord(String recordId) {
		EdmRecordDTO record = new EdmRecordDTO();
		try {

			String query = "europeana_id:" + StringUtils.replace(recordId, "/", "\\/");
			SolrQuery solrQuery = new SolrQuery().setQuery(query);

			QueryResponse response = solrServer.query(solrQuery);
			EdmFieldRecordDTO solrRecord = createSolrFields(response);
			FullBeanImpl fullBean = (FullBeanImpl) mongoServer
					.getFullBean(recordId);
			EdmFieldRecordDTO mongoRecord = createMongoFiels(fullBean);
			record.setMongoRecord(mongoRecord);
			record.setSolrRecord(solrRecord);
		} catch (SolrServerException e) {
			log.log(Level.SEVERE, e.getMessage());
			e.printStackTrace();
		} catch (MongoDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return record;
	}

	private EdmFieldRecordDTO createMongoFiels(FullBeanImpl fullBean) {
		EdmFieldRecordDTO mongoFields = new EdmFieldRecordDTO();
		List<FieldValueDTO> fieldValues = new ArrayList<FieldValueDTO>();
		fieldValues.add(MongoConverter.getFieldValues(FullBeanImpl.class,
				fullBean, "getAbout", EdmLabel.EUROPEANA_ID));
		fieldValues.add(MongoConverter.getFieldValues(FullBeanImpl.class,
				fullBean, "getCountry", EdmLabel.COUNTRY));
		fieldValues.add(MongoConverter.getFieldValues(FullBeanImpl.class,
				fullBean, "getEuropeanaCollectionName",
				EdmLabel.EUROPEANA_COLLECTIONNAME));
		fieldValues.add(MongoConverter.getFieldValues(FullBeanImpl.class,
				fullBean, "getLanguage", EdmLabel.LANGUAGE));
		fieldValues.add(MongoConverter.getFieldValues(FullBeanImpl.class,
				fullBean, "getProvider", EdmLabel.PROVIDER));
		fieldValues.add(MongoConverter.getFieldValues(FullBeanImpl.class,
				fullBean, "getTitle", EdmLabel.TITLE));
		fieldValues.add(MongoConverter.getFieldValues(FullBeanImpl.class,
				fullBean, "getType", EdmLabel.TYPE));
		fieldValues.add(MongoConverter.getFieldValues(FullBeanImpl.class,
				fullBean, "getYear", EdmLabel.YEAR));
		List<ProxyImpl> proxies = fullBean.getProxies();
		if (proxies != null) {
			for (ProxyImpl proxy : proxies) {
				fieldValues.addAll(MongoConverter.convertProxy(proxy));
			}
		}
		List<AgentImpl> agents = fullBean.getAgents();
		if (agents != null) {
			for (AgentImpl agent : agents) {
				fieldValues.addAll(MongoConverter.convertAgent(agent));
			}
		}
		List<ConceptImpl> concepts = fullBean.getConcepts();
		if (concepts != null) {
			for (ConceptImpl concept : concepts) {
				fieldValues.addAll(MongoConverter.convertConcept(concept));
			}
		}
		List<PlaceImpl> places = fullBean.getPlaces();
		if (places != null) {
			for (PlaceImpl place : places) {
				fieldValues.addAll(MongoConverter.convertPlace(place));
			}
		}
		List<TimespanImpl> timespans = fullBean.getTimespans();
		if (timespans != null) {
			for (TimespanImpl timespan : timespans) {
				fieldValues.addAll(MongoConverter.convertTimespan(timespan));
			}
		}
		List<AggregationImpl> aggregations = fullBean.getAggregations();
		if (aggregations != null) {
			for (AggregationImpl aggregation : aggregations) {
				fieldValues.addAll(MongoConverter
						.convertAggregation(aggregation));
				List<WebResourceImpl> webResources = (List<WebResourceImpl>) aggregation
						.getWebResources();
				if (webResources != null) {
					for (WebResourceImpl webResource : webResources) {
						fieldValues.addAll(MongoConverter
								.convertWebResource(webResource));
					}
				}
			}
		}

		EuropeanaAggregationImpl europeanaAggregation = (EuropeanaAggregationImpl) fullBean
				.getEuropeanaAggregation();
		if (europeanaAggregation != null) {
			fieldValues.addAll(MongoConverter
					.convertEuropeanaAggregation(europeanaAggregation));
			List<WebResourceImpl> webResources = (List<WebResourceImpl>) europeanaAggregation
					.getWebResources();
			if (webResources != null) {
				for (WebResourceImpl webResource : webResources) {
					fieldValues.addAll(MongoConverter
							.convertWebResource(webResource));
				}
			}
			
		}
		
		List<LicenseImpl> licenses = fullBean.getLicenses();
		if(licenses!=null){
			for(LicenseImpl lic:licenses){
				fieldValues.addAll(MongoConverter.convertLicense(lic));
			}
		}
		mongoFields.setFieldValue(fieldValues);
		return mongoFields;
	}

	private EdmFieldRecordDTO createSolrFields(QueryResponse response) {
		EdmFieldRecordDTO solrFields = new EdmFieldRecordDTO();
		SolrDocument doc = response.getResults().get(0);
		List<FieldValueDTO> fieldValues = new ArrayList<FieldValueDTO>();
		for (Entry<String, Object> entry : doc.entrySet()) {
			FieldValueDTO fieldValue = new FieldValueDTO();
			List<String> values = new ArrayList<String>();
			fieldValue.setFieldName(entry.getKey());
			if (entry.getValue() instanceof ArrayList) {
				for (Object obj : (ArrayList<Object>) entry.getValue()) {
					values.add(obj.toString());
				}
			}
			if (entry.getValue() instanceof String) {
				values.add((String) entry.getValue());
			}
			if (entry.getValue() instanceof Float) {
				values.add(Float.toString((Float) entry.getValue()));
			}
			if (entry.getValue() instanceof String[]) {
				for (String str : (String[]) entry.getValue()) {
					values.add(str);
				}
			}
			if(entry.getValue() instanceof Date){
				values.add(((Date) entry.getValue()).toString());
			}
			fieldValue.setFieldValue(values);
			fieldValues.add(fieldValue);
		}
		solrFields.setFieldValue(fieldValues);
		return solrFields;
	}

	@Override
	public NGramResultDTO searchSuggestions(String searchQuery, int offset,
			int maxSize) {
		throw new UnsupportedOperationException("Not Implemented Yet");
	}


	
	
	@Override
	public LinksResultDTO getLinks(String recordId) {
		
		determinelocaladress();
			
		ArrayList<LinkDTO> links = new ArrayList<LinkDTO>();
		
		StorageEngine<String> storage = (StorageEngine<String>) getEngine()
				.getRegistry().getStorageEngine();
		if (storage == null) {
			log.log(Level.SEVERE, "Storage connection is null!");
		} else {
			MetaDataRecord<String> metaDataRecord = null;
			try {
				metaDataRecord = storage.getMetaDataRecord(recordId);
			} catch (Throwable t) {
				log.log(Level.WARNING, "Could not retrieve record for id '"
						+ recordId + "'!", t);
			}

			if (metaDataRecord == null) {
				log.log(Level.WARNING, "Could not find record with id '"
						+ recordId + "'!");
			} else {
				
				
				String collid = metaDataRecord.getCollection().getMnemonic();
				String uencoderecordId = null;
				try {
					uencoderecordId = URLEncoder.encode(recordId,"UTF-8");
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				}
				
				links.add(new LinkDTO(PORTAL_SINGLE_RECORD_URL + "record"+ recordId +".html", "Record in portal"));
				
				links.add(new LinkDTO(PORTAL_PREVIEW_URL + "record" + recordId +".html", "Preview Record in test portal"));
				
				links.add(new LinkDTO(REPOSITORY_PREVIEW_URL + "?recordID=" + uencoderecordId, "Preview cahched images in repository"));

				
				List<QualifiedValue<EuropeanaLink>> qualifiedValues = metaDataRecord
						.getQualifiedValues(EuropeanaModelRegistry.EUROPEANALINK);
				if (qualifiedValues == null) {
					return new LinksResultDTO(links);
				}
				for (QualifiedValue<EuropeanaLink> qv : qualifiedValues) {

					StringBuffer description = new StringBuffer("Link in record - Type :");

					EuropeanaLink link = qv.getValue();
					
					if(link.isCacheable()){
						description.append("cacheable object");
					}
					else{
						description.append("non-cacheable object");
					}
					
					if (link != null)
						links.add(new LinkDTO(link.getUrl(), description.toString()));
				}
			}
		}

		LinksResultDTO linksResult = new LinksResultDTO(links);
		return linksResult;
	}
	
	
	/**
	 * Private method that determines the application context so that this does not have to be defined in configuration properties.
	 * Ideally this should be put in the context of an overriden init() method but unfortunately it does not work there.
	 */
	private void determinelocaladress(){
		
		if(REPOSITORY_PREVIEW_URL == null){
    	HttpServletRequest request = this.getThreadLocalRequest();
        String scheme = request.getScheme();            
        String serverName = request.getServerName();     
        int serverPort = request.getServerPort();        
        String contextPath = request.getContextPath();   
        REPOSITORY_PREVIEW_URL = scheme+"://"+serverName+":"+serverPort+contextPath + "/EuropeanaIngestionControlPanel/mongoImageView";
		}
	}
	
	private String getFormattedXml(Document doc) throws Exception {
		String xml = "";
		TransformerFactory transFactory = TransformerFactory.newInstance();
		Transformer transformer = transFactory.newTransformer();

		transformer.setOutputProperty(OutputKeys.ENCODING, "utf-8");
		transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "no");
		transformer.setOutputProperty(OutputKeys.INDENT, "yes");
		transformer.setOutputProperty(
				"{http://xml.apache.org/xslt}indent-amount", "2");

		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);
		DOMSource source = new DOMSource(doc);

		transformer.transform(source, result);

		xml = writer.toString();
		return xml;
	}

	@Override
	public String getEnrichedRecord(String recordId) {
	
		return getXml(recordId, EuropeanaModelRegistry.EDMENRICHEDRECORD);
	}
}
