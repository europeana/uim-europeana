package eu.europeana.enrichment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.codehaus.plexus.logging.console.ConsoleLogger;
import org.junit.Assert;
import org.junit.Test;

import com.mongodb.Mongo;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.MongodConfig;
import de.flapdoodle.embed.mongo.distribution.Version;
import eu.europeana.corelib.solr.exceptions.MongoDBException;
import eu.europeana.corelib.tools.lookuptable.CollectionMongoServer;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdMongoServer;
import eu.europeana.corelib.tools.lookuptable.impl.CollectionMongoServerImpl;
import eu.europeana.corelib.tools.lookuptable.impl.EuropeanaIdMongoServerImpl;
import eu.europeana.uim.enrichment.EnrichmentPlugin;
import eu.europeana.uim.enrichment.service.EnrichmentService;
import eu.europeana.uim.enrichment.service.impl.EnrichmentServiceImpl;
import eu.europeana.uim.enrichment.utils.EuropeanaEnrichmentTagger;
import eu.europeana.uim.enrichment.utils.OsgiEdmMongoServer;
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
import eu.europeana.uim.sugar.QueryResultException;
import eu.europeana.uim.sugar.SugarCrmRecord;
import eu.europeana.uim.sugar.SugarCrmService;
import eu.europeana.uim.sugar.model.SugarCrmField;
import eu.europeana.uim.sugar.model.UpdatableField;

public class EnrichmentPluginTest {

	@Test
	public void test() {
		try {
			String RECORD = FileUtils.readFileToString(new File(
					"src/test/resources/edm_concept.xml"));

			MongodConfig conf = new MongodConfig(Version.V2_0_7, 10000, false,"src/test/resources/annocultor_db");
			
			MongodStarter runtime = MongodStarter.getDefaultInstance();
			MongodExecutable mongoExec = runtime.prepare(conf);
		//	mongoExec.start();
			PropertyReader
					.loadPropertiesFromFile("src/test/resources/uim.properties");
			EnrichmentService serv = mock(EnrichmentServiceImpl.class);
			SugarCrmService sugarService = mock(SugarCrmService.class);
			
			//when(sugarRecord.getItemValue(EuropeanaRetrievableField.PREVIEWS_ONLY_IN_PORTAL)).thenReturn("true");
			if(!new File("src/test/resources/annocultor_db/annocultor_db.3").exists()){
				final TarGZipUnArchiver ua = new TarGZipUnArchiver();
				ua.setSourceFile(new File("src/test/resources/annocultor_db/annocultor_db.tar.gz"));
				ua.enableLogging(new ConsoleLogger(0,"test"));
				ua.extract();
			}
			
			
			//when(sugarRecord.getItemValue(EuropeanaRetrievableField.PREVIEWS_ONLY_IN_PORTAL)).thenReturn("false");
			EnrichmentPlugin plugin  = new EnrichmentPlugin();
			plugin.setEnrichmentService(serv);
			plugin.setSugarCrmService(sugarService);
			Mongo mongoServer = new Mongo("localhost", 10000);
			CollectionMongoServer collectionServer = new CollectionMongoServerImpl(
					mongoServer, "colTest");
			
			EuropeanaIdMongoServer idServer = new EuropeanaIdMongoServerImpl(
					mongoServer, "idTest", "", "");
			OsgiEdmMongoServer edmMongoServer = new OsgiEdmMongoServer(mongoServer, "recordTest", "", "");
			
			idServer.createDatastore();
			HttpSolrServer solrServer = mock(HttpSolrServer.class);
			when(serv.getCollectionMongoServer()).thenReturn(collectionServer);
			when(serv.getEuropeanaIdMongoServer()).thenReturn(idServer);
			when(serv.getSolrServer()).thenReturn(solrServer);
			when(serv.getEuropeanaMongoServer()).thenReturn(edmMongoServer);
			SolrDocumentList list = new SolrDocumentList();
			list.add(new SolrDocument());
			UpdateResponse resp = mock(UpdateResponse.class);
			when(solrServer.deleteByQuery("europeana_collectionName:12345*"))
					.thenReturn(resp);
			ActiveExecution context = mock(ActiveExecution.class);
			Collection collection = new CollectionBean("09431",
					new ProviderBean<String>("test_provider"));
			
			collection.putValue(ControlledVocabularyProxy.SUGARCRMID, "09431");
			collection.setMnemonic("12345");
			collection.setName("12345");
			SugarCrmRecord sugarRecord= new SugarCrmRecord() {
				
				@Override
				public void setItemValue(UpdatableField field, String value) {
					// TODO Auto-generated method stub
					
				}
				
				@Override
				public String getItemValue(SugarCrmField field) {
					// TODO Auto-generated method stub
					
					return "true";
				}
			};
			when(sugarService.retrieveRecord("09431")).thenReturn(sugarRecord);
			MetaDataRecord mdr = new MetaDataRecordBean<String>("09431", collection);
			mdr.addValue(EuropeanaModelRegistry.EDMRECORD, RECORD);
			ExecutionBean execution = new ExecutionBean();
			execution.setDataSet(collection);
			Properties properties = new Properties();
			LoggingEngine logging = LoggingEngineAdapter.LONG;
			when(context.getExecution()).thenReturn(execution);
			EuropeanaEnrichmentTagger tagger = new EuropeanaEnrichmentTagger();
			tagger.init("Europeana","localhost",Integer.toString(10000));
			plugin.setTagger(tagger);
			plugin.initialize(context);
			plugin.process(mdr, context);
			Assert.assertNotNull(edmMongoServer.getFullBean("#ULEI:M0000005"));
//			Assert.assertTrue(idServer.newIdExists("#ULEI:M0000005"));
			
			plugin.completed(context);
			mongoExec.stop();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
