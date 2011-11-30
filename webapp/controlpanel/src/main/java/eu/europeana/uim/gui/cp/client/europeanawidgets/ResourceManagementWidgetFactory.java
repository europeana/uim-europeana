/**
 * 
 */
package eu.europeana.uim.gui.cp.client.europeanawidgets;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import eu.europeana.uim.gui.cp.client.services.IntegrationSeviceProxyAsync;
import eu.europeana.uim.gui.cp.client.utils.RepoxOperationType;
import eu.europeana.uim.gui.cp.shared.IntegrationStatusDTO;
import eu.europeana.uim.gui.cp.shared.RepoxExecutionStatusDTO;
import eu.europeana.uim.gui.cp.shared.IntegrationStatusDTO.TYPE;

/**
 * 
 * 
 * @author Georgios Markakis
 */
public class ResourceManagementWidgetFactory {

	
    final IntegrationSeviceProxyAsync integrationservice;
	
	public ResourceManagementWidgetFactory(IntegrationSeviceProxyAsync integrationservice){
		this.integrationservice = integrationservice;
	}
	

	/**
	 * Creates the integration info sub panels displaying integration-specific information
	 * 
	 * @param status
	 */
	public void generateIntergationInfoPanel(TabLayoutPanel tabInfoSubPanel,IntegrationStatusDTO status,
			IntegrationSeviceProxyAsync integrationservice){

		if(status.getType().equals(TYPE.UNIDENTIFIED)){
			tabInfoSubPanel.setVisible(false);
		}
		else{
			tabInfoSubPanel.setVisible(true);
			tabInfoSubPanel.clear();
			
			tabInfoSubPanel.add(createGeneralInfoTabContent(status), new HTML("Resource Info"));
			tabInfoSubPanel.add(createSugarCrmTabContent(status), new HTML("SugarCRM"));
			tabInfoSubPanel.add(createRepoxTabContent(status), new HTML("Repox"));
			tabInfoSubPanel.add(createMintTabContent(status), new HTML("Mint"));
			tabInfoSubPanel.add(createResourcePropertiesTabContent(status), new HTML("Resource Properties"));
		}


		
	}
	
	
	/**
	 * @param status
	 * @return
	 */
	private  ScrollPanel createGeneralInfoTabContent(IntegrationStatusDTO status){


		ScrollPanel generalInfoContainer =new ScrollPanel();
		FlexTable generalInfoTable = new FlexTable();
		generalInfoContainer.add(generalInfoTable);
				
		
	    	generalInfoTable.setWidget(0, 0, new HTML("Type:"));
	    	generalInfoTable.setWidget(0, 1, new HTML(status.getType().toString()));
	    	
	    	generalInfoTable.setWidget(1, 0, new HTML("Name:"));          	
	    	generalInfoTable.setWidget(1, 1, new HTML(status.getInfo()));
	    	
	    	generalInfoTable.setWidget(2, 0, new HTML("Identifier:"));
	    	generalInfoTable.setWidget(2, 1, new HTML(status.getId()));
	    	
	    	generalInfoTable.setWidget(3, 0, new HTML("SugarCRM Link:"));
	    	
	    	if(status.getSugarCRMID() == null){
	    		generalInfoTable.setWidget(3, 1, new HTML("Not represented in SugarCRM")); 
	    	}
	    	else{
	    		
				Anchor hyper = new Anchor();
				hyper.setName("SugarCRMLink");
				hyper.setText("Click here to edit information in SugarCRM.");
				hyper.setHref(status.getSugarURL());
				hyper.setTarget("NEW");
				generalInfoTable.setWidget(3, 1, hyper);
	   		
	    	}
	    	
	    	generalInfoTable.setWidget(4, 0, new HTML("Repox Link:"));
	    	
	    	if(status.getRepoxID() == null){
	    		generalInfoTable.setWidget(4, 1, new HTML("Not represented in Repox")); 
	    	}
	    	else{
	    		
				Anchor hyper = new Anchor();
				hyper.setName("RepoxLink");
				hyper.setText("Click here to edit REPOX configuration.");
				hyper.setHref(status.getRepoxURL());
				hyper.setTarget("TOP");
				
				generalInfoTable.setWidget(4, 1,hyper);  
	    	}

		
		
		switch(status.getType()){
		
		case COLLECTION:

			generalInfoTable.setWidget(5, 0, new HTML("Harvesting Status:"));
			generalInfoTable.setWidget(5, 1, new HTML(status.getHarvestingStatus().getStatus().getDescription()));         	
			generalInfoTable.setWidget(6, 0, new HTML("<hr></hr>"));           		    	
			generalInfoTable.setWidget(7, 0, new HTML("Permitted operations:"));
		
			GeneralInfoCommandMenu command = this.new GeneralInfoCommandMenu(status);
			
			generalInfoTable.setWidget(7, 1, command.getOperationsListBox());
			// Create a table to layout the content

			
			generalInfoTable.setWidget(7, 2, command.generateRepoxCommandButton());
			
			break;
			
		case PROVIDER:

		  }
		
		return generalInfoContainer;

	}

	
	/**
	 * @param status
	 * @returncreateResourcePropertiesTabContent
	 */
	private  ScrollPanel createResourcePropertiesTabContent(IntegrationStatusDTO status){
		
		
		ScrollPanel container =new ScrollPanel();

		FlexTable resourcePropertiesTable = new FlexTable();
		container.add(resourcePropertiesTable);
		
		HashMap<String,String> propertiesMap =  status.getResourceProperties();
		int i =0;
		
		Iterator it = propertiesMap.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pairs = (Map.Entry)it.next();
	        
	        resourcePropertiesTable.setWidget(i, 0, new HTML(pairs.getKey().toString()));
	        resourcePropertiesTable.setWidget(i, 1, new HTML(pairs.getValue().toString()));
	   
	        it.remove(); // avoids a ConcurrentModificationException
	        i++;
	    }
	    
		return container;
	}
	
	
	
	/**
	 * @param status
	 * @return
	 */
	private  ScrollPanel createSugarCrmTabContent(IntegrationStatusDTO status){
		ScrollPanel container =new ScrollPanel();

		FlexTable resourcePropertiesTable = new FlexTable();
		container.add(resourcePropertiesTable);
		
		
		return container;
	}
	
	
	private  ScrollPanel createRepoxTabContent(IntegrationStatusDTO status){
		ScrollPanel container =new ScrollPanel();

		FlexTable resourcePropertiesTable = new FlexTable();
		container.add(resourcePropertiesTable);
		
		resourcePropertiesTable.setWidget(0, 0, new HTML("Type:"));
		resourcePropertiesTable.setWidget(0, 1, new HTML(status.getType().toString()));
		return container;
	}
	
	private  ScrollPanel createMintTabContent(IntegrationStatusDTO status){
		ScrollPanel container =new ScrollPanel();

		FlexTable resourcePropertiesTable = new FlexTable();
		container.add(resourcePropertiesTable);
		return container;
	}
	
	
	

	
	private class GeneralInfoCommandMenu{
		public  ListBox operationsListBox;



		public  DialogBox operationDialog;

		private final IntegrationStatusDTO status;
		
		GeneralInfoCommandMenu(IntegrationStatusDTO status){

			this.status = status;
			
			operationsListBox = new ListBox(false);
			
			operationsListBox.addItem(RepoxOperationType.INITIATE_COMPLETE_HARVESTING.getDescription());   
			operationsListBox.addItem(RepoxOperationType.INITIATE_INCREMENTAL_HARVESTING.getDescription()); 
	  		operationsListBox.addItem(RepoxOperationType.VIEW_HARVEST_LOG.getDescription());  
	  		
	  		operationDialog = new DialogBox();
	  		operationDialog.setModal(true);
			
			ScrollPanel dialogContents = new ScrollPanel();
			operationDialog.setWidget(dialogContents);
		}
		
		
		/**
		 * 
		 * 
		 * @return
		 */
		public Button generateRepoxCommandButton(){
			Button actionButton = new Button("Execute");
			actionButton.addClickHandler(new ClickHandler() {
			 
			
				
				@Override
				public void onClick(ClickEvent event) {

					
					operationDialog.center();
					
					int index = operationsListBox.getSelectedIndex();
					
					String value = operationsListBox.getValue(index);
					
					RepoxOperationType optype = null;
					
					if (value.equals(RepoxOperationType.INITIATE_COMPLETE_HARVESTING.getDescription())){
						optype = RepoxOperationType.INITIATE_COMPLETE_HARVESTING;
					}
					else if(value.equals(RepoxOperationType.SCHEDULE_HARVESTING.getDescription())){
						optype = RepoxOperationType.SCHEDULE_HARVESTING;
					}
					else if(value.equals(RepoxOperationType.VIEW_HARVEST_LOG.getDescription())){
						optype = RepoxOperationType.VIEW_HARVEST_LOG;
					}
					
					
					if(optype != null){
					integrationservice.performRepoxRemoteOperation(optype, status.getId(), new AsyncCallback<RepoxExecutionStatusDTO>(){

						@Override
						public void onFailure(Throwable caught) {
							operationDialog.clear();
							
							VerticalPanel vp = new VerticalPanel();
							vp.add(new HTML("<verbatim>"+caught.getMessage()+"</verbatim>"));
							vp.add(createOperationsCloseButton());
							operationDialog.setText("An Unclassified Exception Occured (send this to you know whom)");
							operationDialog.setWidget(vp);

						}

						@Override
						public void onSuccess(RepoxExecutionStatusDTO result) {

							operationDialog.clear();
							
							VerticalPanel vp = new VerticalPanel();
							vp.add(new HTML("<verbatim>"+result.getLogMessage()+"</verbatim>"));
							vp.add(createOperationsCloseButton());
							operationDialog.setWidget(vp);
							operationDialog.setText(result.getOperationMessage());

						}	
					});
					}
				}
			});
			
			return actionButton;
		}
		
		
		private Button createOperationsCloseButton(){
			
			Button closebutton = new Button("Close");
			
			closebutton.addClickHandler(new ClickHandler() {

				@Override
				public void onClick(ClickEvent event) {
					operationDialog.hide();
					
				}
				
			});
			
			return closebutton;
		}
		
		
		public ListBox getOperationsListBox() {
			return operationsListBox;
		}


		public void setOperationsListBox(ListBox operationsListBox) {
			this.operationsListBox = operationsListBox;
		}
	}
	
	


	
	
	

}
