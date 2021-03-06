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


import eu.europeana.repox.rest.client.accessors.AggregatorsAccessor;
import eu.europeana.repox.rest.client.accessors.DatasetsAccessor;
import eu.europeana.repox.rest.client.accessors.HarvestAccessor;
import eu.europeana.repox.rest.client.accessors.ProvidersAccessor;
import eu.europeana.uim.Registry;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.repox.model.RepoxConnectionStatus;
import eu.europeana.uim.repoxclient.utils.DSType;
import eu.europeana.uim.repoxclient.utils.PropertyReader;
import eu.europeana.uim.repoxclient.utils.UimConfigurationProperty;
import eu.europeana.uim.storage.StorageEngine;
import eu.europeana.uim.storage.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.utl.ist.dataProvider.Aggregator;
import pt.utl.ist.dataProvider.DataProvider;
import pt.utl.ist.dataProvider.DataSource;
import pt.utl.ist.dataProvider.DataSourceContainer;
import pt.utl.ist.dataProvider.dataSource.FileExtractStrategy;
import pt.utl.ist.dataProvider.dataSource.FileRetrieveStrategy;
import pt.utl.ist.dataProvider.dataSource.RecordIdPolicy;
import pt.utl.ist.ftp.FtpFileRetrieveStrategy;
import pt.utl.ist.http.HttpFileRetrieveStrategy;
import pt.utl.ist.marc.CharacterEncoding;
import pt.utl.ist.marc.DirectoryImporterDataSource;
import pt.utl.ist.marc.FolderFileRetrieveStrategy;
import pt.utl.ist.marc.iso2709.shared.Iso2709Variant;
import pt.utl.ist.metadataTransformation.MetadataTransformation;
import pt.utl.ist.oai.OaiDataSource;
import pt.utl.ist.task.ScheduledTask.Frequency;
import pt.utl.ist.task.Task;
import pt.utl.ist.util.ProviderType;
import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import pt.utl.ist.util.exceptions.InvalidArgumentsException;
import pt.utl.ist.util.exceptions.MissingArgumentsException;
import pt.utl.ist.z3950.DataSourceZ3950;

import javax.ws.rs.InternalServerErrorException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.List;
import java.util.Map;

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
    private static DatasetsAccessor ds;
    private static HarvestAccessor hs;
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

        try {
            ds = new DatasetsAccessor(new URL(defaultURI), "temporary", "temporary");
        } catch (MalformedURLException e) {
            LOGGER.error("DatasetsAccessor has a malformed URL {}", defaultURI);
            e.printStackTrace();
        }

        try {
            hs = new HarvestAccessor(new URL(defaultURI), "temporary", "temporary");
        } catch (MalformedURLException e) {
            LOGGER.error("HarvestAccessor has a malformed URL {}", defaultURI);
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

    /******************** Provider Calls ********************/

    @Override
    public boolean providerExists(String id) {
        try {
            ps.getProvider(id);
        } catch (DoesNotExistException e) {
            return false;
        }
        return true;
    }

    @Override
    public List<DataProvider> getProviderList(String aggregatorId, int offset, int number)
            throws InvalidArgumentsException, DoesNotExistException {
        return ps.getProviderList(aggregatorId, offset, number);
    }

    @Override
    public void createProvider(Provider uimProv, String aggregatorId, String id, String name,
                               String country, String countryCode, String description, String nameCode, String homepage,
                               ProviderType providerType, String email) throws InvalidArgumentsException,
            MissingArgumentsException, AlreadyExistsException, InternalServerErrorException,
            DoesNotExistException {

        if (uimProv.isAggregator()) {
            throw new InvalidArgumentsException("The requested object is not a Provider");
        }

    String providerId = null;
    try {
      providerId =
          ps.createProvider(aggregatorId, id, name, country, countryCode, description, nameCode,
              homepage, providerType, email);
    } catch (AlreadyExistsException e) {
      providerId = e.getDatasetId();
    }

        uimProv.putValue(ControlledVocabularyProxy.REPOXID, providerId);

        StorageEngine<?> engine = registry.getStorageEngine();
        // Store the created RepoxID into the UIM object
        try {
            engine.updateProvider(uimProv);
            engine.checkpoint();
        } catch (StorageEngineException e) {
            throw new InternalServerErrorException("Updating UIM Provider object failed");
        }
    }

    @Override
    public void deleteProvider(String providerId) throws DoesNotExistException,
            InternalServerErrorException {
        ps.deleteProvider(providerId);
    }

    @Override
    public void updateProvider(String id, String newId, String newAggregatorId, String name,
                               String country, String countryCode, String description, String nameCode, String homepage,
                               ProviderType providerType, String email) throws InvalidArgumentsException,
            DoesNotExistException, MissingArgumentsException, AlreadyExistsException {
        ps.updateProvider(id, newId, newAggregatorId, name, country, countryCode, description,
                nameCode, homepage, providerType, email);
    }

    /******************** Datasource Calls ********************/

    @Override
    public boolean datasourceExists(String id) {
        try {
            ds.getDataset(id);
        } catch (DoesNotExistException e) {
            return false;
        }
        return true;
    }

    @Override
    public int getDatasetRecordCount(String id) throws DoesNotExistException,
            InternalServerErrorException {
        return ds.getDatasetRecordCount(id);
    }

  @Override
  public void createDatasourceOai(Collection col, String providerId, String id, String name,
      String nameCode, boolean isSample, String schema, String description, String namespace,
      String metadataFormat, String marcFormat, String oaiUrl, String oaiSet, String exportDir,
      RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException {
    String datasetOaiId = null;
    try {
      datasetOaiId =
          ds.createDatasetOai(providerId, id, name, nameCode, isSample, schema, description,
              namespace, metadataFormat, marcFormat, oaiUrl, oaiSet, exportDir, recordIdPolicy,
              metadataTransformations);
    } catch (AlreadyExistsException e) {
      datasetOaiId = e.getDatasetId();
    }

        col.putValue(ControlledVocabularyProxy.REPOXID, datasetOaiId);

        StorageEngine<?> engine = registry.getStorageEngine();

        // Store the created RepoxID into the UIM object
        try {
            engine.updateCollection(col);
            engine.checkpoint();
        } catch (StorageEngineException e) {
            throw new InternalServerErrorException("Updating UIM Collection object failed");
        }

    }

  @Override
  public void createDatasetFile(Collection col, String providerId, String id, String name,
      String nameCode, boolean isSample, String schema, String description, String namespace,
      String metadataFormat, String marcFormat, String exportDir, RecordIdPolicy recordIdPolicy,
      FileExtractStrategy extractStrategy, FileRetrieveStrategy retrieveStrategy,
      CharacterEncoding characterEncoding, Iso2709Variant isoVariant, String sourceDirectory,
      String recordXPath, Map<String, MetadataTransformation> metadataTransformations)
      throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
      AlreadyExistsException, InternalServerErrorException {
    String datasetFileId = null;
    try{
    datasetFileId =
        ds.createDatasetFile(providerId, id, name, nameCode, isSample, schema, description,
            namespace, metadataFormat, marcFormat, exportDir, recordIdPolicy, extractStrategy,
            retrieveStrategy, characterEncoding, isoVariant, sourceDirectory, recordXPath,
            metadataTransformations);
  } catch (AlreadyExistsException e) {
    datasetFileId = e.getDatasetId();
  }

        col.putValue(ControlledVocabularyProxy.REPOXID, datasetFileId);

        StorageEngine<?> engine = registry.getStorageEngine();

        // Store the created RepoxID into the UIM object
        try {
            engine.updateCollection(col);
            engine.checkpoint();
        } catch (StorageEngineException e) {
            throw new InternalServerErrorException("Updating UIM Collection object failed");
        }
    }

    @Override
    public void updateDatasourceOai(String id, String newId, String name, String nameCode,
                                    boolean isSample, String schema, String description, String namespace, String metadataFormat,
                                    String marcFormat, String oaiUrl, String oaiSet, String exportDir,
                                    RecordIdPolicy recordIdPolicy, Map<String, MetadataTransformation> metadataTransformations)
            throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
            AlreadyExistsException, InternalServerErrorException {
        ds.updateDatasetOai(id, newId, name, nameCode, isSample, schema, description, namespace,
                metadataFormat, marcFormat, oaiUrl, oaiSet, exportDir, recordIdPolicy,
                metadataTransformations);
    }

    @Override
    public void updateDatasourceFile(String id, String newId, String name, String nameCode,
                                     boolean isSample, String schema, String description, String namespace, String metadataFormat,
                                     String marcFormat, String exportDir, RecordIdPolicy recordIdPolicy,
                                     FileExtractStrategy extractStrategy, FileRetrieveStrategy retrieveStrategy,
                                     CharacterEncoding characterEncoding, Iso2709Variant isoVariant, String sourceDirectory,
                                     String recordXPath, Map<String, MetadataTransformation> metadataTransformations)
            throws InvalidArgumentsException, DoesNotExistException, MissingArgumentsException,
            AlreadyExistsException, InternalServerErrorException {
        ds.updateDatasetFile(id, newId, name, nameCode, isSample, schema, description, namespace,
                metadataFormat, marcFormat, exportDir, recordIdPolicy, extractStrategy, retrieveStrategy,
                characterEncoding, isoVariant, sourceDirectory, recordXPath, metadataTransformations);
    }


    @Override
    public void deleteDataset(String datasetId) throws DoesNotExistException {
        ds.deleteDataset(datasetId);
    }

    @Override
    public List<DataSourceContainer> getDatasetList(String providerId, int offset, int number)
            throws InvalidArgumentsException, DoesNotExistException {
        return ds.getDatasetList(providerId, offset, number);
    }


    @Override
    public boolean hasHarvestingTypeChanged(String id, DSType type) throws DoesNotExistException,
            InvalidArgumentsException {

        DataSourceContainer dataset = ds.getDataset(id);

        DataSource dataSource = dataset.getDataSource();
        switch (type) {
            case oai_pmh:
                if (!(dataSource instanceof OaiDataSource)) {
                    return true;
                }
                break;
            case folder:
                if (dataSource instanceof DirectoryImporterDataSource) {
                    DirectoryImporterDataSource dids = (DirectoryImporterDataSource) dataSource;
                    FileRetrieveStrategy retrieveStrategy = dids.getRetrieveStrategy();
                    if (!(retrieveStrategy instanceof FolderFileRetrieveStrategy))
                        return true;
                }
                else return true;
                break;
            case ftp:
                if (dataSource instanceof DirectoryImporterDataSource) {
                    DirectoryImporterDataSource dids = (DirectoryImporterDataSource) dataSource;
                    FileRetrieveStrategy retrieveStrategy = dids.getRetrieveStrategy();
                    if (!(retrieveStrategy instanceof FtpFileRetrieveStrategy))
                        return true;
                }
                else return true;
                break;
            case http:
                if (dataSource instanceof DirectoryImporterDataSource) {
                    DirectoryImporterDataSource dids = (DirectoryImporterDataSource) dataSource;
                    FileRetrieveStrategy retrieveStrategy = dids.getRetrieveStrategy();
                    if (!(retrieveStrategy instanceof HttpFileRetrieveStrategy))
                        return true;
                }
                else return true;
                break;
            case z39_50:
                if (!(dataSource instanceof DataSourceZ3950)) {
                    return true;
                }
                break;
            case none:
            default:
                throw new InvalidArgumentsException(
                        "Harvesting Type Value used for the creation of a datasource was invalid.");
        }
        return false;
    }

    /******************** Harvesting Calls ********************/

    @Override
    public void initiateHarvesting(String id, String type) throws AlreadyExistsException,
            DoesNotExistException, InternalServerErrorException {
        hs.startHarvest(id, type);
    }

    @Override
    public String getHarvestingStatus(String id) throws DoesNotExistException,
            InternalServerErrorException {
        return hs.getDatasetHarvestingStatus(id);
    }

    @Override
    public List<Task> getCurrentHarvestsList() {
        return hs.getCurrentHarvestsList();
    }



    @Override
    public String getDatasetLastIngestLog(String id) throws DoesNotExistException,
            InternalServerErrorException {
        return hs.getDatasetLastIngestLog(id);
    }

    @Override
    public void cancelHarvest(String id) throws DoesNotExistException, InternalServerErrorException {
        hs.cancelHarvest(id);
    }

    @Override
    public void scheduleHarvest(String id, Calendar firstDateTime, Frequency frequency, int xmonths,
                                boolean incremental) throws DoesNotExistException, MissingArgumentsException,
            AlreadyExistsException {
        hs.scheduleHarvest(id, firstDateTime, frequency, xmonths, incremental);
    }

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
