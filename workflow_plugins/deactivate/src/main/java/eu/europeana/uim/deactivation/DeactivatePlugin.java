package eu.europeana.uim.deactivation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.europeana.harvester.client.HarvesterClientImpl;
import eu.europeana.harvester.domain.ReferenceOwner;
import eu.europeana.uim.deactivation.service.InstanceCreator;
import eu.europeana.uim.deactivation.service.InstanceCreatorImpl;
import org.apache.solr.client.solrj.SolrServerException;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.index.IndexHits;
import org.neo4j.rest.graphdb.RestAPI;
import org.neo4j.rest.graphdb.RestGraphDatabase;
import org.neo4j.rest.graphdb.batch.BatchCallback;
import org.neo4j.rest.graphdb.index.RestIndex;
import org.theeuropeanlibrary.model.common.qualifier.Status;

import eu.europeana.corelib.edm.utils.construct.FullBeanHandler;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.deactivation.service.DeactivationService;
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
	private static InstanceCreator creator;
	private final static Logger log = Logger.getLogger(DeactivatePlugin.class
			.getName());

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
			log.log(Level.INFO, "Removing collectionId: " + collectionId);
			String newCollectionId = dService.getCollectionMongoServer()
					.findNewCollectionId(collection.getName().split("_")[0]);
			log.log(Level.INFO, "New collection id is:" + newCollectionId);
			if (newCollectionId != null) {
				collectionId = newCollectionId;
			}
			log.log(Level.INFO, "removing from solr(ingestion)");
			dService.getCloudSolrServer().deleteByQuery(
					"europeana_collectionName:" + collectionId + "*");
			log.log(Level.INFO, "removing from solr(production)");
            dService.getProductionCloudSolrServer().deleteByQuery(
                    "europeana_collectionName:" + collectionId + "*");
			log.log(Level.INFO, "removing from mongo");
			new FullBeanHandler(dService.getMongoServer())
					.clearData(collectionId);
			new FullBeanHandler(dService.getProductionMongoServer())
            .clearData(collectionId);
			clearData(dService.getGraphDb(), dService.getNeo4jIndex(),
					collectionId);
			clearData(dService.getGraphDbProduction(), dService.getNeo4jIndexProduction(),
                collectionId);
			 creator = new InstanceCreatorImpl();
			HarvesterClientImpl client = new HarvesterClientImpl(creator.getDatastore(),creator.getConfig());
			client.deactivateJobs(new ReferenceOwner(arg0.getDataSetCollection().getProvider().getMnemonic(),((Collection)arg0.getExecution().getDataSet()).getMnemonic(),null));
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.log(Level.SEVERE, e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			log.log(Level.SEVERE, e.getMessage());
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
			dService.getCloudSolrServer().commit();
			dService.getProductionCloudSolrServer().commit();
		} catch (SolrServerException e) {
			// TODO Auto-generated catch block
			log.log(Level.SEVERE, e.getMessage());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			log.log(Level.SEVERE, e.getMessage());
		}
	}

	public DeactivationService getdService() {
		return dService;
	}

	public void setdService(DeactivationService dService) {
		DeactivatePlugin.dService = dService;
	}

	private void clearData(RestGraphDatabase graphDb, String neo4jIndex,
			String collectionId) {
		RestIndex<Node> restIndex = graphDb.getRestAPI().getIndex(neo4jIndex);
		List<Node> deletionNodes = new ArrayList<Node>();
		IndexHits<Node> nodes = restIndex.query("rdf_about", "/" + collectionId
				+ "/*");
		if (nodes!=null &&nodes.size() > 0) {

			while (nodes.iterator().hasNext()) {
				deletionNodes.add(nodes.iterator().next());
			}

		}

		removeFromIndex(deletionNodes, graphDb, neo4jIndex);
		removeRelationships(deletionNodes, graphDb);
		removeNodes(deletionNodes, graphDb);
	}

	private void removeFromIndex(List<Node> deletionNodes,
			RestGraphDatabase graphDb, String neo4jIndex) {
		final List<Node> tempList = new ArrayList<Node>();
		int i = 0;

		for (Node node : deletionNodes) {

			tempList.add(node);

			if (tempList.size() == 1000 || i == deletionNodes.size()) {
				Transaction tx = graphDb.getRestAPI().beginTx();
				for (Node tempNode : tempList) {
					graphDb.getRestAPI().getIndex(neo4jIndex).remove(tempNode);
				}
				tempList.clear();
				tx.success();
				tx.finish();

			}

			i++;
		}
	}

	private void removeRelationships(List<Node> deletionNodes,
			RestGraphDatabase graphDb) {
		final Set<Relationship> relationships = new HashSet<Relationship>();

		int i = 0;
		for (Node node : deletionNodes) {
			Iterable<Relationship> rels = node.getRelationships();
			Iterator<Relationship> relIterator = rels.iterator();
			while (relIterator.hasNext()) {
				relationships.add(relIterator.next());
			}

			if (relationships.size() >= 50 || i == deletionNodes.size()) {
				Transaction tx = graphDb.getRestAPI().beginTx();
				graphDb.getRestAPI().executeBatch(new BatchCallback<Node>() {

					@Override
					public Node recordBatch(RestAPI batchRestApi) {
						for (Relationship node : relationships) {
							node.delete();

						}
						return null;
					}

				});
				relationships.clear();
				tx.success();
				tx.finish();
			}
			i++;
		}
	}

	private void removeNodes(List<Node> deletionNodes, RestGraphDatabase graphDb) {
		final List<Node> tempList = new ArrayList<Node>();
		int i = 0;
		for (Node node : deletionNodes) {

			tempList.add(node);

			if (tempList.size() == 50 || i == deletionNodes.size()) {
				Transaction tx = graphDb.getRestAPI().beginTx();
				graphDb.getRestAPI().executeBatch(new BatchCallback<Node>() {

					@Override
					public Node recordBatch(RestAPI batchRestApi) {
						for (Node node : tempList) {
							node.delete();
						}
						return null;
					}

				});
				tempList.clear();
				tx.success();
				tx.finish();
			}
			i++;
		}
	}
}
