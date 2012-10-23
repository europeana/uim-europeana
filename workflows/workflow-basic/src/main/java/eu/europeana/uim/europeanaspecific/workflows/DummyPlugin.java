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

import eu.europeana.uim.api.AbstractIngestionPlugin;
import eu.europeana.uim.api.CorruptedMetadataRecordException;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.store.MetaDataRecord;

/**
 * This plugin does nothing
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 10 Oct 2012
 */
public class DummyPlugin extends AbstractIngestionPlugin{

	
	private static final List<String> params = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

	};
	/**
	 * 
	 */
	public DummyPlugin() {

		super("dummy_plugin","DummyPlugin");
	}

	@Override
	public String getIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TKey<?, ?>[] getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TKey<?, ?>[] getOptionalFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TKey<?, ?>[] getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<String> getParameters() {
		return params;
	}

	@Override
	public int getPreferredThreadCount() {

		return 10;
	}

	@Override
	public int getMaximumThreadCount() {
		// TODO Auto-generated method stub
		return 20;
	}

	@Override
	public <I> void initialize(ExecutionContext<I> context)
			throws IngestionPluginFailedException {
	}

	@Override
	public <I> void completed(ExecutionContext<I> context)
			throws IngestionPluginFailedException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public <I> boolean processRecord(MetaDataRecord<I> mdr,
			ExecutionContext<I> context) throws IngestionPluginFailedException,
			CorruptedMetadataRecordException {

		return true;
	}

}
