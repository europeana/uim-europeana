package eu.europeana.europeanauim;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import com.mongodb.Mongo;
import com.mongodb.MongoException;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.exceptions.MongoDBException;
import eu.europeana.corelib.solr.utils.MongoConstructor;
import eu.europeana.corelib.solr.utils.SolrConstructor;
import eu.europeana.europeanauim.utils.PropertyReader;
import eu.europeana.europeanauim.utils.UimConfigurationProperty;
import eu.europeana.uim.orchestration.ActiveExecution;
import eu.europeana.uim.logging.LoggingEngine;
import eu.europeana.uim.logging.LoggingEngineAdapter;
import eu.europeana.uim.deactivation.DeactivatePlugin;
import eu.europeana.uim.deactivation.service.DeactivationServiceImpl;
import eu.europeana.uim.deactivation.service.ExtendedEdmMongoServer;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.bean.CollectionBean;
import eu.europeana.uim.store.bean.ExecutionBean;
import eu.europeana.uim.store.bean.MetaDataRecordBean;
import eu.europeana.uim.store.bean.ProviderBean;

public class DeactivatePluginTest {

	private final static String RECORD = "<?xml version='1.0' encoding='UTF-8' ?>"
			+ "<rdf:RDF xmlns:edm=\"http://www.europeana.eu/schemas/edm/\" xmlns:foaf=\"http://xmlns.com/foaf/0.1\" "
			+ "xmlns:ore=\"http://www.openarchives.org/ore/terms/\" xmlns:owl=\"http://www.w3.org/2002/07/owl#\" "
			+ "xmlns:xalan=\"http://xml.apache.org/xalan\" xmlns:rdaGr2=\"http://rdvocab.info/ElementsGr2\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" xmlns:rdfs=\"http://www.w3.org/2000/01/rdf-schema#\" "
			+ "xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\" xmlns:crm=\"http://www.cidoc-crm.org/rdfs/cidoc_crm_v5.0.2_english_label.rdfs#\" "
			+ "xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:dcterms=\"http://purl.org/dc/terms/\">+"
			+ "<edm:ProvidedCHO rdf:about=\"http://purl.org/collections/apenet/physical-3_01_01-5-5_3-2149_4\"/>"
			+ "<ore:Aggregation rdf:about=\"http://purl.org/collections/apenet/aggregation-3_01_01-5-5_3-2149-2149_4\"> "
			+ "<edm:aggregatedCHO rdf:resource=\"http://purl.org/collections/apenet/physical-3_01_01-5-5_3-2149_4\"/>"
			+ "<edm:dataProvider>Nationaal Archief</edm:dataProvider>"
			+ "<edm:isShownAt rdf:resource=\"http://proxy.handle.net/10648/1b3e3658-2d08-48ca-87e9-b428c3804547\"/>"
			+ "<edm:object rdf:resource=\"http://na.memorix.nl/oai2/?image=na:col1:dat515829:00004000134.jpg\"/>"
			+ "<edm:provider>APEnet</edm:provider>"
			+ "<edm:rights rdf:resource=\"http://www.europeana.eu/rights/rr-f/\"/>"
			+ "</ore:Aggregation>"
			+ "<edm:Proxy rdf:about=\"http://purl.org/collections/apenet/physical-3_01_01-5-5_3-2149_4\">"
			+ "<dc:creator rdf:resource=\"\">Graaf van Holland</dc:creator>"
			+ "<dc:language>dutch</dc:language>"
			+ "<dc:title>\'Remissorium Philippi\'; index op de grafelijke registers door Pieter van Renesse van Beoostenzwene tot 1440; met afschrift van inv. nr. 2117. >> Pagina 4"
			+ "</dc:title><dc:type rdf:resource=\"\">item</dc:type>"
			+ "<dcterms:alternative>Context information: Inventaris van het archief van de Graven van Holland, 1189-1581 (ca. 1650) >> Graven van Holland Graven van Holland >> STUKKEN BETREFFENDE DE ZORG VOOR HET ARCHIEF >> Indexen"
			+ "</dcterms:alternative><dcterms:alternative>THIS IS A TEST OBJECT</dcterms:alternative>"
			+ "<dcterms:spatial rdf:resource=\"\">Netherlands</dcterms:spatial>"
			+ "<edm:type>TEXT</edm:type>"
			+ "</edm:Proxy>"
			+ "<edm:Proxy rdf:about=\"/proxy/http://purl.org/collections/apenet/physical-3_01_01-5-5_3-2149_4\">"

			+ "<edm:type>TEXT</edm:type>"
			+ "<edm:europeanaProxy>true</edm:europeanaProxy>"
			+ "</edm:Proxy>"
			+ "<edm:EuropeanaAggregation rdf:about=\"http://purl.org/collections/apenet/physical-3_01_01-5-5_3-2149_4\">"
			+ "<edm:aggregatedCHO rdf:resource=\"http://purl.org/collections/apenet/physical-3_01_01-5-5_3-2149_4\"/>"
			+ "<edm:country>cz</edm:country>"
			
			+ "<edm:landingPage rdf:resource=\"http://dummy\"/>"
			+ "<edm:language>eu</edm:language>"
			+ "<edm:rights>www</edm:rights>"
			+ "</edm:EuropeanaAggregation></rdf:RDF>";
	DeactivationServiceImpl dService;

	/**
	 * Test deactivation plugin
	 * 
	 * @throws MongoException
	 * @throws UnknownHostException
	 * @throws MongoDBException
	 * @throws NumberFormatException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Ignore
	@Test
	public void testDeactivation() throws NumberFormatException,
			MongoDBException, UnknownHostException, MongoException {
		String proplocation = DeactivatePluginTest.class.getProtectionDomain()
				.getCodeSource().getLocation()
				+ "uimTest.properties";
		String truncated = proplocation.replace("file:", "");
		PropertyReader.loadPropertiesFromFile(truncated);
		dService = mock(DeactivationServiceImpl.class);
		dService.initialize();

		save(RECORD);

		ActiveExecution context = mock(ActiveExecution.class);
		Collection collection = new CollectionBean("12345_test_collection",
				new ProviderBean<String>("test_provider"));
		collection.setMnemonic("12345");
		collection.setName("12345_test_collection");
		MetaDataRecord mdr = mock(MetaDataRecordBean.class);
		mdr.addValue(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD, RECORD);

		ExecutionBean execution = new ExecutionBean();
		execution.setDataSet(collection);
		Properties properties = new Properties();
		LoggingEngine logging = LoggingEngineAdapter.LONG;

		DeactivatePlugin plugin = new DeactivatePlugin("test", "test");
		plugin.setdService(dService);

		when(context.getExecution()).thenReturn(execution);
		when(context.getProperties()).thenReturn(properties);
		when(context.getLoggingEngine()).thenReturn(logging);
		when(context.getDataSet()).thenReturn(collection);
		List<String> tmpList = mock(List.class);
		tmpList.add(RECORD);
		when(mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD))
				.thenReturn(tmpList);
		when(tmpList.get(0)).thenReturn(RECORD);
		when(dService.getSolrServer())
				.thenReturn(
						new HttpSolrServer(
								PropertyReader
										.getProperty(UimConfigurationProperty.SOLR_HOSTURL)
										+ PropertyReader
												.getProperty(UimConfigurationProperty.SOLR_CORE)));
		when(dService.getMongoServer())
				.thenReturn(
						new ExtendedEdmMongoServer(
								new Mongo(
										PropertyReader
												.getProperty(UimConfigurationProperty.MONGO_HOSTURL),
										Integer.parseInt(PropertyReader
												.getProperty(UimConfigurationProperty.MONGO_HOSTPORT))),
								PropertyReader
										.getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANA),
								"", ""));
		plugin.initialize(context);

		Assert.assertTrue(plugin.process(mdr, context));

		plugin.completed(context);

		plugin.shutdown();
	}

	/**
	 * Save in solr and mongo
	 * 
	 * @param record
	 * @throws MongoException 
	 * @throws MongoDBException 
	 * @throws NumberFormatException 
	 */
	private void save(String record) throws NumberFormatException, MongoDBException, MongoException {
		IBindingFactory bfact;

		try {
			bfact = BindingDirectory.getFactory(RDF.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(RECORD));
			SolrInputDocument doc = new SolrConstructor()
					.constructSolrDocument(rdf);
			new HttpSolrServer(
					PropertyReader
							.getProperty(UimConfigurationProperty.SOLR_HOSTURL)
							+ PropertyReader
									.getProperty(UimConfigurationProperty.SOLR_CORE))
					.add(doc);
			ExtendedEdmMongoServer mongoServer = new ExtendedEdmMongoServer(
					new Mongo(
							PropertyReader
									.getProperty(UimConfigurationProperty.MONGO_HOSTURL),
							Integer.parseInt(PropertyReader
									.getProperty(UimConfigurationProperty.MONGO_HOSTPORT))),
					PropertyReader
							.getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANA),
					"", "");
			FullBeanImpl fullBean = new MongoConstructor().constructFullBean(
					rdf, mongoServer);
			mongoServer.getDatastore().save(fullBean);
		} catch (JiBXException e) {

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
