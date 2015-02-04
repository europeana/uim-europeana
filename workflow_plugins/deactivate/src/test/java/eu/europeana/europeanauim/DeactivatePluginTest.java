package eu.europeana.europeanauim;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Properties;

import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.entity.RestNode;
import org.neo4j.rest.graphdb.index.RestIndex;

import com.mongodb.Mongo;
import com.mongodb.MongoException;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.edm.exceptions.MongoDBException;
import eu.europeana.corelib.edm.utils.MongoConstructor;
import eu.europeana.corelib.lookup.impl.CollectionMongoServerImpl;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;
import eu.europeana.uim.deactivation.DeactivatePlugin;
import eu.europeana.uim.deactivation.service.DeactivationServiceImpl;
import eu.europeana.uim.deactivation.service.ExtendedEdmMongoServer;
import eu.europeana.uim.logging.LoggingEngine;
import eu.europeana.uim.logging.LoggingEngineAdapter;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.orchestration.ActiveExecution;
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
	private DeactivationServiceImpl dService;
	private MongodExecutable mongodExecutable;
	/**
	 * Test deactivation plugin
	 * 
	 * @throws MongoException
	 * @throws UnknownHostException
	 * @throws MongoDBException
	 * @throws NumberFormatException
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	
	@Test
	public void testDeactivation() throws NumberFormatException,
			MongoDBException, UnknownHostException, MongoException {
		
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
		dService.initialize();
		when(context.getExecution()).thenReturn(execution);
		when(context.getProperties()).thenReturn(properties);
		when(context.getLoggingEngine()).thenReturn(logging);
		when(context.getDataSet()).thenReturn(collection);
		List<String> tmpList = mock(List.class);
		tmpList.add(RECORD);
		
		
		when(mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD))
				.thenReturn(tmpList);
		when(tmpList.get(0)).thenReturn(RECORD);
		HttpSolrServer solr = mock(HttpSolrServer.class);
		ExtendedEdmMongoServer mongo = new ExtendedEdmMongoServer(new Mongo("localhost",10000), "test", "", "");
		CollectionMongoServer colMongo = mock(CollectionMongoServerImpl.class);
		when(dService.getSolrServer())
				.thenReturn(
						solr);
		when(dService.getMongoServer())
				.thenReturn(
						mongo);
		when(dService.getCollectionMongoServer()).thenReturn(colMongo);
		when(colMongo.findNewCollectionId("12345")).thenReturn("12345");
		RestGraphDatabase graphdb = mock(RestGraphDatabase.class);
		String neo4j = "edmSearch2";
		RestIndex nodeIndex = mock(RestIndex.class);
		Transaction tx = mock(Transaction.class);
		RestAPI api = mock(RestAPI.class);
		IndexHits<Node> indexhits = mock(IndexHits.class);
		ResourceIterator<Node> iter = mock(ResourceIterator.class);
		when(dService.getGraphDb()).thenReturn(graphdb);
		when(dService.getNeo4jIndex()).thenReturn("edmSearch2");
		when(graphdb.getRestAPI()).thenReturn(api);
		when(api.getIndex(neo4j)).thenReturn(nodeIndex);
		when(api.beginTx()).thenReturn(tx);
		when(nodeIndex.query("rdf_about", "/12345/*")).thenReturn(indexhits);
		when(indexhits.iterator()).thenReturn(iter);
		when(iter.hasNext()).thenReturn(true);
		when(indexhits.size()).thenReturn(1);
		when(iter.hasNext()).thenReturn(false);
		List<Node> mockNodes = mock(List.class);
		
		Node n = new RestNode("test",api);
		when(iter.next()).thenReturn(n);
		mockNodes.add(n);
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
			FullBeanImpl fullBean = new MongoConstructor().constructFullBean(
					rdf);
			ExtendedEdmMongoServer mongo = new ExtendedEdmMongoServer(new Mongo("localhost",10000), "test", "", "");
			mongo.getDatastore().save(fullBean.getAgents());
			mongo.getDatastore().save(fullBean.getAggregations());
			mongo.getDatastore().save(fullBean.getConcepts());
			mongo.getDatastore().save(fullBean.getEuropeanaAggregation());
			mongo.getDatastore().save(fullBean.getPlaces());
			mongo.getDatastore().save(fullBean.getProvidedCHOs());
			mongo.getDatastore().save(fullBean.getProxies());
			mongo.getDatastore().save(fullBean.getTimespans());
			mongo.getDatastore().save(fullBean);
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
		} 
	}

	
	@Before
	public void prepare(){
		try {
			IMongodConfig conf = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
			        .net(new Net(10000, Network.localhostIsIPv6())).build();
			        MongodStarter runtime = MongodStarter.getDefaultInstance();

		mongodExecutable = runtime.prepare(conf);
		mongodExecutable.start();
			       
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
	}
	
	@After
	public void destroy(){
		mongodExecutable.stop();
	}

}
