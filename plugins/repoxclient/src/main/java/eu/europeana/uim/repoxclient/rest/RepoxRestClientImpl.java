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

import org.joda.time.DateTime;
import org.springframework.web.client.RestTemplate;


import eu.europeana.uim.repoxclient.jibxbindings.Aggregator;
import eu.europeana.uim.repoxclient.jibxbindings.Aggregators;
import eu.europeana.uim.repoxclient.jibxbindings.DataSource;
import eu.europeana.uim.repoxclient.jibxbindings.DataSources;
import eu.europeana.uim.repoxclient.jibxbindings.Log;
import eu.europeana.uim.repoxclient.jibxbindings.Provider;
import eu.europeana.uim.repoxclient.jibxbindings.DataProviders;
import eu.europeana.uim.repoxclient.jibxbindings.RecordResult;
import eu.europeana.uim.repoxclient.jibxbindings.Response;
import eu.europeana.uim.repoxclient.jibxbindings.RunningTasks;
import eu.europeana.uim.repoxclient.jibxbindings.ScheduleTasks;
import eu.europeana.uim.repoxclient.jibxbindings.Success;
//import eu.europeana.uim.repoxclient.jibxbindings.Status;

import eu.europeana.uim.repoxclient.objects.HarvestingType;
import eu.europeana.uim.repoxclient.plugin.RepoxRestClient;
import eu.europeana.uim.repoxclient.rest.exceptions.AggregatorOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.DataSourceOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.HarvestingOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.ProviderOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RecordOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RepoxException;



/**
 * Class implementing REST functionality for accessing the REPOX repository.
 * 
 * @author Georgios Markakis
 */
public class RepoxRestClientImpl  implements RepoxRestClient {

	private RestTemplate restTemplate;

	private String defaultURI;

	
	///rest/aggregators/create?name=Judaica&nameCode=093&homepage=http://repox.ist.utl.pt
	@Override
	public Aggregator createAggregator(Aggregator aggregator)
			throws AggregatorOperationException {
		
		StringBuffer name = new StringBuffer();
		StringBuffer nameCode = new StringBuffer();
		StringBuffer homepage = new StringBuffer();

		name.append("name=");
		name.append(aggregator.getName().getName());
		nameCode.append("nameCode=");
		nameCode.append(aggregator.getNameCode().getNameCode());
		homepage.append("homepage=");
		homepage.append(aggregator.getUrl().getUrl());
		
		Response resp = invokRestTemplate("/aggregators/create",Response.class,
				name.toString(),nameCode.toString(),homepage.toString());
		
		if (resp.getAggregator() == null) {
			if (resp.getError() != null) {
				throw new AggregatorOperationException(resp.getError());
			} else {
				throw new AggregatorOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getAggregator();
		}
	}


	@Override
	public Success deleteAggregator(String aggregatorId)
			throws AggregatorOperationException {

		StringBuffer id = new StringBuffer();
		id.append("id=");
		id.append(aggregatorId);
		
		Response resp = invokRestTemplate("/aggregators/delete",Response.class,id.toString());
		
		if (resp.getSuccess() == null) {
			if (resp.getError() != null) {
				throw new AggregatorOperationException(resp.getError());
			} else {
				throw new AggregatorOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getSuccess();
		}
		
	}


	@Override
	public Aggregator updateAggregator(Aggregator aggregator)
			throws AggregatorOperationException {
		StringBuffer id = new StringBuffer();
		StringBuffer name = new StringBuffer();
		StringBuffer nameCode = new StringBuffer();
		StringBuffer homepage = new StringBuffer();

		id.append("id=");
		id.append(aggregator.getId());
		name.append("name=");
		name.append(aggregator.getName().getName());
		nameCode.append("nameCode=");
		nameCode.append(aggregator.getNameCode().getNameCode());
		homepage.append("homepage=");
		homepage.append(aggregator.getUrl().getUrl());
		
		Response resp = invokRestTemplate("/aggregators/update",Response.class,
				id.toString(),name.toString(),nameCode.toString(),homepage.toString());
		
		if (resp.getAggregator() == null) {
			if (resp.getError() != null) {
				throw new AggregatorOperationException(resp.getError());
			} else {
				throw new AggregatorOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getAggregator();
		}
		
	}


	@Override
	public Aggregators retrieveAggregators() throws AggregatorOperationException {
		Response resp = invokRestTemplate("/aggregators/list",Response.class);
		return resp.getAggregators();
	}

	
	/**
	 * 
	 * @return DataSources the available datasources
	 * @throws RepoxException
	 */
	public DataSources retrieveDataSources() throws DataSourceOperationException {

		Response resp = invokRestTemplate("/dataSources/list",Response.class);

		if (resp.getDataSources() == null) {
			if (resp.getError() != null) {
				throw new DataSourceOperationException(resp.getError());
			} else {
				throw new DataSourceOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getDataSources();
		}
	}
	
	
	// Public methods exposed by the service
	
	public void createDatasource(DataSource ds)
			throws DataSourceOperationException {


	}

	public void deleteDatasource(DataSource ds)
			throws DataSourceOperationException {


	}

	public void updateDatasource(DataSource ds)
			throws DataSourceOperationException {


	}

	public RecordResult retrieveRecord(String recordString)
			throws RecordOperationException{

		return null;
	}

	public String initiateHarvesting(HarvestingType type, DataSource ds)
			throws HarvestingOperationException{

		return null;
	}

	public String initiateHarvesting(HarvestingType type, DataSource ds,
			DateTime ingestionDate) throws HarvestingOperationException{

		return null;
	}

	public String harvestingStatus(String ingestionProcessId)
			throws RepoxException {

		return null;
	}



	// Private Methods

	/**
	 * Auxiliary method for invoking a REST operation
	 * 
	 * @param <S>
	 *            the return type
	 * @param wsOperation
	 * @return
	 */
	private <S> S invokRestTemplate(String restOperation,Class<S> responseClass) {

		StringBuffer operation = new StringBuffer();
		operation.append(defaultURI);
		operation.append(restOperation);
		S restResponse = restTemplate.getForObject(operation.toString(), responseClass);

		return restResponse;
	}

	/**
	 * Auxiliary method for invoking a REST operation with parameters
	 * 
	 * @param <S>
	 *            the return type
	 * @param wsOperation
	 * @return
	 */
	private <S> S invokRestTemplate(String restOperation,Class<S> responseClass,String... params) {

		StringBuffer operation = new StringBuffer();
		operation.append(defaultURI);
		operation.append(restOperation);
		operation.append("?");
		
		for (int i=0; i< params.length; i++){
			if (i != 0){
				operation.append("&");
			}
			operation.append(params[i]);
		}
		
		
		S restResponse = restTemplate.getForObject(operation.toString(), responseClass);

		return restResponse;
	}
	// Getters & Setters

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setDefaultURI(String defaultURI) {
		this.defaultURI = defaultURI;
	}

	public String getDefaultURI() {
		return defaultURI;
	}




	@Override
	public void createProvider(Provider prov, Aggregator agr)
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
	public DataProviders retrieveProviders() throws ProviderOperationException {
		Response resp = invokRestTemplate("/dataProviders/list",Response.class);
		return resp.getDataProviders();
	}


	@Override
	public void cancelHarvesting(DataSource ds)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		
	}


	@Override
	public Success getHarvestingStatus(DataSource ds)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RunningTasks getActiveHarvestingSessions()
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ScheduleTasks getScheduledHarvestingSessions()
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Log getHarvestLog(DataSource ds)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}

}
