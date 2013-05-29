/**
 * 
 */
package eu.europeana.uim.gui.cp.client.europeanawidgets;

import java.util.Comparator;
import java.util.Date;
import com.google.gwt.cell.client.ActionCell;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.DateCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.view.client.SelectionModel;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import eu.europeana.uim.gui.cp.client.monitoring.FilteredIngestionHistoryWidget;
import eu.europeana.uim.gui.cp.client.services.ExecutionServiceAsync;
import eu.europeana.uim.gui.cp.client.services.RepositoryServiceAsync;
import eu.europeana.uim.gui.cp.shared.ExecutionDTO;

/**
 * Ingestion specific Widget for europeana. It is an expansion of the
 * FilteredIngestionHistoryWidget. It modifies the column information by adding
 * extra information about satuses of record that are specific to the europeana
 * ingestion cycle (omitted/generated/discarded)
 * 
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since 25 Mar 2013
 * 
 */
public class EuropeanaIngestionHistoryWidget extends
		FilteredIngestionHistoryWidget {

	/**
	 * InfoDialog for displaying records updates information for each execution.
	 */
	public DialogBox infoDialog;

	/**
	 * Table that holds execution results information
	 */
	protected FlexTable execResultsTable;

	/**
	 * Close Button for execution results information dialog
	 */
	Button closeButton;

	/**
	 * @param executionService
	 * @param repositoryService
	 */
	public EuropeanaIngestionHistoryWidget(
			ExecutionServiceAsync executionService,
			RepositoryServiceAsync repositoryService) {
		super(executionService, repositoryService);

	}

	/**
	 * Add the columns to the table.
	 */
	@Override
	protected void initTableColumns(
			final SelectionModel<ExecutionDTO> selectionModel,
			ListHandler<ExecutionDTO> sortHandler) {

		execResultsTable = new FlexTable();
		FlexCellFormatter cellFormatter = execResultsTable
				.getFlexCellFormatter();
		cellFormatter.setHorizontalAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_LEFT);
		cellFormatter.setColSpan(0, 0, 2);

		execResultsTable.setWidth("32em");
		execResultsTable.setCellSpacing(5);
		execResultsTable.setCellPadding(3);

		closeButton = new Button();
		closeButton.setText("Close");

		infoDialog = new DialogBox();
		infoDialog.ensureDebugId("infoDialogBoxx");

		infoDialog.setModal(true);

		// Create a table to layout the content
		VerticalPanel dialogContents = new VerticalPanel();
		dialogContents.setSpacing(0);

		closeButton.addClickHandler(new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				infoDialog.hide();
			}
		});

		dialogContents.add(execResultsTable);
		dialogContents.add(closeButton);
		infoDialog.add(dialogContents); 

		// ID
		Column<ExecutionDTO, String> idColumn = new Column<ExecutionDTO, String>(
				new TextCell()) {
			@Override
			public String getValue(ExecutionDTO object) {
				return object.getId().toString();
			}
		};
		idColumn.setSortable(true);
		sortHandler.setComparator(idColumn, new Comparator<ExecutionDTO>() {
			@Override
			public int compare(ExecutionDTO o1, ExecutionDTO o2) {
				return o1.getId().toString().compareTo(o2.getId().toString());
			}
		});
		cellTable.addColumn(idColumn, "ID");
		cellTable.setColumnWidth(idColumn, 10, Unit.PCT);

		// Name
		Column<ExecutionDTO, String> nameColumn = new Column<ExecutionDTO, String>(
				new TextCell()) {
			@Override
			public String getValue(ExecutionDTO object) {
				return object.getName();
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn, new Comparator<ExecutionDTO>() {
			@Override
			public int compare(ExecutionDTO o1, ExecutionDTO o2) {
				return o1.getName().compareTo(o2.getName());
			}
		});
		cellTable.addColumn(nameColumn, "Name");
		cellTable.setColumnWidth(nameColumn, 20, Unit.PCT);

		// Data set
		Column<ExecutionDTO, String> datasetColumn = new Column<ExecutionDTO, String>(
				new TextCell()) {
			@Override
			public String getValue(ExecutionDTO object) {
				return object.getDataSet();
			}
		};
		datasetColumn.setSortable(true);
		sortHandler.setComparator(datasetColumn,
				new Comparator<ExecutionDTO>() {
					@Override
					public int compare(ExecutionDTO o1, ExecutionDTO o2) {
						return o1.getDataSet().compareTo(o2.getDataSet());
					}
				});
		cellTable.addColumn(datasetColumn, "Dataset");
		cellTable.setColumnWidth(datasetColumn, 18, Unit.PCT);

		DateTimeFormat dtf = DateTimeFormat
				.getFormat("dd.MM.yyyy 'at' HH:mm:ss");
		// Start Time
		Column<ExecutionDTO, Date> startTimeColumn = new Column<ExecutionDTO, Date>(
				new DateCell(dtf)) {
			@Override
			public Date getValue(ExecutionDTO object) {
				return object.getStartTime();
			}
		};
		startTimeColumn.setSortable(true);
		sortHandler.setComparator(startTimeColumn,
				new Comparator<ExecutionDTO>() {
					@Override
					public int compare(ExecutionDTO o1, ExecutionDTO o2) {
						return o1.getStartTime().compareTo(o2.getStartTime());
					}
				});
		cellTable.addColumn(startTimeColumn, "Start Time");
		cellTable.setColumnWidth(startTimeColumn, 13, Unit.PCT);

		// End Time
		Column<ExecutionDTO, Date> endTimeColumn = new Column<ExecutionDTO, Date>(
				new DateCell(dtf)) {
			@Override
			public Date getValue(ExecutionDTO object) {
				return object.getEndTime();
			}
		};
		endTimeColumn.setSortable(true);
		sortHandler.setComparator(endTimeColumn,
				new Comparator<ExecutionDTO>() {
					@Override
					public int compare(ExecutionDTO o1, ExecutionDTO o2) {
						if (o1.getEndTime() != null && o2.getEndTime() != null) {
							return o1.getEndTime().compareTo(o2.getEndTime());
						} else {
							if (o1.getEndTime() == null) {
								return o2.getEndTime() == null ? 0 : -1;
							}
							return o2.getEndTime() == null ? 1 : 0;
						}
					}
				});
		cellTable.addColumn(endTimeColumn, "End Time");
		cellTable.setColumnWidth(endTimeColumn, 13, Unit.PCT);

		// Canceled
		Column<ExecutionDTO, Boolean> doneColumn = new Column<ExecutionDTO, Boolean>(
				new CheckboxCell()) {
			@Override
			public Boolean getValue(ExecutionDTO object) {
				return object.isCanceled();
			}
		};
		doneColumn.setSortable(true);
		cellTable.addColumn(doneColumn, "Canceled/Failed");
		cellTable.setColumnWidth(doneColumn, 5, Unit.PCT);

		// Scheduled/Failure/Completed
		Column<ExecutionDTO, String> scheduledColumn = new Column<ExecutionDTO, String>(
				new TextCell()) {
			@Override
			public String getValue(ExecutionDTO object) {
				return object.getScheduled() + "/" + object.getFailure() + "/"
						+ object.getCompleted();
			}
		};
		cellTable.addColumn(scheduledColumn, "Sche./Fail./Comp.");
		cellTable.setColumnWidth(scheduledColumn, 13, Unit.PCT);

		// Created/Updated/Deleted
		Column<ExecutionDTO, ExecutionDTO> failureColumn = new Column<ExecutionDTO, ExecutionDTO>(
				new ModifiableActionCell<ExecutionDTO>("X",
						new ModifiableActionCell.Delegate<ExecutionDTO>() {
							@Override
							public void execute(ExecutionDTO parameter) {

								openResultsDialog(parameter);

							}
						}) {

					@Override
					public void render(ActionCell.Context context,
							ExecutionDTO value, SafeHtmlBuilder sb) {

						super.render(context, value,
								generateMessageFromDTO(value, sb));
					}
				}) {
			@Override
			public ExecutionDTO getValue(ExecutionDTO object) {
				return object;
			}

		};

		cellTable.addColumn(failureColumn, "Total processed");
		cellTable.setColumnWidth(failureColumn, 13, Unit.PCT);

		// Log file
		Column<ExecutionDTO, ExecutionDTO> logfileColumn = new Column<ExecutionDTO, ExecutionDTO>(
				new ActionCell<ExecutionDTO>("Log",
						new ActionCell.Delegate<ExecutionDTO>() {
							@Override
							public void execute(ExecutionDTO parameter) {
								com.google.gwt.user.client.Window.open(
										GWT.getModuleBaseURL()
												+ "logfile?format=html&execution="
												+ parameter.getId(), "_blank",
										"");
							}
						})) {
			@Override
			public ExecutionDTO getValue(ExecutionDTO object) {
				return object;
			}
		};

		cellTable.addColumn(logfileColumn, "Log");
		cellTable.setColumnWidth(logfileColumn, 5, Unit.PCT);

		updatePastExecutions();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * eu.europeana.uim.gui.cp.client.monitoring.FilteredIngestionHistoryWidget
	 * #asyncOnInitialize(com.google.gwt.user.client.rpc.AsyncCallback)
	 */
	@Override
	protected void asyncOnInitialize(final AsyncCallback<Widget> callback) {
		GWT.runAsync(EuropeanaIngestionHistoryWidget.class,
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

	/**
	 * @param parameter
	 */
	protected void openResultsDialog(ExecutionDTO object) {

		execResultsTable.removeAllRows();

		int createdValue = object.getValue("Created") != null ? Integer
				.parseInt(object.getValue("Created")) : 0;
		int updatedValue = object.getValue("Updated") != null ? Integer
				.parseInt(object.getValue("Updated")) : 0;
		int deletedValue = object.getValue("Deleted") != null ? Integer
				.parseInt(object.getValue("Deleted")) : 0;
		int omittedValue = object.getValue("Omitted") != null ? Integer
				.parseInt(object.getValue("Omitted")) : 0;
		int generatedValue = object.getValue("Generated") != null ? Integer
				.parseInt(object.getValue("Generated")) : 0;
		int discardedValue = object.getValue("Discarded") != null ? Integer
				.parseInt(object.getValue("Discarded")) : 0;

		execResultsTable.removeAllRows();

		execResultsTable.setWidget(0, 0, new HTML("Created:"));
		execResultsTable.setWidget(0, 1, new HTML(
				"Records that have been created"));
		execResultsTable
				.setWidget(0, 2, new HTML(String.valueOf(createdValue)));

		execResultsTable.setWidget(1, 0, new HTML("Updated:"));
		execResultsTable.setWidget(1, 1, new HTML(
				"Records that have been updated"));
		execResultsTable
				.setWidget(1, 2, new HTML(String.valueOf(updatedValue)));

		execResultsTable.setWidget(2, 0, new HTML("Deleted:"));
		execResultsTable.setWidget(2, 1, new HTML(
				"Records that have been marked as deleted"));
		execResultsTable
				.setWidget(2, 2, new HTML(String.valueOf(deletedValue)));

		execResultsTable.setWidget(3, 0, new HTML("Omitted:"));
		execResultsTable
				.setWidget(
						3,
						1,
						new HTML(
								"Records that have been omitted (during the ingestion process this means that"
										+ "they have not been updated since their identifier and content were unaltered)"));
		execResultsTable
				.setWidget(3, 2, new HTML(String.valueOf(omittedValue)));

		execResultsTable.setWidget(4, 0, new HTML("Generated:"));
		execResultsTable
				.setWidget(
						4,
						1,
						new HTML(
								"Extra records Generated after splitting of records during the import process"));
		execResultsTable.setWidget(4, 2,
				new HTML(String.valueOf(generatedValue)));

		execResultsTable.setWidget(5, 0, new HTML("Discarded:"));
		execResultsTable
				.setWidget(
						5,
						1,
						new HTML(
								"Records that have been discarded during the import process (See 'Failed Records Report'"
										+ " screen for more details.)"));
		execResultsTable.setWidget(5, 2,
				new HTML(String.valueOf(discardedValue)));

		infoDialog.setTitle(object.getWorkflow() + " " + object.getDataSet());
		infoDialog.setText(object.getWorkflow() + " " + object.getDataSet());

		infoDialog.show();
	}

	/**
	 * @param object
	 * @param retsb
	 * @return
	 */
	protected SafeHtmlBuilder generateMessageFromDTO(ExecutionDTO object,
			SafeHtmlBuilder retsb) {

		int createdValue = object.getValue("Created") != null ? Integer
				.parseInt(object.getValue("Created")) : 0;
		int updatedValue = object.getValue("Updated") != null ? Integer
				.parseInt(object.getValue("Updated")) : 0;
		int deletedValue = object.getValue("Deleted") != null ? Integer
				.parseInt(object.getValue("Deleted")) : 0;
		int omittedValue = object.getValue("Omitted") != null ? Integer
				.parseInt(object.getValue("Omitted")) : 0;
		int generatedValue = object.getValue("Generated") != null ? Integer
				.parseInt(object.getValue("Generated")) : 0;
		int discardedValue = object.getValue("Discarded") != null ? Integer
				.parseInt(object.getValue("Discarded")) : 0;

		int sum = createdValue + updatedValue + omittedValue;

		if (sum != 0 || discardedValue != 0) {
			StringBuffer buttontype = new StringBuffer();
			buttontype.append("<button type=\"button\"");
			if (discardedValue != 0) {
				buttontype.append(" style=\"color: red\"");
			}
			buttontype.append("tabindex=\"-1\">");
			retsb.appendHtmlConstant(buttontype.toString())
					.append(SafeHtmlUtils.fromString(String.valueOf(sum)))
					.appendHtmlConstant("</button>");
		}

		return retsb;
	}

}
