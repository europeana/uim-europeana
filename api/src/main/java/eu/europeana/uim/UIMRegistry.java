package eu.europeana.uim;

import eu.europeana.uim.api.IngestionPlugin;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.Workflow;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

public class UIMRegistry implements Registry {

	private static Logger log = Logger.getLogger(UIMRegistry.class.getName());

  private StorageEngine activeStorage = null;
	private List<StorageEngine> storages = new ArrayList<StorageEngine>();
	private Map<String, IngestionPlugin> plugins = new HashMap<String, IngestionPlugin>();
	private List<Workflow> workflows = new ArrayList<Workflow>();

	private Orchestrator orchestrator = null;

	public UIMRegistry() {
	}

    @Override
    public List<Workflow> getWorkflows() {
        return workflows;
    }

    @Override
    public Workflow getWorkflow(String identifier) {
        for(Workflow w : workflows) {
            if(w.getName().equals(identifier)) {
                return w;
            }
        }
        return null;
    }

    @Override
    public Workflow getWorkflow(Long id) {
        for(Workflow w : workflows) {
            if(w.getId().equals(id)) {
                return w;
            }
        }
        return null;
    }

    @Override
	public void addPlugin(IngestionPlugin plugin) {
		if (plugin != null) {
			log.info("Added plugin:" + plugin.getIdentifier());
			if (!plugins.containsKey(plugin.getIdentifier()))
				plugins.put(plugin.getIdentifier(), plugin);
		}
	}

    @Override
    public IngestionPlugin getPlugin(String identifier) {
        return plugins.get(identifier);
    }

    @Override
	public void removePlugin(IngestionPlugin plugin) {
		if (plugin != null) {
			log.info("Removed plugin:" + plugin.getIdentifier());
			plugins.remove(plugin.getIdentifier());
		}
	}


	@Override
	public void addStorage(StorageEngine storage) {
		if (storage != null) {
			log.info("Added storage:" + storage.getIdentifier());
			if (!storages.contains(storage))
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
    public List<StorageEngine> getStorages() {
        return storages;
    }


    @Override
	public void addWorkflow(Workflow workflow) {
		if (workflow != null) { 
			log.info("Added workflow: " + workflow.getName());
			if (!workflows.contains(workflow))
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
    public void setActiveStorage(StorageEngine storage) {
        activeStorage = storage;
    }

    @Override
	public StorageEngine getStorage() {
		if (storages == null || storages.isEmpty()) return null;
        if(activeStorage == null) activeStorage = storages.get(0);
        return activeStorage;
	}

	@Override
	public StorageEngine getStorage(String identifier) {
		if (storages == null || storages.isEmpty()) return null;
		for (StorageEngine storage : storages) {
			if (identifier.equals(storage.getIdentifier())) {
				return storage;
			}
		}
		return null;
	}


	public String toString() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("\nRegistered plugins:");
		builder.append("\n--------------------------------------");
		if (plugins.isEmpty()) {
			builder.append("\n\tNo plugins. ");
		} else {
			for (IngestionPlugin plugin : plugins.values()) {
				if (builder.length() > 0) {
					builder.append("\n\tPlugin:");
				}
                builder.append(plugin.getIdentifier()).append(": [").append(plugin.getDescription()).append("]");
			}
		}

        builder.append("\nRegistered workflows:");
        builder.append("\n--------------------------------------");
        if (plugins.isEmpty()) {
            builder.append("\n\tNo workflows. ");
        } else {
            for (Workflow worfklow : workflows) {
                if (builder.length() > 0) {
                    builder.append("\n\tWorkflow:");
                }
                builder.append(worfklow.getName()).append(": [").append(worfklow.getDescription()).append("]");
            }
        }


		builder.append("\nRegistered storage:");
		builder.append("\n--------------------------------------");
		if (storages.isEmpty()) {
			builder.append("\n\tNo storage.");
		} else {
			for (StorageEngine storage : storages) {
				if (builder.length() > 0) {
					builder.append("\n\t");
				}
				builder.append(storage.getIdentifier());
                builder.append(" [").append(storage.getStatus()).append("] ");
				builder.append(storage.getConfiguration().toString());
			}
		}

		return builder.toString();
	}

}
