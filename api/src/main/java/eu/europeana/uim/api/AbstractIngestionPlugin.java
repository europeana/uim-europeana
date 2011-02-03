package eu.europeana.uim.api;

import java.util.logging.Level;
import java.util.logging.Logger;

import eu.europeana.uim.MDRFieldRegistry;
import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.TKey;

public class AbstractIngestionPlugin implements IngestionPlugin {
	private static final Logger log = Logger.getLogger(AbstractIngestionPlugin.class.getName());

	private String qualifier;
	private Level level = Level.FINEST;

	public AbstractIngestionPlugin() {
	}

	public AbstractIngestionPlugin(String qualifier) {
		this.qualifier = qualifier;
	}

	public AbstractIngestionPlugin(String qualifier, Level level) {
		this.qualifier = qualifier;
		this.level = level;
	}

	@Override
	public int getPreferredThreadCount() {
		return 1;
	}

	@Override
	public int getMaximumThreadCount() {
		return 1;
	}


	@Override
	@SuppressWarnings("unchecked")
	public TKey<MDRFieldRegistry, ?>[] getInputParameters() {
		return new TKey[0];
	}

	@Override
	@SuppressWarnings("unchecked")
	public TKey<MDRFieldRegistry, ?>[] getOutputParameters() {
		return new TKey[0];
	}

	@Override
	@SuppressWarnings("unchecked")
	public TKey<MDRFieldRegistry, ?>[] getTransientParameters() {
		return new TKey[0];
	}


	@Override
	public String getIdentifier() {
		return AbstractIngestionPlugin.class.getSimpleName() + (qualifier != null ? ":" + qualifier : "");
	}


	@Override
	public String getDescription() {
		return "Writes the identifiers of MDRs to sysout.";
	}

	@Override
	public void processRecord(MetaDataRecord mdr) {
		String identifier = mdr.getIdentifier();
		if (log.isLoggable(level)) {
			log.log(level, qualifier + ":" + identifier);
		}
	}

}
