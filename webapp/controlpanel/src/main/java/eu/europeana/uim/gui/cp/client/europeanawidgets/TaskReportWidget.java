package eu.europeana.uim.gui.cp.client.europeanawidgets;

import static com.google.gwt.dom.client.BrowserEvents.CLICK;
import static com.google.gwt.dom.client.BrowserEvents.KEYDOWN;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.engine.query.FilterQueryPlan;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.ActionCell.Delegate;
import com.google.gwt.cell.client.Cell.Context;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.EventTarget;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.safecss.shared.SafeStyles;
import com.google.gwt.safecss.shared.SafeStylesUtils;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.cellview.client.SimplePager.TextLocation;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;
import com.google.gwt.view.client.RangeChangeEvent;

import eu.europeana.uim.gui.cp.client.IngestionWidget;
import eu.europeana.uim.gui.cp.client.services.TaskReportServiceAsync;
import eu.europeana.uim.gui.cp.shared.validation.TaskReportDTO;
import eu.europeana.uim.gui.cp.shared.validation.TaskReportResultDTO;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public class TaskReportWidget extends IngestionWidget {

	/**
	 * 
	 */
	@UiField
    TextBox		queryBox;
	
    /**
     * 
     */
    @UiField
    Button		createTaskButton;
    
	/**
	 * 
	 */
	@UiField
    TextBox		filterBox;
	
    /**
     * 
     */
    @UiField
    Button		filterButton;
    
    /**
     * 
     */
    @UiField
    CheckBox	showActiveCheck;
    
    /**
     * 
     */
    @UiField
    Button		clearAllButton;

	/**
	 * The main CellTable.
	 */
	@UiField(provided = true)
	CellTable<TaskReportDTO> cellTable;
	
	/**
     * The pager used to change the range of data.
     */
    @UiField(provided = true)
    SimplePager							pager;
    ListDataProvider<TaskReportDTO> 	sortProvider;
    
    
	interface Binder extends UiBinder<Widget, TaskReportWidget> {
	}

	private final TaskReportServiceAsync taskReportsRetrievalService;
	private final List<TaskReportDTO> reports = new ArrayList<TaskReportDTO>();

	public TaskReportWidget(String name, String description, TaskReportServiceAsync taskReportsRetrievalService) {
		super(name, description);
		this.taskReportsRetrievalService = taskReportsRetrievalService;
	}

	@Override
	public Widget onInitialize() {		
		cellTable = new CellTable<TaskReportDTO>(TaskReportDTO.KEY_PROVIDER);
		cellTable.setWidth("100%", true);
		cellTable.setPageSize(20);
	    
		cellTable.addRangeChangeHandler(new RangeChangeEvent.Handler() {
			@Override
			public void onRangeChange(RangeChangeEvent arg0) {
				Range range = cellTable.getVisibleRange();
				int start = range.getStart();
				int length = range.getLength();
				updateRows(start, length, showActiveCheck.getValue(), null, null, -1);
			}
		});
		
		updateRows(0, 20, false, null, null, -1);
		
		sortProvider = new ListDataProvider<TaskReportDTO>();
        sortProvider.setList(reports);
        sortProvider.addDataDisplay(cellTable);
        
        SimplePager.Resources pagerResources = GWT.create(SimplePager.Resources.class);
        pager = new SimplePager(TextLocation.CENTER, pagerResources, false, 0, true);
        pager.setDisplay(cellTable);
		
		initTableColumns();
		Binder uiBinder = GWT.create(Binder.class);
		Widget widget = uiBinder.createAndBindUi(this);
		
		queryBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
                	updateRows(0, 20, showActiveCheck.getValue(), null, queryBox.getText(), -1);
    				//if we create a task we clean up the filter by query text box
    				filterBox.setText("");
    				queryBox.setText("");
                }
            }
        });
		filterBox.addKeyPressHandler(new KeyPressHandler() {
            @Override
            public void onKeyPress(KeyPressEvent event) {
                if (KeyCodes.KEY_ENTER == event.getNativeEvent().getKeyCode()) {
                	updateRows(0, 20, showActiveCheck.getValue(), filterBox.getText(), null, -1);
                }
            }
        });
		
		createTaskButton.setText("Create Task");
		createTaskButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				updateRows(0, 20, showActiveCheck.getValue(), null, queryBox.getText(), -1);
				//if we create a task we clean up the filter by query text box
				filterBox.setText("");
				queryBox.setText("");
			}

		});
		
		filterButton.setText("Filter by Query");
		filterButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				updateRows(0, 20, showActiveCheck.getValue(), filterBox.getText(), null, -1);
			}

		});
		
		showActiveCheck.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				 CheckBox checkBox = (CheckBox)event.getSource();
				 if (checkBox.getValue()) {
					 updateRows(0, 20, true, filterBox.getText(), null, -1);
				 } else {
					 updateRows(0, 20, false, filterBox.getText(), null, -1);
				 }
			}
		});
		
		clearAllButton.setText("Clear All Filters");
		clearAllButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent arg0) {
				updateRows(0, 20, false, null, null, -1);
				filterBox.setText("");
				queryBox.setText("");
				showActiveCheck.setValue(false);
			}

		});
		return widget;
	}

	private void initTableColumns() {
		Column<TaskReportDTO, String> taskIdColumn = new Column<TaskReportDTO, String>(
				new TextCell()) {

			@Override
			public String getValue(TaskReportDTO object) {
				return object.getTaskId() + "";
			}
		};
		
		cellTable.addColumn(taskIdColumn, "Task Id");
		cellTable.setColumnWidth(taskIdColumn, 5, Unit.PCT);
		
		Column<TaskReportDTO, String> queryColumn = new Column<TaskReportDTO, String>(new TextCell()) {
			@Override
			public String getValue(TaskReportDTO object) {
				return object.getQuery();
			}
		};
		cellTable.addColumn(queryColumn, "Query");
		cellTable.setColumnWidth(queryColumn, 15, Unit.PCT);

		Column<TaskReportDTO, String> dateCreatedColumn = new Column<TaskReportDTO, String>(new TextCell()) {
			@Override
			public String getValue(TaskReportDTO object) {
				return object.getDateCreated();
			}
		};		
		cellTable.addColumn(dateCreatedColumn, "Date Created");
		cellTable.setColumnWidth(dateCreatedColumn, 5, Unit.PCT);
		
		Column<TaskReportDTO, String> dateUpdateColumn = new Column<TaskReportDTO, String>(new TextCell()) {
			@Override
			public String getValue(TaskReportDTO object) {
		        return object.getDateUpdated();	
			}
		};		
		cellTable.addColumn(dateUpdateColumn, "Date Updated");
		cellTable.setColumnWidth(dateUpdateColumn, 5, Unit.PCT);
		
		Column<TaskReportDTO, String> processedColumn = new Column<TaskReportDTO, String>(new TextCell()) {
			@Override
			public String getValue(TaskReportDTO object) {
				return object.getProcessed() + "";
			}
		};
		cellTable.addColumn(processedColumn, "Number of Records Processed");
		cellTable.setColumnWidth(processedColumn, 5, Unit.PCT);
		
		Column<TaskReportDTO, String> totalColumn = new Column<TaskReportDTO, String>(new TextCell()) {
			@Override
			public String getValue(TaskReportDTO object) {
				return object.getTotal() + "";
			}
		};
		cellTable.addColumn(totalColumn, "Total Number of Records");
		cellTable.setColumnWidth(totalColumn, 5, Unit.PCT);

		Column<TaskReportDTO, String> statusColumn = new Column<TaskReportDTO, String>(new ColorCell()) {	
			@Override
			public String getCellStyleNames(Context context, TaskReportDTO task) {
				return "boldColumn";
			}
			
			@Override
			public String getValue(TaskReportDTO object) {
				return object.getStatus();
			}
		};
		statusColumn.setCellStyleNames("boldColumn");
		cellTable.addColumn(statusColumn, "Status");
		cellTable.setColumnWidth(statusColumn, 5, Unit.PCT);
		
		CustomActionCell stopButtonCell = new CustomActionCell("Stop Task",
				new ActionCell.Delegate<TaskReportDTO>() {
					@Override
					public void execute(TaskReportDTO parameter) {
						filterBox.setText("");
						updateRows(0, 20, showActiveCheck.getValue(), null, null, parameter.getTaskId());
					}
				});
		Column<TaskReportDTO, TaskReportDTO> stopTaskReportColumn = new Column<TaskReportDTO, TaskReportDTO>(				
				stopButtonCell) {		
			@Override
			public TaskReportDTO getValue(TaskReportDTO object) {
				return object;
			}
		};

		cellTable.addColumn(stopTaskReportColumn, "Stop Task Report");
		cellTable.setColumnWidth(stopTaskReportColumn, 5, Unit.PCT);
	}

	@Override
	protected void asyncOnInitialize(final AsyncCallback<Widget> callback) {
		GWT.runAsync(TaskReportWidget.class, new RunAsyncCallback() {

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

	private void updateRows(final int offset, final int maxSize, boolean isActive, String query, String newTaskReportQuery, long taskId) {
		taskReportsRetrievalService.getTaskReports(offset, maxSize, isActive, query, newTaskReportQuery, taskId,
				new AsyncCallback<TaskReportResultDTO>() {
					@Override
					public void onFailure(Throwable caught) {
						caught.printStackTrace();
					}

					@Override
					public void onSuccess(TaskReportResultDTO result) {
						reports.clear();
						cellTable.setRowCount(result.getNumberRecords());
						reports.addAll(result.getReports());	
						cellTable.setRowData(offset, reports);
					}
				});
	}
	
	private class CustomActionCell extends AbstractCell<TaskReportDTO> {
		
		private final SafeHtml html;
		private final Delegate<TaskReportDTO> delegate;
		private SafeHtml message;
		
		public CustomActionCell(SafeHtml message, Delegate<TaskReportDTO> delegate) {
			super(CLICK, KEYDOWN);
			this.delegate = delegate;
			this.message = message;
			this.html = new SafeHtmlBuilder().appendHtmlConstant(
			    "<button type=\"button\" tabindex=\"-1\">").append(message).appendHtmlConstant(
			    		"</button>").toSafeHtml();
		}

		public CustomActionCell(String text, Delegate<TaskReportDTO> delegate) {
			this(SafeHtmlUtils.fromString(text), delegate);
		}

		@Override
		public void onBrowserEvent(Context context, Element parent, TaskReportDTO value,
				NativeEvent event, ValueUpdater<TaskReportDTO> valueUpdater) {
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
		public void render(com.google.gwt.cell.client.Cell.Context context, TaskReportDTO value, SafeHtmlBuilder sb) {
			if(value.getStatus().equalsIgnoreCase("stopped") || value.getStatus().equalsIgnoreCase("finished")) {
				sb.append(new SafeHtmlBuilder()
						.appendHtmlConstant("<button type=\"button\" tabindex=\"-1\" style=\"visibility:hidden;\">")
						.append(message).appendHtmlConstant("</button>")
						.toSafeHtml());
			} else {
				sb.append(html);				
			}
		}

		@Override
		protected void onEnterKeyDown(Context context, Element parent,
				TaskReportDTO value, NativeEvent event,
				ValueUpdater<TaskReportDTO> valueUpdater) {
			delegate.execute(value);
		}
	}
	
	static class ColorCell extends AbstractCell<String> {
	    interface Templates extends SafeHtmlTemplates {
	      @SafeHtmlTemplates.Template("<div style=\"{0}\">{1}</div>")
	      SafeHtml cell(SafeStyles styles, SafeHtml value);
	    }

	    private static Templates templates = GWT.create(Templates.class);

	    @Override
	    public void render(Context context, String value, SafeHtmlBuilder sb) {
	      if (value == null) {
	        return;
	      }
			String style = value.toLowerCase().contains("stopped") ? "red"
					: value.toLowerCase().contains("finished") ? "green"
							: "black";
	      SafeStyles styles = SafeStylesUtils.forTrustedColor(style);
	      SafeHtml rendered = templates.cell(styles, SafeHtmlUtils.fromString(value));
	      sb.append(rendered);
	    }   
	}
}
