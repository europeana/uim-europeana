package eu.europeana.uim.gui.cp.client.europeanawidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import eu.europeana.uim.gui.cp.client.IngestionWidget;
import eu.europeana.uim.gui.cp.client.services.PreviewServiceAsync;

public class PreviewCommitWidget extends IngestionWidget{

	@UiField (provided=true)
	FlowPanel previewCommit;
	private final PreviewServiceAsync previewService;
	public PreviewCommitWidget(String name,String description, PreviewServiceAsync previewService){
		super(name, description);
		this.previewService = previewService;
	}
	
	interface Binder extends UiBinder<Widget, PreviewCommitWidget> {
	}
	
	@Override
	public Widget onInitialize() {
		previewCommit = new FlowPanel();
		Label label = new Label("Make preview available in test portal");
		Button button = new Button("Make available");
		button.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				boolean really = Window.confirm("Do you really want to commit?\n It can take some time");
				if(really){
					previewService.commit(new AsyncCallback<Boolean>() {
						
						@Override
						public void onSuccess(Boolean result) {
							Window.alert("Command send correctly");
							
						}
						
						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
							
						}
					});
				}
			}
		});
		previewCommit.add(label);
		previewCommit.add(button);
		
		return previewCommit;
		
	}

	@Override
	protected void asyncOnInitialize(final AsyncCallback<Widget> callback) {
		GWT.runAsync(PreviewCommitWidget.class,
				new RunAsyncCallback() {
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
