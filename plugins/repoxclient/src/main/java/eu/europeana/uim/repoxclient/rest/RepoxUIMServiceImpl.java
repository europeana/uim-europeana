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
package eu.europeana.uim.repoxclient.rest;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import javax.ws.rs.InternalServerErrorException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import eu.europeana.repox.rest.client.accessors.AggregatorsAccessor;
import eu.europeana.repox.rest.client.accessors.ProvidersAccessor;
import eu.europeana.uim.Registry;
import eu.europeana.uim.repox.model.RepoxConnectionStatus;
import eu.europeana.uim.repoxclient.utils.PropertyReader;
import eu.europeana.uim.repoxclient.utils.UimConfigurationProperty;

/**
 * This Class implements the functionality exposed by the OSGI service.
 * 
 * @author Georgios Markakis
 * @author Simon Tzanakis (Simon.Tzanakis@theeuropeanlibrary.org)
 */
public class RepoxUIMServiceImpl implements RepoxUIMServiceT {
  private static final Logger LOGGER = LoggerFactory.getLogger(RepoxUIMServiceImpl.class);

  private Registry registry;

  private static AggregatorsAccessor as;
  private static ProvidersAccessor ps;
  private static String defaultURI;

  public RepoxUIMServiceImpl() {
    defaultURI = PropertyReader.getProperty(UimConfigurationProperty.REPOX_HOST);

    try {
      as = new AggregatorsAccessor(new URL(defaultURI), "temporary", "temporary");
    } catch (MalformedURLException e) {
      LOGGER.error("AggregatorAccessor has a malformed URL {}", defaultURI);
      e.printStackTrace();
    }
    
    try {
      ps = new ProvidersAccessor(new URL(defaultURI), "temporary", "temporary");
    } catch (MalformedURLException e) {
      LOGGER.error("ProvidersAccessor has a malformed URL {}", defaultURI);
      e.printStackTrace();
    }
    
  }
  
  @Override
  public RepoxConnectionStatus showConnectionStatus() {
    RepoxConnectionStatus status = new RepoxConnectionStatus();
    status.setDefaultURI(defaultURI);
    return status;
  }

  /******************** Aggregator Calls ********************/
  @Override
  public void createAggregator(String id, String name, String nameCode, String homepage)
      throws InvalidArgumentsException, MissingArgumentsException, AlreadyExistsException,
      InternalServerErrorException {
    as.createAggregator(id, name, nameCode, homepage);
  }

  @Override
  public void deleteAggregator(String aggregatorId) throws DoesNotExistException,
      InternalServerErrorException {
    as.deleteAggregator(aggregatorId);
  }

  @Override
  public boolean aggregatorExists(String aggregatorId) {
    try {
      as.getAggregator(aggregatorId);
    } catch (DoesNotExistException e) {
      return false;
    }
    return true;
  }

  @Override
  public void updateAggregator(String id, String newId, String name, String nameCode,
      String homepage) throws InvalidArgumentsException, MissingArgumentsException,
      DoesNotExistException, InternalServerErrorException {
    as.updateAggregator(id, newId, name, nameCode, homepage);
  }

  @Override
  public List<Aggregator> getAggregatorList(int offset, int number)
      throws InvalidArgumentsException {
    return as.getAggregatorList(offset, number);
  }

  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see eu.europeana.uim.repox.RepoxUIMService#providerExists(eu.europeana.uim.store.Provider)
  // */
  // @Override
  // public boolean providerExists(Provider<?> provider) throws ProviderOperationException {
  //
  // // String provId = provider.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (provId == null) {
  // // return false;
  // // }
  // //
  // //
  // // eu.europeana.uim.repoxclient.jibxbindings.Provider prov =
  // // repoxRestClient.retrieveProvider(provId);
  // //
  // // boolean exists = prov != null ? true : false;
  // //
  // // return exists;
  // return false;
  //
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // *
  // eu.europeana.uim.repox.RepoxUIMService#createProviderfromUIMObj(eu.europeana.uim.store.Provider
  // * )
  // */
  // @Override
  // public void createProviderfromUIMObj(Provider uimProv) throws ProviderOperationException {
  //
  // // if (uimProv.isAggregator()) {
  // // throw new ProviderOperationException("The requested object is not a Provider");
  // // }
  // //
  // //
  // // eu.europeana.uim.repoxclient.jibxbindings.Provider jibxProv =
  // // JibxObjectProvider.createProvider(uimProv);
  // //
  // // Aggregator aggr = new Aggregator();
  // //
  // // if (jibxProv.getCountry().getCountry() == null) {
  // // aggr.setId("euaggregatorr0");
  // // } else {
  // // aggr.setId(jibxProv.getCountry().getCountry() + defaultAggrgatorIDPostfix);
  // // }
  // //
  // // eu.europeana.uim.repoxclient.jibxbindings.Provider createdProv =
  // // repoxRestClient.createProvider(jibxProv, aggr);
  // //
  // //
  // // uimProv.putValue(ControlledVocabularyProxy.REPOXID, createdProv.getId());
  // //
  // // StorageEngine<?> engine = registry.getStorageEngine();
  // //
  // // // Store the created RepoxID into the UIM object
  // // try {
  // // engine.updateProvider(uimProv);
  // // engine.checkpoint();
  // // } catch (StorageEngineException e) {
  // // throw new ProviderOperationException("Updating UIM Provider object failed");
  // // }
  //
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // *
  // eu.europeana.uim.repox.RepoxUIMService#deleteProviderfromUIMObj(eu.europeana.uim.store.Provider
  // * )
  // */
  // @Override
  // public void deleteProviderfromUIMObj(Provider<?> prov) throws ProviderOperationException {
  //
  // // String id = prov.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (id == null) {
  // // throw new ProviderOperationException("Missing repoxID element from Provider object");
  // // }
  // //
  // //
  // // repoxRestClient.deleteProvider(id);
  //
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // *
  // eu.europeana.uim.repox.RepoxUIMService#updateProviderfromUIMObj(eu.europeana.uim.store.Provider
  // * )
  // */
  // @Override
  // public void updateProviderfromUIMObj(Provider<?> uimProv) throws ProviderOperationException {
  //
  // // if (uimProv.isAggregator()) {
  // // throw new ProviderOperationException("The requested object is not a Provider");
  // // }
  // //
  // // String id = uimProv.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (id == null) {
  // // throw new ProviderOperationException("Missing repoxID element from Provider object");
  // // }
  // //
  // // eu.europeana.uim.repoxclient.jibxbindings.Provider jibxProv =
  // // JibxObjectProvider.createProvider(uimProv);
  // //
  // // jibxProv.setId(id);
  // //
  // // repoxRestClient.updateProvider(jibxProv);
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see eu.europeana.uim.repox.RepoxUIMService#retrieveProviders()
  // */
  // @Override
  // public Set<Provider<?>> retrieveProviders() throws ProviderOperationException {
  // // StorageEngine<?> engine = registry.getStorageEngine();
  // //
  // // HashSet<Provider<?>> uimProviders = new HashSet<Provider<?>>();
  // //
  // // DataProviders provs = repoxRestClient.retrieveProviders();
  // //
  // // ArrayList<eu.europeana.uim.repoxclient.jibxbindings.Provider> provList =
  // // (ArrayList<eu.europeana.uim.repoxclient.jibxbindings.Provider>) provs.getProviderList();
  // //
  // // for (eu.europeana.uim.repoxclient.jibxbindings.Provider prov : provList) {
  // //
  // // if (prov.getNameCode() != null) {
  // // String id = prov.getNameCode().getNameCode();
  // //
  // // try {
  // // Provider<?> uimprov = engine.findProvider(id);
  // // if (uimprov != null) {
  // // uimProviders.add(uimprov);
  // // }
  // // } catch (StorageEngineException e) {
  // // // TODO Decide what to do here
  // // }
  // // }
  // //
  // // }
  // //
  // // return uimProviders;
  // return null;
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // eu.europeana.uim.repox.RepoxUIMService#datasourceExists(eu.europeana.uim.store.Collection)
  // */
  // @Override
  // public boolean datasourceExists(Collection<?> col) throws DataSourceOperationException {
  //
  // // String colid = col.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (colid == null) {
  // // return false;
  // // }
  // //
  // // Source src = repoxRestClient.retrieveDataSource(colid);
  // //
  // // boolean exists = src != null ? true : false;
  // //
  // // return exists;
  // return false;
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // *
  // eu.europeana.uim.repox.RepoxUIMService#createDatasourcefromUIMObj(eu.europeana.uim.store.Collection
  // * , eu.europeana.uim.store.Provider)
  // */
  // @Override
  // public void createDatasourcefromUIMObj(Collection col, Provider prov)
  // throws DataSourceOperationException {
  //
  //
  // // String htypeString = col.getValue(ControlledVocabularyProxy.HARVESTING_TYPE);
  // //
  // // if (htypeString == null) {
  // // throw new DataSourceOperationException("Error during the creation of a Datasource: "
  // // + htypeInfo);
  // // }
  // //
  // // DSType harvestingtype = DSType.valueOf(htypeString);
  // //
  // // if (harvestingtype == null) {
  // // throw new DataSourceOperationException(
  // // "Error during the creation of a Datasource: "
  // // +
  // // "HARVESTING_TYPE for the specific object does not match the predefined acceptable values.");
  // // }
  // //
  // //
  // // Source ds = JibxObjectProvider.createDataSource(col, harvestingtype);
  // // eu.europeana.uim.repoxclient.jibxbindings.Provider jibxProv =
  // // new eu.europeana.uim.repoxclient.jibxbindings.Provider();
  // // jibxProv.setId(col.getProvider().getValue(ControlledVocabularyProxy.REPOXID));
  // //
  // //
  // // Source retsource = null;
  // //
  // // switch (harvestingtype) {
  // // case oai_pmh:
  // // retsource = repoxRestClient.createDatasourceOAI(ds, jibxProv);
  // // break;
  // //
  // // case z39_50:
  // // Z3950Methods z3950method =
  // // Z3950Methods.valueOf(col.getValue(ControlledVocabularyProxy.Z3950METHOD));
  // // switch (z3950method) {
  // // case timestamp:
  // // retsource = repoxRestClient.createDatasourceZ3950Timestamp(ds, jibxProv);
  // // break;
  // // case filepath:
  // // retsource = repoxRestClient.createDatasourceZ3950IdFile(ds, jibxProv);
  // // break;
  // // case maximumid:
  // // retsource = repoxRestClient.createDatasourceZ3950IdSequence(ds, jibxProv);
  // // break;
  // // }
  // // break;
  // //
  // // case ftp:
  // // retsource = repoxRestClient.createDatasourceFtp(ds, jibxProv);
  // // break;
  // //
  // // case http:
  // // retsource = repoxRestClient.createDatasourceHttp(ds, jibxProv);
  // // break;
  // //
  // // case folder:
  // // retsource = repoxRestClient.createDatasourceFolder(ds, jibxProv);
  // // break;
  // // default:
  // // throw new DataSourceOperationException(
  // // "Error during the creation of a Datasource: "
  // // +
  // // "HARVESTING_TYPE for the specific object does not match the predefined acceptable values.");
  // // }
  // //
  // //
  // // col.putValue(ControlledVocabularyProxy.REPOXID, retsource.getId());
  // //
  // // StorageEngine<?> engine = registry.getStorageEngine();
  // //
  // // // Store the created RepoxID into the UIM object
  // // try {
  // // engine.updateCollection(col);
  // // engine.checkpoint();
  // // } catch (StorageEngineException e) {
  // // throw new DataSourceOperationException("Updating UIM Collection object failed");
  // // }
  //
  //
  //
  // }
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // *
  // eu.europeana.uim.repox.RepoxUIMService#updateDatasourcefromUIMObj(eu.europeana.uim.store.Collection
  // * )
  // */
  // @Override
  // public void updateDatasourcefromUIMObj(Collection col) throws DataSourceOperationException {
  //
  // // Create Id from Collection name and mnemonic
  // // String htypeString = col.getValue(ControlledVocabularyProxy.HARVESTING_TYPE);
  // //
  // // if (htypeString == null) {
  // // throw new DataSourceOperationException("Error during the creation of a Datasource: "
  // // + htypeInfo);
  // // }
  // //
  // // DSType harvestingtype = DSType.valueOf(htypeString);
  // //
  // // if (harvestingtype == null) {
  // // throw new DataSourceOperationException(
  // // "Error during the creation of a Datasource: "
  // // +
  // // "HARVESTING_TYPE for the specific object does not match the predefined acceptable values.");
  // // }
  // //
  // // String id = col.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (id == null) {
  // // throw new DataSourceOperationException("Missing repoxID element from Collection object");
  // // }
  // //
  // // Source ds = JibxObjectProvider.createDataSource(col, harvestingtype);
  // // ds.setId(col.getValue(ControlledVocabularyProxy.REPOXID));
  // //
  // //
  // // switch (harvestingtype) {
  // // case oai_pmh:
  // // repoxRestClient.updateDatasourceOAI(ds);
  // // break;
  // //
  // // case z39_50:
  // // Z3950Methods z3950method =
  // // Z3950Methods.valueOf(col.getValue(ControlledVocabularyProxy.Z3950METHOD));
  // // switch (z3950method) {
  // // case timestamp:
  // // repoxRestClient.updateDatasourceZ3950Timestamp(ds);
  // // break;
  // // case filepath:
  // // repoxRestClient.updateDatasourceZ3950IdFile(ds);
  // // break;
  // // case maximumid:
  // // repoxRestClient.updateDatasourceZ3950IdSequence(ds);
  // // break;
  // // default:
  // // throw new DataSourceOperationException(
  // // "Z3950 Method Value used for the creation of a datasource was invalid.");
  // // }
  // // break;
  // //
  // // case ftp:
  // // repoxRestClient.updateDatasourceFtp(ds);
  // // break;
  // //
  // // case http:
  // // repoxRestClient.updateDatasourceHttp(ds);
  // // break;
  // //
  // // case folder:
  // // repoxRestClient.updateDatasourceFolder(ds);
  // // break;
  // //
  // // default:
  // // throw new DataSourceOperationException(
  // // "Harvesting Type Value used for the creation of a datasource was invalid.");
  // // }
  //
  //
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // *
  // eu.europeana.uim.repox.RepoxUIMService#deleteDatasourcefromUIMObj(eu.europeana.uim.store.Collection
  // * )
  // */
  // @Override
  // public void deleteDatasourcefromUIMObj(Collection<?> col) throws DataSourceOperationException {
  // //
  // // String id = col.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (id == null) {
  // // throw new DataSourceOperationException("Missing repoxID element from Collection object");
  // // }
  // // repoxRestClient.deleteDatasource(id);
  // }
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // *
  // eu.europeana.uim.repox.RepoxUIMService#hasHarvestingTypeChanged(eu.europeana.uim.store.Collection
  // * )
  // */
  // @Override
  // public boolean hasHarvestingTypeChanged(Collection<?> col) throws DataSourceOperationException
  // {
  //
  // // StorageEngine<?> engine = registry.getStorageEngine();
  // //
  // // String stringtype = col.getValue(ControlledVocabularyProxy.HARVESTING_TYPE);
  // //
  // // DSType harvestingtype = DSType.valueOf(stringtype);
  // //
  // // DataSources datasources = repoxRestClient.retrieveDataSources();
  // // ArrayList<Source> sourceList = (ArrayList<Source>) datasources.getSourceList();
  // //
  // //
  // // for (Source src : sourceList) {
  // //
  // // if (src.getNameCode() != null) {
  // // String id = src.getNameCode();
  // // if (id.equals(col.getMnemonic())) {
  // //
  // // switch (harvestingtype) {
  // // case oai_pmh:
  // // if (!src.getType().equals("DataSourceOai")) {
  // // return true;
  // // }
  // // break;
  // // case z39_50:
  // //
  // // if (!src.getType().equals("DataSourceZ3950")) {
  // // return true;
  // // }
  // // break;
  // // case ftp:
  // // if (!src.getType().equals("DataSourceDirectoryImporter")) {
  // // return true;
  // // }
  // // break;
  // //
  // // case http:
  // // if (!src.getType().equals("DataSourceDirectoryImporter")) {
  // // return true;
  // // }
  // // break;
  // // case folder:
  // // if (!src.getType().equals("DataSourceDirectoryImporter")) {
  // // return true;
  // // }
  // // break;
  // // default:
  // // throw new DataSourceOperationException(
  // // "Harvesting Type Value used for the creation of a datasource was invalid.");
  // // }
  // //
  // // return false;
  // //
  // // }
  // //
  // // }
  // //
  // // }
  // //
  //
  //
  // return false;
  //
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see eu.europeana.uim.repox.RepoxUIMService#retrieveDataSources()
  // */
  // @Override
  // public HashSet<Collection<?>> retrieveDataSources() throws DataSourceOperationException {
  //
  // // StorageEngine<?> engine = registry.getStorageEngine();
  // //
  // // HashSet<Collection<?>> uimCollections = new HashSet<Collection<?>>();
  // //
  // // DataSources datasources = repoxRestClient.retrieveDataSources();
  // //
  // // ArrayList<Source> sourceList = (ArrayList<Source>) datasources.getSourceList();
  // //
  // // for (Source src : sourceList) {
  // //
  // // if (src.getNameCode() != null) {
  // // String id = src.getNameCode();
  // //
  // // try {
  // // Collection<?> coll = engine.findCollection(id);
  // // if (coll != null) {
  // // uimCollections.add(coll);
  // // }
  // // } catch (StorageEngineException e) {
  // // // TODO Decide what to do here
  // // }
  // // }
  // //
  // // }
  // //
  // // return uimCollections;
  // return null;
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see eu.europeana.uim.repox.RepoxUIMService#retrieveRecord(java.lang.String)
  // */
  // @Override
  // public String retrieveRecord(String recordString) throws RecordOperationException {
  //
  // throw new UnsupportedOperationException("Not implemented yet");
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // * eu.europeana.uim.repox.RepoxUIMService#initiateHarvestingfromUIMObj(eu.europeana.uim.store.
  // * Collection, boolean)
  // */
  // @Override
  // public void initiateHarvestingfromUIMObj(Collection<?> col, boolean isfull)
  // throws HarvestingOperationException {
  //
  // // String id = col.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (id == null) {
  // // throw new HarvestingOperationException("Missing repoxID element from Collection object");
  // // }
  // // repoxRestClient.initiateHarvesting(id, isfull);
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // * eu.europeana.uim.repox.RepoxUIMService#scheduleHarvestingfromUIMObj(eu.europeana.uim.store.
  // * Collection, eu.europeana.uim.repoxclient.objects.ScheduleInfo)
  // */
  // @Override
  // public void scheduleHarvestingfromUIMObj(Collection<?> col, ScheduleInfo info)
  // throws HarvestingOperationException {
  //
  // // String id = col.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (id == null) {
  // // throw new HarvestingOperationException("Missing repoxID element from Collection object");
  // // }
  // //
  // // Source ds = new Source();
  // // ds.setId(id);
  // // repoxRestClient.scheduleHarvesting(id, info.getDatetime(), info.getFrequency(),
  // // info.isFullingest());
  //
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // eu.europeana.uim.repox.RepoxUIMService#cancelHarvesting(eu.europeana.uim.store.Collection)
  // */
  // @Override
  // public void cancelHarvesting(Collection<?> col) throws HarvestingOperationException {
  // // String id = col.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (id == null) {
  // // throw new HarvestingOperationException("Missing repoxID element from Collection object");
  // // }
  // //
  // // repoxRestClient.cancelHarvesting(id);
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // * eu.europeana.uim.repox.RepoxUIMService#getHarvestingStatus(eu.europeana.uim.store.Collection)
  // */
  // @Override
  // public RepoxHarvestingStatus getHarvestingStatus(Collection<?> col)
  // throws HarvestingOperationException {
  // // String id = col.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (id == null) {
  // // throw new HarvestingOperationException("Missing repoxID element from Collection object");
  // // }
  // // HarvestingStatus jibxstatus = repoxRestClient.getHarvestingStatus(id);
  // //
  // // RepoxHarvestingStatus returnStatus = new RepoxHarvestingStatus();
  // //
  // // returnStatus.setStatus(HarvestingState.valueOf(jibxstatus.getStatus().getStatus()));
  // //
  // // if (jibxstatus.getPercentage() != null) {
  // // returnStatus.setPercentage(jibxstatus.getPercentage().getPercentage());
  // // }
  // //
  // // if (jibxstatus.getRecords() != null) {
  // //
  // // returnStatus.setRecords(jibxstatus.getRecords().getRecords());
  // // }
  // //
  // // if (jibxstatus.getTimeLeft() != null) {
  // // returnStatus.setTimeLeft(jibxstatus.getTimeLeft().getTimeLeft());
  // // }
  // //
  // //
  // // return returnStatus;
  // return null;
  //
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see eu.europeana.uim.repox.RepoxUIMService#getActiveHarvestingSessions()
  // */
  // @Override
  // public Set<Collection<?>> getActiveHarvestingSessions() throws HarvestingOperationException {
  //
  // // StorageEngine<?> engine = registry.getStorageEngine();
  // //
  // // HashSet<Collection<?>> uimCollections = new HashSet<Collection<?>>();
  // //
  // // RunningTasks rTasks = repoxRestClient.getActiveHarvestingSessions();
  // //
  // // ArrayList<DataSource> sourceList = (ArrayList<DataSource>) rTasks.getDataSourceList();
  // //
  // // for (DataSource src : sourceList) {
  // //
  // // String id = src.getDataSource();
  // //
  // // try {
  // // Collection<?> coll = engine.findCollection(id);
  // // uimCollections.add(coll);
  // // } catch (StorageEngineException e) {
  // // // TODO Decide what to do here
  // // }
  // //
  // // }
  // //
  // // return uimCollections;
  //
  // return null;
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see eu.europeana.uim.repox.RepoxUIMService#getScheduledHarvestingSessions()
  // */
  // @Override
  // public Set<ScheduleInfo> getScheduledHarvestingSessions(Collection<?> col)
  // throws HarvestingOperationException {
  //
  // // String id = col.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (id == null) {
  // // throw new HarvestingOperationException("Missing repoxID element from Collection object");
  // // }
  // //
  // // HashSet<ScheduleInfo> schinfos = new HashSet<ScheduleInfo>();
  // //
  // //
  // // ScheduleTasks sTasks = repoxRestClient.getScheduledHarvestingSessions(id);
  // //
  // // ArrayList<Task> taskList = (ArrayList<Task>) sTasks.getTaskList();
  // //
  // // for (Task tsk : taskList) {
  // //
  // // ScheduleInfo schinfo = new ScheduleInfo();
  // //
  // // String ingTypeStr = tsk.getFrequency().getType();
  // //
  // // IngestFrequency ingTypeEnum = IngestFrequency.valueOf(ingTypeStr);
  // //
  // // boolean isfull = tsk.getFullIngest().isFullIngest();
  // //
  // // String time = tsk.getTime().getTime();
  // // String[] datetimeArray = time.split(" ");
  // // String[] dateArray = datetimeArray[0].split("-");
  // // String[] timeArray = datetimeArray[1].split(":");
  // //
  // //
  // // int year = Integer.valueOf(dateArray[0]);
  // // int monthOfYear = Integer.valueOf(dateArray[1]);
  // // int dayOfMonth = Integer.valueOf(dateArray[2]);
  // // int hourOfDay = Integer.valueOf(timeArray[0]);
  // // int minuteOfHour = Integer.valueOf(timeArray[1]);
  // //
  // // DateTime dt = new DateTime(year, monthOfYear, dayOfMonth, hourOfDay, minuteOfHour, 0, 0);
  // //
  // // schinfo.setDatetime(dt);
  // // schinfo.setFullingest(isfull);
  // // schinfo.setFrequency(ingTypeEnum);
  // //
  // // schinfos.add(schinfo);
  // //
  // // }
  // //
  // // return schinfos;
  // return null;
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see eu.europeana.uim.repox.RepoxUIMService#getHarvestLog(eu.europeana.uim.store.Collection)
  // */
  // @Override
  // public String getHarvestLog(Collection<?> col) throws HarvestingOperationException {
  //
  // // StringBuffer sb = new StringBuffer();
  // //
  // // String id = col.getValue(ControlledVocabularyProxy.REPOXID);
  // //
  // // if (id == null) {
  // // throw new HarvestingOperationException("Missing repoxID element from Collection object");
  // // }
  // //
  // // Log harvestLog = repoxRestClient.getHarvestLog(id);
  // //
  // // ArrayList<Line> linelist = (ArrayList<Line>) harvestLog.getLineList();
  // //
  // // for (Line ln : linelist) {
  // // sb.append(ln.getLine());
  // // }
  // // return sb.toString();
  //
  //
  // return null;
  // }
  //
  //
  //
  // /*
  // * (non-Javadoc)
  // *
  // * @see
  // eu.europeana.uim.repox.RepoxUIMService#initializeExport(eu.europeana.uim.store.Collection,
  // * int)
  // */
  // @Override
  // public void initializeExport(Collection<?> col, int numberOfRecords)
  // throws HarvestingOperationException {
  // // String id = col.getValue(ControlledVocabularyProxy.REPOXID);
  // // if (id == null) {
  // // throw new HarvestingOperationException("Missing repoxID element from Collection object");
  // // }
  // // repoxRestClient.initializeExport(id, numberOfRecords);
  //
  // }

  /*
   * Getters & Setters
   */


  public void setRegistry(Registry registry) {
    this.registry = registry;
  }

  public Registry getRegistry() {
    return registry;
  }

}
