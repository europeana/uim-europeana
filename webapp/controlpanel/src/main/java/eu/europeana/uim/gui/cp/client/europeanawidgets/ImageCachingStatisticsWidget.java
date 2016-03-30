package eu.europeana.uim.gui.cp.client.europeanawidgets;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;

import eu.europeana.uim.gui.cp.client.IngestionWidget;
import eu.europeana.uim.gui.cp.client.services.ExecutionServiceAsync;
import eu.europeana.uim.gui.cp.client.services.ImageCachingStatisticsServiceAsync;
import eu.europeana.uim.gui.cp.client.services.RepositoryServiceAsync;
import eu.europeana.uim.gui.cp.client.services.RetrievalServiceAsync;
import eu.europeana.uim.gui.cp.shared.CollectionDTO;
import eu.europeana.uim.gui.cp.shared.ExecutionDTO;
import eu.europeana.uim.gui.cp.shared.ProviderDTO;
import eu.europeana.uim.gui.cp.shared.validation.ImageCachingStatisticsDTO;
import eu.europeana.uim.gui.cp.shared.validation.ImageCachingStatisticsResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.MetaDataResultDTO;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public class ImageCachingStatisticsWidget extends IngestionWidget {
    
    private final ImageCachingStatisticsServiceAsync	imageCachingStatisticsRetrievalService;

    private final List<ImageCachingStatisticsDTO> 		statisticsReports     = new ArrayList<ImageCachingStatisticsDTO>();
    
    private ListDataProvider<ImageCachingStatisticsDTO> sortProvider;
    
    private final RepositoryServiceAsync	repositoryService;
    
    private final ExecutionServiceAsync		executionService;
    
    private final RetrievalServiceAsync		retrievalService;
    
    private final List<ProviderDTO>			providers   = new ArrayList<ProviderDTO>();
    
    private final List<CollectionDTO>		collections = new ArrayList<CollectionDTO>();
    
    private Date							dateProcessingStarted = null;		
    
    private int								recordsCount = 0;
    
    private ProviderDTO 					selectedProvider = null;
    
    private CollectionDTO 					selectedCollection = null;
    
    @UiField
    ListBox									providerBox;
    
    @UiField
    ListBox									collectionBox;

    @UiField
    Button									clearAllButton;
    
    @UiField
    Button									generatePDFButton;

	@UiField(provided = true)
	CellTable<ImageCachingStatisticsDTO> 	cellTable;

    @UiField(provided = true)
    SimplePager								pager;
    
    private static final short 				MAX_SIZE = 20; 
    
    
	interface Binder extends UiBinder<Widget, ImageCachingStatisticsWidget> {
	}

	public ImageCachingStatisticsWidget(
			String name,
			String description,
			RepositoryServiceAsync repositoryService,
			ExecutionServiceAsync executionService,
			RetrievalServiceAsync retrievalService,
			ImageCachingStatisticsServiceAsync imageCachingStatisticsRetrievalService) {
		super(name, description);
		this.repositoryService = repositoryService;
		this.executionService = executionService;
		this.retrievalService = retrievalService;
		this.imageCachingStatisticsRetrievalService = imageCachingStatisticsRetrievalService;
	}

	@Override
	public Widget onInitialize() {		
		cellTable = new CellTable<ImageCachingStatisticsDTO>(ImageCachingStatisticsDTO.KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setPageSize(MAX_SIZE);
		cellTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent arg0) {
				Range range = cellTable.getVisibleRange();
				int start = range.getStart();
				int length = range.getLength();
				updateRows(start, length);
			}
		});
		
		updateRows(0, MAX_SIZE);
		
		sortProvider = new ListDataProvider<ImageCachingStatisticsDTO>();
        sortProvider.setList(statisticsReports);
        sortProvider.addDataDisplay(cellTable);
        
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(cellTable);
		
		initTableColumns();
		Binder uiBinder = GWT.create(Binder.class);
		Widget widget = uiBinder.createAndBindUi(this);
		
		loadProviders();
		loadCollections();
		
		//if provider is selected
		providerBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (providerBox.getSelectedIndex() != 0) {
					//"selectedIndex-1" - because we have an empty item on 0-index!
					selectedProvider = providers.get(providerBox.getSelectedIndex() - 1);
					selectedCollection = null;
					dateProcessingStarted = null;
					recordsCount = 0;
					loadCollections();
				}
				generatePDFButton.setVisible(false);
				updateRows(0, MAX_SIZE);
			}
		});
		
		//if collection is selected
		collectionBox.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				if (collectionBox.getSelectedIndex() != 0) {
					selectedCollection = collections.get(collectionBox.getSelectedIndex() - 1);
					generatePDFButton.setVisible(true);
					setDateProcessingStarted();
					setRecordsCount();
				} else {
					selectedCollection = null;
					generatePDFButton.setVisible(false);
				}
				updateRows(0, MAX_SIZE);					
			}
		});
		
		clearAllButton.setText("Clear All Filters");
		clearAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				providerBox.setSelectedIndex(0);
				collectionBox.clear();
				selectedCollection = null;
				selectedProvider = null;
				generatePDFButton.setVisible(false);
				updateRows(0, MAX_SIZE);
			}

		});
		
		generatePDFButton.setVisible(false);
		generatePDFButton.setText("Statistics Report");
		generatePDFButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				Window.Location.assign(GWT.getHostPageBaseURL()
					+ "EuropeanaIngestionControlPanel/generatePDF?providerId=" + selectedProvider.getName() 
					+ "&collectionId=" + selectedCollection.getName() 
					+ (dateProcessingStarted != null ? "&dateStart=" + dateProcessingStarted.toString() : "")
					+ "&recordsCount=" + recordsCount);
			}

		});
		return widget;
	}

	/**
	 * Method populates the CellTable with CRF statistics.
	 */
	private void initTableColumns() {
		//Cell columns
		Column<ImageCachingStatisticsDTO, String> providerColumn = new Column<ImageCachingStatisticsDTO, String>(new TextCell()) {
			@Override
			public String getValue(ImageCachingStatisticsDTO object) {
				return object.getProviderId();
			}
		};
		cellTable.addColumn(providerColumn, "Provider");
		cellTable.setColumnWidth(providerColumn, 10, Unit.PCT);
		
		Column<ImageCachingStatisticsDTO, String> collectionColumn = new Column<ImageCachingStatisticsDTO, String>(new TextCell()) {
			@Override
			public String getValue(ImageCachingStatisticsDTO object) {
				return object.getCollectionId();
			}
		};
		cellTable.addColumn(collectionColumn, "Collection");
		cellTable.setColumnWidth(collectionColumn, 10, Unit.PCT);
		
		Column<ImageCachingStatisticsDTO, String> dateCreatedColumn = new Column<ImageCachingStatisticsDTO, String>(new TextCell()) {
			@Override
			public String getValue(ImageCachingStatisticsDTO object) {
				return object.getDateCreated();
			}
		};		
		cellTable.addColumn(dateCreatedColumn, "Date Created");
		cellTable.setColumnWidth(dateCreatedColumn, 5, Unit.PCT);
		
		Column<ImageCachingStatisticsDTO, String> dateCompletedColumn = new Column<ImageCachingStatisticsDTO, String>(new TextCell()) {
			@Override
			public String getValue(ImageCachingStatisticsDTO object) {
				return object.getDateCompleted();
			}
		};		
		cellTable.addColumn(dateCompletedColumn, "Date Completed");
		cellTable.setColumnWidth(dateCompletedColumn, 5, Unit.PCT);

		Column<ImageCachingStatisticsDTO, String> execIdColumn = new Column<ImageCachingStatisticsDTO, String>(new TextCell()) {
			@Override
			public String getValue(ImageCachingStatisticsDTO object) {
				return object.getExecutionId() + "";
			}
		};
		
		cellTable.addColumn(execIdColumn, "Execution Id");
		cellTable.setColumnWidth(execIdColumn, 4, Unit.PCT);
		
		Column<ImageCachingStatisticsDTO, String> failedJobsColumn = new Column<ImageCachingStatisticsDTO, String>(new TextCell()) {
			@Override
			public String getValue(ImageCachingStatisticsDTO object) {
		        return object.getFailedJobs() + "";	
			}
		};		
		cellTable.addColumn(failedJobsColumn, "Jobs Failed");
		cellTable.setColumnWidth(failedJobsColumn, 4, Unit.PCT);
		
		Column<ImageCachingStatisticsDTO, String> pedndingJobsColumn = new Column<ImageCachingStatisticsDTO, String>(new TextCell()) {
			@Override
			public String getValue(ImageCachingStatisticsDTO object) {
				return object.getPendingJobs() + "";
			}
		};
		cellTable.addColumn(pedndingJobsColumn, "Jobs Pending");
		cellTable.setColumnWidth(pedndingJobsColumn, 4, Unit.PCT);
		
		Column<ImageCachingStatisticsDTO, String> successfulJobsColumn = new Column<ImageCachingStatisticsDTO, String>(new TextCell()) {
			@Override
			public String getValue(ImageCachingStatisticsDTO object) {
				return object.getSuccessfulJobs() + "";
			}
		};
		cellTable.addColumn(successfulJobsColumn, "Jobs Successful");
		cellTable.setColumnWidth(successfulJobsColumn, 4, Unit.PCT);
		
		Column<ImageCachingStatisticsDTO, String> totalJobsColumn = new Column<ImageCachingStatisticsDTO, String>(new TextCell()) {
			@Override
			public String getValue(ImageCachingStatisticsDTO object) {
				return object.getTotalJobs() + "";
			}
		};
		cellTable.addColumn(totalJobsColumn, "Jobs Total");
		cellTable.setColumnWidth(totalJobsColumn, 4, Unit.PCT);
		
//		Column<ImageCachingStatisticsDTO, String> totalColumn = new Column<ImageCachingStatisticsDTO, String>(new TextCell()) {
//			@Override
//			public String getValue(ImageCachingStatisticsDTO object) {
//				return object.getTotal() + "";
//			}
//		};
//		cellTable.addColumn(totalColumn, "Total Number of Records");
//		cellTable.setColumnWidth(totalColumn, 4, Unit.PCT);		

		//Button columns
//		CustomActionCell pdfButtonCell = new CustomActionCell("PDF", new ActionCell.Delegate<ImageCachingStatisticsDTO>() {
//			@Override
//			public void execute(ImageCachingStatisticsDTO statisticsDTO) {
//						Window.Location.assign(GWT.getHostPageBaseURL()
//								+ "EuropeanaIngestionControlPanel/generatePDF?providerId=" + statisticsDTO.getProviderId() + "&collectionId="
//								+ statisticsDTO.getCollectionId() + "&executionId="
//								+ statisticsDTO.getExecutionId() + "&dateStart=" + statisticsDTO.getDateCreated());
//			}
//		});
//		
//		Column<ImageCachingStatisticsDTO, ImageCachingStatisticsDTO> generatePdfColumn = new Column<ImageCachingStatisticsDTO, ImageCachingStatisticsDTO>(pdfButtonCell) {		
//			@Override
//			public ImageCachingStatisticsDTO getValue(ImageCachingStatisticsDTO object) {
//				return object;
//			}
//		};
//
//		cellTable.addColumn(generatePdfColumn, "Generate Report");
//		cellTable.setColumnWidth(generatePdfColumn, 3, Unit.PCT);
		
		CustomActionCell csvButtonCell = new CustomActionCell("CSV", new ActionCell.Delegate<ImageCachingStatisticsDTO>() {
			@Override
			public void execute(ImageCachingStatisticsDTO statisticsDTO) {
						Window.Location.assign(GWT.getHostPageBaseURL()
								+ "EuropeanaIngestionControlPanel/generateCSV?providerId=" + statisticsDTO.getProviderId() + "&collectionId="
								+ statisticsDTO.getCollectionId() + "&executionId="
								+ statisticsDTO.getExecutionId() + "&dateStart=" + statisticsDTO.getDateCreated());
			}
		});
		
		Column<ImageCachingStatisticsDTO, ImageCachingStatisticsDTO> generateCsvColumn = new Column<ImageCachingStatisticsDTO, ImageCachingStatisticsDTO>(	csvButtonCell) {		
			@Override
			public ImageCachingStatisticsDTO getValue(ImageCachingStatisticsDTO object) {
				return object;
			}
		};

		cellTable.addColumn(generateCsvColumn, "Generate Failure Report");
		cellTable.setColumnWidth(generateCsvColumn, 3, Unit.PCT);
	}

	/**
	 * The method refreshes the content of a CellTable after each change of filtering parameters. 
	 * @param offset - for pagination
	 * @param maxSize - to limit the result returned
	 * @param collection - selected Collection
	 * @param provider - selected Provider
	 */
	private void updateRows(final int offset, final int maxSize) {
		List<String> collectionsForStatisics = new ArrayList<String>();
		if (selectedCollection != null) {
			collectionsForStatisics.add(selectedCollection.getName());
		} else if (selectedProvider != null) {
			for (CollectionDTO coll : collections) {
				collectionsForStatisics.add(coll.getName());
			}			
		}
		imageCachingStatisticsRetrievalService.getImageCachingStatistics(offset, maxSize, collectionsForStatisics, selectedProvider != null ? selectedProvider.getName() : "",
				new AsyncCallback<ImageCachingStatisticsResultDTO>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(ImageCachingStatisticsResultDTO result) {
						statisticsReports.clear();
						cellTable.setRowCount(result.getNumberStatisticsReports());
						statisticsReports.addAll(result.getStatisticsReports());	
						cellTable.setRowData(offset, statisticsReports);
					}
				});
	}
	
	/**
	 * A class implementing a button in a cell of a CellTable.
	 * @author Alena Fedasenka
	 *
	 */
	private class CustomActionCell extends AbstractCell<ImageCachingStatisticsDTO> {
		private final SafeHtml html;
		private final Delegate<ImageCachingStatisticsDTO> delegate;
		
		public CustomActionCell(SafeHtml message, Delegate<ImageCachingStatisticsDTO> delegate) {
			super(CLICK, KEYDOWN);
			this.delegate = delegate;
			this.html = new SafeHtmlBuilder().appendHtmlConstant(
			    "<button type=\"button\" tabindex=\"-1\">").append(message).appendHtmlConstant(
			    		"</button>").toSafeHtml();
		}

		public CustomActionCell(String text, Delegate<ImageCachingStatisticsDTO> delegate) {
			this(SafeHtmlUtils.fromString(text), delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, ImageCachingStatisticsDTO value,
				NativeEvent event, ValueUpdater<ImageCachingStatisticsDTO> valueUpdater) {
			super.onBrowserEvent(context, parent, value, event, valueUpdater);
			if (CLICK.equals(event.getType())) {
				EventTarget eventTarget = event.getEventTarget();
				if (!Element.is(eventTarget)) {
					return;
				}
				if (parent.getFirstChildElement().isOrHasChild(
						Element.as(eventTarget))) {
					// Ignore clicks that occur outside of the main element.
					onEnterKeyDown(context, parent, value, event, valueUpdater);
				}
			}
		}
		
		@Override
		public void render(Context context, ImageCachingStatisticsDTO value, SafeHtmlBuilder sb) {
			sb.append(html);				
		}

		@Override
		protected void onEnterKeyDown(Context context, Element parent,
				ImageCachingStatisticsDTO value, NativeEvent event,
				ValueUpdater<ImageCachingStatisticsDTO> valueUpdater) {
			delegate.execute(value);
		}
	}
	
	/**
	 * The method gets the list of collections for a particular provider and
	 * then calls the method to populate Collections drop-down.
	 * 
	 * @param provider
	 */
	private void loadCollections() {
		if (selectedProvider != null) {
			repositoryService.getCollections(selectedProvider.getId(), new AsyncCallback<List<CollectionDTO>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				
				@Override
				public void onSuccess(List<CollectionDTO> result) {
					collectionBox.clear();
					collectionBox.addItem("-|-");  					
					collections.clear();
					Collections.sort(result, new CollectionComparator());
					collections.addAll(result);
					
					for (CollectionDTO collection : collections) {
						String name = collection.getMnemonic() + ": " + collection.getName();
						collectionBox.addItem(name);
					}
				}
			});
		} else {
			collectionBox.clear();
			collectionBox.addItem("-|-"); 
		}
	}

	/**
	 * The method gets the list of providers and
	 * then calls the method to populate Providers drop-down.
	 * 
	 */
	private void loadProviders() {
		repositoryService.getProviders(new AsyncCallback<List<ProviderDTO>>() {
			@Override
			public void onFailure(Throwable caught) {
				caught.printStackTrace();
			}

			@Override
			public void onSuccess(List<ProviderDTO> result) {
				providerBox.clear();
				providerBox.addItem("-|-");
				providers.clear();
				Collections.sort(result, new ProviderComparator());
				providers.addAll(result);

				for (ProviderDTO provider : providers) {
					String name = provider.getCountry() == null ? "eu: "
							+ provider.getName() : provider.getCountry() + ": "
							+ provider.getName();
					providerBox.addItem(name);
				}
			}
		});
	}
	

	/**
	 * to get start date for generation PDF report per collection.
	 */
	private void setDateProcessingStarted() {
		final String[] imageCachingWorkflow = {"ImageCacheWorkflow"};
		if (selectedCollection != null) {
			executionService.getPastExecutions(imageCachingWorkflow, selectedCollection.getMnemonic(), null, null, new AsyncCallback<List<ExecutionDTO>>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				
				@Override
				public void onSuccess(List<ExecutionDTO> result) {
					List<Date> startDates = new ArrayList<Date>();
					for (ExecutionDTO execution : result) {
						startDates.add(execution.getStartTime());
					}
					if (!result.isEmpty()) {
						Collections.sort(startDates);
						dateProcessingStarted = startDates.get(0);
					}
				}
			});
		}
	}
	
	/**
	 * to get number of active records for generation PDF report per collection.
	 */
	private void setRecordsCount() {
		if (selectedCollection != null) {
			retrievalService.getRecordsForCollection((String)selectedCollection.getId(), 0, MAX_SIZE, "", new AsyncCallback<MetaDataResultDTO>() {
				@Override
				public void onFailure(Throwable caught) {
					caught.printStackTrace();
				}
				@Override
				public void onSuccess(MetaDataResultDTO result) {
					recordsCount = result.getActiverecords();
				};
			});
		}
	}
	
	/**
	 * Comparator for sorting Collections in a Collections drop-down.
	 * @author Alena Fedasenka
	 *
	 */
	private class CollectionComparator implements Comparator<CollectionDTO> {
		@Override
		public int compare(CollectionDTO o1, CollectionDTO o2) {
			String n1 = o1.getMnemonic() + ": " + o1.getName();
			String n2 = o2.getMnemonic() + ": " + o2.getName();
			return n1.compareTo(n2);
		}
	}
	
	/**
	 * Comparator for sorting Providers in a Providers drop-down.
	 * @author Alena Fedasenka
	 *
	 */
	private class ProviderComparator implements Comparator<ProviderDTO> {
        @Override
        public int compare(ProviderDTO o1, ProviderDTO o2) {
            String n1 = o1.getCountry() == null ? "eu: " + o1.getName() : o1.getCountry() + ": " + o1.getName();
            String n2 = o2.getCountry() == null ? "eu: " + o2.getName() : o2.getCountry() + ": " + o2.getName();
            return n1.compareTo(n2);
        }
	}

	@Override
	protected void asyncOnInitialize(final AsyncCallback<Widget> callback) {
		GWT.runAsync(ImageCachingStatisticsWidget.class, new RunAsyncCallback() {

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
