package eu.europeana.uim.gui.gwt.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.RootPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class Application implements EntryPoint {

    private final OrchestrationServiceAsync orchestrationService = (OrchestrationServiceAsync) GWT.create(OrchestrationService.class);

    private OverviewPanel overview = null;
    private ExecutionPanel executions = null;
    private CollectionsPanel collections = null;

    private TabLayoutPanel tabs = null;

    /**
     * This is the entry point method.
     */
    public void onModuleLoad() {
        tabs = new TabLayoutPanel(1.5, Style.Unit.EM);
        tabs.setHeight("500px");

        overview = new OverviewPanel(orchestrationService);
        executions = new ExecutionPanel(orchestrationService, this);
        collections = new CollectionsPanel(orchestrationService, this);

        overview.setHeight("500px");
        executions.setHeight("500px");
        collections.setHeight("500px");

        tabs.add(overview, "Overview");
        tabs.add(executions, "New Execution");
        tabs.add(collections, "Collections overview");

        RootPanel.get().add(tabs);
    }

    public OverviewPanel getOverview() {
        return this.overview;
    }

    public void selectOverviewTab() {
        tabs.selectTab(0);
    }

    public void addTabSelectionHandler(SelectionHandler<Integer> handler) {
        tabs.addSelectionHandler(handler);
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
