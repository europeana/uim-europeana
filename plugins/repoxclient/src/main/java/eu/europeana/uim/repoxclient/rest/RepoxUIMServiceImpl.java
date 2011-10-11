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
import eu.europeana.uim.repoxclient.jibxbindings.Country;
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
import eu.europeana.uim.repoxclient.jibxbindings.RecordIdPolicy;
import eu.europeana.uim.repoxclient.jibxbindings.RecordResult;
import eu.europeana.uim.repoxclient.jibxbindings.RunningTasks;
import eu.europeana.uim.repoxclient.jibxbindings.ScheduleTasks;
import eu.europeana.uim.repoxclient.jibxbindings.Source;
import eu.europeana.uim.repoxclient.jibxbindings.Success;
import eu.europeana.uim.repoxclient.jibxbindings.Task;
import eu.europeana.uim.repoxclient.jibxbindings.Type;
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

	private static final String defaultAggrgatorURL = "http://repox.ist.utl.pt";
	private static final String defaultAggrgatorIDPostfix = "aggregatorr0";
	private RepoxRestClient repoxRestClient;
	private Registry registry;


	
	
	@Override
	public boolean aggregatorExists(String countrycode)
			throws AggregatorOperationException {
		
		if(countrycode.equals("")){
			countrycode ="eu";
		}
		
		String aggrID = countrycode + defaultAggrgatorIDPostfix;
		Aggregators aggrs = repoxRestClient.retrieveAggregators();
		
		List<Aggregator> aggregatorList = aggrs.getAggregatorList();
				
		for(Aggregator aggr: aggregatorList){
		
			if(aggrID.equals(aggr.getId())){
				return true;
			}
		}		
		return false;
	}


	
	@Override
	public void createAggregator(String countryCode,String urlString)
			throws AggregatorOperationException {
		
		if(countryCode.equals("")){
			countryCode ="eu";
		}
		
		String aggrName = countryCode + "aggregator";
		Aggregator aggr = new Aggregator();

		Name name = new Name();
		name.setName(aggrName);
		aggr.setName(name);
		NameCode namecode = new NameCode();
		namecode.setNameCode(aggrName);
		aggr.setNameCode(namecode);
		Url url = new Url();
		
		if(urlString == null){
			url.setUrl(defaultAggrgatorURL);
		}
		else{
			url.setUrl(urlString);
		}
		
		aggr.setUrl(url);
		Aggregator createdAggregator = repoxRestClient.createAggregator(aggr);
		
	}
	
	
		
	
	@Override
	public void deleteAggregator(String countryCode)
			throws AggregatorOperationException {
		String aggrID = countryCode + defaultAggrgatorIDPostfix;
		repoxRestClient.deleteAggregator(aggrID);
	}

	
	
	@Override
	public void updateAggregator(String countryCode,String aggrname,String aggrNameCode, String urlString)
			throws AggregatorOperationException {
		
		String aggrID = countryCode + defaultAggrgatorIDPostfix;		
		Aggregator aggr = new Aggregator();
		aggr.setId(aggrID);
		Name name = new Name();
		
		name.setName(aggrname);
		aggr.setName(name);
		NameCode namecode = new NameCode();
		namecode.setNameCode(aggrNameCode);
		aggr.setNameCode(namecode);
		Url url = new Url();
		
		if(urlString == null){
			url.setUrl(defaultAggrgatorURL);
		}
		else{
			url.setUrl(urlString);
		}
		
		aggr.setUrl(url);

		repoxRestClient.updateAggregator(aggr);
	}

	
	
	@Override
	public HashSet<Provider<?>> retrieveAggregators()
			throws AggregatorOperationException {

		StorageEngine<?> engine = registry.getStorageEngine();

		HashSet<Provider<?>> uimAggregators = new HashSet<Provider<?>>();

		Aggregators aggrs = repoxRestClient.retrieveAggregators();

		ArrayList<Aggregator> aggrList = (ArrayList<Aggregator>) aggrs
				.getAggregatorList();

		for (Aggregator agg : aggrList) {

			String id = agg.getNameCode().getNameCode();

			try {
				Provider<?> prov = engine.findProvider(id);
				uimAggregators.add(prov);
			} catch (StorageEngineException e) {
				// TODO Decide what to do here
			}
		}

		return uimAggregators;
	}

	
	
	
	
	@Override
	public boolean providerExists(Provider<?> provider)
			throws ProviderOperationException {
		if (provider.isAggregator()) {
			throw new ProviderOperationException(
					"The requested object is not a Provider");
		}

		HashSet<Provider<?>> prov = retrieveProviders();

		return prov.contains(provider);

	}

	
	
	
	
	@Override
	public void createProviderfromUIMObj(Provider uimProv)
			throws ProviderOperationException {

		if (uimProv.isAggregator()) {
			throw new ProviderOperationException(
					"The requested object is not a Provider");
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

		Description description = new Description();
		description.setDescription(uimProv.getValue("repoxDescription"));
		jibxProv.setDescription(description);
		
		Country country =  new Country();
		country.setCountry(uimProv.getValue("repoxCountry").toLowerCase());
		jibxProv.setCountry(country);
		
		Type type = new Type();
		type.setType(uimProv.getValue("repoxProvType"));
		
		jibxProv.setType(type);
		
		
		Aggregator aggr = new Aggregator();
		
		if(country.getCountry() == null){
			aggr.setId("euaggregatorr0");
		}
		else{
			aggr.setId(country.getCountry() + defaultAggrgatorIDPostfix);
		}
		


		eu.europeana.uim.repoxclient.jibxbindings.Provider createdProv = repoxRestClient
				.createProvider(jibxProv, aggr);

		
		uimProv.putValue("repoxID", createdProv.getId());

		StorageEngine<?> engine = registry.getStorageEngine();
		try {
			engine.updateProvider(uimProv);
			engine.checkpoint();
		} catch (StorageEngineException e) {
			throw new ProviderOperationException("Updating UIM Provider object failed");
		}
		
	}

	
	
	
	
	@Override
	public void deleteProviderfromUIMObj(Provider<?> prov)
			throws ProviderOperationException {

		String id = prov.getValue("repoxID");

		if (id == null) {
			throw new ProviderOperationException(
					"Missing repoxID element from Provider object");
		}


		repoxRestClient.deleteProvider(id);

	}

	
	
	@Override
	public void updateProviderfromUIMObj(Provider<?> uimProv)
			throws ProviderOperationException {

		if (uimProv.isAggregator()) {
			throw new ProviderOperationException(
					"The requested object is not a Provider");
		}

		String id = uimProv.getValue("repoxID");

		if (id == null) {
			throw new ProviderOperationException(
					"Missing repoxID element from Provider object");
		}

		eu.europeana.uim.repoxclient.jibxbindings.Provider jibxProv = new eu.europeana.uim.repoxclient.jibxbindings.Provider();

		jibxProv.setId(id);
		Name name = new Name();
		name.setName(uimProv.getName());
		jibxProv.setName(name);
		NameCode namecode = new NameCode();
		namecode.setNameCode(uimProv.getMnemonic());
		jibxProv.setNameCode(namecode);
		Url url = new Url();
		url.setUrl(uimProv.getOaiBaseUrl());
		jibxProv.setUrl(url);

		Description description = new Description();
		description.setDescription(uimProv.getValue("repoxDescription"));
		jibxProv.setDescription(description);
		
		Country country =  new Country();
		country.setCountry(uimProv.getValue("repoxCountry").toLowerCase());
		jibxProv.setCountry(country);
		
		Type type = new Type();
		type.setType(uimProv.getValue("repoxProvType"));
		
		jibxProv.setType(type);

		repoxRestClient.updateProvider(jibxProv);
	}

	
	
	@Override
	public HashSet<Provider<?>> retrieveProviders()
			throws ProviderOperationException {
		StorageEngine<?> engine = registry.getStorageEngine();

		HashSet<Provider<?>> uimProviders = new HashSet<Provider<?>>();

		DataProviders provs = repoxRestClient.retrieveProviders();

		ArrayList<eu.europeana.uim.repoxclient.jibxbindings.Provider> provList = (ArrayList<eu.europeana.uim.repoxclient.jibxbindings.Provider>) provs
				.getProviderList();

		for (eu.europeana.uim.repoxclient.jibxbindings.Provider prov : provList) {

			if (prov.getNameCode() != null){
				String id = prov.getNameCode().getNameCode();

				try {
					Provider<?> uimprov = engine.findProvider(id);
					uimProviders.add(uimprov);
				} catch (StorageEngineException e) {
					// TODO Decide what to do here
				}	
			}

		}

		return uimProviders;
	}

	
	
	@Override
	public boolean datasourceExists(Collection<?>col)
			throws DataSourceOperationException {

		/*
		String id = col.getValue("repoxID");

		if (id == null) {
			return false;
		}
		*/

		HashSet<Collection<?>> colls = retrieveDataSources();

		return colls.contains(col);
	}

	
	
	@Override
	public void createDatasourcefromUIMObj(Collection col, Provider prov)
			throws DataSourceOperationException {
		
		Source ds = new Source();
		ds.setId(col.getValue("collectionID"));

		Description des = new Description();
		des.setDescription(col.getValue("description"));
		ds.setDescription(des);
		ds.setNameCode(col.getMnemonic());
		ds.setName(col.getName());
		//TODO: link this to a sugarCRM field
		//ds.setExportPath(col.getValue("exportpath"));
		ds.setExportPath("");
		//TODO: link this to a sugarCRM field
		//ds.setSchema(col.getValue("schema"));
		ds.setSchema("http://www.europeana.eu/schemas/ese/");
		//TODO: link this to a sugarCRM field
		//ds.setNamespace(col.getValue("namespace"));
		ds.setNamespace("http://www.europeana.eu/schemas/ese/");
		//TODO: link this to a sugarCRM field
		//ds.setMetadataFormat(col.getValue("metadataformat"));
		ds.setMetadataFormat("ese");

		Sequence seq = new Sequence();
		OaiSet oaiSet = new OaiSet();
		oaiSet.setOaiSet(col.getOaiSet());
		seq.setOaiSet(oaiSet);
		OaiSource oaiSource = new OaiSource();
		oaiSource.setOaiSource(col.getOaiBaseUrl(true));
		seq.setOaiSource(oaiSource);
		ds.setSequence(seq);

		RecordIdPolicy recordIdPolicy = new RecordIdPolicy();
		recordIdPolicy.setType("IdGenerated");
		
		ds.setRecordIdPolicy(recordIdPolicy );
		
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
		ds.setNameCode(col.getMnemonic());
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
	public HashSet<Collection<?>> retrieveDataSources()
			throws DataSourceOperationException {
		
		StorageEngine<?> engine = registry.getStorageEngine();

		HashSet<Collection<?>> uimCollections = new HashSet<Collection<?>>();

		DataSources datasources = repoxRestClient.retrieveDataSources();

		ArrayList<Source> sourceList = (ArrayList<Source>) datasources.getSourceList();

		for (Source src : sourceList) {

			if(src.getNameCode() != null)
			{
				String id = src.getNameCode().toString();

				try {
					Collection<?> coll = engine.findCollection(id);
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
	public void initiateHarvestingfromUIMObj(Collection<?> col) throws HarvestingOperationException {

		String id = col.getValue("repoxID");

		if (id == null) {
			throw new HarvestingOperationException(
					"Missing repoxID element from Collection object");
		}


		repoxRestClient.initiateHarvesting(id);
	}

	
	@Override
	public void initiateHarvestingfromUIMObj(Collection<?> col, DateTime ingestionDate)
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
	public void cancelHarvesting(Collection<?> col)
			throws HarvestingOperationException {
		String id = col.getValue("repoxID");

		if (id == null) {
			throw new HarvestingOperationException(
					"Missing repoxID element from Collection object");
		}

		repoxRestClient.cancelHarvesting(id);
	}

	
	
	@Override
	public Success getHarvestingStatus(Collection<?> col)
			throws HarvestingOperationException {
		String id = col.getValue("repoxID");

		if (id == null) {
			throw new HarvestingOperationException(
					"Missing repoxID element from Collection object");
		}

		return repoxRestClient.getHarvestingStatus(id);

	}

	
	
	
	@Override
	public HashSet<Collection<?>> getActiveHarvestingSessions()
			throws HarvestingOperationException {
		
		StorageEngine<?> engine = registry.getStorageEngine();

		HashSet<Collection<?>> uimCollections = new HashSet<Collection<?>>();

		RunningTasks rTasks = repoxRestClient.getActiveHarvestingSessions();

		ArrayList<DataSource> sourceList = (ArrayList<DataSource>) rTasks.getDataSourceList();

		for (DataSource src : sourceList) {

			String id = src.getDataSource();

			try {
				Collection<?> coll = engine.findCollection(id);
				uimCollections.add(coll);
			} catch (StorageEngineException e) {
				// TODO Decide what to do here
			}

		}
		
		return uimCollections;
	}

	
	
	@Override
	public HashSet<Collection<?>> getScheduledHarvestingSessions()
			throws HarvestingOperationException {
		StorageEngine<?> engine = registry.getStorageEngine();

		HashSet<Collection<?>> uimCollections = new HashSet<Collection<?>>();

		ScheduleTasks sTasks = repoxRestClient.getScheduledHarvestingSessions();

		ArrayList<Task> taskList = (ArrayList<Task>) sTasks.getTaskList();

		for (Task tsk : taskList) {

			String id = tsk.getId();

			try {
				Collection<?> coll = engine.findCollection(id);
				uimCollections.add(coll);
			} catch (StorageEngineException e) {
				// TODO Decide what to do here
			}

		}
		
		return uimCollections;
	}

	
	
	@Override
	public String getHarvestLog(Collection<?> col)
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

	public void setRegistry(Registry registry) {
		this.registry = registry;
	}

	public Registry getRegistry() {
		return registry;
	}








}
