package eu.europeana.uim.gui.cp.client;

import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.prefetch.RunAsyncCode;
import eu.europeana.uim.gui.cp.client.europeanawidgets.*;
import eu.europeana.uim.gui.cp.client.management.IngestionTriggerWidget;
import eu.europeana.uim.gui.cp.client.monitoring.IngestionDetailWidget;
import eu.europeana.uim.gui.cp.client.services.*;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @author Yorgos Mamakis (yorgos.mamakis@ kb.nl)
 * @since Apr 26, 2011
 */
public class EuropeanaIngestionControlPanel extends
		AbstractIngestionControlPanel implements EntryPoint {
	@Override
	public void onModuleLoad() {
		initialize();
	}

	@Override
	protected IngestionCustomization getDynamics() {
		return new EuropeanaCustomization();
	}

	@Override
	protected void addMenuEntries(SidebarMenu treeModel) {

		// Initialize services here
		final RepositoryServiceAsync repositoryService = (RepositoryServiceAsync) GWT
				.create(RepositoryService.class);
		final ResourceServiceAsync resourceService = (ResourceServiceAsync) GWT
				.create(ResourceService.class);
		final ExecutionServiceAsync executionService = (ExecutionServiceAsync) GWT
				.create(ExecutionService.class);
		final IntegrationSeviceProxyAsync integrationService = (IntegrationSeviceProxyAsync) GWT
				.create(IntegrationSeviceProxy.class);
		final ImportVocabularyProxyAsync importVocabulary = (ImportVocabularyProxyAsync) GWT
				.create(ImportVocabularyProxy.class);
		final CollectionManagementProxyAsync collectionManagement = (CollectionManagementProxyAsync) GWT
				.create(CollectionManagementProxy.class);
		final RetrievalServiceAsync retrievalService = (RetrievalServiceAsync) GWT
				.create(RetrievalService.class);

		final ReportingServiceAsync reportService = (ReportingServiceAsync) GWT
				.create(ReportingService.class);
		final FailedRecordServiceAsync failedRecordService = (FailedRecordServiceAsync) GWT
				.create(FailedRecordService.class);
		final TaskReportServiceAsync taskReportService = (TaskReportServiceAsync) GWT
				.create(TaskReportService.class);
		final ImageCachingStatisticsServiceAsync imageCachingStatisticsService = (ImageCachingStatisticsServiceAsync) GWT
				.create(ImageCachingStatisticsService.class);
		// Initialize Panel Components here
		treeModel.addMenuEntry("Monitoring", new IngestionDetailWidget(
				executionService), RunAsyncCode
				.runAsyncCode(IngestionDetailWidget.class));
		treeModel.addMenuEntry("Monitoring", new EuropeanaIngestionHistoryWidget(
				executionService,repositoryService), RunAsyncCode
				.runAsyncCode(EuropeanaIngestionHistoryWidget.class));
		treeModel.addMenuEntry("Managing", new IngestionTriggerWidget(
				repositoryService, resourceService, executionService),
				RunAsyncCode.runAsyncCode(IngestionTriggerWidget.class));
		treeModel.addMenuEntry("Managing",
				new ExpandedResourceManagementWidget(repositoryService,
						resourceService, integrationService), RunAsyncCode
						.runAsyncCode(ExpandedResourceManagementWidget.class));
		treeModel.addMenuEntry("Managing", new TaskReportWidget(
				"Re-indexing Service", "This page allows you to preview the task reports, filter the task reports by task report query or task report status, create new task reports.",
				taskReportService), RunAsyncCode
				.runAsyncCode(TaskReportWidget.class));
		treeModel.addMenuEntry("Validation", new LinkValidationWidget(
				repositoryService, retrievalService), RunAsyncCode
				.runAsyncCode(LinkValidationWidget.class));
		treeModel.addMenuEntry("Validation", new FailedRecordsWidget(
				"Failed Records Report", "This page allows you to preview the duplicate records that have not been ingested",
				repositoryService, failedRecordService), RunAsyncCode
				.runAsyncCode(FailedRecordsWidget.class));
		
		treeModel.addMenuEntry("Link Checker/ Thumbler", new ImageCachingStatisticsWidget(
				"Image Caching Statistics",
				"This page allows you to preview the image cache statistics, filter the image caching jobs by provider or dataset, generate statistics reports in PDF and failure reports in TXT.",
				repositoryService, executionService, retrievalService, imageCachingStatisticsService
				), RunAsyncCode
				.runAsyncCode(ImageCachingStatisticsWidget.class));

		treeModel.addMenuEntry("Importing", new ImportResourcesWidget(
				repositoryService, resourceService, integrationService),
				RunAsyncCode.runAsyncCode(ImportResourcesWidget.class));

		treeModel.addMenuEntry("Importing",
				new ImportControlledVocabularyWidget(repositoryService,
						resourceService, importVocabulary), RunAsyncCode
						.runAsyncCode(ImportControlledVocabularyWidget.class));

		treeModel.addMenuEntry("Importing", new CollectionManagement(
				repositoryService, resourceService, collectionManagement),
				RunAsyncCode.runAsyncCode(CollectionManagement.class));
	}
}
