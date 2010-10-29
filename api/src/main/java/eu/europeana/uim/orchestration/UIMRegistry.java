package eu.europeana.uim.orchestration;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import eu.europeana.uim.api.IngestionPlugin;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.Workflow;
import eu.europeana.uim.store.StorageEngine;
import eu.europeana.uim.store.memory.MemoryStorageEngine;

public class UIMRegistry implements Registry {

	private static Logger log = Logger.getLogger(UIMRegistry.class.getName());

	private List<StorageEngine> storages = new ArrayList<StorageEngine>();
	private List<IngestionPlugin> plugins = new ArrayList<IngestionPlugin>();
	private List<Workflow> workflows = new ArrayList<Workflow>();

	private StorageEngine fallbackStore = new MemoryStorageEngine();

	@Autowired
	private Orchestrator orchestrator = null;

	public UIMRegistry() {
	}

	@Override
	public void addPlugin(IngestionPlugin plugin) {
		if (plugin != null) {
			log.info("Added plugin:" + plugin.getIdentifier());
			plugins.add(plugin);
		}
	}
	

	@Override
	public void removePlugin(IngestionPlugin plugin) {
		if (plugin != null) {
			log.info("Removed plugin:" + plugin.getIdentifier());
			plugins.remove(plugin);
		}
	}
	

	@Override
	public void addStorage(StorageEngine storage) {
		if (storage != null) {
			log.info("Added storage:" + storage.getIdentifier());
			this.storages.add(storage);
		}
	}
	

	@Override
	public void removeStorage(StorageEngine storage) {
		if (storage != null) {
			log.info("Removed storage:" + storage.getIdentifier());
			this.storages.remove(storage);
		}
	}


	@Override
	public void addWorkflow(Workflow workflow) {
		if (workflow != null) { 
			log.info("Added workflow: " + workflow.getName());
			workflows.add(workflow);
		}
	}

	@Override
	public void removeWorkflow(Workflow workflow) {
		if (workflow != null) {
			log.info("Removed workflow: " + workflow.getName());
			workflows.remove(workflow);
		}
	}

	@Override
	public StorageEngine getActiveStorage() {
		if (storages == null || storages.isEmpty()) return getFallbackStore();
		return storages.get(0);
	}


	/**
	 * @return the fallbackStore
	 */
	public StorageEngine getFallbackStore() {
		return fallbackStore;
	}

	/**
	 * @param fallbackStore the fallbackStore to set
	 */
	public void setFallbackStore(StorageEngine fallbackStore) {
		this.fallbackStore = fallbackStore;
	}



	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (plugins.isEmpty()) {
			builder.append("No plugins. ");
		} else {
			for (IngestionPlugin plugin : plugins) {
				if (builder.length() > 0) {
					builder.append("\nPlugin:");
				}
				builder.append(plugin.getIdentifier() + ": [" + plugin.getDescription() + "]");
			}
			builder.append(". ");
		}

		if (storages.isEmpty()) {
			builder.append("No storage. Fallback storage: " + getFallbackStore().getIdentifier());
		} else {
			StringBuilder storelist = new StringBuilder();
			for (StorageEngine storage : storages) {
				if (storelist.length() > 0) {
					storelist.append(", ");
				}
				storelist.append(storage.getIdentifier());
			}
			builder.append(storelist + ". ");
		}

		return builder.toString();
	}

}
