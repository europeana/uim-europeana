package eu.europeana.uim.plugin.solr.test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Properties;

import org.junit.Assert;
import org.junit.Test;

import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.LoggingEngine;
import eu.europeana.uim.api.LoggingEngineAdapter;
import eu.europeana.uim.model.europeanaspecific.EuropeanaModelRegistry;
import eu.europeana.uim.plugin.solr.service.SolrWorkflowPlugin;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.store.bean.CollectionBean;
import eu.europeana.uim.store.bean.ExecutionBean;
import eu.europeana.uim.store.bean.MetaDataRecordBean;


public class SolrPluginTest {
	private static final String RECORD="<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			"<rdf:RDF xmlns:dcterms=\"http://purl.org/dc/terms/\""+
			"xmlns:edm=\"http://www.europeana.eu/schemas/edm/\""+
			"xmlns:enrichment=\"http://www.europeana.eu/schemas/edm/enrichment/\""+
			"xmlns:owl=\"http://www.w3.org/2002/07/owl#\"" +
			"xmlns:wgs84=\"http://www.w3.org/2003/01/geo/wgs84_pos#\""+
			"xmlns:skos=\"http://www.w3.org/2004/02/skos/core\"" +
			"xmlns:oai=\"http://www.openarchives.org/OAI/2.0/\""+
			"xmlns:ore=\"http://www.openarchives.org/ore/terms/\""+
			"xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\""+
			"xmlns:dc=\"http://purl.org/dc/elements/1.1/\""+
			"xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\""+
			"xsi:schemaLocation=\"http://www.w3.org/1999/02/22-rdf-syntax-ns# EDM.xsd\">"+
			"<edm:ProvidedCHO rdf:about=\"#GNM:728331\">"+
			"<dc:identifier>#GNM:728331</dc:identifier>"+
			"<dc:coverage></dc:coverage>" +
			"<dc:description xml:lang=\"de\">Stoßmechanik.</dc:description>"+
			"<dc:title xml:lang=\"de\">Modell einer primitiven Stoßmechanik</dc:title>"+
			"<edm:type>IMAGE</edm:type>"+
			"</edm:ProvidedCHO>"+
			"<edm:WebResource rdf:about=\"http://www.mimo-db.eu/media/GNM/IMAGE/MINe298_H_1304604690701_2.jpg\">"+
			"<edm:rights rdf:resource=\"http://creativecommons.org/licenses/by-nc-sa/3.0/\"></edm:rights>"+
			"</edm:WebResource>"+
			"<ore:Aggregation rdf:about=\"http://www.mimo-db.eu/GNM/728331\">"+
			"<edm:aggregatedCHO rdf:resource=\"#GNM:728331\"></edm:aggregatedCHO>"+
			"<edm:hasView rdf:resource=\"http://www.mimo-db.eu/media/GNM/IMAGE/MINe298_H_1304604690701_2.jpg\"></edm:hasView>"+
			"<edm:dataProvider>Germanisches Nationalmuseum</edm:dataProvider>"+
			"<edm:provider>MIMO - Musical Instrument Museums Online</edm:provider>"+
			"<edm:rights rdf:resource=\"http://creativecommons.org/licenses/by-nc-sa/3.0/\"></edm:rights>"+
			"<edm:isShownBy rdf:resource=\"http://www.mimo-db.eu/media/GNM/IMAGE/MINe298_H_1304604690701_2.jpg\"></edm:isShownBy>"+
			"<edm:isShownAt rdf:resource=\"http://www.mimo-db.eu/GNM/728331\"></edm:isShownAt>"+
			"<edm:object rdf:resource=\"http://www.mimo-db.eu/media/GNM/IMAGE/MINe298_H_1304604690701_2.jpg\"></edm:object>"+
			"</ore:Aggregation>" +
			"</rdf:RDF>";
	
	private SolrWorkflowPlugin plugin;
	@Test
	@SuppressWarnings({  "rawtypes", "unchecked" })
	public void store(){
		plugin = new SolrWorkflowPlugin();
		ActiveExecution context = mock(ActiveExecution.class);
		CollectionBean collection = new CollectionBean();
        collection.setName("test");
        ExecutionBean execution = new ExecutionBean(1L);
        execution.setDataSet(collection);
        Properties properties = new Properties();
        LoggingEngine logging = LoggingEngineAdapter.LONG;
        when(context.getProperties()).thenReturn(properties);
        when(context.getExecution()).thenReturn(execution);
        when(context.getLoggingEngine()).thenReturn(logging);
        MetaDataRecord mdr = new MetaDataRecordBean();
      
        mdr.addValue(EuropeanaModelRegistry.EDMRECORD, RECORD);
        plugin.initialize(context);
        
        Assert.assertTrue(plugin.processRecord(mdr, context));
        
        plugin.completed(context);
        Assert.assertEquals(1, SolrWorkflowPlugin.getRecords());
        plugin.shutdown();
        
	}
	
	
}
