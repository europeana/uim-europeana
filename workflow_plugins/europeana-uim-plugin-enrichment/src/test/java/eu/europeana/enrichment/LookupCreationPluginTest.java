package eu.europeana.enrichment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.junit.Ignore;
import org.junit.Test;

import com.mongodb.Mongo;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;
import eu.europeana.corelib.lookup.impl.CollectionMongoServerImpl;
import eu.europeana.corelib.lookup.impl.EuropeanaIdMongoServerImpl;
import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;
import eu.europeana.uim.enrichment.LookupCreationPlugin;
import eu.europeana.uim.enrichment.service.EnrichmentService;
import eu.europeana.uim.enrichment.service.impl.EnrichmentServiceImpl;
import eu.europeana.uim.enrichment.utils.PropertyReader;
import eu.europeana.uim.logging.LoggingEngine;
import eu.europeana.uim.logging.LoggingEngineAdapter;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.orchestration.ActiveExecution;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.bean.CollectionBean;
import eu.europeana.uim.store.bean.ExecutionBean;
import eu.europeana.uim.store.bean.MetaDataRecordBean;
import eu.europeana.uim.store.bean.ProviderBean;

public class LookupCreationPluginTest {

	@Ignore
	@Test
	public void testProcess() {
		try {
			String RECORD =FileUtils.readFileToString(new File("src/test/resources/edm_concept.xml"));
			IMongodConfig conf = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
			        .net(new Net(10001, Network.localhostIsIPv6()))
			        .build();
			MongodStarter runtime = MongodStarter.getDefaultInstance();
			MongodExecutable mongoExec = runtime.prepare(conf);
			mongoExec.start();
			PropertyReader.loadPropertiesFromFile("src/test/resources/uim.properties");
			EnrichmentService serv = mock(EnrichmentServiceImpl.class);

			LookupCreationPlugin plugin = new LookupCreationPlugin();
			plugin.setEnrichmentService(serv);
			Mongo mongoServer = new Mongo("localhost", 10003);
			CollectionMongoServer collectionServer = new CollectionMongoServerImpl(
					mongoServer, "colTest");
			
			EuropeanaIdMongoServer idServer = new EuropeanaIdMongoServerImpl(
					mongoServer, "idTest", "", "");
			idServer.createDatastore();
			HttpSolrServer solrServer = mock(HttpSolrServer.class);
			when(serv.getCollectionMongoServer()).thenReturn(collectionServer);
			when(serv.getEuropeanaIdMongoServer()).thenReturn(idServer);
//			when(serv.getSolrServer()).thenReturn(solrServer);
			SolrDocumentList list = new SolrDocumentList();
			list.add(new SolrDocument());
			QueryResponse resp = mock(QueryResponse.class);
			when(solrServer.query(new ModifiableSolrParams())).thenReturn(resp);
			when(resp.getResults())
					.thenReturn(list);
			
		

			ActiveExecution context = mock(ActiveExecution.class);
			Collection collection = new CollectionBean("09431",
					new ProviderBean<String>("test_provider"));
			
			collection.putValue(ControlledVocabularyProxy.SUGARCRMID, "09431");
			collection.setMnemonic("12345");
			
			MetaDataRecord mdr = new MetaDataRecordBean<String>("09431", collection);
			mdr.addValue(EuropeanaModelRegistry.EDMRECORD, RECORD);
			ExecutionBean execution = new ExecutionBean();
			execution.setDataSet(collection);
			Properties properties = new Properties();
			LoggingEngine logging = LoggingEngineAdapter.LONG;
			plugin.initialize(context);
			plugin.process(mdr, context);
			
//			Assert.assertTrue(idServer.newIdExists("#ULEI:M0000005"));
			
			plugin.completed(context);
			mongoExec.stop();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
