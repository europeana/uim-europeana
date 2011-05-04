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
import eu.europeana.uim.repoxclient.jibxbindings.DataSources;
import eu.europeana.uim.repoxclient.jibxbindings.DataSource;
import eu.europeana.uim.repoxclient.jibxbindings.RecordResult;
import eu.europeana.uim.repoxclient.objects.HarvestingType;
import eu.europeana.uim.repoxclient.rest.exceptions.DataSourceOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.HarvestingOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RecordOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RepoxException;

/**
 * Interface declaration of the Repox plugin OSGI service 
 * 
 * @author Georgios Markakis
 */
public interface RepoxUIMService {

	
	/**
	 * Retrieve all available Repox DataSources
	 * 
	 * @return a DataSources object
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public DataSources retrieveDataSources() throws DataSourceOperationException,RepoxException;
	
	/**
	 * Create a Repox DataSource 
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public void createDatasource(DataSource ds) throws DataSourceOperationException,RepoxException;
	
	/**
	 * Delete a Repox DataSource
	 * 
	 * @param ds
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public void deleteDatasource(DataSource ds) throws DataSourceOperationException,RepoxException;

	/**
	 * Update an existing DataSource
	 * 
	 * @param ds a DataSource object
	 * @throws DataSourceOperationException
	 * @throws RepoxException
	 */
	public void updateDatasource(DataSource ds) throws DataSourceOperationException,RepoxException;
	

	/**
	 * Retrieve a specific Record
	 * 
	 * @param recordString
	 * @return a RecordResult object
	 * @throws RecordOperationException
	 * @throws RepoxException
	 */
	public RecordResult retrieveRecord(String recordString) throws RecordOperationException,RepoxException;
	
	/**
	 * Starts a remote harvesting process  
	 * 
	 * @param type the type of harvesting to perform
	 * @param ds the DataSource to be used 
	 * @return the harvesting processId 
	 * @throws HarvestingOperationException
	 * @throws RepoxException
	 */
	public String initiateHarvesting(HarvestingType type,DataSource ds) throws HarvestingOperationException,RepoxException;
	
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
	public String initiateHarvesting(HarvestingType type,DataSource ds,DateTime ingestionDate) throws HarvestingOperationException,RepoxException;
	
	/**
	 * Check the status of an existing harvesting job 
	 * @param ingestionProcessId the harvesting processId 
	 * @return the status
	 * @throws RepoxException
	 */
	public String harvestingStatus(String ingestionProcessId) throws RepoxException;
	
}
