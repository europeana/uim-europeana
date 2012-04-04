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
package eu.europeana.uim.repoxclient.workflow;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.uim.api.AbstractIngestionPlugin;
import eu.europeana.uim.api.CorruptedMetadataRecordException;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.EuropeanaDatasetStates;
import eu.europeana.uim.repox.DataSourceOperationException;
import eu.europeana.uim.repox.HarvestingOperationException;
import eu.europeana.uim.repox.RepoxUIMService;
import eu.europeana.uim.repox.model.HarvestingState;
import eu.europeana.uim.repox.model.RepoxHarvestingStatus;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.sugarcrm.QueryResultException;
import eu.europeana.uim.sugarcrm.SugarCrmService;
import eu.europeana.uim.util.BatchWorkflowStart;

/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 2 Apr 2012
 */
public class RepoxInitHarvestingPlugin extends AbstractIngestionPlugin{

	private static RepoxUIMService repoxservice;
	private static SugarCrmService sugarservice;
	/** Property which allows to overwrite base url from collection/provider */
	public static final String fullingest = "repox.fullingest";
	
	/**
	 * The parameters used by this Plugin
	 */
	private static final List<String> params = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;
		{
			add(fullingest);
		}
	};
	
	
	/**
	 * @param repoxservice
	 * @param sugarservice
	 * @param name
	 * @param description
	 */
	public RepoxInitHarvestingPlugin(RepoxUIMService repoxservice,
			SugarCrmService sugarservice,String name, String description) {
		super(name, description);
		RepoxInitHarvestingPlugin.repoxservice = repoxservice;
		RepoxInitHarvestingPlugin.sugarservice = sugarservice;
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.api.IngestionPlugin#completed(eu.europeana.uim.api.ExecutionContext)
	 */
	@Override
	public <I> void completed(ExecutionContext<I> context)
			throws IngestionPluginFailedException {
		
		Collection<?> coll =  (Collection<?>) context.getDataSet();
		
		try {
			
			boolean collectionExists = repoxservice.datasourceExists(coll);
			
			if(collectionExists){
				repoxservice.initiateHarvestingfromUIMObj(coll, true);
				
				RepoxHarvestingStatus status = repoxservice.getHarvestingStatus(coll);
				
				context.getExecution().setActive(true);
				
				while(status.getStatus() == HarvestingState.RUNNING){
					String norecords = status.getRecords().split("/")[0];
					
					context.getExecution().setProcessedCount(Integer.parseInt(norecords));
					status = repoxservice.getHarvestingStatus(coll);
				}
				
				context.getExecution().setActive(false);
				
				String sugarID = coll.getValue("sugarCRMID");
				switch(status.getStatus()){
				case OK:
					sugarservice.changeEntryStatus(sugarID, 
							EuropeanaDatasetStates.READY_FOR_REPLICATION);
					break;
				case ERROR:
					
					sugarservice.changeEntryStatus(sugarID, 
							EuropeanaDatasetStates.HARVESTING_PENDING);
					break;
				
				}
			}
			else{
				
			}
		} catch (DataSourceOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (HarvestingOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (QueryResultException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO Auto-generated method stub
		
	}

	@Override
	public TKey<?, ?>[] getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaximumThreadCount() {
		return 100;
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
	public List<String> getParameters() {
		return params;
	}

	@Override
	public int getPreferredThreadCount() {
		
		return 10;
	}

	@Override
	public void initialize() {
		// nothing done here
		
	}

	@Override
	public <I> void initialize(ExecutionContext<I> arg0)
			throws IngestionPluginFailedException {
		
		
		
	}

	@Override
	public <I> boolean processRecord(MetaDataRecord<I> arg0,
			ExecutionContext<I> arg1) throws IngestionPluginFailedException,
			CorruptedMetadataRecordException {
	
		//Does nothing
		return true;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		
	}

}
