package eu.europeana.uim.gui.cp.client.europeanawidgets;

import java.util.List;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.RichTextArea;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

import eu.europeana.uim.gui.cp.client.services.RetrievalServiceAsync;
import eu.europeana.uim.gui.cp.shared.validation.EdmFieldRecordDTO;
import eu.europeana.uim.gui.cp.shared.validation.EdmRecordDTO;
import eu.europeana.uim.gui.cp.shared.validation.FieldValueDTO;

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


        final TextArea xml = new TextArea();
        xml.setCharacterWidth(100);
        xml.setVisibleLines(20);
        tabPanel.add(xml, "XML");

        final RichTextArea search = new RichTextArea();
        search.setPixelSize(900, 600);
        //setCharacterWidth(100);
        //search.setVisibleLines(20);
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

        
        retrievalServiceAsync.getSearchRecord(recordId, new AsyncCallback<EdmRecordDTO>() {
            @Override
            public void onFailure(Throwable caught) {
                caught.printStackTrace();
            }

            @Override
            public void onSuccess(EdmRecordDTO result) {
                search.setHTML(convertToTable(result));
            }
        });
        
       
    }
    private String convertToTable(EdmRecordDTO result){
    	EdmFieldRecordDTO solrFields= result.getSolrRecord();
    	EdmFieldRecordDTO mongoFields = result.getMongoRecord();
    	StringBuilder sb = new StringBuilder();
    	sb.append("<table border='1'><tr><td><b>SOLR Fields</b></td></tr>");
    	createTable(sb,solrFields);
    	sb.append("</table>\n<table border='1'><tr><td><b>MongoDB Fields</b></td></tr>");
    	createTable(sb,mongoFields);
    	sb.append("</table>");
    	return sb.toString();
    }
    
    private void createTable(StringBuilder sb,EdmFieldRecordDTO rec){
    	List<FieldValueDTO> solrFieldList = rec.getFieldValue();
    	for(FieldValueDTO fieldValueDTO:solrFieldList){
    		sb.append("<tr><td>"+fieldValueDTO.getFieldName()+"</td><td>");
    		List<String> values = fieldValueDTO.getFieldValue();
    		for(String value:values){
    			sb.append(value+"\n");
    		}
    		sb.append("</td></tr>");
    		
    	}
    }
}