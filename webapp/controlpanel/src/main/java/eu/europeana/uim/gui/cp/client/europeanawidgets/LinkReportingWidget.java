package eu.europeana.uim.gui.cp.client.europeanawidgets;

import eu.europeana.uim.gui.cp.client.services.ReportingServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;

/**
 * Table view showing current executions.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @since Apr 27, 2011
 */
public class LinkReportingWidget extends AbstractReportingWidget {
    /**
     * Creates a new instance of this class.
     * 
     * @param reportService
     * @param widgetName
     * @param workflows
     * @param reportName
     * @param outputFormats
     */
    public LinkReportingWidget(ReportingServiceAsync reportService, String widgetName,
                               String[] workflows, String reportName, String[] outputFormats) {
        super(reportService, widgetName, workflows, reportName, outputFormats);
        super.IGNORED_KEY = "linkcheck.ignored";
        super.PROCESSED_KEY = "linkcheck.processed";
        super.SUBMITTED_KEY = "linkcheck.submitted";
    }

    @Override
    protected void asyncOnInitialize(final AsyncCallback<Widget> callback) {
        GWT.runAsync(LinkReportingWidget.class, new RunAsyncCallback() {
            @Override
            public void onFailure(Throwable caught) {
                callback.onFailure(caught);
            }

            @Override
            public void onSuccess() {
                callback.onSuccess(onInitialize());
            }
        });
    }
}