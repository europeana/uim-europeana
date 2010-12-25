package eu.europeana.uim.gui.gwt.client;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import eu.europeana.uim.gui.gwt.shared.Collection;
import eu.europeana.uim.gui.gwt.shared.Execution;
import eu.europeana.uim.gui.gwt.shared.Provider;
import eu.europeana.uim.gui.gwt.shared.Workflow;

import java.util.List;

/**
 * Service to get the available workflows and so on
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
@RemoteServiceRelativePath("orchestrator")
public interface OrchestrationService extends RemoteService {

    List<Workflow> getWorkflows();

    List<Provider> getProviders();

    List<Collection> getCollections(Long provider);

    Execution startCollection(Long workflow, Long collection);

    Execution startProvider(Long workflow, Long provider);



}
