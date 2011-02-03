package eu.europeana.uim.api;

import eu.europeana.uim.MetaDataRecord;

public class IngestionWorkflowStep extends AbstractWorkflowStep {

	private final IngestionPlugin plugin;
	
	public IngestionWorkflowStep(IngestionPlugin plugin) {
		super(plugin.getIdentifier());
		this.plugin = plugin;
	}
	
	public IngestionWorkflowStep(IngestionPlugin plugin, boolean savepoint) {
		super(plugin.getIdentifier(), savepoint);
		this.plugin = plugin;
	}
	
	@Override
	public int getPreferredThreadCount() {
		return plugin.getPreferredThreadCount();
	}

	@Override
	public int getMaximumThreadCount() {
		return plugin.getMaximumThreadCount();
	}

	@Override
	public void processRecord(MetaDataRecord mdr) {
		plugin.processRecord(mdr);
	}

}
