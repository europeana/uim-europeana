package eu.europeana.uim.deactivation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.solr.client.solrj.SolrServerException;
import org.jibx.runtime.IBindingFactory;
import org.theeuropeanlibrary.model.common.qualifier.Status;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteConcern;

import eu.europeana.uim.common.TKey;
import eu.europeana.uim.deactivation.service.DeactivationService;
import eu.europeana.uim.deactivation.service.ExtendedEdmMongoServer;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;

/**
 * Collection Deactivation Plugin
 * 
 * @author gmamakis
 * 
 */
public class DeactivatePlugin<I> extends
		AbstractIngestionPlugin<MetaDataRecord<I>, I> {

	private static DeactivationService dService;

	public DeactivatePlugin(String name, String description) {
		super(name, description);
	}

	public DeactivatePlugin() {
		super("", "");
	}

	public DeactivatePlugin(DeactivationService dService, String name,
			String description) {
		super(name, description);
		dService.initialize();
		DeactivatePlugin.dService = dService;

	}

	public TKey<?, ?>[] getInputFields() {
		return null;
	}

	public int getMaximumThreadCount() {
		return 12;
	}

	public TKey<?, ?>[] getOptionalFields() {
		return null;
	}

	public TKey<?, ?>[] getOutputFields() {
		return null;
	}

	public List<String> getParameters() {
		return new ArrayList<String>();
	}

	public int getPreferredThreadCount() {
		return 10;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.plugin.Plugin#initialize()
	 */
	public void initialize() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.plugin.ExecutionPlugin#initialize(eu.europeana.uim.
	 * orchestration.ExecutionContext)
	 */
	@Override
	public void initialize(ExecutionContext<MetaDataRecord<I>, I> arg0)
			throws IngestionPluginFailedException {
		try {
			Collection collection = (Collection) arg0.getExecution()
					.getDataSet();
			String collectionId = collection.getName().split("_")[0];
			String newCollectionId = dService.getCollectionMongoServer()
					.findNewCollectionId(collection.getName().split("_")[0]);
			System.out.println(newCollectionId);
			if (newCollectionId != null) {
				collectionId = newCollectionId;
			}
			dService.getSolrServer().deleteByQuery(
					"europeana_collectionName:" + collectionId + "*");
			clearData(dService.getMongoServer(), collectionId);

		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.plugin.ingestion.IngestionPlugin#process(eu.europeana
	 * .uim.store.UimDataSet, eu.europeana.uim.orchestration.ExecutionContext)
	 */
	@Override
	public boolean process(MetaDataRecord<I> mdr,
			ExecutionContext<MetaDataRecord<I>, I> arg1)
			throws IngestionPluginFailedException, CorruptedDatasetException {
		if (mdr.getValues(EuropeanaModelRegistry.STATUS).size() == 0
				|| !mdr.getValues(EuropeanaModelRegistry.STATUS).get(0)
						.equals(Status.DELETED)) {
			mdr.deleteValues(EuropeanaModelRegistry.REMOVED);
			mdr.addValue(EuropeanaModelRegistry.REMOVED, new Date().getTime());

		}
		return true;
	}

	private void clearData(ExtendedEdmMongoServer mongoServer2,
			String collection) {
		DBCollection records = mongoServer2.getDatastore().getDB()
				.getCollection("record");
		DBCollection proxies = mongoServer2.getDatastore().getDB()
				.getCollection("Proxy");
		DBCollection providedCHOs = mongoServer2.getDatastore().getDB()
				.getCollection("ProvidedCHO");
		DBCollection aggregations = mongoServer2.getDatastore().getDB()
				.getCollection("Aggregation");
		DBCollection europeanaAggregations = mongoServer2.getDatastore()
				.getDB().getCollection("EuropeanaAggregation");

		DBObject query = new BasicDBObject("about", Pattern.compile("^/"
				+ collection + "/"));
		DBObject proxyQuery = new BasicDBObject("about", "^/proxy/provider"
				+ Pattern.compile("/" + collection + "/"));
		DBObject europeanaProxyQuery = new BasicDBObject("about",
				"^/proxy/europeana" + Pattern.compile("/" + collection + "/"));

		DBObject providedCHOQuery = new BasicDBObject("about", "^/item"
				+ Pattern.compile("/" + collection + "/"));
		DBObject aggregationQuery = new BasicDBObject("about",
				"^/aggregation/provider"
						+ Pattern.compile("/" + collection + "/"));
		DBObject europeanaAggregationQuery = new BasicDBObject("about",
				"^/aggregation/europeana"
						+ Pattern.compile("/" + collection + "/"));

		europeanaAggregations.remove(europeanaAggregationQuery,
				WriteConcern.FSYNC_SAFE);
		records.remove(query, WriteConcern.FSYNC_SAFE);
		proxies.remove(europeanaProxyQuery, WriteConcern.FSYNC_SAFE);
		proxies.remove(proxyQuery, WriteConcern.FSYNC_SAFE);
		providedCHOs.remove(providedCHOQuery, WriteConcern.FSYNC_SAFE);
		aggregations.remove(aggregationQuery, WriteConcern.FSYNC_SAFE);
	}

	/*
	 * (non catch (IOException e) { // TODO Auto-generated catch block
	 * e.printStackTrace(); }-Javadoc)
	 * 
	 * @see eu.europeana.uim.plugin.Plugin#shutdown()
	 */
	public void shutdown() {

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.plugin.ExecutionPlugin#completed(eu.europeana.uim.
	 * orchestration.ExecutionContext)
	 */
	@Override
	public void completed(ExecutionContext<MetaDataRecord<I>, I> arg0)
			throws IngestionPluginFailedException {
		try {
			dService.getSolrServer().commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public DeactivationService getdService() {
		return dService;
	}

	public void setdService(DeactivationService dService) {
		DeactivatePlugin.dService = dService;
	}

}
