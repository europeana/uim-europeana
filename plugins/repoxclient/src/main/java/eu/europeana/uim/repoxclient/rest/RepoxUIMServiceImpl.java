/**
 * 
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

	@Override
	public void createAggregator(Provider provider)
			throws AggregatorOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteAggregator(Provider provider)
			throws AggregatorOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateAggregator(Provider provider)
			throws AggregatorOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Provider> retrieveAggregators()
			throws AggregatorOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createProvider(Provider prov, Provider agr)
			throws ProviderOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createProvider(Provider prov) throws ProviderOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteProvider(Provider prov) throws ProviderOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProvider(Provider prov) throws ProviderOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<Provider> retrieveProviders() throws ProviderOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Collection> retrieveDataSources()
			throws DataSourceOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void createDatasource(Collection col)
			throws DataSourceOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteDatasource(Collection col)
			throws DataSourceOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDatasource(Collection col)
			throws DataSourceOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RecordResult retrieveRecord(String recordString)
			throws RecordOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String initiateHarvesting(HarvestingType type, Collection col)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String initiateHarvesting(HarvestingType type, Collection col,
			DateTime ingestionDate) throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelHarvesting(Collection col)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Status getHarvestingStatus(Collection col)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Collection> getActiveHarvestingSessions()
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Collection> getScheduledHarvestingSessions()
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Harvestlog getHarvestLog(Collection col)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}

}
