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
package eu.europeana.uim.repoxclient.plugin;

import org.joda.time.DateTime;
import eu.europeana.uim.repox.AggregatorOperationException;
import eu.europeana.uim.repox.DataSourceOperationException;
import eu.europeana.uim.repox.HarvestingOperationException;
import eu.europeana.uim.repox.ProviderOperationException;
import eu.europeana.uim.repox.RecordOperationException;
import eu.europeana.uim.repox.model.IngestFrequency;
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
	 String getDefaultURI();
	
	
	/**
	 * Creates an Aggregator in Repox
	 * 
	 * @param aggregator an Aggregator object 
	 * @throws AggregatorOperationException
	 */
	 Aggregator createAggregator(Aggregator aggregator) throws AggregatorOperationException;
	
	
	/**
	 * Deletes an existing Aggregator from Repox
	 * 
	 * @param aggregator a reference to the Aggregator object
	 * @throws AggregatorOperationException
	 */
	 Success deleteAggregator(String aggregatorId)throws AggregatorOperationException;	
	
	
	/**
	 * Updates an existing Aggregator in Repox
	 * 
	 * @param aggregator the Aggregator object to update
	 * @throws AggregatorOperationException
	 */
	 Aggregator updateAggregator(Aggregator aggregator)throws AggregatorOperationException;	
	
	
	/**
	 * Retrieves all the available Aggregators from Repox
	 * 
	 * @return an object containing all available Aggregators 
	 * @throws AggregatorOperationException
	 */
	 Aggregators retrieveAggregators() throws AggregatorOperationException;	
	
	 
	/**
	 * Retrieves an Aggregator given its id
	 * 
	 * @param aggregatorID
	 * @return
	 * @throws AggregatorOperationException
	 */
	Aggregator retrieveAggregator(String aggregatorID)throws AggregatorOperationException;	
	 
	
	/**
	 * Creates a provider in Repox and assigns it to the specific Aggregator
	 * 
	 * @param prov the Provider definition
	 * @param agr the Aggregator reference
	 * @throws ProviderOperationException
	 */
	 Provider createProvider(Provider prov,Aggregator agr) throws ProviderOperationException;
		
	
	/**
	 * Deletes a provider from Repox
	 * 
	 * @param prov the Provider reference
	 * @throws ProviderOperationException
	 */
	 Success deleteProvider(String provID) throws ProviderOperationException;
	
	
	/**
	 * Updates a provider within Repox
	 * 
	 * @param the Provider object to update
	 * @throws ProviderOperationException
	 */
	 Provider updateProvider(Provider prov) throws ProviderOperationException;
	
	
	/**
	 * Retrieves all available providers within Repox
	 * 
	 * @return an object containing all provider references
	 * @throws ProviderOperationException
	 */
	 DataProviders retrieveProviders() throws ProviderOperationException;	
	
	 
	 /**
	  * Retrieves a specific provider given its ID
	  * 
	 * @param providerID
	 * @return
	 * @throws ProviderOperationException
	 */
	Provider retrieveProvider(String providerID) throws ProviderOperationException;
	 
	/**
	 * Retrieve all available Repox DataSources
	 * 
	 * @return a DataSources object
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	 DataSources retrieveDataSources() throws DataSourceOperationException;
	

    /**
	 * Retrieves a specific datasource based on its ID
	 * 
	 * @param dsID
	 * @return
	 * @throws DataSourceOperationException
	 */
	Source retrieveDataSource(String dsID) throws DataSourceOperationException;
	 
	/**
	 * Creates a OAI-PMH Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	 Source createDatasourceOAI(Source ds,Provider prov) throws DataSourceOperationException;
	
	
	/**
	 * Creates a Z3950Timestamp Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	 Source createDatasourceZ3950Timestamp(Source ds,Provider prov) throws DataSourceOperationException;
	
	
	/**
	 * Creates a Z3950IdFile Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	 Source createDatasourceZ3950IdFile(Source ds,Provider prov) throws DataSourceOperationException;
	
	
	
	/**
	 * Creates a Z3950IdSequence Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	 Source createDatasourceZ3950IdSequence(Source ds,Provider prov) throws DataSourceOperationException;	
	
	
	/**
	 * Creates an FTP Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	 Source createDatasourceFtp(Source ds,Provider prov) throws DataSourceOperationException;		
	
	
	/**
	 * Creates an HTTP Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	 Source createDatasourceHttp(Source ds,Provider prov) throws DataSourceOperationException;		
	
	
	/**
	 * Creates a Folder Datasource
	 * 
	 * @param ds
	 * @param prov
	 * @return
	 * @throws DataSourceOperationException
	 */
	 Source createDatasourceFolder(Source ds,Provider prov) throws DataSourceOperationException;
	
	
	/**
	 * Update an existing OAI DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	 Source updateDatasourceOAI(Source ds) throws DataSourceOperationException;
	
	
	/**
	 * Update an existing Z3950Timestamp DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	 Source updateDatasourceZ3950Timestamp(Source ds) throws DataSourceOperationException;
	
	
	/**
	 * Update an existing Z3950IdFile DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	 Source updateDatasourceZ3950IdFile(Source ds) throws DataSourceOperationException;
	
	/**
	 * Update an existing Z3950IdSequence DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	 Source updateDatasourceZ3950IdSequence(Source ds) throws DataSourceOperationException;
	
	/**
	 * Update an existing Ftp DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	 Source updateDatasourceFtp(Source ds) throws DataSourceOperationException;
	
	/**
	 * Update an existing Http DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	 Source updateDatasourceHttp(Source ds) throws DataSourceOperationException;
	
	
	/**
	 * Update an existing Folder DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 */
	 Source updateDatasourceFolder(Source ds) throws DataSourceOperationException;
	
	
	/**
	 * Delete a Repox DataSource
	 * 
	 * @param ds
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	 Success deleteDatasource(String dsID) throws DataSourceOperationException;


	

	/**
	 * Retrieve a specific Record
	 * 
	 * @param recordString
	 * @return a RecordResult object
	 * @throws RecordOperationException
	 * @throws RepoxException
	 */
	 RecordResult retrieveRecord(String recordID) throws RecordOperationException;
	
	
	
	/**
	 * Retrieve a specific Record
	 * 
	 * @param recordID
	 * @param ds
	 * @param recordXML
	 * @return
	 * @throws RecordOperationException
	 */
	 Success saveRecord(String recordID,Source ds,String recordXML) throws RecordOperationException;
	
	
	/**
	 * Marks a Record as eligible for deletion
	 * 
	 * @param recordID
	 * @return
	 * @throws RecordOperationException
	 */
	 Success markRecordAsDeleted(String recordID) throws RecordOperationException;
	
	
	/**
	 * Erases a Record
	 * 
	 * @param recordID
	 * @return
	 * @throws RecordOperationException
	 */
	 Success eraseRecord(String recordID) throws RecordOperationException;
	
	
	/**
	 * Starts a remote harvesting process  
	 * 
	 * @param type the type of harvesting to perform
	 * @param ds the DataSource to be used 
	 * @return the harvesting processId 
	 * @throws HarvestingOperationException
	 * @throws RepoxException
	 */
	 Success initiateHarvesting(String dsID,boolean isfull) throws HarvestingOperationException;
	
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
	 Success scheduleHarvesting(String dsID,DateTime ingestionDate,IngestFrequency frequency, boolean isfull) throws HarvestingOperationException;
	
	
	
	/**
	 * Cancels a harvesting session
	 * 
	 * @param ds
	 * @throws HarvestingOperationException
	 */
	 Success cancelHarvesting(String dsID) throws HarvestingOperationException;
	
	/**
	 * Check the status of an existing harvesting job 
	 * @param ingestionProcessId the harvesting processId 
	 * @return the status
	 * @throws RepoxException
	 */
	 HarvestingStatus getHarvestingStatus(String dsID) throws HarvestingOperationException;
	


	/**
	 * Gets a list of Datasources currently being harvested
	 * @return an object containing a reference to all DataSources
	 * @throws HarvestingOperationException
	 */
	 RunningTasks getActiveHarvestingSessions() throws HarvestingOperationException;
	
	
	/**
	 * Gets a list of Datasources scheduled for harvesting (ingestion)
	 * @return an object containing a reference to all DataSources
	 * @throws HarvestingOperationException
	 */
	 ScheduleTasks getScheduledHarvestingSessions(String dsID) throws HarvestingOperationException;
	
	
	
	/**
	 * Gets the latest harvesting Log for a specific DataSource
	 * @param ds the DataSource reference
	 * @return the HarvestLog
	 * @throws HarvestingOperationException
	 */
	 Log getHarvestLog(String dsID) throws HarvestingOperationException;
	
	
	
	/**
	 * Initializes the export of records
	 * 
	 * @param dsID the DataSource reference
	 * @param records no of records per file
	 */
	 Success initializeExport(String dsID,int records) throws HarvestingOperationException;
}
