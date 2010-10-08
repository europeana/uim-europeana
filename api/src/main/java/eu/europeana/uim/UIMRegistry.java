package eu.europeana.uim;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import eu.europeana.uim.plugin.IngestionPlugin;
import eu.europeana.uim.store.StorageEngine;
import eu.europeana.uim.workflow.Workflow;

public class UIMRegistry {

	private static Logger log = Logger.getLogger(UIMRegistry.class.getName());

	private List<StorageEngine> storages = new ArrayList<StorageEngine>();
	private List<IngestionPlugin> plugins = new ArrayList<IngestionPlugin>();
	private List<Workflow> workflows = new ArrayList<Workflow>();

	private Orchestrator orchestrator = null;
	
	private static UIMRegistry instance = null;

	public UIMRegistry(){
		instance = this;
	}

	public static UIMRegistry getInstance() {
		if (instance ==null) {
			instance = new UIMRegistry();
		}
		return instance;
	}


	public void addPlugin(IngestionPlugin plugin) {
		plugins.add(plugin);
		log.info("Added plugin:" + plugin.getIdentifier());
	}

	public void removePlugin(IngestionPlugin plugin) {
		plugins.remove(plugin);
		log.info("Removed plugin:" + plugin.getIdentifier());
	}

	public void addStorage(StorageEngine storage) {
		log.info("Added storage:" + storage.getIdentifier());
		this.storages.add(storage);
	}

	public void removeStorage(StorageEngine storage) {
		log.info("Removed storage:" + storage.getIdentifier());
		this.storages.remove(storage);
	}


	public void addWorkflow(Workflow workflow) {
		workflows.add(workflow);
		log.info("Added workflow: " + workflow.getName());
	}

	public void removeWorkflow(Workflow workflow) {
		workflows.remove(workflow);
		log.info("Removed workflow: " + workflow.getName());
	}



	public StorageEngine getFirstStorage() {
		return storages.get(0);
	}

	public void setFirstStorage(StorageEngine storage) {
		this.storages.add(0, storage);
	}
	

	public Orchestrator getOrchestrator() {
		return orchestrator;
	}

	public void setOrchestrator(Orchestrator orchestrator) {
		this.orchestrator = orchestrator;
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
			builder.append("No storage. ");
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
		
		builder.append("Orchestrator: " + orchestrator.getIdentifier());
		return builder.toString();
	}
}
