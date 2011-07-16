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

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.joda.time.DateTime;

import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.repoxclient.jibxbindings.Aggregator;
import eu.europeana.uim.repoxclient.jibxbindings.Aggregators;
import eu.europeana.uim.repoxclient.jibxbindings.Log;
import eu.europeana.uim.repoxclient.jibxbindings.Name;
import eu.europeana.uim.repoxclient.jibxbindings.NameCode;
import eu.europeana.uim.repoxclient.jibxbindings.RecordResult;
import eu.europeana.uim.repoxclient.jibxbindings.Success;
import eu.europeana.uim.repoxclient.jibxbindings.Url;
import eu.europeana.uim.repoxclient.objects.HarvestingType;
import eu.europeana.uim.repoxclient.plugin.RepoxRestClient;
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

	private RepoxRestClient repoxRestClient;
	private Orchestrator orchestrator;
	private Registry registry;
	
	
	
	@Override
	public boolean aggregatorExists(Provider provider)
			throws AggregatorOperationException {
		
		if(!provider.isAggregator()){
			throw new AggregatorOperationException("The requested object is not of Aggregator type");
		}
		
		String id = provider.getValue("repoxID");
		
		if(id == null){
			throw new AggregatorOperationException("Missing repoxID element from Agregator object");
		}
		
		HashSet<Provider> prov = retrieveAggregators();
		
		return prov.contains(provider);
		
	}

	
	
	@Override
	public void createAggregatorfromUIMObj(Provider aggregator,
			boolean isRecursive) throws AggregatorOperationException, ProviderOperationException {

		if(!aggregator.isAggregator()){
			throw new AggregatorOperationException("The requested object is not of Aggregator type");
		}
		
		Aggregator aggr = new Aggregator();
		
	    Name name = new Name();
	    name.setName(aggregator.getName());
		aggr.setName(name);
		NameCode namecode = new NameCode();
		namecode.setNameCode(aggregator.getMnemonic());
		aggr.setNameCode(namecode);
		Url url = new Url();
		url.setUrl(aggregator.getOaiBaseUrl());
		aggr.setUrl(url);
		
		Aggregator createdAggregator = repoxRestClient.createAggregator(aggr);
		
		aggregator.putValue("repoxID", createdAggregator.getId());
		
		if(isRecursive == true)
		{
			HashSet<Provider> provset =  (HashSet<Provider>)aggregator.getRelatedIn();
			
			Iterator<Provider> it = provset.iterator();
			
			while(it.hasNext()){
				createProviderfromUIMObj(it.next(),true);
			}
			
		}
		
	}

	
	
	@Override
	public void deleteAggregatorfromUIMObj(Provider aggregator)
			throws AggregatorOperationException {

		if(!aggregator.isAggregator()){
			throw new AggregatorOperationException("The requested object is not of Aggregator type");
		}
		
		String id = aggregator.getValue("repoxID");
		
		if(id == null){
			throw new AggregatorOperationException("Missing repoxID element from Agregator object");
		}
		
		repoxRestClient.deleteAggregator(id);
	}

	
	
	@Override
	public void updateAggregatorfromUIMObj(Provider aggregator)
			throws AggregatorOperationException {

		if(!aggregator.isAggregator()){
			throw new AggregatorOperationException("The requested object is not of Aggregator type");
		}
		
		String id = aggregator.getValue("repoxID");
		
		if(id == null){
			throw new AggregatorOperationException("Missing repoxID element from Agregator object");
		}
		
		Aggregator aggr = new Aggregator();
		
	    Name name = new Name();
	    name.setName(aggregator.getName());
		aggr.setName(name);
		NameCode namecode = new NameCode();
		namecode.setNameCode(aggregator.getMnemonic());
		aggr.setNameCode(namecode);
		Url url = new Url();
		url.setUrl(aggregator.getOaiBaseUrl());
		aggr.setUrl(url);
		
		repoxRestClient.updateAggregator(aggr);
	}


	@Override
	public HashSet<Provider> retrieveAggregators()
			throws AggregatorOperationException {

		StorageEngine<?> engine = registry.getStorageEngine();
		
		HashSet<Provider> uimAggregators = new HashSet<Provider>();
		
		Aggregators aggrs = repoxRestClient.retrieveAggregators();
		
		ArrayList<Aggregator> aggrList =  (ArrayList<Aggregator>) aggrs.getAggregatorList();

		for(Aggregator agg:aggrList){
			
			String id =  agg.getNameCode().getNameCode();
			
			try {
				Provider prov = engine.findProvider(id);
				uimAggregators.add(prov);
			} catch (StorageEngineException e) {
				// TODO Decide what to do here
			}
		
			}
		
		return uimAggregators;
	}

	@Override
	public boolean providerExists(Provider provider) throws ProviderOperationException {
		if(provider.isAggregator()){
			throw new ProviderOperationException("The requested object is not a Provider");
		}
		
		String id = provider.getValue("repoxID");
		
		if(id == null){
			throw new ProviderOperationException("Missing repoxID element from Agregator object");
		}
		
		HashSet<Provider> prov = retrieveProviders();
		
		return prov.contains(provider);
		
	}


	@Override
	public void createProviderfromUIMObj(Provider prov, boolean isRecursive)
			throws ProviderOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteProviderfromUIMObj(Provider prov)
			throws ProviderOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateProviderfromUIMObj(Provider prov)
			throws ProviderOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashSet<Provider> retrieveProviders() throws ProviderOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public HashSet<Collection> retrieveDataSources()
			throws DataSourceOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean datasourceExists(Collection col)
			throws DataSourceOperationException {
				return false;
		// TODO Auto-generated method stub
		
	}

	@Override
	public void createDatasourcefromUIMObj(Collection col, Provider prov)
			throws DataSourceOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void deleteDatasourcefromUIMObj(Collection col)
			throws DataSourceOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateDatasourcefromUIMObj(Collection col)
			throws DataSourceOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public RecordResult retrieveRecord(String recordString)
			throws RecordOperationException {

            throw new UnsupportedOperationException("Not implemented yet");
	}

	@Override
	public String initiateHarvestingfromUIMObj(HarvestingType type,
			Collection col) throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String initiateHarvestingfromUIMObj(HarvestingType type,
			Collection col, DateTime ingestionDate)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void cancelHarvesting(Collection col)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Success getHarvestingStatus(Collection col)
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
	public Log getHarvestLog(Collection col)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}

	
	
	/*
	 * Getters & Setters 
	 */
	
	public void setRepoxRestClient(RepoxRestClient repoxRestClient) {
		this.repoxRestClient = repoxRestClient;
	}

	public RepoxRestClient getRepoxRestClient() {
		return repoxRestClient;
	}

	public void setOrchestrator(Orchestrator orchestrator) {
		this.orchestrator = orchestrator;
	}

	public Orchestrator getOrchestrator() {
		return orchestrator;
	}

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public Registry getRegistry() {
		return registry;
	}

	

}
