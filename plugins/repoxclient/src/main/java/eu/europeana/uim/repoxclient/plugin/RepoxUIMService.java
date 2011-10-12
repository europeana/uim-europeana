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
package eu.europeana.uim.repoxclient.plugin;

import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.repoxclient.jibxbindings.Log;
import eu.europeana.uim.repoxclient.jibxbindings.RecordResult;
import eu.europeana.uim.repoxclient.jibxbindings.Success;
import eu.europeana.uim.repoxclient.objects.HarvestingType;
import eu.europeana.uim.repoxclient.rest.exceptions.AggregatorOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.DataSourceOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.HarvestingOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.ProviderOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RecordOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RepoxException;

/**
 * Interface declaration of the Repox REST based OSGI service for UIM.
 * This is basically a wrapper over the previous UIM service which provides
 * UIM specific functionality. 
 * 
 * @author Georgios Markakis
 */
public interface RepoxUIMService {
	
	/**
	 * Checks if an aggregator instance in UIM also exists in Repox 
	 * 
	 * @param aggregator an Aggregator object 
	 * @throws AggregatorOperationException
	 */
	public boolean aggregatorExists(String countrycode) throws AggregatorOperationException;

	
	/**
	 * Creates an Aggregator in Repox
	 * 
	 * @param countryCode the Country Code for the Aggregator 
	 * @throws AggregatorOperationException
	 * @throws ProviderOperationException 
	 */
	public void createAggregator(String countryCode,String url) throws AggregatorOperationException;
	
	/**
	 * Deletes an existing Aggregator from Repox
	 * @param countryCode the Country Code for the Aggregator 
	 * @throws AggregatorOperationException
	 */
	public void deleteAggregator(String countryCode)throws AggregatorOperationException;	
	
	
	/**
	 * Updates an existing Aggregator in Repox
	 * @param aggregator the Aggregator object to update
	 * @throws AggregatorOperationException
	 */
	public void updateAggregator(String countryCode,String name,String nameCode,String url)throws AggregatorOperationException;	
	
	
	/**
	 * Checks if a UIM provider instance already exists in Repox
	 * 
	 * @param prov a UIM Provider Object
	 * @throws ProviderOperationException
	 */
	public boolean providerExists(Provider<?> prov) throws ProviderOperationException;
	
		
	
	/**
	 * Creates a provider in Repox
	 * 
	 * @param prov a UIM Provider Object
	 * @throws ProviderOperationException
	 */
	public void createProviderfromUIMObj(Provider<?> prov) throws ProviderOperationException;
	
	
	/**
	 * Deletes a provider from Repox (the Collection Objects belonging to this provider are deleted as well)
	 * 
	 * @param prov a UIM Provider Object
	 * @throws ProviderOperationException
	 */
	public void deleteProviderfromUIMObj(Provider<?> prov) throws ProviderOperationException;
	
	
	/**
	 * Updates a provider within Repox
	 * 
	 * @param prov a UIM Provider Object
	 * @throws ProviderOperationException
	 */
	public void updateProviderfromUIMObj(Provider<?> prov) throws ProviderOperationException;
	

	/**
	 * Retrieves all the available Aggregators from Repox
	 * @return an object containing all available Aggregators 
	 * @throws AggregatorOperationException
	 */
	public Set<Provider<?>> retrieveAggregators() throws AggregatorOperationException;	
	
	
	/**
	 * Retrieves all available providers within Repox
	 * @return a List of UIM Provider objects
	 * @throws ProviderOperationException
	 */
	public Set<Provider<?>> retrieveProviders() throws ProviderOperationException;	
	
	
	/**
	 * Retrieve all available Repox DataSources
	 * 
	 * @return a DataSources object
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public Set<Collection<?>> retrieveDataSources() throws DataSourceOperationException;
	
	
	/**
	 * Checks if a collection is represented as a datasource in Repox
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public boolean datasourceExists(Collection<?> col) throws DataSourceOperationException;

	
	
	/**
	 * Create a Repox DataSource from an UIM object. The uim object is also assigned a "repoxid" attribute
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public void createDatasourcefromUIMObj(Collection<?> col,Provider<?> prov) throws DataSourceOperationException;
	
	
	/**
	 * Delete a Repox DataSource
	 * 
	 * @param col a UIM Collection object
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public void deleteDatasourcefromUIMObj(Collection<?> col) throws DataSourceOperationException;

	
	/**
	 * Update an existing DataSource
	 * 
	 * @param col a UIM Collection object
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public void updateDatasourcefromUIMObj(Collection<?> col) throws DataSourceOperationException;
	

	/**
	 * Retrieve a specific Record
	 * 
	 * @param recordString
	 * @return a RecordResult object
	 * @throws RecordOperationException
	 * @throws RepoxException
	 */
	public RecordResult retrieveRecord(String recordString) throws RecordOperationException;
	
	
	/**
	 * Starts a remote harvesting process  
	 * 
	 * @param type the type of harvesting to perform
	 * @param col the Collection to harvest
	 * @return the harvesting processId 
	 * @throws HarvestingOperationException
	 * @throws RepoxException
	 */
	public void initiateHarvestingfromUIMObj(Collection<?> col) throws HarvestingOperationException;
	
	
	/**
	 * Schedules a remote harvesting process at a specific Date (scheduling) 
	 * 
	 * @param type the type of harvesting to perform
	 * @param ds the DataSource to be used 
	 * @param ingestionDate the specific Date upon which the
	 * @return the harvesting processId 
	 * @throws HarvestingOperationException
	 * @throws RepoxException
	 */
	public void initiateHarvestingfromUIMObj(Collection<?> col,DateTime ingestionDate) throws HarvestingOperationException;
	
	
	
	/**
	 * Cancels the active execution of a harvesting session 
	 * 
	 * @param ds
	 * @throws HarvestingOperationException
	 */
	public void cancelHarvesting(Collection<?> col) throws HarvestingOperationException;
	
	
	/**
	 * Check the status of an existing harvesting job 
	 * @param ingestionProcessId the harvesting processId 
	 * @return the status
	 * @throws RepoxException
	 */
	public Success getHarvestingStatus(Collection<?> col) throws HarvestingOperationException;
	


	/**
	 * Gets a list of Datasources currently being harvested
	 * @return an object containing a reference to all DataSources
	 * @throws HarvestingOperationException
	 */
	public Set<Collection<?>> getActiveHarvestingSessions() throws HarvestingOperationException;
	
	
	/**
	 * Gets a list of UIM Collections scheduled for harvesting (ingestion)
	 * @return a List of UIM Collection object references
	 * @throws HarvestingOperationException
	 */
	public Set<Collection<?>> getScheduledHarvestingSessions() throws HarvestingOperationException;
	
	
	
	/**
	 * Gets the latest harvesting Log for a specific DataSource
	 * @param ds a UIM Collection object reference
	 * @return the HarvestLog
	 * @throws HarvestingOperationException
	 */
	public String getHarvestLog(Collection<?> col) throws HarvestingOperationException;

}
