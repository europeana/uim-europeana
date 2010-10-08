package eu.europeana.uim;

import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Execution;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.Request;
import eu.europeana.uim.workflow.Workflow;

/**
 * Orchestrates the ingestion job execution.
 *
 * @author manu
 */
public interface Orchestrator {
	
	public String getIdentifier();

    Execution executeWorkflow(Workflow w, MetaDataRecord mdr);

    Execution executeWorkflow(Workflow w, Collection c);

    Execution executeWorkflow(Workflow w, Request r);

    Execution executeWorkflow(Workflow w, Provider p);

    /**
     * Gets the next batch of MetaDataRecord IDs for a given Execution
     * @param e the Execution for which to retrieve the next batch of IDs
     * @return an array of MetaDataRecord IDs to process
     */
    long[] getBatchFor(Execution e);
}
