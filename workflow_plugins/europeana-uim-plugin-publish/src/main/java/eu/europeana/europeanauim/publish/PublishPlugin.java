package eu.europeana.europeanauim.publish;

import java.io.IOException;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;

import eu.europeana.europeanauim.publish.service.PublishService;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.store.MetaDataRecord;

public class PublishPlugin<I> extends AbstractIngestionPlugin<MetaDataRecord<I>, I>{

	private static PublishService publishService;
	public PublishPlugin(String name, String description) {
		super(name, description);
		// TODO Auto-generated constructor stub
	}

	public PublishPlugin(PublishService publishService, String name,
			String description) {
		super(name,description);
		PublishPlugin.publishService = publishService;
	}

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

	public boolean process(MetaDataRecord<I> dataset,
			ExecutionContext<MetaDataRecord<I>, I> context)
			throws IngestionPluginFailedException, CorruptedDatasetException {
		return true;
	}

	public void initialize(ExecutionContext<MetaDataRecord<I>, I> context)
			throws IngestionPluginFailedException {
		HttpSolrServer solrServer = publishService.getSolrServer();
		try {
			solrServer.optimize();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

	public void completed(ExecutionContext<MetaDataRecord<I>, I> context)
			throws IngestionPluginFailedException {
		// TODO Auto-generated method stub
		
	}

	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	public List<String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPreferredThreadCount() {
		// TODO Auto-generated method stub
		return 1;
	}

	public int getMaximumThreadCount() {
		// TODO Auto-generated method stub
		return 1;
	}

}
