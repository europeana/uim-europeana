/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.plugin.linkchecker.service;

import java.util.List;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.api.CorruptedMetadataRecordException;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPlugin;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;

/**
 * @author georgiosmarkakis
 *
 */
public class LinkCheckerServiceImpl implements IngestionPlugin {

	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

	public String getDescription() {
		// TODO Auto-generated method stub
		return null;
	}

	public TKey<?, ?>[] getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public TKey<?, ?>[] getOptionalFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public TKey<?, ?>[] getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	public void initialize() {
		// TODO Auto-generated method stub
		
	}

	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

	public List<String> getParameters() {
		// TODO Auto-generated method stub
		return null;
	}

	public int getPreferredThreadCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public int getMaximumThreadCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	public void initialize(ExecutionContext context)
			throws IngestionPluginFailedException {
		// TODO Auto-generated method stub
		
	}

	public void completed(ExecutionContext context)
			throws IngestionPluginFailedException {
		// TODO Auto-generated method stub
		
	}

	public boolean processRecord(MetaDataRecord<?> mdr, ExecutionContext context)
			throws IngestionPluginFailedException,
			CorruptedMetadataRecordException {

		
		
		return false;
	}

}
