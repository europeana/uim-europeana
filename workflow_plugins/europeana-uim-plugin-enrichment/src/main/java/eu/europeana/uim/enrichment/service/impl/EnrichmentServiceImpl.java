package eu.europeana.uim.enrichment.service.impl;

import java.util.List;

import org.apache.solr.client.solrj.impl.CommonsHttpSolrServer;
import org.apache.solr.common.SolrInputDocument;

import com.mongodb.Mongo;

import eu.annocultor.converters.europeana.Entity;
import eu.europeana.uim.enrichment.service.EnrichmentService;
import eu.europeana.uim.enrichment.utils.EuropeanaEnrichmentTagger;

public class EnrichmentServiceImpl implements EnrichmentService {

	public static EuropeanaEnrichmentTagger tagger;
	private static CommonsHttpSolrServer solrServer;
	private static Mongo mongo;
	private static String mongoDB="europeana";
	private static String mongoHost="127.0.0.1";
	private static String mongoPort="27017";
	private static String solrUrl="http://localhost:8282/";
	private static String solrCore="apache-solr-3.5.0";
	


	public EnrichmentServiceImpl(){
		tagger = new EuropeanaEnrichmentTagger();
		try {
		solrServer = new CommonsHttpSolrServer(solrUrl+solrCore);
		 solrServer.setSoTimeout(1000);  // socket read timeout
		  solrServer.setConnectionTimeout(100);
		  solrServer.setDefaultMaxConnectionsPerHost(100);
		  solrServer.setMaxTotalConnections(100);
		  solrServer.setFollowRedirects(false);
		//mongo  = new Mongo(mongoHost, Integer.parseInt(mongoPort));
			
		
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

//	@Override
//	public Mongo getMongo() {
//		return mongo;
//	}
//	

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

//	public void setMongo(Mongo mongo) {
//		EnrichmentServiceImpl.mongo = mongo;
//	}
	public  EuropeanaEnrichmentTagger getTagger() {
		return tagger;
	}

	public void setTagger(EuropeanaEnrichmentTagger tagger) {
		EnrichmentServiceImpl.tagger = tagger;
	}

}
