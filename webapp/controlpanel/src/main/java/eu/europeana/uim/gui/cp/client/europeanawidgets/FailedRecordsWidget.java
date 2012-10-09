package eu.europeana.uim.gui.cp.client.europeanawidgets;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;

import eu.europeana.uim.gui.cp.client.IngestionWidget;
import eu.europeana.uim.gui.cp.client.services.FailedRecordServiceAsync;
import eu.europeana.uim.gui.cp.client.services.RepositoryServiceAsync;
import eu.europeana.uim.gui.cp.shared.CollectionDTO;
import eu.europeana.uim.gui.cp.shared.ProviderDTO;
import eu.europeana.uim.gui.cp.shared.validation.FailedRecordDTO;
import eu.europeana.uim.gui.cp.shared.validation.FailedRecordResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.MetaDataRecordDTO;

public class FailedRecordsWidget extends IngestionWidget{
	 
	
	/**
     * Box with providers for selection
     */
    @UiField
    ListBox                               providerBox;
    /**
     * Box with collections for selection
     */
    @UiField
    ListBox                               collectionBox;
	
	/**
	 * 
	 */
	@UiField
	Button								   searchButton;
	
	/**
     * The main CellTable.
     */
    @UiField(provided = true)
    CellTable<FailedRecordDTO>          cellTable;
   
    interface Binder extends UiBinder<Widget, FailedRecordsWidget> {
    }

    private final RepositoryServiceAsync  repositoryService;
    private final FailedRecordServiceAsync   failedRecordRetrievalService;
    private final List<FailedRecordDTO> records     = new ArrayList<FailedRecordDTO>();
    private final List<ProviderDTO>       providers   = new ArrayList<ProviderDTO>();
    private final List<CollectionDTO>     collections = new ArrayList<CollectionDTO>();
    
	public FailedRecordsWidget(String name, String description, RepositoryServiceAsync repositoryService, FailedRecordServiceAsync retrievalService) {
		super(name, description);
		this.repositoryService = repositoryService;
		this.failedRecordRetrievalService = retrievalService;

	}

	@Override
	public Widget onInitialize() {
		 
		cellTable = new CellTable<FailedRecordDTO>();
        cellTable.setWidth("100%", true);
        cellTable.setPageSize(5);
        cellTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {
            @Override
            public void onRangeChange(RangeChangeEvent arg0) {
                if (collections.size() > 0 && collectionBox.getSelectedIndex() >= 0 &&
                    collectionBox.getSelectedIndex() < collections.size()) {
                    CollectionDTO collection = collections.get(collectionBox.getSelectedIndex());
                    if (collection != null) {
                        Range range = cellTable.getVisibleRange();
                        int start = range.getStart();
                        int length = range.getLength();
                        updateRows(collection, start, length);
                    }
                } else {
                    Window.alert("Please select a provider and collection to browse the repository!");
                }
            }
        });
        initTableColumns();
		Binder uiBinder = GWT.create(Binder.class);
	        Widget widget = uiBinder.createAndBindUi(this);

	        repositoryService.getProviders(new AsyncCallback<List<ProviderDTO>>() {
	            @Override
	            public void onFailure(Throwable caught) {
	                caught.printStackTrace();
	            }

	            @Override
	            public void onSuccess(List<ProviderDTO> result) {
	                providers.clear();
	                providerBox.clear();
	                
	                
	                Collections.sort(result, new Comparator<ProviderDTO>() {
	                    @Override
	                    public int compare(ProviderDTO o1, ProviderDTO o2) {
	                        String n1 = o1.getCountry() == null ? "eu: " + o1.getName()
	                                : o1.getCountry() + ": " + o1.getName();
	                        String n2 = o2.getCountry() == null ? "eu: " + o2.getName()
	                                : o2.getCountry() + ": " + o2.getName();

	                        return n1.compareTo(n2);
	                    }
	                });
	                
	                providers.addAll(result);
	                
	                for (ProviderDTO provider : providers) {
	                    String name = provider.getCountry() == null ? "eu: " + provider.getName()
	                            : provider.getCountry() + ": " + provider.getName();
	                    providerBox.addItem(name);
	                }

	                if (providers.size() == 0) { return; }

	                repositoryService.getCollections(providers.get(0).getId(),
	                        new AsyncCallback<List<CollectionDTO>>() {
	                            @Override
	                            public void onFailure(Throwable caught) {
	                                caught.printStackTrace();
	                            }

	                            @Override
	                            public void onSuccess(List<CollectionDTO> result) {
	                                collections.clear();
	                                collectionBox.clear();
	                                Collections.sort(result, new Comparator<CollectionDTO>() {
	                                    @Override
	                                    public int compare(CollectionDTO o1, CollectionDTO o2) {
	                                        String n1 = o1.getMnemonic() + ": " + o1.getName();
	                                        String n2 = o2.getMnemonic() + ": " + o2.getName();
	                                        return n1.compareTo(n2);
	                                    }
	                                });

	                                
	                                collections.addAll(result);

	                                for (CollectionDTO collection : collections) {
	                                    String name = collection.getMnemonic() + ": " + collection.getName();
	                                    collectionBox.addItem(name);
	                                }
	                            }
	                        });
	            }
	        });

	        providerBox.addChangeHandler(new ChangeHandler() {
	            @Override
	            public void onChange(ChangeEvent event) {
	                ProviderDTO provider = providers.get(providerBox.getSelectedIndex());

	                repositoryService.getCollections(provider.getId(),
	                        new AsyncCallback<List<CollectionDTO>>() {
	                            @Override
	                            public void onFailure(Throwable caught) {
	                                caught.printStackTrace();
	                            }

	                            @Override
	                            public void onSuccess(List<CollectionDTO> result) {
	                                collections.clear();
	                                collectionBox.clear();
	                                
	                                Collections.sort(result, new Comparator<CollectionDTO>() {
	                                    @Override
	                                    public int compare(CollectionDTO o1, CollectionDTO o2) {
	                                        String n1 = o1.getMnemonic() + ": " + o1.getName();
	                                        String n2 = o2.getMnemonic() + ": " + o2.getName();
	                                        return n1.compareTo(n2);
	                                    }
	                                });

	                                
	                                collections.addAll(result);

	                                for (CollectionDTO collection : collections) {
	                                    String name = collection.getMnemonic() + ": " + collection.getName();
	                                    collectionBox.addItem(name);
	                                }
	                            }
	                        });
	            }
	        });
	        searchButton.setText("Select");
	        searchButton.addClickHandler(new ClickHandler() {
	            @Override
	            public void onClick(ClickEvent arg0) {
	                if (collections.size() > 0 && collectionBox.getSelectedIndex() >= 0 &&
	                    collectionBox.getSelectedIndex() < collections.size()) {
	                    CollectionDTO collection = collections.get(collectionBox.getSelectedIndex());
	                    if (collection != null) {
	                        updateRows(collection, 0, 20);
	                    }
	                } else {
	                    Window.alert("Please select a provider and collection to browse the repository!");
	                }
	            }

				
	        });

	        return widget;
	}
	
	
	private void initTableColumns() {

		 Column<FailedRecordDTO, String> oridColumn = new Column<FailedRecordDTO, String>(
	                new TextCell()) {

	            @Override
	            public String getValue(FailedRecordDTO object) {
	                return object.getOriginalId();

	            }
	        };
	        cellTable.addColumn(oridColumn, "Original Id");
	        cellTable.setColumnWidth(oridColumn, 10, Unit.PCT);

	        // Title
	        Column<FailedRecordDTO, String> colidColumn = new Column<FailedRecordDTO, String>(
	                new TextCell()) {
	            @Override
	            public String getValue(FailedRecordDTO object) {
	                return object.getCollectionId();
	            }
	        };
	        cellTable.addColumn(colidColumn, "Collection Id");
	        cellTable.setColumnWidth(colidColumn, 10, Unit.PCT);

	        // Creator
	        Column<FailedRecordDTO, String> eidColumn = new Column<FailedRecordDTO, String>(
	                new TextCell()) {
	            @Override
	            public String getValue(FailedRecordDTO object) {
	                return object.getEuropeanaId();
	            }
	        };
	        cellTable.addColumn(eidColumn, "Europeana Id");
	        cellTable.setColumnWidth(eidColumn, 10, Unit.PCT);

	        // Contributor
	        Column<FailedRecordDTO, String> contributorColumn = new Column<FailedRecordDTO, String>(
	                new TextCell()) {
	            @Override
	            public String getValue(FailedRecordDTO object) {
	                return object.getLookupState();
	            }
	        };
	        cellTable.addColumn(contributorColumn, "Failure Reason");
	        cellTable.setColumnWidth(contributorColumn, 30, Unit.PCT);

	        
	        // Show Record Details
	        Column<FailedRecordDTO, FailedRecordDTO> edmColumn = new Column<FailedRecordDTO, FailedRecordDTO>(
	                new ActionCell<FailedRecordDTO>("Content",
	                        new ActionCell.Delegate<FailedRecordDTO>() {
	                            @Override
	                            public void execute(FailedRecordDTO record) {
	                                final DialogBox updateBox = new DialogBox();
	                                updateBox.setText("Failed Record EDM");
	                                updateBox.setAnimationEnabled(true);
	                                updateBox.setGlassEnabled(true);
	                                updateBox.center();
	                                final TextArea xml = new TextArea();
	                                xml.setCharacterWidth(100);
	                                xml.setVisibleLines(20);
	                                updateBox.add(xml);
	                                xml.setText(record.getEdm());
	                                updateBox.show();
	                            }
	                        })) {
	            @Override
	            public FailedRecordDTO getValue(FailedRecordDTO object) {
	                return object;
	            }
	        };
	        cellTable.addColumn(edmColumn, "Show EDM");
	        cellTable.setColumnWidth(edmColumn, 4, Unit.PCT);


	}

	@Override
	protected void asyncOnInitialize(final AsyncCallback<Widget> callback) {
		 GWT.runAsync(FailedRecordsWidget.class, new RunAsyncCallback() {

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

	private void updateRows(final CollectionDTO collection, final int offset, final int maxSize) {

        failedRecordRetrievalService.getFailedRecords((String)collection.getId(), offset, maxSize,
                new AsyncCallback<FailedRecordResultDTO>() {
                    @Override
                    public void onFailure(Throwable caught) {
                        caught.printStackTrace();
                    }

                    @Override
                    public void onSuccess(FailedRecordResultDTO result) {
                        records.clear();
                        cellTable.setRowCount(result.getNumberRecords());
                        records.addAll(result.getRecords());
                        cellTable.setRowData(offset, records);
                    }
                });

    }
	
}
