package eu.europeana.uim.api;

import eu.europeana.uim.MetaDataRecord;
import eu.europeana.uim.common.ProgressMonitor;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;

/**
 * Orchestrates the ingestion job execution.
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public interface Orchestrator {
	
	public String getIdentifier();

    Execution executeWorkflow(Workflow w, MetaDataRecord<?> mdr, ProgressMonitor monitor);

    Execution executeWorkflow(Workflow w, Collection c, ProgressMonitor monitor);

    Execution executeWorkflow(Workflow w, Request r, ProgressMonitor monitor);

    Execution executeWorkflow(Workflow w, Provider p, ProgressMonitor monitor);

    /**
     * Gets the next batch of MetaDataRecord IDs for a given Execution
     * @param e the Execution for which to retrieve the next batch of IDs
     * @return an array of MetaDataRecord IDs to process
     */
    long[] getBatchFor(Execution e);
}
