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

import org.joda.time.DateTime;


import eu.europeana.uim.repoxclient.jibxbindings.Aggregator;
import eu.europeana.uim.repoxclient.jibxbindings.Aggregators;
import eu.europeana.uim.repoxclient.jibxbindings.HarvestingStatus;
import eu.europeana.uim.repoxclient.jibxbindings.Log;
import eu.europeana.uim.repoxclient.jibxbindings.Provider;
import eu.europeana.uim.repoxclient.jibxbindings.DataProviders;
import eu.europeana.uim.repoxclient.jibxbindings.DataSources;
import eu.europeana.uim.repoxclient.jibxbindings.Source;



import eu.europeana.uim.repoxclient.jibxbindings.RunningTasks;
import eu.europeana.uim.repoxclient.jibxbindings.ScheduleTasks;
import eu.europeana.uim.repoxclient.jibxbindings.Success;

import eu.europeana.uim.repoxclient.jibxbindings.RecordResult;
import eu.europeana.uim.repoxclient.objects.IngestFrequency;
import eu.europeana.uim.repoxclient.rest.exceptions.AggregatorOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.DataSourceOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.HarvestingOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.ProviderOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RecordOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RepoxException;

/**
 * Interface declaration of the Repox REST client OSGI service 
 * 
 * @author Georgios Markakis
 * @author Yorgos Mamakis
 */
public interface RepoxRestClient {

	
	
	/**
	 * Gets the default URI for the client.
	 * 
	 * @return the http address where REPOX resides
	 */
	public String getDefaultURI();
	
	
	/**
	 * Creates an Aggregator in Repox
	 * 
	 * @param aggregator an Aggregator object 
	 * @throws AggregatorOperationException
	 */
	public Aggregator createAggregator(Aggregator aggregator) throws AggregatorOperationException;
	
	
	/**
	 * Deletes an existing Aggregator from Repox
	 * @param aggregator a reference to the Aggregator object
	 * @throws AggregatorOperationException
	 */
	public Success deleteAggregator(String aggregatorId)throws AggregatorOperationException;	
	
	
	/**
	 * Updates an existing Aggregator in Repox
	 * @param aggregator the Aggregator object to update
	 * @throws AggregatorOperationException
	 */
	public Aggregator updateAggregator(Aggregator aggregator)throws AggregatorOperationException;	
	
	
	/**
	 * Retrieves all the available Aggregators from Repox
	 * @return an object containing all available Aggregators 
	 * @throws AggregatorOperationException
	 */
	public Aggregators retrieveAggregators() throws AggregatorOperationException;	
	
	
	/**
	 * Creates a provider in Repox and assigns it to the specific Aggregator
	 * @param prov the Provider definition
	 * @param agr the Aggregator reference
	 * @throws ProviderOperationException
	 */
	public Provider createProvider(Provider prov,Aggregator agr) throws ProviderOperationException;
		
	
	/**
	 * Deletes a provider from Repox
	 * 
	 * @param prov the Provider reference
	 * @throws ProviderOperationException
	 */
	public Success deleteProvider(String provID) throws ProviderOperationException;
	
	
	/**
	 * Updates a provider within Repox
	 * 
	 * @param the Provider object to update
	 * @throws ProviderOperationException
	 */
	public Provider updateProvider(Provider prov) throws ProviderOperationException;
	
	
	/**
	 * Retrieves all available providers within Repox
	 * @return an object containing all provider references
	 * @throws ProviderOperationException
	 */
	public DataProviders retrieveProviders() throws ProviderOperationException;	
	
	/**
	 * Retrieve all available Repox DataSources
	 * 
	 * @return a DataSources object
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public DataSources retrieveDataSources() throws DataSourceOperationException;
	

	/**
	 * Creates a OAI-PMH Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	public Source createDatasourceOAI(Source ds,Provider prov) throws DataSourceOperationException;
	
	
	/**
	 * Creates a Z3950Timestamp Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	public Source createDatasourceZ3950Timestamp(Source ds,Provider prov) throws DataSourceOperationException;
	
	
	/**
	 * Creates a Z3950IdFile Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	public Source createDatasourceZ3950IdFile(Source ds,Provider prov) throws DataSourceOperationException;
	
	
	
	/**
	 * Creates a Z3950IdSequence Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	public Source createDatasourceZ3950IdSequence(Source ds,Provider prov) throws DataSourceOperationException;	
	
	
	/**
	 * Creates an FTP Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	public Source createDatasourceFtp(Source ds,Provider prov) throws DataSourceOperationException;		
	
	
	/**
	 * Creates an HTTP Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	public Source createDatasourceHttp(Source ds,Provider prov) throws DataSourceOperationException;		
	
	
	/**
	 * Creates a Folder Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	public Source createDatasourceFolder(Source ds,Provider prov) throws DataSourceOperationException;
	
	
	/**
	 * Update an existing OAI DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	public Source updateDatasourceOAI(Source ds) throws DataSourceOperationException;
	
	
	/**
	 * Update an existing Z3950Timestamp DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	public Source updateDatasourceZ3950Timestamp(Source ds) throws DataSourceOperationException;
	
	
	/**
	 * Update an existing Z3950IdFile DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	public Source updateDatasourceZ3950IdFile(Source ds) throws DataSourceOperationException;
	
	/**
	 * Update an existing Z3950IdSequence DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	public Source updateDatasourceZ3950IdSequence(Source ds) throws DataSourceOperationException;
	
	/**
	 * Update an existing Ftp DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	public Source updateDatasourceFtp(Source ds) throws DataSourceOperationException;
	
	/**
	 * Update an existing Http DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	public Source updateDatasourceHttp(Source ds) throws DataSourceOperationException;
	
	
	/**
	 * Update an existing Folder DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	public Source updateDatasourceFolder(Source ds) throws DataSourceOperationException;
	
	
	/**
	 * Delete a Repox DataSource
	 * 
	 * @param ds
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public Success deleteDatasource(String dsID) throws DataSourceOperationException;


	

	/**
	 * Retrieve a specific Record
	 * 
	 * @param recordString
	 * @return a RecordResult object
	 * @throws RecordOperationException
	 * @throws RepoxException
	 */
	public RecordResult retrieveRecord(String recordID) throws RecordOperationException;
	
	
	
	/**
	 * Retrieve a specific Record
	 * 
	 * @param recordID
	 * @param ds
	 * @param recordXML
	 * @return
	 * @throws RecordOperationException
	 */
	public Success saveRecord(String recordID,Source ds,String recordXML) throws RecordOperationException;
	
	
	/**
	 * Marks a Record as eligible for deletion
	 * 
	 * @param recordID
	 * @return
	 * @throws RecordOperationException
	 */
	public Success markRecordAsDeleted(String recordID) throws RecordOperationException;
	
	
	/**
	 * Erases a Record
	 * 
	 * @param recordID
	 * @return
	 * @throws RecordOperationException
	 */
	public Success eraseRecord(String recordID) throws RecordOperationException;
	
	
	/**
	 * Starts a remote harvesting process  
	 * 
	 * @param type the type of harvesting to perform
	 * @param ds the DataSource to be used 
	 * @return the harvesting processId 
	 * @throws HarvestingOperationException
	 * @throws RepoxException
	 */
	public Success initiateHarvesting(String dsID,boolean isfull) throws HarvestingOperationException;
	
	/**
	 * Starts a remote harvesting process at a specific Date (scheduling) 
	 * 
	 * @param type the type of harvesting to perform
	 * @param ds the DataSource to be used 
	 * @param ingestionDate the specific Date upon which the
	 * @return the harvesting processId 
	 * @throws HarvestingOperationException
	 * @throws RepoxException
	 */
	public Success scheduleHarvesting(String dsID,DateTime ingestionDate,IngestFrequency frequency, boolean isfull) throws HarvestingOperationException;
	
	
	
	/**
	 * @param ds
	 * @throws HarvestingOperationException
	 */
	public Success cancelHarvesting(String dsID) throws HarvestingOperationException;
	
	/**
	 * Check the status of an existing harvesting job 
	 * @param ingestionProcessId the harvesting processId 
	 * @return the status
	 * @throws RepoxException
	 */
	public HarvestingStatus getHarvestingStatus(String dsID) throws HarvestingOperationException;
	


	/**
	 * Gets a list of Datasources currently being harvested
	 * @return an object containing a reference to all DataSources
	 * @throws HarvestingOperationException
	 */
	public RunningTasks getActiveHarvestingSessions() throws HarvestingOperationException;
	
	
	/**
	 * Gets a list of Datasources scheduled for harvesting (ingestion)
	 * @return an object containing a reference to all DataSources
	 * @throws HarvestingOperationException
	 */
	public ScheduleTasks getScheduledHarvestingSessions(String dsID) throws HarvestingOperationException;
	
	
	
	/**
	 * Gets the latest harvesting Log for a specific DataSource
	 * @param ds the DataSource reference
	 * @return the HarvestLog
	 * @throws HarvestingOperationException
	 */
	public Log getHarvestLog(String dsID) throws HarvestingOperationException;
	
	
	
	/**
	 * Initializes the export of records
	 * 
	 * @param dsID the DataSource reference
	 * @param records no of records per file
	 */
	public Success initializeExport(String dsID,int records) throws HarvestingOperationException;
}
