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
package eu.europeana.uim.repoxclient.rest;

import java.util.List;

import org.joda.time.DateTime;

import eu.europeana.uim.repoxclient.jibxbindings.Harvestlog;
import eu.europeana.uim.repoxclient.jibxbindings.RecordResult;
import eu.europeana.uim.repoxclient.jibxbindings.Status;
import eu.europeana.uim.repoxclient.objects.HarvestingType;
import eu.europeana.uim.repoxclient.plugin.RepoxUIMService;
import eu.europeana.uim.repoxclient.rest.exceptions.AggregatorOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.DataSourceOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.HarvestingOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.ProviderOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RecordOperationException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;

/**
 * 
 * 
 * @author Georgios Markakis
 */
public class RepoxUIMServiceImpl implements RepoxUIMService {

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#createAggregator(eu.europeana.uim.store.Provider)
	 */
	@Override
	public void createAggregator(Provider provider)
			throws AggregatorOperationException {
		
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#deleteAggregator(eu.europeana.uim.store.Provider)
	 */
	@Override
	public void deleteAggregator(Provider provider)
			throws AggregatorOperationException {

		
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#updateAggregator(eu.europeana.uim.store.Provider)
	 */
	@Override
	public void updateAggregator(Provider provider)
			throws AggregatorOperationException {

		
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#retrieveAggregators()
	 */
	@Override
	public List<Provider> retrieveAggregators()
			throws AggregatorOperationException {

		return null;
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#createProvider(eu.europeana.uim.store.Provider, eu.europeana.uim.store.Provider)
	 */
	@Override
	public void createProvider(Provider prov, Provider agr)
			throws ProviderOperationException {
		
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#createProvider(eu.europeana.uim.store.Provider)
	 */
	@Override
	public void createProvider(Provider prov) throws ProviderOperationException {

		
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#deleteProvider(eu.europeana.uim.store.Provider)
	 */
	@Override
	public void deleteProvider(Provider prov) throws ProviderOperationException {

		
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#updateProvider(eu.europeana.uim.store.Provider)
	 */
	@Override
	public void updateProvider(Provider prov) throws ProviderOperationException {

		
	}

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#retrieveProviders()
	 */
	@Override
	public List<Provider> retrieveProviders() throws ProviderOperationException {

		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#retrieveDataSources()
	 */
	@Override
	public List<Collection> retrieveDataSources()
			throws DataSourceOperationException {

		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#createDatasource(eu.europeana.uim.store.Collection)
	 */
	@Override
	public void createDatasource(Collection col)
			throws DataSourceOperationException {
		
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#deleteDatasource(eu.europeana.uim.store.Collection)
	 */
	@Override
	public void deleteDatasource(Collection col)
			throws DataSourceOperationException {
		
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#updateDatasource(eu.europeana.uim.store.Collection)
	 */
	@Override
	public void updateDatasource(Collection col)
			throws DataSourceOperationException {
		
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#retrieveRecord(java.lang.String)
	 */
	@Override
	public RecordResult retrieveRecord(String recordString)
			throws RecordOperationException {
		
		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#initiateHarvesting(eu.europeana.uim.repoxclient.objects.HarvestingType, eu.europeana.uim.store.Collection)
	 */
	@Override
	public String initiateHarvesting(HarvestingType type, Collection col)
			throws HarvestingOperationException {

		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#initiateHarvesting(eu.europeana.uim.repoxclient.objects.HarvestingType, eu.europeana.uim.store.Collection, org.joda.time.DateTime)
	 */
	@Override
	public String initiateHarvesting(HarvestingType type, Collection col,
			DateTime ingestionDate) throws HarvestingOperationException {

		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#cancelHarvesting(eu.europeana.uim.store.Collection)
	 */
	@Override
	public void cancelHarvesting(Collection col)
			throws HarvestingOperationException {

		
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#getHarvestingStatus(eu.europeana.uim.store.Collection)
	 */
	@Override
	public Status getHarvestingStatus(Collection col)
			throws HarvestingOperationException {

		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#getActiveHarvestingSessions()
	 */
	@Override
	public List<Collection> getActiveHarvestingSessions()
			throws HarvestingOperationException {

		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#getScheduledHarvestingSessions()
	 */
	@Override
	public List<Collection> getScheduledHarvestingSessions()
			throws HarvestingOperationException {

		return null;
	}

	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#getHarvestLog(eu.europeana.uim.store.Collection)
	 */
	@Override
	public Harvestlog getHarvestLog(Collection col)
			throws HarvestingOperationException {

		return null;
	}

}
