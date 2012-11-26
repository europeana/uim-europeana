package eu.europeana.europeanauim.publish.service;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.solr.client.solrj.impl.HttpSolrServer;

import eu.europeana.europeanauim.publish.utils.PropertyReader;
import eu.europeana.europeanauim.publish.utils.UimConfigurationProperty;
import eu.europeana.uim.common.BlockingInitializer;

public class PublishServiceImpl implements PublishService {
	HttpSolrServer solrServer;
	public HttpSolrServer getSolrServer() {
		final String solrUrl= PropertyReader.getProperty(UimConfigurationProperty.SOLR_HOSTURL);
		final String solrCore=PropertyReader.getProperty(UimConfigurationProperty.SOLR_CORE);
		
		
		BlockingInitializer solrInit = new BlockingInitializer() {
			
			@Override
			protected void initializeInternal() {
				try {
					solrServer = new HttpSolrServer(new URL(solrUrl)+solrCore);
				} catch (MalformedURLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		};
		solrInit.initialize(HttpSolrServer.class.getClassLoader());
		return solrServer;
	}

	

}
