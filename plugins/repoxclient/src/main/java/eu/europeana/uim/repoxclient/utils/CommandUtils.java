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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.InternalServerErrorException;

import pt.utl.ist.dataProvider.Aggregator;
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
    String homepage = assignValue("URL", argument3, out, in);

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
    String homepage = assignValue("URL", argument4, out, in);

    repoxservice.updateAggregator(id, newId, name, nameCode, homepage);
    return "Aggregator updated succesfully";
  }

  /**
   * Creates or Updates a Provider
   * 
   * @param action - "create" or "update"
   * @param repoxservice - The instance of the repox
   * @param registry - The instance of the registry
   * @param argument0 - Provider Name
   * @param argument1 - Provider Mnemonic
   * @param argument2 - Provider URL
   * @param argument3 - Provider Description
   * @param argument4 - Provider Country
   * @param argument5 - Provider Website
   * @param argument6 - Provider SugarCRM ID
   * @param argument7 - Provider Type
   * @param argument8 - Provider OAI-PMH Metadata Type prefix
   * @param out - Console Output
   * @param in - Console Input
   * @return Provider creation or update confirmation
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public static String executeCreateUpdateProviderAction(String action,
      RepoxUIMService repoxservice, Registry registry, String argument0, String argument1,
      String argument2, String argument3, String argument4, String argument5, String argument6,
      String argument7, PrintStream out, BufferedReader in) throws Exception {

    String providerName = assignValue("Provider Name", argument0, out, in);
    String providerMnemonic = assignValue(provMemonicVar, argument1, out, in);
    String providerUrl = assignValue("Provider URL", argument2, out, in);
    String providerDescription = assignValue("Provider Description", argument3, out, in);
    String providerCountryName = assignValue("Provider Country", argument4, out, in);
    String providerSugarId = assignValue("Provider SugarCRM ID", argument5, out, in);
    String providerType = assignValue("Provider Type", argument6, out, in);
    String providerOaiMetadataPrefix =
        assignValue("Provider OAI Metadata Prefix", argument7, out, in);

    try {

      StorageEngine<?> engine = registry.getStorageEngine();
      @SuppressWarnings("rawtypes")
      Provider prov = engine.createProvider();
      prov.setAggregator(false);
      prov.setMnemonic(providerMnemonic);
      prov.setName(providerName);
      prov.setOaiBaseUrl(providerUrl);

      prov.putValue(ControlledVocabularyProxy.PROVIDERDESCRIPTION, providerDescription);
      prov.putValue(ControlledVocabularyProxy.PROVIDERCOUNTRY, providerCountryName);
      prov.putValue(ControlledVocabularyProxy.PROVIDERWEBSITE, providerUrl);
      prov.putValue(ControlledVocabularyProxy.SUGARCRMID, providerSugarId);
      prov.putValue(ControlledVocabularyProxy.PROVIDERTYPE, providerType);
      prov.setOaiMetadataPrefix(providerOaiMetadataPrefix);
      engine.updateProvider(prov);
      engine.checkpoint();
      if (action.equals("create")) {
        repoxservice.createProviderfromUIMObj(prov);
      } else if (action.equals("update")) {
        repoxservice.updateProviderfromUIMObj(prov);
      } else {
        return "Unknown command " + action;
      }
      return "Provider " + action + "d successfully. ";

    } catch (ProviderOperationException e) {
      return "Error in " + action.substring(0, action.length() - 1) + "ing the provider. "
          + e.getMessage();
    }

  }

  /**
   * Deletes a provider
   * 
   * @param repoxservice - The instance of the Repox service
   * @param registry - The instance of the registry
   * @param argument0 - Provider Name
   * @param argument1 - Provider Mnemonic
   * @param out - Console Output
   * @param in - Console Input
   * @return Provider deletion confirmation
   * @throws IOException
   * @throws StorageEngineException
   */
  public static String deleteProvider(RepoxUIMService repoxservice, Registry registry,
      String argument0, String argument1, PrintStream out, BufferedReader in) throws IOException,
      StorageEngineException {
    String providerName = assignValue("Provider Name", argument0, out, in);
    String providerMnemonic = assignValue(provMemonicVar, argument1, out, in);

    try {

      StorageEngine<?> engine = registry.getStorageEngine();
      @SuppressWarnings("rawtypes")
      Provider provider = engine.createProvider();
      provider.setName(providerName);
      provider.setMnemonic(providerMnemonic);
      provider.putValue(repoxIDVar, providerName + "r0");
      repoxservice.deleteProviderfromUIMObj(provider);
      return "Provider deleted successfully.";
    } catch (ProviderOperationException e) {
      return "Error in deleting the provider. " + e.getMessage();
    }

  }


  /**
   * Creates/Updates a Datasource
   * 
   * @param action - The action to peerform "create" or "update"
   * @param repoxservice - The instance of the Repox service
   * @param registry - The instance of the registry
   * @param argument0 - Provider Name
   * @param argument1 - Provider Mnemonic
   * @param argument2 - Datasource Language
   * @param argument3 - Datasource Name
   * @param argument4 - Datasource Mnemonic
   * @param argument5 - Datasource OAI-PMH URI
   * @param argument6 - Datasource Metadata Prefix
   * @param out - Console Output
   * @param in - Console Input
   * @return Datasource creation confirmation
   * @throws IOException
   * @throws StorageEngineException
   */


  @SuppressWarnings("unchecked")
  public static String createUpdateDataSource(String action, RepoxUIMService repoxservice,
      Registry registry, String argument0, String argument1, String argument2, String argument3,
      String argument4, String argument5, String argument6, PrintStream out, BufferedReader in)
      throws IOException, StorageEngineException {


    String providerName = assignValue(provnameVar, argument0, out, in);
    String providerMnemonic = assignValue(provMemonicVar, argument1, out, in);
    String dsLanguage = assignValue("Datasource Language", argument2, out, in);
    String dsName = assignValue(dsnameVar, argument3, out, in);
    String dsMnemonic = assignValue(dsmnemonicVar, argument4, out, in);
    String dsOAIPMHURI = assignValue("Datasource OAI-PMH URI", argument5, out, in);
    String dsOAIMetadataPrefix = assignValue("Datasource Metadata Prefix", argument6, out, in);
    try {

      StorageEngine<?> engine = registry.getStorageEngine();

      @SuppressWarnings("rawtypes")
      Provider provider = engine.createProvider();
      provider.setName(providerName);
      provider.setMnemonic(providerMnemonic);
      provider.putValue(repoxIDVar, providerName + "r0");

      @SuppressWarnings("rawtypes")
      Collection collection = engine.createCollection(provider);

      collection.setLanguage(dsLanguage);
      collection.setMnemonic(dsMnemonic);
      collection.setName(dsName);
      collection.setOaiBaseUrl(dsOAIPMHURI);
      collection.setOaiMetadataPrefix(dsOAIMetadataPrefix);
      collection.putValue("collectionID", dsName + "r0");

      engine.updateCollection(collection);
      engine.checkpoint();
      if (action.equals("create")) {
        repoxservice.createDatasourcefromUIMObj(collection, provider);
      } else if (action.equals("update")) {
        repoxservice.updateDatasourcefromUIMObj(collection);
      } else {
        return "Unknown command " + action;
      }
      return "Datasource " + action + "d successfully";
    } catch (DataSourceOperationException e) {
      return "Error in " + action.substring(0, action.length() - 1) + "ing the Collection. "
          + e.getMessage();
    }
  }

  /**
   * Delete a Datasource
   * 
   * @param repoxservice - The instance of the Repox service
   * @param registry - The instance of the Registry
   * @param argument0 - Provider Name
   * @param argument1 - Provider Mnemonic
   * @param argument2 - Datasource Language
   * @param argument3 - Datasource Name
   * @param argument4 - Datasource Mnemonic
   * @param out - Console output
   * @param in - Console input
   * @return Datasource deletion confirmation
   * @throws IOException
   * @throws StorageEngineException
   */
  @SuppressWarnings("unchecked")
  public static String deleteDatasource(RepoxUIMService repoxservice, Registry registry,
      String argument0, String argument1, String argument2, String argument3, String argument4,
      PrintStream out, BufferedReader in) throws IOException, StorageEngineException {
    String providerName = assignValue(provnameVar, argument0, out, in);
    String providerMnemonic = assignValue(provMemonicVar, argument1, out, in);
    @SuppressWarnings("unused")
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

      @SuppressWarnings("rawtypes")
      Collection collection = engine.createCollection(provider);
      collection.setMnemonic(dsMnemonic);
      collection.setName(dsName);
      collection.putValue("collectionID", dsName + "r0");
      collection.putValue(repoxIDVar, dsName + dsMnemonic + "r0");
      engine.updateCollection(collection);
      engine.checkpoint();

      repoxservice.deleteDatasourcefromUIMObj(collection);
      return "Datasource deleted successfully";
    } catch (DataSourceOperationException e) {
      return "Error in creating the Collection. " + e.getMessage();
    }
  }

  /**
   * Retrieves all agreggators
   * 
   * @param repoxservice - Theservice to look the aggregators in
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

    String offset = assignValue("Id", argument0, out, in);
    String number = assignValue("New Id", argument1, out, in);

    List<Aggregator> aggregatorList =
        repoxservice.getAggregatorList(Integer.parseInt(offset), Integer.parseInt(number));
    StringBuffer sb = new StringBuffer();
    sb.append("Id\t\t\tName\t\t\tNameCode\t\t\tURL\n\n");

    for (Aggregator aggregator : aggregatorList)
      sb.append(aggregator.getId() + tripleTVar + aggregator.getName() + tripleTVar
          + aggregator.getNameCode() + tripleTVar + aggregator.getHomepage() + "\n");

    return sb.toString();
  }

  /**
   * Retrieve all providers
   * 
   * @param repoxservice - The instance of the service to look for providers
   * @param out - Console Output
   * @param in - Console Input
   * @return All Providers
   */
  public static String retrieveProviders(RepoxUIMService repoxservice, PrintStream out,
      BufferedReader in) {
    StringBuffer sb = new StringBuffer();
    try {
      Set<Provider<?>> providers = repoxservice.retrieveProviders();
      sb.append("ID\t\t\tName\t\t\tMnemonic\t\t\tBase URL\t\t\tOAI Prefix\t\t\tDescription\t\t\tCountry\t\t\tWebsite\t\t\tSugarCRM ID\t\t\tType\n\n");
      for (Provider<?> provider : providers) {
        sb.append(provider.getId() + tripleTVar + provider.getName() + tripleTVar
            + provider.getMnemonic() + tripleTVar + provider.getOaiBaseUrl() + tripleTVar
            + provider.getOaiMetadataPrefix() + tripleTVar
            + provider.getValue("providerDescription") + tripleTVar
            + provider.getValue("providerCountry") + tripleTVar
            + provider.getValue("providerWebsite") + tripleTVar + provider.getValue("sugarID")
            + tripleTVar + provider.getValue("providerType") + "\n");
      }
    } catch (Exception e) {
      sb.append("Error occurred in the retrieval of providers. ");
      sb.append(e.getMessage());
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
  public static String retrieveDatasources(RepoxUIMService repoxservice, PrintStream out,
      BufferedReader in) {
    StringBuffer sb = new StringBuffer();
    try {
      sb.append("Provider\tID        \tCollectionID\tName\tMnemonic\tLast Modified\tLast Synchronized\tOAI-PMH Base URL\tOAI-PMH Metadata Prefix");
      HashSet<Collection<?>> dsCol = (HashSet<Collection<?>>) repoxservice.retrieveDataSources();
      for (Collection<?> col : dsCol) {
        sb.append(col.getProvider() + "\t" + col.getId() + "\t" + col.getValue("collectionId")
            + "\t" + col.getLanguage() + "\t" + col.getName() + "\t" + col.getMnemonic() + "\t"
            + col.getLastModified() + "\t" + col.getLastSynchronized() + "\t"
            + col.getOaiBaseUrl(true) + "\t" + col.getOaiMetadataPrefix(true) + "\t"
            + col.getOaiSet() + "\n");

      }

    } catch (DataSourceOperationException e) {
      sb.append("Error occurred in the retrieval of Datasources.");
      sb.append(e.getMessage());
    }
    return sb.toString();
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
