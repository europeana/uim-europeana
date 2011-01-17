package eu.europeana.uim.gui.gwt.client;

import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.SingleSelectionModel;
import com.google.gwt.widgetideas.client.ProgressBar;
import eu.europeana.uim.gui.gwt.shared.Execution;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The overview panel with current and past executions
 *
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 */
public class OverviewPanel extends FlowPanel {

    private OrchestrationServiceAsync orchestrationService = null;

    private final List<Execution> executions = new ArrayList<Execution>();
    private final List<Execution> pastExecutions = new ArrayList<Execution>();

    private VerticalPanel currentExecutionsPanel = null;
    private Map<Long, ProgressBar> progressBars = new HashMap<Long, ProgressBar>();
    private final CellTable<Execution> pastExecutionsCellTable = new CellTable<Execution>();


    public OverviewPanel(OrchestrationServiceAsync orchestrationServiceAsync) {
        this.orchestrationService = orchestrationServiceAsync;

        add(new Label("Current executions"));
        currentExecutionsPanel = new VerticalPanel();
        currentExecutionsPanel.setWidth("500px");
        add(currentExecutionsPanel);

        loadActiveExecutions();

        add(new Label("Past executions"));
        add(pastExecutionsCellTable);
        buildPastExecutionsCellTable();
        updatePastExecutions();
    }

    private void loadActiveExecutions() {
        // load active executions
        orchestrationService.getActiveExecutions(new AsyncCallback<List<Execution>>() {
            @Override
            public void onFailure(Throwable throwable) {
                throwable.printStackTrace();
            }

            @Override
            public void onSuccess(List<Execution> executions) {
                for (Execution e : executions) {
                    addExecution(e);
                }
            }
        });
    }

    private void buildPastExecutionsCellTable() {
        // cell table
        final ListDataProvider<Execution> dataProvider = new ListDataProvider<Execution>();
        dataProvider.setList(executions);
        dataProvider.addDataDisplay(pastExecutionsCellTable);


        final SingleSelectionModel<Execution> selectionModel = new SingleSelectionModel<Execution>();
        pastExecutionsCellTable.setSelectionModel(selectionModel);

        CellTableUtils.addColumn(pastExecutionsCellTable, new TextCell(), "Execution", new CellTableUtils.GetValue<String, Execution>() {
            public String getValue(Execution execution) {
                return execution.getName();
            }
        });
        DateTimeFormat dtf = DateTimeFormat.getFormat("dd.MM.yyyy 'at' HH:mm:ss");
        CellTableUtils.addColumn(pastExecutionsCellTable, new DateCell(dtf), "Start time", new CellTableUtils.GetValue<Date, Execution>() {
            public Date getValue(Execution execution) {
                return execution.getStartTime();
            }
        });
        CellTableUtils.addColumn(pastExecutionsCellTable, new DateCell(dtf), "End time", new CellTableUtils.GetValue<Date, Execution>() {
            public Date getValue(Execution execution) {
                return execution.getEndTime();
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

    public void addExecution(final Execution execution) {
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
                CellTableUtils.updateCellTableData(pastExecutionsCellTable, pastExecutions);
            }
        });
    }

}
