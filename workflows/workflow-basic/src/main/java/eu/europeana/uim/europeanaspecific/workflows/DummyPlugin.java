/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.europeanaspecific.workflows;

import java.util.ArrayList;
import java.util.List;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;


/**
 * This plugin does nothing
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 10 Oct 2012
 */
public class DummyPlugin<I> extends AbstractIngestionPlugin<MetaDataRecord<I>,I>{

	
	private static final List<String> params = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
	};
	
	/**
	 * 
	 */
	public DummyPlugin() {

		super("dummy_plugin","DummyPlugin");
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin#getIdentifier()
	 */
	@Override
	public String getIdentifier() {
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin#getName()
	 */
	@Override
	public String getName() {
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin#getDescription()
	 */
	@Override
	public String getDescription() {
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.ingestion.IngestionPlugin#getInputFields()
	 */
	@Override
	public TKey<?, ?>[] getInputFields() {
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.ingestion.IngestionPlugin#getOptionalFields()
	 */
	@Override
	public TKey<?, ?>[] getOptionalFields() {
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.ingestion.IngestionPlugin#getOutputFields()
	 */
	@Override
	public TKey<?, ?>[] getOutputFields() {
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.Plugin#initialize()
	 */
	@Override
	public void initialize() {
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.Plugin#shutdown()
	 */
	@Override
	public void shutdown() {		
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.Plugin#getParameters()
	 */
	@Override
	public List<String> getParameters() {
		return params;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.Plugin#getPreferredThreadCount()
	 */
	@Override
	public int getPreferredThreadCount() {
		return 10;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.Plugin#getMaximumThreadCount()
	 */
	@Override
	public int getMaximumThreadCount() {
		return 20;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.ExecutionPlugin#initialize(eu.europeana.uim.orchestration.ExecutionContext)
	 */
	@Override
	public void initialize(ExecutionContext<MetaDataRecord<I>,I> context)
			throws IngestionPluginFailedException {
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.ExecutionPlugin#completed(eu.europeana.uim.orchestration.ExecutionContext)
	 */
	@Override
	public void completed(ExecutionContext<MetaDataRecord<I>,I> context)
			throws IngestionPluginFailedException {		
	}



	/* (non-Javadoc)
	 * @see eu.europeana.uim.plugin.ingestion.IngestionPlugin#process(eu.europeana.uim.store.UimDataSet, eu.europeana.uim.orchestration.ExecutionContext)
	 */
	@Override
	public boolean process(MetaDataRecord<I> arg0,
			ExecutionContext<MetaDataRecord<I>,I> arg1)
			throws IngestionPluginFailedException, CorruptedDatasetException {

		return true;
	}

}
