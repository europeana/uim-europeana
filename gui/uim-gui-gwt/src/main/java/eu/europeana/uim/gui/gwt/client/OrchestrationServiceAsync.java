package eu.europeana.uim.gui.gwt.client;

import com.google.gwt.user.client.rpc.AsyncCallback;
import eu.europeana.uim.gui.gwt.shared.Collection;
import eu.europeana.uim.gui.gwt.shared.Execution;
import eu.europeana.uim.gui.gwt.shared.Provider;
import eu.europeana.uim.gui.gwt.shared.Workflow;

import java.util.List;

public interface OrchestrationServiceAsync {
    void getWorkflows(AsyncCallback<List<Workflow>> async);

    void getProviders(AsyncCallback<List<Provider>> async);

    void getCollections(Long provider, AsyncCallback<List<Collection>> async);

    void getAllCollections(AsyncCallback<List<Collection>> async);

    void startCollection(Long workflow, Long collection, AsyncCallback<Execution> async);

    void startProvider(Long workflow, Long provider, AsyncCallback<Execution> async);

    void getExecution(Long id, AsyncCallback<Execution> async);

    void getCollectionTotal(Long collection, AsyncCallback<Integer> async);

    void getActiveExecutions(AsyncCallback<List<Execution>> async);

    void getPastExecutions(AsyncCallback<List<Execution>> async);
}
