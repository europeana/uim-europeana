package eu.europeana.uim.gui.cp.client.europeanawidgets;

import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitCompleteHandler;
import com.google.gwt.user.client.ui.FormPanel.SubmitEvent;
import com.google.gwt.user.client.ui.FormPanel.SubmitHandler;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.PopupPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;

import eu.europeana.uim.gui.cp.client.IngestionWidget;
import eu.europeana.uim.gui.cp.client.services.CollectionManagementProxyAsync;
import eu.europeana.uim.gui.cp.client.services.RepositoryServiceAsync;
import eu.europeana.uim.gui.cp.client.services.ResourceServiceAsync;
import eu.europeana.uim.gui.cp.shared.CollectionMappingDTO;

public class CollectionManagement extends IngestionWidget {

	@UiField(provided = true)
	SplitLayoutPanel collectionManagement;
	CellTable<CollectionMappingDTO> mappedCollections;
	CellList<CollectionMappingDTO> csvCollections;
	Button save;
	
	private final CollectionManagementProxyAsync collectionManagementProxy;

	public CollectionManagement(String name, String description,
			CollectionManagementProxyAsync collectionManagementProxy) {
		super(name, description);
		this.collectionManagementProxy = collectionManagementProxy;
	}

	public CollectionManagement(RepositoryServiceAsync repositoryService,
			ResourceServiceAsync resourceService,
			CollectionManagementProxyAsync collectionManagementProxy) {
		super("Import Collection", "Import Collection");
		this.collectionManagementProxy = collectionManagementProxy;
	}

	interface Binder extends UiBinder<Widget, CollectionManagement> {
	}

	@Override
	public Widget onInitialize() {
		collectionManagement = new SplitLayoutPanel();
		collectionManagement.setWidth("32em");
		collectionManagement.addNorth(createCsvUploadForm(), 150);
		collectionManagement.addSouth(createMappedCollectionTable(), 150);
		collectionManagement.add(mapOneCollectionPanel());

		Binder uiBinder = GWT.create(Binder.class);
		Widget widget = (Widget) uiBinder.createAndBindUi(this);
		return widget;
	}

	private ScrollPanel createMappedCollectionTable() {
		ScrollPanel scroll = new ScrollPanel();
		ProvidesKey<CollectionMappingDTO> key  = new ProvidesKey<CollectionMappingDTO>() {
			
			@Override
			public Object getKey(CollectionMappingDTO arg0) {
				// TODO Auto-generated method stub
				return arg0.getOriginalCollection();
			}
		};
		mappedCollections = new CellTable<CollectionMappingDTO>(
				key);
		TextColumn<CollectionMappingDTO> originalColumn = new TextColumn<CollectionMappingDTO>() {

			@Override
			public String getValue(CollectionMappingDTO arg0) {
				// TODO Auto-generated method stub
				return arg0.getOriginalCollection();
			}
		};
		TextColumn<CollectionMappingDTO> newColumn = new TextColumn<CollectionMappingDTO>() {

			@Override
			public String getValue(CollectionMappingDTO arg0) {
				// TODO Auto-generated method stub
				return arg0.getNewCollection();
			}
		
		};
		
		mappedCollections.addColumn(originalColumn, "Original Collection ID");
		mappedCollections.addColumn(newColumn, "New Collection ID");
		scroll.add(mappedCollections);
		fillMappedCollections();
		return scroll;
	}

	private void fillMappedCollections() {

		collectionManagementProxy
		.retrieveCollections(new AsyncCallback<List<CollectionMappingDTO>>() {

			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();

			}

			@Override
			public void onSuccess(List<CollectionMappingDTO> result) {
				if (result.size() > 0) {
					mappedCollections.setRowData(result);
				} else {
					mappedCollections
							.setEmptyTableWidget(new CellList<List<CollectionMappingDTO>>(
									null));
				}

			}
		});
	}

	private FlexTable mapOneCollectionPanel() {
		FlexTable flex = new FlexTable();
		final TextBox oldCollection = new TextBox();
		final TextBox newCollection = new TextBox();
		Button save = new Button("Save Collection");
		save.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				if (oldCollection.getValue().length() > 0
						&& newCollection.getValue().length() > 0) {
					CollectionMappingDTO collection = new CollectionMappingDTO();
					collection.setNewCollection(newCollection.getValue());
					collection.setOriginalCollection(oldCollection.getValue());
					collectionManagementProxy.saveOneCollection(collection,
							new AsyncCallback<Boolean>() {

								@Override
								public void onFailure(Throwable caught) {
									caught.printStackTrace();

								}

								@Override
								public void onSuccess(Boolean result) {
									Window.alert("Collection Saved");

								}
							});
				} else {
					final PopupPanel popup = new PopupPanel();
					popup.setTitle("Information is missing");
					Button close = new Button("OK");
					close.addClickHandler(new ClickHandler() {

						@Override
						public void onClick(ClickEvent event) {
							popup.hide();

						}
					});
					Label label = new Label();
					label.setText("Value(s) missing: "
							+ (oldCollection.getValue().length() == 0 ? "Old collection id missing\n"
									: "")
							+ (newCollection.getValue().length() == 0 ? "New collection id missing\n"
									: ""));
					popup.add(label);
					popup.add(close);
					popup.show();

				}

			}

		});
		flex.setWidget(0, 0, new Label("Old CollectioID"));
		flex.setWidget(0, 1, oldCollection);
		flex.setWidget(1, 0, new Label("New CollectionID"));
		flex.setWidget(1, 1, newCollection);
		flex.setWidget(2, 0, save);
		return flex;
	}

	private FormPanel createCsvUploadForm() {
		final FormPanel form = new FormPanel();
		form.setEncoding(FormPanel.ENCODING_MULTIPART);
		form.setMethod(FormPanel.METHOD_POST);
		form.setAction(GWT.getHostPageBaseURL()+"EuropeanaIngestionControlPanel/uploadservlet");
		FlexTable flex = new FlexTable();
		Label delLabel = new Label("Select delimiter");
		final TextBox delimiter = new TextBox();
		delimiter.setName("delimiter");
		flex.setWidget(0, 0, delLabel);
		flex.setWidget(0, 1, delimiter);
		Label label = new Label("Select csv to upload");
		flex.setWidget(1, 0, label);
		final FileUpload uploadCsv = new FileUpload();
		uploadCsv.setName("fileUpload");

		flex.setWidget(2, 0, uploadCsv);
		Button submit = new Button("Upload File");

		submit.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {

				form.submit();
			}
		});
		form.addSubmitHandler(new SubmitHandler() {

			@Override
			public void onSubmit(SubmitEvent event) {
				Window.alert("Submitting");

			}
		});
		form.addSubmitCompleteHandler(new SubmitCompleteHandler() {
			
			@Override
			public void onSubmitComplete(SubmitCompleteEvent event) {

				collectionManagementProxy.retrieveCsvCollections(
						uploadCsv.getFilename(), delimiter.getValue(),
						new AsyncCallback<List<CollectionMappingDTO>>() {

							@Override
							public void onFailure(Throwable caught) {
								caught.printStackTrace();
							}

							@Override
							public void onSuccess(
									List<CollectionMappingDTO> result) {
							
								final PopupPanel popup = new PopupPanel(false);
								final List<CollectionMappingDTO> resultCopy = result;
								popup.setGlassEnabled(true);
								popup.setPopupPosition(300, 200);
								popup.setPixelSize(300, 300);
								popup.setVisible(true);
								final ScrollPanel popupContents = new ScrollPanel();
								FlexTable table = new FlexTable();
								
								
								save = new Button(
										"Save collections");
								save.addClickHandler(new ClickHandler() {

									@Override
									public void onClick(ClickEvent event) {
										collectionManagementProxy.saveCollections(
												resultCopy,
												new AsyncCallback<Boolean>() {

													@Override
													public void onSuccess(
															Boolean result) {
														Window.alert("Collections Succesfully Saved");
													}

													@Override
													public void onFailure(
															Throwable caught) {
														
														Window.alert("Collections not saved");
														

													}
												});
										fillMappedCollections();
										popup.hide();
									}
									
								});
								csvCollections = new CellList<CollectionMappingDTO>(
										new CollectionCell());
								if (result.size() > 0) {
									csvCollections.setRowData(result);
								} else {
									csvCollections
											.setEmptyListWidget(new CellList<CollectionMappingDTO>(
													(new CollectionCell())));
								}
								
								table.setWidget(0, 0, csvCollections);
								table.setWidget(1, 0, save);
								table.setBorderWidth(1);
								popupContents.add(table);
								popup.add(popupContents);
								popup.show();
							}
						});

			}

		});
		flex.setWidget(2, 1, submit);
		form.add(flex);
		return form;
	}

	@Override
	protected void asyncOnInitialize(final AsyncCallback<Widget> callback) {
		GWT.runAsync(CollectionManagement.class, new RunAsyncCallback() {
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

	private class CollectionCell extends AbstractCell<CollectionMappingDTO> {
		@Override
		public void render(com.google.gwt.cell.client.Cell.Context context,
				CollectionMappingDTO value, SafeHtmlBuilder sb) {
			sb.appendHtmlConstant("<table border=\"1\">");
			sb.appendHtmlConstant("<tr><td>");
			sb.appendHtmlConstant(value.getOriginalCollection() + "</td><td>"
					+ value.getNewCollection() + "</td></tr></table>");

		}
	}
}
