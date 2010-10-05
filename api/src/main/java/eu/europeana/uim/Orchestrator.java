package eu.europeana.uim;

import eu.europeana.uim.plugin.IngestionPlugin;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.workflow.Workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class Orchestrator {
	
	private static Logger log = Logger.getLogger(Orchestrator.class.getName());
	
	private List<IngestionPlugin> plugins = new ArrayList<IngestionPlugin>();
    private List<Workflow> workflows = new ArrayList<Workflow>();

	
	public void addPlugin(IngestionPlugin plugin) { 
		plugins.add(plugin);
		log.info("Added plugin:" + plugin.getIdentifier());
	}
	
	public void removePlugin(IngestionPlugin plugin) { 
		plugins.remove(plugin);
		log.info("Removed plugin:" + plugin.getIdentifier());
	}

    public void addWorkflow(Workflow workflow) {
        workflows.add(workflow);
        log.info("Added workflow: " + workflow.getName());
    }

    public void removeWorkflow(Workflow workflow) {
        workflows.remove(workflow);
        log.info("Removed workflow: " + workflow.getName());
    }

    public Execution executeWorkflow(Workflow w, MetaDataRecord mdr) {
        return null;
    }

    public Execution executeWorkflow(Workflow w, Collection c) {
        return null;
    }


    public Execution executeWorkflow(Workflow w, Request r) {
        return null;

    }

    public Execution executeWorkflow(Workflow w, Provider p) {
        return null;
    }



	public String toString() {
		if (plugins.isEmpty()) {
			return "Orchestrator: No Plugins";
		}
		
		StringBuilder builder = new StringBuilder();
		for (IngestionPlugin plugin : plugins) {
			if (builder.length() > 0) {
				builder.append("\nPlugin:");
			}
			builder.append(plugin.getIdentifier() + ": [" + plugin.getDescription() + "]");
		}
		return "Orchestrator: " + builder.toString();
	}
}
