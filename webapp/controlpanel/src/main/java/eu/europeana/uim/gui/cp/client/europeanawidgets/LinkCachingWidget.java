/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.gui.cp.client.europeanawidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import eu.europeana.uim.gui.cp.client.services.ReportingServiceAsync;

/**
 * Displays progress information and provides report printing functionality
 * for the whole link caching process
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 *
 * 29 Jul 2012
 */
public class LinkCachingWidget extends AbstractReportingWidget{

	
	 /**
     * Creates a new instance of this class.
     * 
     * @param reportService
     * @param widgetName
     * @param workflows
     * @param reportName
     * @param outputFormats
     */
    public LinkCachingWidget(ReportingServiceAsync reportService, String widgetName,
                               String[] workflows, String reportName, String[] outputFormats) {
        super(reportService, widgetName, workflows, reportName, outputFormats);
        super.IGNORED_KEY = "thumbler.ignored";
        super.PROCESSED_KEY = "thumbler.processed";
        super.SUBMITTED_KEY = "thumbler.submitted";
    }

    @Override
    protected void asyncOnInitialize(final AsyncCallback<Widget> callback) {
        GWT.runAsync(LinkCachingWidget.class, new RunAsyncCallback() {
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