package eu.europeana.uim.gui.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import eu.europeana.uim.gui.gwt.shared.Collection;
import eu.europeana.uim.gui.gwt.shared.Execution;
import eu.europeana.uim.gui.gwt.shared.Provider;
import eu.europeana.uim.gui.gwt.shared.Workflow;

import java.util.ArrayList;
import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint {

    private static final String ALL_COLLECTIONS = "ALL";

    private final OrchestrationServiceAsync orchestrationService = (OrchestrationServiceAsync) GWT.create(OrchestrationService.class);

    private final List<Execution> executions = new ArrayList<Execution>();

    private TabLayoutPanel tabs = null;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        tabs = new TabLayoutPanel(1.5, Style.Unit.EM);
        tabs.setHeight("500px");

        FlowPanel overview = new FlowPanel();
        FlowPanel executions = new FlowPanel();
        overview.setHeight("500px");
        executions.setHeight("500px");

        tabs.add(overview, "Overview");
        tabs.add(executions, "New Execution");
        RootPanel.get().add(tabs);

        buildOverviewPanel(overview);
        buildExecutionPanel(executions);

    }

    private void buildOverviewPanel(FlowPanel overview) {
        HTML welcome = new HTML("Welcome to the Matrix");
        overview.add(welcome);

    }

    private void buildExecutionPanel(FlowPanel executionPanel) {

        // pick a workflow
        final Label workflowLabel = new Label("Workflow");
        final ListBox workflowList = new ListBox(false);
        executionPanel.add(workflowLabel);
        executionPanel.add(workflowList);

        orchestrationService.getWorkflows(new AsyncCallback<List<Workflow>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
                // TODO: panic
            }

            @Override
            public void onSuccess(List<Workflow> workflows) {
                for (Workflow w : workflows) {
                    workflowList.addItem(w.getName());
                }
            }
        });

        // pick a provider and collection


        final Label providerLabel = new Label("Provider");
        final ListBox providerList = new ListBox(false);
        final Label collectionLabel = new Label("Collection");
        final ListBox collectionList = new ListBox(false);
        final Button start = new Button("Go!");

        collectionList.setEnabled(false);
        start.setEnabled(false);

        executionPanel.add(providerLabel);
        executionPanel.add(providerList);
        executionPanel.add(collectionLabel);
        executionPanel.add(collectionList);
        executionPanel.add(start);

        // load providers
        orchestrationService.getProviders(new AsyncCallback<List<Provider>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
                // TODO: panic
            }

            @Override
            public void onSuccess(List<Provider> providers) {
                for (Provider p : providers) {
                    providerList.addItem(p.getName(), p.getId().toString());
                }
            }
        });

        // add change listener to provision collections on provider change
        providerList.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent changeEvent) {
                if (!collectionList.isEnabled()) {
                    collectionList.setEnabled(true);
                    start.setEnabled(true);
                }
                Long providerId = Long.parseLong(providerList.getValue(providerList.getSelectedIndex()));
                orchestrationService.getCollections(providerId, new AsyncCallback<List<Collection>>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        throwable.printStackTrace();
                        // TODO: panic
                    }

                    @Override
                    public void onSuccess(List<Collection> collections) {
                        collectionList.clear();
                        if(collections.size() > 0) {
                            // "all" option
                            collectionList.addItem("All collections", ALL_COLLECTIONS);
                        }
                        for (Collection collection : collections) {
                            collectionList.addItem(collection.getName(), collection.getId().toString());
                        }
                    }
                });

            }
        });

        start.addMouseDownHandler(new MouseDownHandler() {

            @Override
            public void onMouseDown(MouseDownEvent mouseDownEvent) {
                String selectedWorkflow = workflowList.getValue(workflowList.getSelectedIndex());
                Long workflowId = Long.parseLong(selectedWorkflow);
                String selectedDataSource = collectionList.getValue(collectionList.getSelectedIndex());
                if (selectedDataSource.equals(ALL_COLLECTIONS)) {
                    // start on provider
                    Long providerId = Long.parseLong(providerList.getValue(providerList.getSelectedIndex()));
                    orchestrationService.startProvider(workflowId, providerId, new AsyncCallback<Execution>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            // TODO panic
                            throwable.printStackTrace();
                        }

                        @Override
                        public void onSuccess(Execution execution) {
                            executions.add(execution);
                        }
                    });
                } else {
                    // start on collection
                    Long collectionId = Long.parseLong(selectedDataSource);
                    orchestrationService.startCollection(workflowId, collectionId, new AsyncCallback<Execution>() {
                        @Override
                        public void onFailure(Throwable throwable) {
                            // TODO panic
                            throwable.printStackTrace();
                        }

                        @Override
                        public void onSuccess(Execution execution) {
                            executions.add(execution);
                        }
                    });
                }

                // jump to the overview panel and clear this one
                tabs.selectTab(0);
                collectionList.clear();
                collectionList.setEnabled(false);
                start.setEnabled(false);
                providerList.setSelectedIndex(0);
                workflowList.setSelectedIndex(0);
            }
        });

    }
}
