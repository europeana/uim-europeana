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
package eu.europeana.uim.repoxclient.workflow;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

import javax.ws.rs.InternalServerErrorException;

import pt.utl.ist.util.exceptions.AlreadyExistsException;
import pt.utl.ist.util.exceptions.DoesNotExistException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.repox.model.HarvestingState;
import eu.europeana.uim.repoxclient.rest.RepoxUIMServiceT;
import eu.europeana.uim.store.Collection;

/**
 *
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 2 Apr 2012
 */
public class RepoxInitHarvestingPlugin<I> extends AbstractIngestionPlugin<Collection<I>, I> {

  private static RepoxUIMServiceT repoxservice;
  // private static SugarCrmService sugarservice;
  /** Property which allows to overwrite base url from collection/provider */
  public static final String fullingest = "repox.fullingest";

  /**
   * The parameters used by this Plugin
   */
  private static final List<String> params = new ArrayList<String>() {
    private static final long serialVersionUID = 1L;
    {
      add(fullingest);
    }
  };


  /**
   * @param repoxservice2
   * @param name
   * @param description
   */
  public RepoxInitHarvestingPlugin(RepoxUIMServiceT repoxservice2, String name, String description) {
    super(name, description);
    RepoxInitHarvestingPlugin.repoxservice = repoxservice2;
  }


  /*
   * (non-Javadoc)
   *
   * @see eu.europeana.uim.api.IngestionPlugin#completed(eu.europeana.uim.api.ExecutionContext)
   */
  @Override
  public void completed(ExecutionContext<Collection<I>, I> context)
      throws IngestionPluginFailedException {

    Collection<?> coll = (Collection<?>) context.getDataSet();

    // try {
    //
    boolean collectionExists = repoxservice.datasourceExists(coll.getMnemonic());

    try {

      if (collectionExists) {
        repoxservice.initiateHarvesting(coll.getMnemonic(), "full");

        String status;
        status = repoxservice.getHarvestingStatus(coll.getMnemonic());

        context.getExecution().setActive(true);

        while (status.equalsIgnoreCase(HarvestingState.RUNNING.toString())
            || status.equalsIgnoreCase(HarvestingState.undefined.toString())) {
          Thread.sleep(100);
          context.getExecution().setProcessedCount(
              repoxservice.getDatasetRecordCount(coll.getMnemonic()));
          status = repoxservice.getHarvestingStatus(coll.getMnemonic());
        }

        context.getExecution().setActive(false);

        /*
         * String sugarID = coll.getValue("sugarCRMID"); switch(status.getStatus()){ case OK:
         * sugarservice.changeEntryStatus(sugarID, EuropeanaDatasetStates.READY_FOR_REPLICATION);
         * break; case ERROR: sugarservice.changeEntryStatus(sugarID,
         * EuropeanaDatasetStates.HARVESTING_PENDING); break;
         *
         * }
         */
        String log = repoxservice.getDatasetLastIngestLog(coll.getMnemonic());
        context.getLoggingEngine().log(Level.INFO, "Repox Harvesting Session Results", log);

      }
    } catch (InternalServerErrorException | InterruptedException | AlreadyExistsException
        | DoesNotExistException e) {
      throw new IngestionPluginFailedException("Error during accessing remote Datasource from UIM",
          e);
    }

    // } catch (DataSourceOperationException e) {
    // throw new
    // IngestionPluginFailedException("Error during accessing remote Datasource from UIM",e);
    // } catch (HarvestingOperationException e) {
    // throw new IngestionPluginFailedException("Error while initiating harvestiing at the remote "
    // +
    // "Datasource from UIM",e);
    // }
    // /*
    // catch (QueryResultException e) {
    // throw new
    // IngestionPluginFailedException("Error while trying to write information back to SugarCRM",e);
    // } */
    // catch (InterruptedException e) {
    // throw new IngestionPluginFailedException("InterruptedException",e);
    // }
  }

  /*
   * (non-Javadoc)
   *
   * @see eu.europeana.uim.api.IngestionPlugin#getInputFields()
   */
  @Override
  public TKey<?, ?>[] getInputFields() {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see eu.europeana.uim.api.IngestionPlugin#getMaximumThreadCount()
   */
  @Override
  public int getMaximumThreadCount() {
    return 100;
  }

  /*
   * (non-Javadoc)
   *
   * @see eu.europeana.uim.api.IngestionPlugin#getOptionalFields()
   */
  @Override
  public TKey<?, ?>[] getOptionalFields() {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see eu.europeana.uim.api.IngestionPlugin#getOutputFields()
   */
  @Override
  public TKey<?, ?>[] getOutputFields() {
    return null;
  }

  /*
   * (non-Javadoc)
   *
   * @see eu.europeana.uim.api.IngestionPlugin#getParameters()
   */
  @Override
  public List<String> getParameters() {
    return params;
  }

  /*
   * (non-Javadoc)
   *
   * @see eu.europeana.uim.api.IngestionPlugin#getPreferredThreadCount()
   */
  @Override
  public int getPreferredThreadCount() {
    return 10;
  }


  @Override
  public boolean process(Collection<I> dataset, ExecutionContext<Collection<I>, I> context)
      throws IngestionPluginFailedException, CorruptedDatasetException {
    return true;
  }


  @Override
  public void initialize(ExecutionContext<Collection<I>, I> context)
      throws IngestionPluginFailedException {}


  @Override
  public void initialize() {}


  @Override
  public void shutdown() {}



}
