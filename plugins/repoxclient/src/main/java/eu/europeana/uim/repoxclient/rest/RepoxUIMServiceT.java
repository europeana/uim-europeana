/*
 * Copyright 2007-2015 The Europeana Foundation
 * 
 * Licensed under the EUPL, Version 1.1 (the "License") and subsequent versions as approved by the
 * European Commission; You may not use this work except in compliance with the License.
 * 
 * You may obtain a copy of the License at: http://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" basis, without warranties or conditions of any kind, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package eu.europeana.uim.repoxclient.rest;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

import javax.ws.rs.InternalServerErrorException;

import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.FileExtractStrategy;
import pt.utl.ist.dataProvider.dataSource.FileRetrieveStrategy;
import pt.utl.ist.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.marc.CharacterEncoding;
import pt.utl.ist.marc.iso2709.shared.Iso2709Variant;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.task.ScheduledTask.Frequency;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.ProviderType;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import eu.europeana.uim.Registry;
import eu.europeana.uim.repox.model.RepoxConnectionStatus;
import eu.europeana.uim.repoxclient.utils.DSType;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;

/**
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 * @since Apr 29, 2015
 */
public interface RepoxUIMServiceT {

  /**
   * Get the connection status URL
   * 
   * @return RepoxConnectionStatus
   */
  RepoxConnectionStatus showConnectionStatus();

  /******************** Aggregator Calls ********************/
  /**
   * Create an aggregator.
   * 
   * @param id
   * @param name
   * @param nameCode
   * @param homepage
   * @throws InvalidArgumentsException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  void createAggregator(String id, String name, String nameCode, String homepage)
      throws InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      InternalServerErrorException;

  /**
   * Delete an aggregator by specifying the Id.
   * 
   * @param aggregatorId
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  void deleteAggregator(String aggregatorId) throws DoesNotExistException,
      InternalServerErrorException;

  /**
   * Check if aggregator exists.
   * 
   * @param aggregatorId
   * @return boolean value
   */
  boolean aggregatorExists(String aggregatorId);

  /**
   * Update an aggregator by specifying the Id. Aggregator newId can be null if there is no need to
   * change the id.
   * 
   * @param id
   * @param newId
   * @param name
   * @param nameCode
   * @param homepage
   * @throws InvalidArgumentsException
   * @throws MissingArgumentsException
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  void updateAggregator(String id, String newId, String name, String nameCode, String homepage)
      throws InvalidArgumentsException, MissingArgumentsException, DoesNotExistException,
      InternalServerErrorException;

  /**
   * Get a list of aggregators in the specified range. Returned number can be smaller than the
   * requested. Offset not allowed negative. If number is negative then it returns all the items
   * from offset until the total number of items.
   * 
   * @param offset
   * @param number
   * @return
   * @throws InvalidArgumentsException
   */
  List<Aggregator> getAggregatorList(int offset, int number) throws InvalidArgumentsException;

  /******************** Provider Calls ********************/

  /**
   * Check if provider exists.
   * 
   * @param id
   * @return boolean value
   */
  boolean providerExists(String id);

  /**
   * Get a list of provider in the specified range. Returned number can be smaller than the
   * requested. Offset not allowed negative. If number is negative then it returns all the items
   * from offset until the total number of items.
   * 
   * @param aggregatorId
   * @param offset
   * @param number
   * @return
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   */
  List<DataProvider> getProviderList(String aggregatorId, int offset, int number)
      throws InvalidArgumentsException, DoesNotExistException;

  /**
   * Create a provider.
   * 
   * @param aggregatorId
   * @param id
   * @param name
   * @param country
   * @param description
   * @param nameCode
   * @param homepage
   * @param providerType
   * @param email
   * @throws InvalidArgumentsException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   * @throws DoesNotExistException
   */
  void createProvider(Provider uimProv, String aggregatorId, String id, String name, String country, String countryCode,
      String description, String nameCode, String homepage, ProviderType providerType, String email)
      throws InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      InternalServerErrorException, DoesNotExistException;

  /**
   * Delete an provider by specifying the Id.
   * 
   * @param providerId
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  void deleteProvider(String providerId) throws DoesNotExistException, InternalServerErrorException;

  /**
   * Update a provider by specifying the Id. Provider newId can be null if there is no need to
   * change the id.
   * 
   * @param id
   * @param newId
   * @param newAggregatorId
   * @param name
   * @param country
   * @param description
   * @param nameCode
   * @param homepage
   * @param providerType
   * @param email
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   */
  void updateProvider(String id, String newId, String newAggregatorId, String name, String country, String countryCode,
      String description, String nameCode, String homepage, ProviderType providerType, String email)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException;

  /******************** Datasource Calls ********************/

  /**
   * Check if datasource exists.
   * 
   * @param id
   * @return boolean value
   */
  boolean datasourceExists(String id);

  /**
   * Get the number of records of the dataset.
   * 
   * @param id
   * @return
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  int getDatasetRecordCount(String id) throws DoesNotExistException, InternalServerErrorException;

  /**
   * Create a dataset oai.
   * 
   * @param providerId
   * @param id
   * @param name
   * @param nameCode
   * @param isSample
   * @param schema
   * @param description
   * @param namespace
   * @param metadataFormat
   * @param marcFormat
   * @param oaiUrl
   * @param oaiSet
   * @param exportDir
   * @param recordIdPolicy
   * @param metadataTransformations
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  void createDatasourceOai(Collection col, String providerId, String id, String name, String nameCode,
      boolean isSample, String schema, String description, String namespace, String metadataFormat,
      String marcFormat, String oaiUrl, String oaiSet, String exportDir,
      RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException;

  /**
   * Create a dataset directory, ftp, http.
   * 
   * @param providerId
   * @param id
   * @param name
   * @param nameCode
   * @param isSample
   * @param schema
   * @param description
   * @param namespace
   * @param metadataFormat
   * @param marcFormat
   * @param exportDir
   * @param recordIdPolicy
   * @param extractStrategy
   * @param retrieveStrategy
   * @param characterEncoding
   * @param isoVariant
   * @param sourceDirectory
   * @param recordXPath
   * @param metadataTransformations
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  void createDatasetFile(Collection col, String providerId, String id, String name, String nameCode,
      boolean isSample, String schema, String description, String namespace, String metadataFormat,
      String marcFormat, String exportDir, RecordIdPolicy recordIdPolicy,
      FileExtractStrategy extractStrategy, FileRetrieveStrategy retrieveStrategy,
      CharacterEncoding characterEncoding, Iso2709Variant isoVariant, String sourceDirectory,
      String recordXPath, Map<String, MetadataTransformation> metadataTransformations)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException;

  /**
   * Update a dataset by specifying the Id.
   * 
   * @param id
   * @param newId
   * @param name
   * @param nameCode
   * @param isSample
   * @param schema
   * @param description
   * @param namespace
   * @param metadataFormat
   * @param marcFormat
   * @param oaiUrl
   * @param oaiSet
   * @param exportDir
   * @param recordIdPolicy
   * @param metadataTransformations
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  void updateDatasourceOai(String id, String newId, String name, String nameCode, boolean isSample,
      String schema, String description, String namespace, String metadataFormat,
      String marcFormat, String oaiUrl, String oaiSet, String exportDir,
      RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException;
  
  /**
   * Update a dataset by specifying the Id.
   * 
   * @param id
   * @param newId
   * @param name
   * @param nameCode
   * @param isSample
   * @param schema
   * @param description
   * @param namespace
   * @param metadataFormat
   * @param marcFormat
   * @param exportDir
   * @param recordIdPolicy
   * @param extractStrategy
   * @param retrieveStrategy
   * @param characterEncoding
   * @param isoVariant
   * @param sourceDirectory
   * @param recordXPath
   * @param metadataTransformations
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   * @throws InternalServerErrorException
   */
  void updateDatasourceFile(String id, String newId, String name, String nameCode,
      boolean isSample, String schema, String description, String namespace, String metadataFormat,
      String marcFormat, String exportDir, RecordIdPolicy recordIdPolicy,
      FileExtractStrategy extractStrategy, FileRetrieveStrategy retrieveStrategy,
      CharacterEncoding characterEncoding, Iso2709Variant isoVariant, String sourceDirectory,
      String recordXPath, Map<String, MetadataTransformation> metadataTransformations)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException;

  /**
   * Delete an dataset by specifying the Id.
   * 
   * @param datasetId
   * @throws DoesNotExistException
   */
  void deleteDataset(String datasetId) throws DoesNotExistException;

  /**
   * 
   * Get a list of datasets in the specified range. Returned number can be smaller than the
   * requested. Offset not allowed negative. If number is negative then it returns all the items
   * from offset until the total number of items.
   * 
   * @param providerId
   * @param offset
   * @param number
   * @return
   * @throws InvalidArgumentsException
   * @throws DoesNotExistException
   */
  List<DataSourceContainer> getDatasetList(String providerId, int offset, int number)
      throws InvalidArgumentsException, DoesNotExistException;
  
  
  
  boolean hasHarvestingTypeChanged(String id, DSType type) throws DoesNotExistException, InvalidArgumentsException;
  


  /**
   * Initiates a new harvest of the dataset with id.
   * 
   * @param id
   * @param type
   * @throws AlreadyExistsException
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  void initiateHarvesting(String id, String type) throws AlreadyExistsException,
      DoesNotExistException, InternalServerErrorException;

  /**
   * Gets the status of a specific dataset harvesting.
   * 
   * @param id
   * @return
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  String getHarvestingStatus(String id) throws DoesNotExistException, InternalServerErrorException;

  /**
   * Gets a list of currently executing dataset harvests.
   * 
   * @return list with the running tasks
   */
  List<Task> getCurrentHarvestsList();

  /**
   * Gets the logs of the last ingest.
   * 
   * @param id
   * @return
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  String getDatasetLastIngestLog(String id) throws DoesNotExistException,
      InternalServerErrorException;

  /**
   * Cancels a harvesting ingest.
   * 
   * @param id
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   */
  void cancelHarvest(String id) throws DoesNotExistException, InternalServerErrorException;
  
  /**
   * Schedules an automatic harvesting.
   * 
   * @param id
   * @param firstDateTime
   * @param frequency
   * @param xmonths
   * @param incremental
   * @throws DoesNotExistException
   * @throws MissingArgumentsException
   * @throws AlreadyExistsException
   */
  void scheduleHarvest(String id, Calendar firstDateTime, Frequency frequency, int xmonths,
      boolean incremental) throws DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException;



  public void setRegistry(Registry registry);

  public Registry getRegistry();
}
