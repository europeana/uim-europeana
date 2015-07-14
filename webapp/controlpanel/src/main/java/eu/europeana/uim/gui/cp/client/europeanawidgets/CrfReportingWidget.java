package eu.europeana.uim.gui.cp.client.europeanawidgets;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;
import eu.europeana.uim.gui.cp.client.IngestionWidget;
import eu.europeana.uim.gui.cp.client.services.CrfReportProxyAsync;
import eu.europeana.uim.gui.cp.client.services.RepositoryServiceAsync;
import eu.europeana.uim.gui.cp.shared.CRFReplyDTO;
import eu.europeana.uim.gui.cp.shared.CollectionDTO;
import eu.europeana.uim.gui.cp.shared.ProviderDTO;
import eu.europeana.uim.gui.cp.shared.validation.FailedRecordDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


/**
 * Created by ymamakis on 9-7-15.
 */
public class CrfReportingWidget extends IngestionWidget {
    /**
     * Box with providers for selection
     */
    @UiField
    ListBox providerBox;
    /**
     * Box with collections for selection
     */
    @UiField
    ListBox collectionBox;

    /**
     *
     */
    @UiField
    Button searchButton;

    ListDataProvider<CRFReplyDTO> sortProvider;
    private final RepositoryServiceAsync repositoryService;
    private final CrfReportProxyAsync crfReportProxy;

    @UiField(provided = true)
    SimplePager pager;
    @UiField(provided = true)
    CellTable<CRFReplyDTO> cellTable;
    private final List<CRFReplyDTO> records = new ArrayList<>();
    private final List<ProviderDTO> providers = new ArrayList<>();
    private final List<CollectionDTO> collections = new ArrayList<>();


    public CrfReportingWidget(String name, String description,RepositoryServiceAsync repositoryService,CrfReportProxyAsync crfReportProxy) {
        super(name, description);
        this.repositoryService = repositoryService;
        this.crfReportProxy = crfReportProxy;
    }

    @Override
    public Widget onInitialize() {
        cellTable = new CellTable<>(CRFReplyDTO.KEY_PROVIDER);
        cellTable.setWidth("100%", true);
        cellTable.setPageSize(20);
        cellTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {
            @Override
            public void onRangeChange(RangeChangeEvent arg0) {
                if (collections.size() > 0
                        && collectionBox.getSelectedIndex() >= 0
                        && collectionBox.getSelectedIndex() < collections
                        .size()) {
                    CollectionDTO collection = collections.get(collectionBox
                            .getSelectedIndex());
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
        sortProvider = new ListDataProvider<>();
        sortProvider.setList(records);
        //set the cellTable as the display for the provider!
        sortProvider.addDataDisplay(cellTable);

        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(SimplePager.TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(cellTable);

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
                        String n1 = o1.getCountry() == null ? "eu: "
                                + o1.getName() : o1.getCountry() + ": "
                                + o1.getName();
                        String n2 = o2.getCountry() == null ? "eu: "
                                + o2.getName() : o2.getCountry() + ": "
                                + o2.getName();

                        return n1.compareTo(n2);
                    }
                });

                providers.addAll(result);

                for (ProviderDTO provider : providers) {
                    String name = provider.getCountry() == null ? "eu: "
                            + provider.getName() : provider.getCountry() + ": "
                            + provider.getName();
                    providerBox.addItem(name);
                }

                if (providers.size() == 0) {
                    return;
                }

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
                                Collections.sort(result,
                                        new Comparator<CollectionDTO>() {
                                            @Override
                                            public int compare(
                                                    CollectionDTO o1,
                                                    CollectionDTO o2) {
                                                String n1 = o1.getMnemonic()
                                                        + ": " + o1.getName();
                                                String n2 = o2.getMnemonic()
                                                        + ": " + o2.getName();
                                                return n1.compareTo(n2);
                                            }
                                        });

                                collections.addAll(result);

                                for (CollectionDTO collection : collections) {
                                    String name = collection.getMnemonic()
                                            + ": " + collection.getName();
                                    collectionBox.addItem(name);
                                }
                            }
                        });
            }
        });

        providerBox.addChangeHandler(new ChangeHandler() {
            @Override
            public void onChange(ChangeEvent event) {
                ProviderDTO provider = providers.get(providerBox
                        .getSelectedIndex());

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

                                Collections.sort(result,
                                        new Comparator<CollectionDTO>() {
                                            @Override
                                            public int compare(
                                                    CollectionDTO o1,
                                                    CollectionDTO o2) {
                                                String n1 = o1.getMnemonic()
                                                        + ": " + o1.getName();
                                                String n2 = o2.getMnemonic()
                                                        + ": " + o2.getName();
                                                return n1.compareTo(n2);
                                            }
                                        });

                                collections.addAll(result);

                                for (CollectionDTO collection : collections) {
                                    String name = collection.getMnemonic()
                                            + ": " + collection.getName();
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
                if (collections.size() > 0
                        && collectionBox.getSelectedIndex() >= 0
                        && collectionBox.getSelectedIndex() < collections
                        .size()) {
                    CollectionDTO collection = collections.get(collectionBox
                            .getSelectedIndex());
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

    }

    private void updateRows(CollectionDTO collection, int start, int length) {
        crfReportProxy.getByCollection(null, new AsyncCallback<List<CRFReplyDTO>>() {
            @Override
            public void onFailure(Throwable throwable) {

            }

            @Override
            public void onSuccess(List<CRFReplyDTO> crfReplyDTOs) {

            }
        });
    }

    @Override
    protected void asyncOnInitialize(final AsyncCallback<Widget> asyncCallback) {
        GWT.runAsync(CrfReportingWidget.class, new RunAsyncCallback() {

            @Override
            public void onFailure(Throwable caught) {
                asyncCallback.onFailure(caught);
            }

            @Override
            public void onSuccess() {
                asyncCallback.onSuccess(onInitialize());
            }
        });
    }

    interface Binder extends UiBinder<Widget, CrfReportingWidget> {
    }
}
