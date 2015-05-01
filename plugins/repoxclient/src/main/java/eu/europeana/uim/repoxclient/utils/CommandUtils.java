/*
 * Copyright 2007-2012 The Europeana Foundation
 * 
 * Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved by the
 * European Commission; You may not use this work except in compliance with the Licence.
 * 
 * You may obtain a copy of the Licence at: http://joinup.ec.europa.eu/software/page/eupl
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the Licence
 * is distributed on an "AS IS" basis, without warranties or conditions of any kind, either express
 * or implied. See the Licence for the specific language governing permissions and limitations under
 * the Licence.
 */
package eu.europeana.uim.repoxclient.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.PrintStream;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.InternalServerErrorException;

import org.dom4j.DocumentException;

import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.DefaultDataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.FileExtractStrategy;
import pt.utl.ist.dataProvider.dataSource.FileRetrieveStrategy;
import pt.utl.ist.dataProvider.dataSource.IdExtractedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.IdProvidedRecordIdPolicy;
import pt.utl.ist.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.marc.CharacterEncoding;
import pt.utl.ist.marc.iso2709.shared.Iso2709Variant;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.util.ProviderType;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import eu.europeana.uim.Registry;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.repox.DataSourceOperationException;
import eu.europeana.uim.repox.HarvestingOperationException;
import eu.europeana.uim.repox.ProviderOperationException;
import eu.europeana.uim.repox.RepoxUIMService;
import eu.europeana.uim.repox.model.RepoxHarvestingStatus;
import eu.europeana.uim.repoxclient.rest.RepoxUIMServiceT;
import eu.europeana.uim.storage.StorageEngine;
import eu.europeana.uim.storage.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;

/**
 * Utility class containing Operations used by the command console
 * 
 * @author Yorgos Mamakis Email:yorgos.mamakis@kb.nl
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since Oct 2012
 */

public class CommandUtils {

  private final static String provMemonicVar = "Provider Mnemonic";
  private final static String repoxIDVar = "repoxID";
  private final static String provnameVar = "ProviderName";
  private final static String dsnameVar = "Datasource Name";
  private final static String dsmnemonicVar = "Datasource Mnemonic";
  private final static String tripleTVar = "\t\t\t";

  /**
   * Private constructor (Utility classes should not have a public or default constructor.)
   */
  private CommandUtils() {

  }

  /**
   * Retrieves the default URI of the Repox
   * 
   * @param repoxservice The instance to acquire repox data from
   * @return The connection status of the action
   */
  public static String retrieveRepoxConnectionStatus(RepoxUIMServiceT repoxservice) {
    try {
      return repoxservice.showConnectionStatus().getDefaultURI();
    } catch (Exception e) {
      return "Unknown error occurred. " + e.getMessage();
    }
  }

  /**
   * Creates a new Aggregator in the Repox
   * 
   * @param repoxservice The instance to create the aggregator in
   * @param argument0 Aggregator id
   * @param argument1 Aggregator Name
   * @param argument2 Aggregator Name Code
   * @param argument3 Aggregator URL
   * @param out Console output
   * @param in Console input
   * @return Aggregator creation confirmation
   * @throws IOException
   * @throws AlreadyExistsException
   * @throws MissingArgumentsException
   * @throws InvalidArgumentsException
   * @throws InternalServerErrorException
   * @throws Exception
   */
  public static String createAggregator(RepoxUIMServiceT repoxservice, String argument0,
      String argument1, String argument2, String argument3, PrintStream out, BufferedReader in)
      throws IOException, InternalServerErrorException, InvalidArgumentsException,
      MissingArgumentsException, AlreadyExistsException {


    String id = assignValue("Id", argument0, out, in);
    String name = assignValue("Name", argument1, out, in);
    String nameCode = assignValue("Name Code", argument2, out, in);
    String homepage = assignValue("Homepage", argument3, out, in);

    repoxservice.createAggregator(id, name, nameCode, homepage);
    return "Aggregator created successfully. \n";

  }

  /**
   * Deletes an Aggregator from the Repox
   * 
   * @param repoxservice The instance to delete the Aggregator from
   * @param argument0 Aggregator Id
   * @param out Console out
   * @param in Console input
   * @return Aggregator deletion confirmation
   * @throws DoesNotExistException
   * @throws InternalServerErrorException
   * @throws IOException
   * @throws Exception
   */
  public static String deleteAggregator(RepoxUIMServiceT repoxservice, String argument0,
      PrintStream out, BufferedReader in) throws InternalServerErrorException,
      DoesNotExistException, IOException {

    String id = assignValue("Id", argument0, out, in);
    repoxservice.deleteAggregator(id);
    return "Aggregator deleted successfully. \n";
  }

  /**
   * Updates a selected Aggregator
   * 
   * @param repoxservice - The instance to update an aggregator to
   * @param argument0 - Aggregator Id
   * @param argument1 - Aggregator New Id
   * @param argument2 - Aggregator Name
   * @param argument3 - Aggregator Name Code
   * @param argument4 - Aggregator URL
   * @param out - Console Output
   * @param in - Console Input
   * @return Aggregator update confirmation
   * @throws Exception
   */
  public static String updateAggregator(RepoxUIMServiceT repoxservice, String argument0,
      String argument1, String argument2, String argument3, String argument4, PrintStream out,
      BufferedReader in) throws Exception {

    String id = assignValue("Id", argument0, out, in);
    String newId = assignValue("New Id", argument1, out, in);
    String name = assignValue("Name", argument2, out, in);
    String nameCode = assignValue("Name Code", argument3, out, in);
    String homepage = assignValue("Homepage", argument4, out, in);

    repoxservice.updateAggregator(id, newId, name, nameCode, homepage);
    return "Aggregator updated succesfully";
  }

  /**
   * Creates a Provider
   * 
   * @param repoxservice - The instance of the repox
   * @param registry - The instance of the registry
   * @param argument0 - Aggregator Id
   * @param argument1 - Provider Id
   * @param argument2 - Provider Name
   * @param argument3 - Provider Country
   * @param argument4 - Provider description
   * @param argument5 - Provider Name Code
   * @param argument6 - Provider URL
   * @param argument7 - Provider Type
   * @param argument8 - Provider Email
   * @param out - Console Output
   * @param in - Console Input
   * @return Provider creation confirmation
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public static String executeCreateProvider(RepoxUIMServiceT repoxservice, Registry registry,
      String argument0, String argument1, String argument2, String argument3, String argument4,
      String argument5, String argument6, String argument7, String argument8, PrintStream out,
      BufferedReader in) {

    try {
      String aggregatorId = assignValue("Aggregator Id", argument0, out, in);
      String id = assignValue("Id", argument1, out, in);
      String name = assignValue("Name", argument2, out, in);
      String country = assignValue("Country", argument3, out, in);
      String description = assignValue("Description", argument4, out, in);
      String nameCode = assignValue("Name Code", argument5, out, in);
      String homepage = assignValue("Homepage", argument6, out, in);
      String providerType = assignValue("Provider Type", argument7, out, in);
      String email = assignValue("Email", argument8, out, in);

      StorageEngine<?> engine = registry.getStorageEngine();
      @SuppressWarnings("rawtypes")
      Provider prov = engine.createProvider();
      prov.setAggregator(false);
      prov.setMnemonic(id);
      prov.setName(name);
      prov.setOaiBaseUrl("");

      prov.putValue(ControlledVocabularyProxy.PROVIDERDESCRIPTION, description);
      prov.putValue(ControlledVocabularyProxy.PROVIDERCOUNTRY, country);
      prov.putValue(ControlledVocabularyProxy.PROVIDERWEBSITE, homepage);
      prov.putValue(ControlledVocabularyProxy.SUGARCRMID, id);
      prov.putValue(ControlledVocabularyProxy.PROVIDERTYPE, providerType);
      prov.setOaiMetadataPrefix("");
      engine.updateProvider(prov);
      engine.checkpoint();
      repoxservice.createProvider(aggregatorId, id, name, country, description, nameCode, homepage,
          ProviderType.get(providerType), email);
    } catch (InternalServerErrorException | InvalidArgumentsException | MissingArgumentsException
        | AlreadyExistsException | DoesNotExistException e) {
      return "Error in creating the provider in repox. " + e.getMessage();
    } catch (StorageEngineException e) {

      return "Error in creating the provider. " + e.getMessage();
    } catch (IOException e) {
      return "Error in creating the provider. " + e.getMessage();
    }
    return "Provider created succesfully";
  }

  /**
   * Updates a Provider
   * 
   * @param repoxservice - The instance of the repox
   * @param registry - The instance of the registry
   * @param argument0 - Provider Id
   * @param argument1 - Provider New Id
   * @param argument2 - Aggregator New Id
   * @param argument3 - Provider Name
   * @param argument4 - Provider Country
   * @param argument5 - Provider Description
   * @param argument6 - Provider Name Code
   * @param argument7 - Provider Homepage
   * @param argument8 - Provider Type
   * @param argument9 - Provider Email
   * @param out - Console Output
   * @param in - Console Input
   * @return Provider creation confirmation
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public static String executeUpdateProvider(RepoxUIMServiceT repoxservice, Registry registry,
      String argument0, String argument1, String argument2, String argument3, String argument4,
      String argument5, String argument6, String argument7, String argument8, String argument9,
      PrintStream out, BufferedReader in) {

    try {

      String id = assignValue("Id", argument0, out, in);
      String newId = assignValue("New Id", argument1, out, in);
      String newAggregatorId = assignValue("New Aggregator Id", argument2, out, in);
      String name = assignValue("Name", argument3, out, in);
      String country = assignValue("Country", argument4, out, in);
      String description = assignValue("Description", argument5, out, in);
      String nameCode = assignValue("Name Code", argument6, out, in);
      String homepage = assignValue("Homepage", argument7, out, in);
      String providerType = assignValue("Provider Type", argument8, out, in);
      String email = assignValue("Email", argument9, out, in);

      StorageEngine<?> engine = registry.getStorageEngine();
      @SuppressWarnings("rawtypes")
      Provider prov = engine.findProvider(id);
      prov.setAggregator(false);
      prov.setMnemonic((newId != null && !newId.equals("")) ? newId : id);
      prov.setName(name);
      prov.setOaiBaseUrl("");

      prov.putValue(ControlledVocabularyProxy.PROVIDERDESCRIPTION, description);
      prov.putValue(ControlledVocabularyProxy.PROVIDERCOUNTRY, country);
      prov.putValue(ControlledVocabularyProxy.PROVIDERWEBSITE, homepage);
      prov.putValue(ControlledVocabularyProxy.SUGARCRMID, id);
      prov.putValue(ControlledVocabularyProxy.PROVIDERTYPE, providerType);
      prov.setOaiMetadataPrefix("");
      engine.updateProvider(prov);
      engine.checkpoint();
      repoxservice.updateProvider(id, newId, newAggregatorId, name, country, description, nameCode,
          homepage, ProviderType.get(providerType), email);

    } catch (InternalServerErrorException | InvalidArgumentsException | MissingArgumentsException
        | AlreadyExistsException | DoesNotExistException e) {
      return "Error in updating the provider in repox. " + e.getMessage();
    } catch (StorageEngineException e) {
      return "Error in updating the provider. " + e.getMessage();
    } catch (IOException e) {
      return "Error in updating the provider. " + e.getMessage();
    }
    return "Provider updated succesfully";
  }

  /**
   * Deletes a provider.
   * 
   * @param repoxservice - The instance of the Repox service
   * @param registry - The instance of the registry
   * @param argument0 - Provider Id
   * @param out - Console Output
   * @param in - Console Input
   * @return Provider deletion confirmation
   * @throws IOException
   * @throws StorageEngineException
   */
  public static String deleteProvider(RepoxUIMServiceT repoxservice, Registry registry,
      String argument0, PrintStream out, BufferedReader in) throws IOException,
      StorageEngineException {
    String providerId = assignValue("Provider Id", argument0, out, in);

    try {
      StorageEngine<?> engine = registry.getStorageEngine();
      @SuppressWarnings("rawtypes")
      Provider provider = engine.findProvider(providerId);
      provider.setMnemonic(providerId);
      provider.putValue(repoxIDVar, providerId);
      engine.updateProvider(provider);
      engine.checkpoint();
      repoxservice.deleteProvider(providerId);
      return "Provider deleted successfully.";
    } catch (InternalServerErrorException | DoesNotExistException e) {
      return "Error in deleting the provider in repox. " + e.getMessage();
    }
  }

  /**
   * Creates a Datasource OAI.
   * 
   * @param repoxservice - The instance of the Repox service
   * @param registry - The instance of the registry
   * @param argument0 - Provider Id
   * @param argument1 - Datasource Id
   * @param argument2 - Name
   * @param argument3 - Name Code
   * @param argument4 - Is Sample
   * @param argument5 - Schema
   * @param argument6 - Description
   * @param argument7 - Namespace
   * @param argument8 - Metadata Format
   * @param argument9 - Marc Format
   * @param argument10 - OAI URL
   * @param argument11 - OAI Set
   * @param argument12 - Export Directory
   * @param out - Console Output
   * @param in - Console Input
   * @return Datasource creation confirmation
   * @throws IOException
   * @throws StorageEngineException
   */

  @SuppressWarnings("unchecked")
  public static String createDataSourceOai(RepoxUIMServiceT repoxservice, Registry registry,
      String argument0, String argument1, String argument2, String argument3, String argument4,
      String argument5, String argument6, String argument7, String argument8, String argument9,
      String argument10, String argument11, String argument12, PrintStream out, BufferedReader in) {

    try {

      String providerId = assignValue("Provider Id", argument0, out, in);
      String id = assignValue("Datasource Id", argument1, out, in);
      String name = assignValue("Name", argument2, out, in);
      String nameCode = assignValue("Name Code", argument3, out, in);
      String isSample = assignValue("Is Sample", argument4, out, in);
      String schema = assignValue("Schema", argument5, out, in);
      String description = assignValue("Description", argument6, out, in);
      String namespace = assignValue("Namespace", argument7, out, in);
      String metadataFormat = assignValue("Metadata Format", argument8, out, in);
      String marcFormat = assignValue("Marc Format", argument9, out, in);
      String oaiUrl = assignValue("OAI URL", argument10, out, in);
      String oaiSet = assignValue("OAI Set", argument11, out, in);
      String exportDir = assignValue("Export Directory", argument12, out, in);

      StorageEngine<?> engine = registry.getStorageEngine();

      @SuppressWarnings("rawtypes")
      Provider provider = engine.createProvider();
      // provider.setName(providerName);
      provider.setMnemonic(providerId);
      // provider.putValue(repoxIDVar, providerName + "r0");

      @SuppressWarnings("rawtypes")
      Collection collection = engine.createCollection(provider);

      // collection.setLanguage(dsLanguage);
      collection.setMnemonic(id);
      collection.setName(name);
      collection.setOaiBaseUrl(oaiUrl);
      collection.setOaiMetadataPrefix(metadataFormat);
      collection.putValue("collectionID", name + "r0");

      engine.updateCollection(collection);
      engine.checkpoint();
      RecordIdPolicy repoxRecordIdPolicy = new IdProvidedRecordIdPolicy(); // only for testing

      repoxservice.createDatasourceOai(providerId, id, name, nameCode,
          Boolean.parseBoolean(isSample), schema, description, namespace, metadataFormat,
          marcFormat, oaiUrl, oaiSet, exportDir, repoxRecordIdPolicy, null);
    } catch (InternalServerErrorException | InvalidArgumentsException | DoesNotExistException
        | MissingArgumentsException | AlreadyExistsException e) {
      return "Error in creating the datasource in repox. " + e.getMessage();
    } catch (StorageEngineException e) {
      return "Error in creating the datasource. " + e.getMessage();
    } catch (IOException e) {
      return "Error in creating the datasource. " + e.getMessage();
    }
    return "Datasource created succesfully";
  }


  /**
   * Creates a Update OAI.
   * 
   * @param repoxservice - The instance of the Repox service
   * @param registry - The instance of the registry
   * @param argument0 - Datasource Id
   * @param argument1 - Datasource New Id
   * @param argument2 - Name
   * @param argument3 - Name Code
   * @param argument4 - Is Sample
   * @param argument5 - Schema
   * @param argument6 - Description
   * @param argument7 - Namespace
   * @param argument8 - Metadata Format
   * @param argument9 - Marc Format
   * @param argument10 - OAI URL
   * @param argument11 - OAI Set
   * @param argument12 - Export Directory
   * @param out - Console Output
   * @param in - Console Input
   * @return Datasource update confirmation
   * @throws IOException
   * @throws StorageEngineException
   */
  @SuppressWarnings("unchecked")
  public static String updateDataSourceOai(RepoxUIMServiceT repoxservice, Registry registry,
      String argument0, String argument1, String argument2, String argument3, String argument4,
      String argument5, String argument6, String argument7, String argument8, String argument9,
      String argument10, String argument11, String argument12, PrintStream out, BufferedReader in) {

    try {

      String providerId = assignValue("Provider Id", argument0, out, in);
      String id = assignValue("Datasource Id", argument1, out, in);
      String name = assignValue("Name", argument2, out, in);
      String nameCode = assignValue("Name Code", argument3, out, in);
      String isSample = assignValue("Is Sample", argument4, out, in);
      String schema = assignValue("Schema", argument5, out, in);
      String description = assignValue("Description", argument6, out, in);
      String namespace = assignValue("Namespace", argument7, out, in);
      String metadataFormat = assignValue("Metadata Format", argument8, out, in);
      String marcFormat = assignValue("Marc Format", argument9, out, in);
      String oaiUrl = assignValue("OAI URL", argument10, out, in);
      String oaiSet = assignValue("OAI Set", argument11, out, in);
      String exportDir = assignValue("Export Directory", argument12, out, in);

      StorageEngine<?> engine = registry.getStorageEngine();

      @SuppressWarnings("rawtypes")
      Provider provider = engine.createProvider();
      // provider.setName(providerName);
      provider.setMnemonic(providerId);
      // provider.putValue(repoxIDVar, providerName + "r0");

      @SuppressWarnings("rawtypes")
      Collection collection = engine.createCollection(provider);

      // collection.setLanguage(dsLanguage);
      collection.setMnemonic(id);
      collection.setName(name);
      collection.setOaiBaseUrl(oaiUrl);
      collection.setOaiMetadataPrefix(metadataFormat);
      collection.putValue("collectionID", name + "r0");

      engine.updateCollection(collection);
      engine.checkpoint();
      RecordIdPolicy repoxRecordIdPolicy = new IdProvidedRecordIdPolicy(); // only for testing

      repoxservice.updateDatasourceOai(providerId, id, name, nameCode,
          Boolean.parseBoolean(isSample), schema, description, namespace, metadataFormat,
          marcFormat, oaiUrl, oaiSet, exportDir, repoxRecordIdPolicy, null);
    } catch (InternalServerErrorException | InvalidArgumentsException | DoesNotExistException
        | MissingArgumentsException | AlreadyExistsException e) {
      return "Error in updating the datasource in repox. " + e.getMessage();
    } catch (StorageEngineException e) {
      return "Error in updating the datasource. " + e.getMessage();
    } catch (IOException e) {
      return "Error in updating the datasource. " + e.getMessage();
    }
    return "Datasource updated succesfully";
  }

  /**
   * Delete a Datasource
   * 
   * @param repoxservice - The instance of the Repox service
   * @param registry - The instance of the Registry
   * @param argument0 - Provider Name
   * 
   * @param out - Console output
   * @param in - Console input
   * @return Datasource deletion confirmation
   * @throws IOException
   * @throws StorageEngineException
   */
  @SuppressWarnings("unchecked")
  public static String deleteDatasource(RepoxUIMServiceT repoxservice, Registry registry,
      String argument0, PrintStream out, BufferedReader in) throws IOException,
      StorageEngineException {
    String id = assignValue("Datasource Id", argument0, out, in);
    try {

      // StorageEngine<?> engine = registry.getStorageEngine();
      //
      // @SuppressWarnings("rawtypes")
      // Provider provider = engine.createProvider();
      // provider.setName(providerName);
      // provider.setMnemonic(providerMnemonic);
      // provider.putValue(repoxIDVar, providerName + "r0");
      //
      // @SuppressWarnings("rawtypes")
      // Collection collection = engine.createCollection(provider);
      // collection.setMnemonic(dsMnemonic);
      // collection.setName(dsName);
      // collection.putValue("collectionID", dsName + "r0");
      // collection.putValue(repoxIDVar, dsName + dsMnemonic + "r0");
      // engine.updateCollection(collection);
      // engine.checkpoint();

      repoxservice.deleteDataset(id);
      return "Datasource deleted successfully";
    } catch (DoesNotExistException e) {
      return "Error in deleting the datasource in repox. " + e.getMessage();
    }
  }

  /**
   * Retrieves all agreggators
   * 
   * @param repoxservice - The service to look the aggregators in
   * @param argument0 - Provider Offset
   * @param argument1 - Provider Number
   * @param out - Console output
   * @param in - Console input
   * @return The aggregators
   * @throws InvalidArgumentsException
   * @throws NumberFormatException
   * @throws IOException
   */
  public static String retrieveAggregators(RepoxUIMServiceT repoxservice, String argument0,
      String argument1, PrintStream out, BufferedReader in) throws NumberFormatException,
      InvalidArgumentsException, IOException {

    String offset = assignValue("Offset", argument0, out, in);
    String number = assignValue("Number", argument1, out, in);

    List<Aggregator> aggregatorList =
        repoxservice.getAggregatorList(Integer.parseInt(offset), Integer.parseInt(number));
    StringBuffer sb = new StringBuffer();

    sb.append(String.format("%-30s %-30s %-30s %-30s %n%n", "ID", "Name", "NameCode", "Homepage"));
    for (Aggregator aggregator : aggregatorList)
      sb.append(String.format("%-30s %-30s %-30s %-30s %n%n", aggregator.getId(),
          aggregator.getName(), aggregator.getNameCode(), aggregator.getHomepage()));

    return sb.toString();
  }

  /**
   * Retrieve all providers for aggregator.
   * 
   * @param repoxservice - The instance of the service to look for providers
   * @param argument0 - Aggregator Id
   * @param argument1 - Provider Offset
   * @param argument2 - Provider Number
   * @param out - Console Output
   * @param in - Console Input
   * @return All Providers
   * @throws IOException
   * @throws DoesNotExistException
   * @throws InvalidArgumentsException
   * @throws NumberFormatException
   */
  public static String retrieveProviders(RepoxUIMServiceT repoxservice, String argument0,
      String argument1, String argument2, PrintStream out, BufferedReader in) throws IOException,
      NumberFormatException, InvalidArgumentsException, DoesNotExistException {

    String aggregatorId = assignValue("Aggregator Id", argument0, out, in);
    String offset = assignValue("Offset", argument1, out, in);
    String number = assignValue("Number", argument2, out, in);

    List<DataProvider> providerList =
        repoxservice.getProviderList(aggregatorId, Integer.parseInt(offset),
            Integer.parseInt(number));
    StringBuffer sb = new StringBuffer();
    sb.append(String.format("%-30s %-30s %-30s %-30s %-30s %-30s %-30s %n%n", "ID", "Name",
        "NameCode", "Homepage", "Description", "Country", "Type"));
    for (DataProvider dataProvider : providerList) {
      sb.append(String.format("%-30s %-30s %-30s %-30s %-30s %-30s %-30s %n", dataProvider.getId(),
          dataProvider.getName(), dataProvider.getNameCode(), dataProvider.getHomepage(),
          dataProvider.getDescription(), dataProvider.getCountry(), dataProvider.getProviderType()
              .toString()));
    }
    return sb.toString();
  }

  /**
   * Retrieves all available datasources
   * 
   * @param repoxservice - The instance of the service to look for datasources in
   * @param out - Console output
   * @param in - Console input
   * @return The datasources
   */
  public static String retrieveDatasources(RepoxUIMServiceT repoxservice, String argument0,
      String argument1, String argument2, PrintStream out, BufferedReader in) {
    try {
      String providerId = assignValue("Provider Id", argument0, out, in);
      String offset = assignValue("Offset", argument1, out, in);
      String number = assignValue("Number", argument2, out, in);

      List<DataSourceContainer> datasetList =
          repoxservice.getDatasetList(providerId, Integer.parseInt(offset),
              Integer.parseInt(number));

      StringBuffer sb = new StringBuffer();
      sb.append(String.format("%-30s %-30s %-30s %-30s %-30s %-30s %-30s %n%n", "ID", "Name",
          "NameCode", "Description", "ExportDir", "Last Update", "Status"));

      for (DataSourceContainer dataSourceContainer : datasetList) {
        DefaultDataSourceContainer ddsc = (DefaultDataSourceContainer) dataSourceContainer;
        DataSource dataSource = ddsc.getDataSource();
        sb.append(String.format("%-30s %-30s %-30s %-30s %-30s %-30s %-30s %n", dataSource.getId(),
            ddsc.getName(), ddsc.getNameCode(), dataSource.getDescription(),
            dataSource.getExportDir(), dataSource.getLastUpdate(), dataSource.getStatus()));
      }
      return sb.toString();
    } catch (IOException e) {
      return "Error in reading the datasource. " + e.getMessage();
    } catch (NumberFormatException | InvalidArgumentsException | DoesNotExistException e) {
      return "Error in reading datasources in repox. " + e.getMessage();
    }
  }

  /**
   * Initiate Harvesting of a collection
   * 
   * @param repoxservice - The instance of the Service
   * @param registry - The instance of the registry
   * @param argument0 - Provider Name
   * @param argument1 - Provider Mnemonic
   * @param argument2 - Datasource Name
   * @param argument3 - Datasource Mnemonic
   * @param out - Console output
   * @param in - Console Input
   * @return Confirmation of the initiation of harvesting
   * @throws IOException
   * @throws StorageEngineException
   */
  public static String initiateHarvesting(RepoxUIMService repoxservice, Registry registry,
      String argument0, String argument1, String argument2, String argument3, PrintStream out,
      BufferedReader in) throws IOException, StorageEngineException {

    String providerName = assignValue(provnameVar, argument0, out, in);
    String providerMnemonic = assignValue(provMemonicVar, argument1, out, in);
    String dsName = assignValue(dsnameVar, argument2, out, in);
    String dsMnemonic = assignValue(dsmnemonicVar, argument3, out, in);
    try {
      StorageEngine<?> engine = registry.getStorageEngine();


      @SuppressWarnings("rawtypes")
      Provider provider = engine.createProvider();
      provider.setName(providerName);
      provider.setMnemonic(providerMnemonic);
      provider.putValue(repoxIDVar, providerName + "r0");

      @SuppressWarnings("unchecked")
      Collection<?> coll = engine.createCollection(provider);
      coll.setName(dsName);
      coll.setMnemonic(dsMnemonic);
      coll.putValue(repoxIDVar, dsName + dsMnemonic + "r0");

      repoxservice.initiateHarvestingfromUIMObj(coll, true);
      return "Harvesting has been initiated. ";
    } catch (HarvestingOperationException e) {
      return "Harvesting initiation failed. " + e.getMessage();
    }
  }

  /**
   * Gets the harvesting status
   * 
   * @param repoxservice - The instance of the service
   * @param registry - The instance of the registry
   * @param argument0 - Provider Name
   * @param argument1 - Provider Mnemonic
   * @param argument2 - Datasource Language
   * @param argument3 - Datasource Name
   * @param argument4 - Datasource Mnemonic
   * @param out - Console output
   * @param in - Console input
   * @return The harvesting status
   * @throws IOException
   * @throws StorageEngineException
   */
  public static String getHarvestingStatus(RepoxUIMService repoxservice, Registry registry,
      String argument0, String argument1, String argument2, String argument3, String argument4,
      PrintStream out, BufferedReader in) throws IOException, StorageEngineException {

    String providerName = assignValue(provnameVar, argument0, out, in);
    String providerMnemonic = assignValue(provMemonicVar, argument1, out, in);
    String dsLanguage = assignValue("Datasource Language", argument2, out, in);
    String dsName = assignValue(dsnameVar, argument3, out, in);
    String dsMnemonic = assignValue(dsmnemonicVar, argument4, out, in);
    try {
      StorageEngine<?> engine = registry.getStorageEngine();

      @SuppressWarnings("rawtypes")
      Provider provider = engine.createProvider();
      provider.setName(providerName);
      provider.setMnemonic(providerMnemonic);
      provider.putValue(repoxIDVar, providerName + "r0");

      Collection<?> coll = engine.createCollection(provider);
      coll.setName(dsName);
      coll.setMnemonic(dsMnemonic);
      coll.putValue(repoxIDVar, dsName + dsMnemonic + "r0");
      RepoxHarvestingStatus res = repoxservice.getHarvestingStatus(coll);
      return "Status \n" + res.getStatus();
    } catch (HarvestingOperationException e) {
      return "Error in getting harvest status for collection " + dsName + ". " + e.getMessage();
    }
  }

  /**
   * Gets Active Harvests
   * 
   * @param repoxservice - The instance of the harvests
   * @param out - Console output
   * @param in - Console Input
   * @return - The active Harvests
   */
  public static String getActiveHarvests(RepoxUIMService repoxservice, PrintStream out,
      BufferedReader in) {
    StringBuffer sb = new StringBuffer();
    try {
      sb.append("Provider\tID        \tCollectionID\tName\tMnemonic\tLast Modified\tLast Synchronized\tOAI-PMH Base URL\tOAI-PMH Metadata Prefix");
      HashSet<Collection<?>> collectionSet =
          (HashSet<Collection<?>>) repoxservice.getActiveHarvestingSessions();
      for (Collection<?> col : collectionSet) {
        sb.append(col.getProvider() + "\t" + col.getId() + "\t" + col.getValue("collectionId")
            + "\t" + col.getLanguage() + "\t" + col.getName() + "\t" + col.getMnemonic() + "\t"
            + col.getLastModified() + "\t" + col.getLastSynchronized() + "\t"
            + col.getOaiBaseUrl(true) + "\t" + col.getOaiMetadataPrefix(true) + "\t"
            + col.getOaiSet() + "\n");
      }

    } catch (HarvestingOperationException e) {
      sb.append("Error in getting active harvests. ");
      sb.append(e.getMessage());
    }
    return sb.toString();
  }

  /**
   * @param description - The type of data to enter
   * @param argument
   * @param out - Console output
   * @param in - Console Input
   * @return A string that represent input data
   * @throws IOException
   */
  private static String assignValue(String description, String argument, PrintStream out,
      BufferedReader in) throws IOException {
    String retval = null;
    if (argument != null) {
      return argument;
    } else {
      while (retval == null) {
        out.println("Please enter the " + description + ":");
        retval = in.readLine();
      }
      return retval;
    }
  }
}
