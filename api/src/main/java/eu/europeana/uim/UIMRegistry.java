package eu.europeana.uim;

import eu.europeana.uim.plugin.IngestionPlugin;
import eu.europeana.uim.store.StorageEngine;
import eu.europeana.uim.workflow.Workflow;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class UIMRegistry implements Registry {

	private static Logger log = Logger.getLogger(UIMRegistry.class.getName());

	private List<StorageEngine> storages = new ArrayList<StorageEngine>();
	private List<IngestionPlugin> plugins = new ArrayList<IngestionPlugin>();
	private List<Workflow> workflows = new ArrayList<Workflow>();

    @Autowired
	private Orchestrator orchestrator = null;
	
	public UIMRegistry() {
	}

	@Override
    public void addPlugin(IngestionPlugin plugin) {
		plugins.add(plugin);
		log.info("Added plugin:" + plugin.getIdentifier());
	}

	@Override
    public void removePlugin(IngestionPlugin plugin) {
		plugins.remove(plugin);
		log.info("Removed plugin:" + plugin.getIdentifier());
	}

	@Override
    public void addStorage(StorageEngine storage) {
		log.info("Added storage:" + storage.getIdentifier());
		this.storages.add(storage);
	}

	@Override
    public void removeStorage(StorageEngine storage) {
		log.info("Removed storage:" + storage.getIdentifier());
		this.storages.remove(storage);
	}


	@Override
    public void addWorkflow(Workflow workflow) {
		workflows.add(workflow);
		log.info("Added workflow: " + workflow.getName());
	}

	@Override
    public void removeWorkflow(Workflow workflow) {
		workflows.remove(workflow);
		log.info("Removed workflow: " + workflow.getName());
	}

	@Override
    public StorageEngine getFirstStorage() {
		return storages.get(0);
	}

	@Override
    public void setFirstStorage(StorageEngine storage) {
		this.storages.add(0, storage);
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
		
		return builder.toString();
	}
}
