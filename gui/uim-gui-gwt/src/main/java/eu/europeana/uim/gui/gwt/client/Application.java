package eu.europeana.uim.gui.gwt.client;

import com.google.gwt.cell.client.Cell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.NumberCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.widgetideas.client.ProgressBar;
import eu.europeana.uim.gui.gwt.shared.Collection;
import eu.europeana.uim.gui.gwt.shared.Execution;
import eu.europeana.uim.gui.gwt.shared.Provider;
import eu.europeana.uim.gui.gwt.shared.Workflow;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint {

    private static final String ALL_COLLECTIONS = "ALL";

    private final OrchestrationServiceAsync orchestrationService = (OrchestrationServiceAsync) GWT.create(OrchestrationService.class);

    private final List<Execution> executions = new ArrayList<Execution>();

    private TabLayoutPanel tabs = null;

    private VerticalPanel currentExecutionsPanel = null;

    private Map<Long, ProgressBar> progressBars = new HashMap<Long, ProgressBar>();

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        tabs = new TabLayoutPanel(1.5, Style.Unit.EM);
        tabs.setHeight("500px");

        FlowPanel overview = new FlowPanel();
        FlowPanel executions = new FlowPanel();
        FlowPanel storage = new FlowPanel();
        overview.setHeight("500px");
        executions.setHeight("500px");
        storage.setHeight("500px");

        tabs.add(overview, "Overview");
        tabs.add(executions, "New Execution");
        tabs.add(storage, "Storage overview");
        RootPanel.get().add(tabs);

        buildOverviewPanel(overview);
        buildExecutionPanel(executions);
        buildCollectionsPanel(storage);

    }

    private final CellTable<Execution> pastExecutionsCellTable = new CellTable<Execution>();
    private List<Execution> pastExecutions = new ArrayList<Execution>();


    private void buildOverviewPanel(FlowPanel overview) {
        Label currentExecutionsLabel = new Label("Current executions");
        overview.add(currentExecutionsLabel);
        currentExecutionsPanel = new VerticalPanel();
        currentExecutionsPanel.setWidth("500px");
        overview.add(currentExecutionsPanel);

        // load active executions
        orchestrationService.getActiveExecutions(new AsyncCallback<List<Execution>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Execution> executions) {
                for(Execution e : executions) {
                    addExecution(e);
                }
            }
        });

        overview.add(new Label("Past executions"));
        overview.add(pastExecutionsCellTable);

        // cell table
        final ListDataProvider<Execution> dataProvider = new ListDataProvider<Execution>();
        dataProvider.setList(executions);
        dataProvider.addDataDisplay(pastExecutionsCellTable);


        final SingleSelectionModel<Execution> selectionModel = new SingleSelectionModel<Execution>();
        pastExecutionsCellTable.setSelectionModel(selectionModel);

        addColumn(pastExecutionsCellTable, new TextCell(), "Execution", new GetValue<String, Execution>() {
            public String getValue(Execution execution) {
                return execution.getName();
            }
        });
        addColumn(pastExecutionsCellTable, new DateCell(), "Start time", new GetValue<Date, Execution>() {
            public Date getValue(Execution execution) {
                return execution.getStartTime();
            }
        });
        addColumn(pastExecutionsCellTable, new DateCell(), "End time", new GetValue<Date, Execution>() {
            public Date getValue(Execution execution) {
                return execution.getEndTime();
            }
        });

        updatePastExecutions();
    }


    private void updatePastExecutions() {
        // load past executions
        orchestrationService.getPastExecutions(new AsyncCallback<List<Execution>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Execution> executions) {
                pastExecutions.clear();
                pastExecutions.addAll(executions);
                updateCellTableData(pastExecutionsCellTable, pastExecutions);
            }
        });
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
                    workflowList.addItem(w.getName(), w.getId().toString());
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
                providerList.clear();
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
                        if (collections.size() > 0) {
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

        start.addClickHandler(new ClickHandler() {

            @Override
            public void onClick(ClickEvent clickEvent) {
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
                            addExecution(execution);
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
                            addExecution(execution);
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

    private void addExecution(final Execution execution) {
        executions.add(execution);
        final ProgressBar bar = new ProgressBar(0, execution.getTotal());
        bar.setTitle(execution.getName());
        bar.setTextVisible(true);
        currentExecutionsPanel.add(bar);
        progressBars.put(execution.getId(), bar);

        // poll the execution status every second
        Timer t = new Timer() {
            @Override
            public void run() {
                orchestrationService.getExecution(execution.getId(), new AsyncCallback<Execution>() {
                    @Override
                    public void onFailure(Throwable throwable) {
                        // TODO panic
                        throwable.printStackTrace();
                    }

                    @Override
                    public void onSuccess(Execution execution) {
                        bar.setProgress(execution.getProgress());
                        bar.redraw();
                        if (execution.isDone()) {
                            cancel();
                            executionDone(execution);
                        }
                    }
                });

            }
        };
        t.scheduleRepeating(1000);
    }

    private void executionDone(Execution e) {
        ProgressBar widget = progressBars.get(e.getId());
        currentExecutionsPanel.remove(widget);
        updatePastExecutions();
    }


    private final CellTable<Collection> collectionsCellTable = new CellTable<Collection>();
    private List<Collection> collections = new ArrayList<Collection>();

    private void updateCollections(List<Collection> collections) {
        this.collections.clear();
        this.collections.addAll(collections);
        updateCellTableData(collectionsCellTable, collections);
    }

    private Timer collectionsRefreshTimer = new Timer() {
        @Override
        public void run() {
            updateCollections();
        }
    };

    private void buildCollectionsPanel(final FlowPanel storage) {

        storage.add(collectionsCellTable);

        updateCollections();

        // auto-refresh this panel if it is active
        tabs.addSelectionHandler(new SelectionHandler<Integer>() {
            @Override
            public void onSelection(SelectionEvent<Integer> integerSelectionEvent) {
                if (integerSelectionEvent.getSelectedItem().equals(2)) {
                    collectionsRefreshTimer.scheduleRepeating(10000);
                } else {
                    collectionsRefreshTimer.cancel();
                }
            }
        });


        // cell table
        final ListDataProvider<Collection> dataProvider = new ListDataProvider<Collection>();
        dataProvider.setList(collections);
        dataProvider.addDataDisplay(collectionsCellTable);


        final SingleSelectionModel<Collection> selectionModel = new SingleSelectionModel<Collection>();
        collectionsCellTable.setSelectionModel(selectionModel);

        addColumn(collectionsCellTable, new TextCell(), "Collection", new GetValue<String, Collection>() {
            public String getValue(Collection collection) {
                return collection.getName();
            }
        });
        addColumn(collectionsCellTable, new TextCell(), "Provider", new GetValue<String, Collection>() {
            public String getValue(Collection collection) {
                return collection.getProvider().getName();
            }
        });
        addColumn(collectionsCellTable, new NumberCell(), "Total records", new GetValue<Number, Collection>() {
            public Integer getValue(Collection collection) {
                return collection.getTotal();
            }
        });

        /*
        addColumn(new ActionCell<Collection>(
                "Remove", new ActionCell.Delegate<Collection>() {
                    public void execute(Collection collection) {
                        collections.remove(collection);
                        updateCellTableData();
                    }
                }), "Action", new GetValue<Collection>() {

            public Collection getValue(Collection contact) {
                return contact;
            }
        });
        */

    }

    private void updateCollections() {
        orchestrationService.getAllCollections(new AsyncCallback<List<Collection>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Collection> collections) {
                updateCollections(collections);
            }
        });
    }


    // helper methods for the collections cellTable
    private <T> void updateCellTableData(CellTable<T> table, List<T> data) {
        table.setRowData(0, data);
        table.setRowCount(data.size());
    }

    private <C, T> void addColumn(CellTable<T> table, Cell<C> cell, String headerText, final GetValue<C, T> getter) {
        Column<T, C> column = new Column<T, C>(cell) {
            @Override
            public C getValue(T object) {
                return getter.getValue(object);
            }
        };
        table.addColumn(column, headerText);
    }

    private static interface GetValue<C, T> {
        C getValue(T object);
    }


    private void alert(String text) {
        final DialogBox popup = new DialogBox(false);
        popup.setText("DBG");
        VerticalPanel vpanel = new VerticalPanel();
        vpanel.add(new HTML(text));

        Button ok = new Button("OK", new ClickHandler() {
            @Override
            public void onClick(ClickEvent arg0) {
                popup.hide();
            }
        });
        vpanel.add(ok);

        popup.add(vpanel);
        popup.center();
        popup.show();
    }


}
