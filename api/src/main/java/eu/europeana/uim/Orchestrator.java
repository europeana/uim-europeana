package eu.europeana.uim;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import eu.europeana.uim.plugin.IngestionPlugin;

public class Orchestrator {
	
	private static Logger log = Logger.getLogger(Orchestrator.class.getName());
	
	private List<IngestionPlugin> plugins = new ArrayList<IngestionPlugin>();

	
	public void addPlugin(IngestionPlugin plugin) { 
		plugins.add(plugin);
		log.info("Added plugin:" + plugin.getIdentifier());
	}
	
	
	public void removePlugin(IngestionPlugin plugin) { 
		plugins.remove(plugin);
		log.info("Removed plugin:" + plugin.getIdentifier());
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
