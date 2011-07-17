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

import java.math.BigInteger;
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
import eu.europeana.uim.repoxclient.jibxbindings.DataProviders;
import eu.europeana.uim.repoxclient.jibxbindings.DataSource;
import eu.europeana.uim.repoxclient.jibxbindings.DataSources;
import eu.europeana.uim.repoxclient.jibxbindings.Description;
import eu.europeana.uim.repoxclient.jibxbindings.Line;
import eu.europeana.uim.repoxclient.jibxbindings.Log;
import eu.europeana.uim.repoxclient.jibxbindings.Name;
import eu.europeana.uim.repoxclient.jibxbindings.NameCode;
import eu.europeana.uim.repoxclient.jibxbindings.OaiSet;
import eu.europeana.uim.repoxclient.jibxbindings.OaiSource;
import eu.europeana.uim.repoxclient.jibxbindings.RecordResult;
import eu.europeana.uim.repoxclient.jibxbindings.RunningTasks;
import eu.europeana.uim.repoxclient.jibxbindings.ScheduleTasks;
import eu.europeana.uim.repoxclient.jibxbindings.Source;
import eu.europeana.uim.repoxclient.jibxbindings.Success;
import eu.europeana.uim.repoxclient.jibxbindings.Task;
import eu.europeana.uim.repoxclient.jibxbindings.Url;
import eu.europeana.uim.repoxclient.jibxbindings.Source.Sequence;
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
 * This Class implements the functionality exposed by the
 * OSGI service.
 * 
 * @author Georgios Markakis
 */
public class RepoxUIMServiceImpl implements RepoxUIMService {

	private RepoxRestClient repoxRestClient;
	private Orchestrator orchestrator;
	private Registry registry;

	
	/* (non-Javadoc)
	 * @see eu.europeana.uim.repoxclient.plugin.RepoxUIMService#aggregatorExists(eu.europeana.uim.store.Provider)
	 */
	@Override
	public boolean aggregatorExists(Provider provider)
			throws AggregatorOperationException {

		if (!provider.isAggregator()) {
			throw new AggregatorOperationException(
					"The requested object is not of Aggregator type");
		}

		String id = provider.getValue("repoxID");

		if (id == null) {
			throw new AggregatorOperationException(
					"Missing repoxID element from Agregator object");
		}

		HashSet<Provider> prov = retrieveAggregators();

		return prov.contains(provider);

	}

	
	
	
	@Override
	public void createAggregatorfromUIMObj(Provider aggregator,
			boolean isRecursive) throws AggregatorOperationException,
			ProviderOperationException {

		if (!aggregator.isAggregator()) {
			throw new AggregatorOperationException(
					"The requested object is not of Aggregator type");
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

		if (isRecursive == true) {
			HashSet<Provider> provset = (HashSet<Provider>) aggregator
					.getRelatedIn();

			Iterator<Provider> it = provset.iterator();

			while (it.hasNext()) {
				createProviderfromUIMObj(it.next(), true);
			}

		}

	}

	
	
	
	
	@Override
	public void deleteAggregatorfromUIMObj(Provider aggregator)
			throws AggregatorOperationException {

		if (!aggregator.isAggregator()) {
			throw new AggregatorOperationException(
					"The requested object is not of Aggregator type");
		}

		String id = aggregator.getValue("repoxID");

		if (id == null) {
			throw new AggregatorOperationException(
					"Missing repoxID element from Agregator object");
		}

		repoxRestClient.deleteAggregator(id);
	}

	
	
	
	@Override
	public void updateAggregatorfromUIMObj(Provider aggregator)
			throws AggregatorOperationException {

		if (!aggregator.isAggregator()) {
			throw new AggregatorOperationException(
					"The requested object is not of Aggregator type");
		}

		String id = aggregator.getValue("repoxID");

		if (id == null) {
			throw new AggregatorOperationException(
					"Missing repoxID element from Agregator object");
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

		ArrayList<Aggregator> aggrList = (ArrayList<Aggregator>) aggrs
				.getAggregatorList();

		for (Aggregator agg : aggrList) {

			String id = agg.getNameCode().getNameCode();

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
	public boolean providerExists(Provider provider)
			throws ProviderOperationException {
		if (provider.isAggregator()) {
			throw new ProviderOperationException(
					"The requested object is not a Provider");
		}

		String id = provider.getValue("repoxID");

		if (id == null) {
			throw new ProviderOperationException(
					"Missing repoxID element from Agregator object");
		}

		HashSet<Provider> prov = retrieveProviders();

		return prov.contains(provider);

	}

	@Override
	public void createProviderfromUIMObj(Provider uimProv, boolean isRecursive)
			throws ProviderOperationException {

		if (uimProv.isAggregator()) {
			throw new ProviderOperationException(
					"The requested object is not a Provider");
		}

		String id = uimProv.getValue("repoxID");

		if (id == null) {
			throw new ProviderOperationException(
					"Missing repoxID element from Agregator object");
		}

		eu.europeana.uim.repoxclient.jibxbindings.Provider jibxProv = new eu.europeana.uim.repoxclient.jibxbindings.Provider();

		Name name = new Name();
		name.setName(uimProv.getName());
		jibxProv.setName(name);
		NameCode namecode = new NameCode();
		namecode.setNameCode(uimProv.getMnemonic());
		jibxProv.setNameCode(namecode);
		Url url = new Url();
		url.setUrl(uimProv.getOaiBaseUrl());
		jibxProv.setUrl(url);

		Aggregator aggr = new Aggregator();
		aggr.setId("");

		eu.europeana.uim.repoxclient.jibxbindings.Provider createdProv = repoxRestClient
				.createProvider(jibxProv, aggr);

		uimProv.putValue("repoxID", createdProv.getId());

		if (isRecursive == true) {

			HashSet<Provider> provset = (HashSet<Provider>) uimProv
					.getRelatedIn();

			Iterator<Provider> it = provset.iterator();

			while (it.hasNext()) {
				createProviderfromUIMObj(it.next(), true);
			}

		}

	}

	@Override
	public void deleteProviderfromUIMObj(Provider prov)
			throws ProviderOperationException {

		String id = prov.getValue("repoxID");

		if (id == null) {
			throw new ProviderOperationException(
					"Missing repoxID element from Agregator object");
		}


		repoxRestClient.deleteProvider(id);

	}

	@Override
	public void updateProviderfromUIMObj(Provider uimProv)
			throws ProviderOperationException {

		if (uimProv.isAggregator()) {
			throw new ProviderOperationException(
					"The requested object is not a Provider");
		}

		String id = uimProv.getValue("repoxID");

		if (id == null) {
			throw new ProviderOperationException(
					"Missing repoxID element from Agregator object");
		}

		eu.europeana.uim.repoxclient.jibxbindings.Provider jibxProv = new eu.europeana.uim.repoxclient.jibxbindings.Provider();

		Name name = new Name();
		name.setName(uimProv.getName());
		jibxProv.setName(name);
		NameCode namecode = new NameCode();
		namecode.setNameCode(uimProv.getMnemonic());
		jibxProv.setNameCode(namecode);
		Url url = new Url();
		url.setUrl(uimProv.getOaiBaseUrl());
		jibxProv.setUrl(url);

		repoxRestClient.updateProvider(jibxProv);
	}

	@Override
	public HashSet<Provider> retrieveProviders()
			throws ProviderOperationException {
		StorageEngine<?> engine = registry.getStorageEngine();

		HashSet<Provider> uimProviders = new HashSet<Provider>();

		DataProviders provs = repoxRestClient.retrieveProviders();

		ArrayList<eu.europeana.uim.repoxclient.jibxbindings.Provider> provList = (ArrayList<eu.europeana.uim.repoxclient.jibxbindings.Provider>) provs
				.getProviderList();

		for (eu.europeana.uim.repoxclient.jibxbindings.Provider prov : provList) {

			if (prov.getNameCode() != null){
				String id = prov.getNameCode().getNameCode();

				try {
					Provider uimprov = engine.findProvider(id);
					uimProviders.add(uimprov);
				} catch (StorageEngineException e) {
					// TODO Decide what to do here
				}	
			}

		}

		return uimProviders;
	}

	@Override
	public boolean datasourceExists(Collection col)
			throws DataSourceOperationException {

		String id = col.getValue("repoxID");

		if (id == null) {
			throw new DataSourceOperationException(
					"Missing repoxID element from Collection object");
		}

		HashSet<Collection> colls = retrieveDataSources();

		return colls.contains(col);
	}

	@Override
	public void createDatasourcefromUIMObj(Collection col, Provider prov)
			throws DataSourceOperationException {

		String id = col.getValue("repoxID");

		if (id == null) {
			throw new DataSourceOperationException(
					"Missing repoxID element from Collection object");
		}

		Source ds = new Source();

		Description des = new Description();
		des.setDescription(col.getValue("description"));
		ds.setDescription(des);
		ds.setNameCode(new BigInteger(col.getMnemonic()));
		ds.setName(col.getName());
		ds.setExportPath(col.getValue("exportpath"));
		ds.setSchema(col.getValue("schema"));
		ds.setNamespace(col.getValue("namespace"));
		ds.setMetadataFormat(col.getValue("metadataformat"));

		Sequence seq = new Sequence();
		OaiSet oaiSet = new OaiSet();
		oaiSet.setOaiSet(col.getOaiSet());
		seq.setOaiSet(oaiSet);
		OaiSource oaiSource = new OaiSource();
		oaiSource.setOaiSource(col.getOaiBaseUrl(true));
		seq.setOaiSource(oaiSource);
		ds.setSequence(seq);

		eu.europeana.uim.repoxclient.jibxbindings.Provider jibxProv = new eu.europeana.uim.repoxclient.jibxbindings.Provider();

		jibxProv.setId(col.getProvider().getValue("repoxID"));

		Source retsource = repoxRestClient.createDatasourceOAI(ds, jibxProv);

		col.putValue("repoxID", retsource.getId());

	}

	@Override
	public void deleteDatasourcefromUIMObj(Collection col)
			throws DataSourceOperationException {

		String id = col.getValue("repoxID");

		if (id == null) {
			throw new DataSourceOperationException(
					"Missing repoxID element from Collection object");
		}


		repoxRestClient.deleteDatasource(id);
	}

	@Override
	public void updateDatasourcefromUIMObj(Collection col)
			throws DataSourceOperationException {

		String id = col.getValue("repoxID");

		if (id == null) {
			throw new DataSourceOperationException(
					"Missing repoxID element from Collection object");
		}

		Source ds = new Source();
		ds.setId(col.getValue("repoxID"));
		Description des = new Description();
		des.setDescription(col.getValue("description"));
		ds.setDescription(des);
		ds.setNameCode(new BigInteger(col.getMnemonic()));
		ds.setName(col.getName());
		ds.setExportPath(col.getValue("exportpath"));
		ds.setSchema(col.getValue("schema"));
		ds.setNamespace(col.getValue("namespace"));
		ds.setMetadataFormat(col.getValue("metadataformat"));

		Sequence seq = new Sequence();
		OaiSet oaiSet = new OaiSet();
		oaiSet.setOaiSet(col.getOaiSet());
		seq.setOaiSet(oaiSet);
		OaiSource oaiSource = new OaiSource();
		oaiSource.setOaiSource(col.getOaiBaseUrl(true));
		seq.setOaiSource(oaiSource);
		ds.setSequence(seq);
		
		repoxRestClient.updateDatasourceOAI(ds);

	}

	
	
	@Override
	public HashSet<Collection> retrieveDataSources()
			throws DataSourceOperationException {
		
		StorageEngine<?> engine = registry.getStorageEngine();

		HashSet<Collection> uimCollections = new HashSet<Collection>();

		DataSources datasources = repoxRestClient.retrieveDataSources();

		ArrayList<Source> sourceList = (ArrayList<Source>) datasources.getSourceList();

		for (Source src : sourceList) {

			if(src.getNameCode() != null)
			{
				String id = src.getNameCode().toString();

				try {
					Collection coll = engine.findCollection(id);
					uimCollections.add(coll);
				} catch (StorageEngineException e) {
					// TODO Decide what to do here
				}
			}

		}
		
		return uimCollections;
	}

	@Override
	public RecordResult retrieveRecord(String recordString)
			throws RecordOperationException {

		throw new UnsupportedOperationException("Not implemented yet");
	}

	
	@Override
	public void initiateHarvestingfromUIMObj(Collection col) throws HarvestingOperationException {

		String id = col.getValue("repoxID");

		if (id == null) {
			throw new HarvestingOperationException(
					"Missing repoxID element from Collection object");
		}


		repoxRestClient.initiateHarvesting(id);
	}

	
	@Override
	public void initiateHarvestingfromUIMObj(Collection col, DateTime ingestionDate)
			throws HarvestingOperationException {

		String id = col.getValue("repoxID");

		if (id == null) {
			throw new HarvestingOperationException(
					"Missing repoxID element from Collection object");
		}

		Source ds = new Source();
		ds.setId(id);
		repoxRestClient.initiateHarvesting(id);

	}

	@Override
	public void cancelHarvesting(Collection col)
			throws HarvestingOperationException {
		String id = col.getValue("repoxID");

		if (id == null) {
			throw new HarvestingOperationException(
					"Missing repoxID element from Collection object");
		}

		repoxRestClient.cancelHarvesting(id);
	}

	
	@Override
	public Success getHarvestingStatus(Collection col)
			throws HarvestingOperationException {
		String id = col.getValue("repoxID");

		if (id == null) {
			throw new HarvestingOperationException(
					"Missing repoxID element from Collection object");
		}

		return repoxRestClient.getHarvestingStatus(id);

	}

	
	@Override
	public HashSet<Collection> getActiveHarvestingSessions()
			throws HarvestingOperationException {
		
		StorageEngine<?> engine = registry.getStorageEngine();

		HashSet<Collection> uimCollections = new HashSet<Collection>();

		RunningTasks rTasks = repoxRestClient.getActiveHarvestingSessions();

		ArrayList<DataSource> sourceList = (ArrayList<DataSource>) rTasks.getDataSourceList();

		for (DataSource src : sourceList) {

			String id = src.getDataSource();

			try {
				Collection coll = engine.findCollection(id);
				uimCollections.add(coll);
			} catch (StorageEngineException e) {
				// TODO Decide what to do here
			}

		}
		
		return uimCollections;
	}

	@Override
	public HashSet<Collection> getScheduledHarvestingSessions()
			throws HarvestingOperationException {
		StorageEngine<?> engine = registry.getStorageEngine();

		HashSet<Collection> uimCollections = new HashSet<Collection>();

		ScheduleTasks sTasks = repoxRestClient.getScheduledHarvestingSessions();

		ArrayList<Task> taskList = (ArrayList<Task>) sTasks.getTaskList();

		for (Task tsk : taskList) {

			String id = tsk.getId();

			try {
				Collection coll = engine.findCollection(id);
				uimCollections.add(coll);
			} catch (StorageEngineException e) {
				// TODO Decide what to do here
			}

		}
		
		return uimCollections;
	}

	
	@Override
	public String getHarvestLog(Collection col)
			throws HarvestingOperationException {

		StringBuffer sb = new StringBuffer();
		
		String id = col.getValue("repoxID");

		if (id == null) {
			throw new HarvestingOperationException(
					"Missing repoxID element from Collection object");
		}
		Log harvestLog = repoxRestClient.getHarvestLog(id);
		
		ArrayList<Line> linelist = (ArrayList<Line>) harvestLog.getLineList();
		
		for(Line ln:linelist){
			sb.append(ln.getLine());
		}
		return sb.toString();
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
