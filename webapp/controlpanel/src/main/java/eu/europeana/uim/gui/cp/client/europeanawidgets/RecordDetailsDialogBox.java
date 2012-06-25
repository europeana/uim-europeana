package eu.europeana.uim.gui.cp.client.europeanawidgets;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import eu.europeana.uim.gui.cp.client.services.RetrievalServiceAsync;

/**
 * Shows raw, xml and search index representation of a record.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @since May 10, 2011
 */
public class RecordDetailsDialogBox extends DialogBox {
    /**
     * Creates a new instance of this class.
     * 
     * @param recordId
     * @param retrievalServiceAsync
     */
    public RecordDetailsDialogBox(String recordId, final RetrievalServiceAsync retrievalServiceAsync) {
        setText("Metadata Record Details");
        setGlassEnabled(true);
        setAnimationEnabled(true);
        center();

        VerticalPanel dialogContents = new VerticalPanel();
        dialogContents.setSpacing(4);
        setWidget(dialogContents);

        TabPanel tabPanel = new TabPanel();

        final TextArea raw = new TextArea();
        raw.setCharacterWidth(100);
        raw.setVisibleLines(20);
        tabPanel.add(raw, "Raw");

        final TextArea xml = new TextArea();
        xml.setCharacterWidth(100);
        xml.setVisibleLines(20);
        tabPanel.add(xml, "XML");

        final TextArea search = new TextArea();
        search.setCharacterWidth(100);
        search.setVisibleLines(20);
        tabPanel.add(search, "Search");

        tabPanel.selectTab(0);

        dialogContents.add(tabPanel);

        Button closeButton = new Button("Close", new ClickHandler() {
            @Override
            public void onClick(ClickEvent event) {
                hide();
            }
        });
        dialogContents.add(closeButton);
        dialogContents.setCellHorizontalAlignment(closeButton, HasHorizontalAlignment.ALIGN_RIGHT);

        retrievalServiceAsync.getRawRecord(recordId, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(String result) {
                raw.setText(result);
            }
        });

        retrievalServiceAsync.getXmlRecord(recordId, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(String result) {
                xml.setText(result);
            }
        });

        retrievalServiceAsync.getSearchRecord(recordId, new AsyncCallback<String>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(String result) {
                search.setText(result);
            }
        });
    }
}