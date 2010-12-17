package eu.europeana.uim.gui.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.LayoutPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import eu.europeana.uim.gui.gwt.shared.Workflow;

import java.util.List;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint {

    private final WorkflowServiceAsync workflowService = (WorkflowServiceAsync) GWT.create(WorkflowService.class);


    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        TabLayoutPanel tabs = new TabLayoutPanel(1.5, Style.Unit.EM);
        LayoutPanel overview = new LayoutPanel();
        LayoutPanel executions = new LayoutPanel();

        tabs.add(overview, "Overview");
        tabs.add(executions, "Executions");
        RootPanel.get().add(tabs);

        buildOverview(overview);
        buildExecutions(executions);

    }

    private void buildOverview(LayoutPanel overview) {
        HTML welcome = new HTML("Welcome to the Matrix");
        overview.add(welcome);
    }

    private void buildExecutions(LayoutPanel executions) {

        // pick a workflow
        final ListBox workflowList = new ListBox(false);
        executions.add(workflowList);

        workflowService.getWorkflows(new AsyncCallback<List<Workflow>>() {
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


        // pick a data source
        // pick a whole provider or a single collection
        // press start and pray

    }
}
