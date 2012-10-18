package eu.europeana.uim.enrichment.service.impl;

import java.net.URL;
import java.util.List;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import eu.annocultor.converters.europeana.Entity;
import eu.europeana.uim.enrichment.service.EnrichmentService;
import eu.europeana.uim.enrichment.utils.EuropeanaEnrichmentTagger;
import eu.europeana.uim.enrichment.utils.PropertyReader;
import eu.europeana.uim.enrichment.utils.UimConfigurationProperty;

public class EnrichmentServiceImpl implements EnrichmentService {

	public static EuropeanaEnrichmentTagger tagger;
	private static CommonsHttpSolrServer solrServer;
	private static CommonsHttpSolrServer suggestionServer;
	private static String mongoDB=PropertyReader.getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANA);
	private static String mongoHost=PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTURL);
	private static String mongoPort= PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTPORT);
	private static String solrUrl= PropertyReader.getProperty(UimConfigurationProperty.SOLR_HOSTURL);
	private static String solrCore=PropertyReader.getProperty(UimConfigurationProperty.SOLR_CORE);
	private static String solrCoreSuggestions = PropertyReader.getProperty(UimConfigurationProperty.SOLR_CORE_SUGGESTIONS);

	public EnrichmentServiceImpl(){
		tagger = new EuropeanaEnrichmentTagger();
		try {
		solrServer = new CommonsHttpSolrServer(new URL(solrUrl)+solrCore);
		suggestionServer = new CommonsHttpSolrServer(new URL(solrUrl)+solrCoreSuggestions);
			tagger.init("Europeana");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	

	@Override
	public  CommonsHttpSolrServer getSolrServer() {
		return solrServer;
	}

	

	@Override
	public List<Entity> enrich(SolrInputDocument solrDocument) throws Exception {
		return tagger.tagDocument(solrDocument);
	}
	@Override
	public String getMongoDB() {
		return mongoDB;
	}

	public void setMongoDB(String mongoDB) {
		EnrichmentServiceImpl.mongoDB = mongoDB;
	}

	public  String getMongoHost() {
		return mongoHost;
	}

	public void setMongoHost(String mongoHost) {
		EnrichmentServiceImpl.mongoHost = mongoHost;
	}

	public String getMongoPort() {
		return mongoPort;
	}

	public void setMongoPort(String mongoPort) {
		EnrichmentServiceImpl.mongoPort = mongoPort;
	}

	public String getSolrUrl() {
		return solrUrl;
	}

	public void setSolrUrl(String solrUrl) {
		EnrichmentServiceImpl.solrUrl = solrUrl;
	}

	public String getSolrCore() {
		return solrCore;
	}

	public void setSolrCore(String solrCore) {
		EnrichmentServiceImpl.solrCore = solrCore;
	}

	

	

	public void setSolrServer(CommonsHttpSolrServer solrServer) {
		EnrichmentServiceImpl.solrServer = solrServer;
	}

	public  EuropeanaEnrichmentTagger getTagger() {
		return tagger;
	}

	public void setTagger(EuropeanaEnrichmentTagger tagger) {
		EnrichmentServiceImpl.tagger = tagger;
	}

	@Override
	public CommonsHttpSolrServer getSuggestionServer(){
		return EnrichmentServiceImpl.suggestionServer;
	}


}
