/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved 
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 *  
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under 
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of 
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under 
 *  the Licence.
 */
package eu.europeana.uim.repoxclient.test;



/**
 * Mock Implementation of Unit Tests for Repox Client
 * 
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since Oct 10, 2013
 */
public class RepoxClientMockTest extends AbstractRepoxClientTest{

//	/**
//	 * A mocked instance of Repox client
//	 */
//	private RepoxRestClient repoxRestClient;	
//	
//	/**
//	 * Initialize method call mock behavior here
//	 * 
//	 * @throws DataSourceOperationException
//	 * @throws AggregatorOperationException
//	 * @throws ProviderOperationException
//	 * @throws HarvestingOperationException
//	 */
//	@Before
//	public void setupClient() throws DataSourceOperationException, AggregatorOperationException, ProviderOperationException, HarvestingOperationException{
//		super.repoxRestClient = repoxRestClient = mock(RepoxRestClient.class);
//		DataSources rdatatsources = new DataSources();
//		List<Source> dslist = new ArrayList<Source>();
//		
//		Source dssrc = new Source();
//		dssrc.setCharacterEncoding("UTF-8");
//		
//		Description dsdescription = new Description();
//		dsdescription.setDescription("Mock Datasource");
//		dssrc.setDescription(dsdescription);
//		dslist.add(dssrc);
//		
//		rdatatsources.setSourceList(dslist );
//		when(repoxRestClient.retrieveDataSources()).thenReturn(rdatatsources);
//
//		Aggregators raggregators = new Aggregators();
//		when(repoxRestClient.retrieveAggregators()).thenReturn(raggregators);
//		
//		DataProviders rproviders = new DataProviders();
//		when(repoxRestClient.retrieveProviders()).thenReturn(rproviders);
//		
//		
//		// Define argument captors
//		final ArgumentCaptor<Provider> prargument = ArgumentCaptor.forClass(Provider.class);
//
//		final ArgumentCaptor<Aggregator> aggargument = ArgumentCaptor.forClass(Aggregator.class);
//		
//		final ArgumentCaptor<String> stringargument = ArgumentCaptor.forClass(String.class);
//		
//		final ArgumentCaptor<Source> sourceargument = ArgumentCaptor.forClass(Source.class);
//		
//		
//		// Mock API methods
//		when(repoxRestClient.createAggregator(aggargument.capture())).thenAnswer(
//
//				new Answer<Aggregator>(){
//					@Override
//					public Aggregator answer(InvocationOnMock invocation)
//							throws Throwable {
//						Aggregator agg = new Aggregator();
//						agg.setId("mockAggID");
//						agg.setName(aggargument.getValue().getName());
//						agg.setNameCode(aggargument.getValue().getNameCode());
//						agg.setUrl(aggargument.getValue().getUrl());
//						
//						when(repoxRestClient.retrieveAggregator(agg.getId())).thenReturn(agg);
//						return agg;
//					}
//					
//				}
//				
//				);
//		
//		when(repoxRestClient.updateAggregator(aggargument.capture())).thenAnswer(
//
//				new Answer<Aggregator>(){
//					@Override
//					public Aggregator answer(InvocationOnMock invocation)
//							throws Throwable {
//						Aggregator agg = new Aggregator();
//						agg.setId("mockAggID");						
//						agg.setName(aggargument.getValue().getName());
//						agg.setNameCode(aggargument.getValue().getNameCode());
//						agg.setUrl(aggargument.getValue().getUrl());
//						when(repoxRestClient.retrieveAggregator(agg.getId())).thenReturn(agg);
//						return agg;
//					}
//					
//				}
//				
//				);
//		
//		
//		when(repoxRestClient.deleteAggregator(stringargument.capture())).thenAnswer(
//
//				new Answer<Success>(){
//					@Override
//					public Success answer(InvocationOnMock invocation)
//							throws Throwable {
//						Success agg = new Success();
//						agg.setSuccess("true");
//						when(repoxRestClient.retrieveAggregator(stringargument.getValue())).thenReturn(null);
//						return agg;
//					}
//					
//				}
//				
//				);
//		
//		
//		when(repoxRestClient.createProvider(prargument.capture(), (Aggregator) anyObject())).thenAnswer(
//
//				new Answer<Provider>(){
//					@Override
//					public Provider answer(InvocationOnMock invocation)
//							throws Throwable {
//						Provider prov = new Provider();
//						prov.setCountry(prargument.getValue().getCountry());
//						prov.setDescription(prargument.getValue().getDescription());
//						prov.setId("mockProvID");
//						prov.setName(prargument.getValue().getName());
//						prov.setNameCode(prargument.getValue().getNameCode());
//						prov.setType(prargument.getValue().getType());
//						prov.setUrl(prargument.getValue().getUrl());
//						
//						when(repoxRestClient.retrieveProvider(prov.getId())).thenReturn(prov);
//						return prov;
//					}
//				}
//				); 
//		
//		when(repoxRestClient.updateProvider(prargument.capture())).thenAnswer(
//
//				new Answer<Provider>(){
//					@Override
//					public Provider answer(InvocationOnMock invocation)
//							throws Throwable {
//						Provider prov = new Provider();
//						prov.setCountry(prargument.getValue().getCountry());
//						prov.setDescription(prargument.getValue().getDescription());
//						prov.setId("mockProvID");
//						prov.setName(prargument.getValue().getName());
//						prov.setNameCode(prargument.getValue().getNameCode());
//						prov.setType(prargument.getValue().getType());
//						prov.setUrl(prargument.getValue().getUrl());
//						
//						when(repoxRestClient.retrieveProvider(prov.getId())).thenReturn(prov);
//						return prov;
//					}
//				}
//				); 
//
//		when(repoxRestClient.deleteProvider(stringargument.capture())).thenAnswer(
//				new Answer<Success>(){
//					@Override
//					public Success answer(InvocationOnMock invocation)
//							throws Throwable {
//						Success agg = new Success();
//						agg.setSuccess("true");
//						when(repoxRestClient.retrieveProvider(stringargument.getValue())).thenReturn(null);
//						return agg;
//					}
//				}
//				);
//		
//		
//		when(repoxRestClient.createDatasourceOAI(sourceargument.capture(), (Provider) anyObject())).thenAnswer(
//				new Answer<Source>(){
//					@Override
//					public Source answer(InvocationOnMock invocation)
//							throws Throwable {
//						sourceargument.getValue().setLastIngest("Mock Date");
//						sourceargument.getValue().setStatus("Mock status");
//						sourceargument.getValue().setType("Mock Type");						
//						ExportDirPath exportDirPath = new ExportDirPath();
//						exportDirPath.setExportDirPath("Mock Path");
//						sourceargument.getValue().setExportDirPath(exportDirPath);
//						RecordIdPolicy recordIdPolicy = new RecordIdPolicy();
//						recordIdPolicy.setType("mockType");
//						sourceargument.getValue().setRecordIdPolicy(recordIdPolicy);
//						sourceargument.getValue().setSample(BigInteger.valueOf(12121));
//						sourceargument.getValue().setId("mockDSID");
//						sourceargument.getValue().setIsSample(false);
//						when(repoxRestClient.retrieveDataSource(sourceargument.getValue().getId())).thenReturn(sourceargument.getValue());
//						return sourceargument.getValue();
//					}
//				}
//				); 
//		
//		when(repoxRestClient.updateDatasourceOAI(sourceargument.capture())).thenAnswer(
//				new Answer<Source>(){
//					@Override
//					public Source answer(InvocationOnMock invocation)
//							throws Throwable {
//						sourceargument.getValue().setLastIngest("Mock Date");
//						sourceargument.getValue().setStatus("Mock status");
//						sourceargument.getValue().setType("Mock Type");
//						ExportDirPath exportDirPath = new ExportDirPath();
//						exportDirPath.setExportDirPath("Mock Path");
//						sourceargument.getValue().setExportDirPath(exportDirPath);
//						RecordIdPolicy recordIdPolicy = new RecordIdPolicy();
//						recordIdPolicy.setType("mockType");
//						sourceargument.getValue().setRecordIdPolicy(recordIdPolicy);
//						sourceargument.getValue().setSample(BigInteger.valueOf(12121));
//						sourceargument.getValue().setId("mockDSID");
//						sourceargument.getValue().setIsSample(false);
//						when(repoxRestClient.retrieveDataSource(sourceargument.getValue().getId())).thenReturn(sourceargument.getValue());
//						return sourceargument.getValue();
//					}
//				}
//				); 
//		
//		when(repoxRestClient.createDatasourceFtp(sourceargument.capture(), (Provider) anyObject())).thenAnswer(
//				new Answer<Source>(){
//					@Override
//					public Source answer(InvocationOnMock invocation)
//							throws Throwable {
//						sourceargument.getValue().setLastIngest("Mock Date");
//						sourceargument.getValue().setStatus("Mock status");
//						sourceargument.getValue().setType("Mock Type");
//						ExportDirPath exportDirPath = new ExportDirPath();
//						exportDirPath.setExportDirPath("Mock Path");
//						sourceargument.getValue().setExportDirPath(exportDirPath);
//						RecordIdPolicy recordIdPolicy = new RecordIdPolicy();
//						recordIdPolicy.setType("mockType");
//						sourceargument.getValue().setRecordIdPolicy(recordIdPolicy);
//						sourceargument.getValue().setSample(BigInteger.valueOf(12121));
//						sourceargument.getValue().setId("mockDSID");
//						sourceargument.getValue().setIsSample(false);
//						when(repoxRestClient.retrieveDataSource(sourceargument.getValue().getId())).thenReturn(sourceargument.getValue());
//						return sourceargument.getValue();
//					}
//				}
//				); 
//		
//		when(repoxRestClient.updateDatasourceFtp(sourceargument.capture())).thenAnswer(
//				new Answer<Source>(){
//					@Override
//					public Source answer(InvocationOnMock invocation)
//							throws Throwable {
//						sourceargument.getValue().setLastIngest("Mock Date");
//						sourceargument.getValue().setStatus("Mock status");
//						sourceargument.getValue().setType("Mock Type");
//						ExportDirPath exportDirPath = new ExportDirPath();
//						exportDirPath.setExportDirPath("Mock Path");
//						sourceargument.getValue().setExportDirPath(exportDirPath);
//						RecordIdPolicy recordIdPolicy = new RecordIdPolicy();
//						recordIdPolicy.setType("mockType");
//						sourceargument.getValue().setRecordIdPolicy(recordIdPolicy);
//						sourceargument.getValue().setSample(BigInteger.valueOf(12121));
//						sourceargument.getValue().setId("mockDSID");
//						sourceargument.getValue().setIsSample(false);
//						when(repoxRestClient.retrieveDataSource(sourceargument.getValue().getId())).thenReturn(sourceargument.getValue());
//						return sourceargument.getValue();
//					}
//				}
//				);
//		
//		when(repoxRestClient.createDatasourceHttp(sourceargument.capture(), (Provider) anyObject())).thenAnswer(
//				new Answer<Source>(){
//					@Override
//					public Source answer(InvocationOnMock invocation)
//							throws Throwable {
//						sourceargument.getValue().setLastIngest("Mock Date");
//						sourceargument.getValue().setStatus("Mock status");
//						sourceargument.getValue().setType("Mock Type");
//						ExportDirPath exportDirPath = new ExportDirPath();
//						exportDirPath.setExportDirPath("Mock Path");
//						sourceargument.getValue().setExportDirPath(exportDirPath);
//						RecordIdPolicy recordIdPolicy = new RecordIdPolicy();
//						recordIdPolicy.setType("mockType");
//						sourceargument.getValue().setRecordIdPolicy(recordIdPolicy);
//						sourceargument.getValue().setSample(BigInteger.valueOf(12121));
//						sourceargument.getValue().setId("mockDSID");
//						sourceargument.getValue().setIsSample(false);
//						when(repoxRestClient.retrieveDataSource(sourceargument.getValue().getId())).thenReturn(sourceargument.getValue());
//						return sourceargument.getValue();
//					}
//				}
//				); 
//		
//		when(repoxRestClient.updateDatasourceHttp(sourceargument.capture())).thenAnswer(
//				new Answer<Source>(){
//					@Override
//					public Source answer(InvocationOnMock invocation)
//							throws Throwable {
//						sourceargument.getValue().setLastIngest("Mock Date");
//						sourceargument.getValue().setStatus("Mock status");
//						sourceargument.getValue().setType("Mock Type");
//						ExportDirPath exportDirPath = new ExportDirPath();
//						exportDirPath.setExportDirPath("Mock Path");
//						sourceargument.getValue().setExportDirPath(exportDirPath);
//						RecordIdPolicy recordIdPolicy = new RecordIdPolicy();
//						recordIdPolicy.setType("mockType");
//						sourceargument.getValue().setRecordIdPolicy(recordIdPolicy);
//						sourceargument.getValue().setSample(BigInteger.valueOf(12121));
//						sourceargument.getValue().setId("mockDSID");
//						sourceargument.getValue().setIsSample(false);
//						when(repoxRestClient.retrieveDataSource(sourceargument.getValue().getId())).thenReturn(sourceargument.getValue());
//						return sourceargument.getValue();
//					}
//				}
//				);
//		
//		when(repoxRestClient.createDatasourceFolder(sourceargument.capture(), (Provider) anyObject())).thenAnswer(
//				new Answer<Source>(){
//					@Override
//					public Source answer(InvocationOnMock invocation)
//							throws Throwable {
//						sourceargument.getValue().setLastIngest("Mock Date");
//						sourceargument.getValue().setStatus("Mock status");
//						sourceargument.getValue().setType("Mock Type");
//						ExportDirPath exportDirPath = new ExportDirPath();
//						exportDirPath.setExportDirPath("Mock Path");
//						sourceargument.getValue().setExportDirPath(exportDirPath);
//						RecordIdPolicy recordIdPolicy = new RecordIdPolicy();
//						recordIdPolicy.setType("mockType");
//						sourceargument.getValue().setRecordIdPolicy(recordIdPolicy);
//						sourceargument.getValue().setSample(BigInteger.valueOf(12121));
//						sourceargument.getValue().setId("mockDSID");
//						sourceargument.getValue().setIsSample(false);
//						when(repoxRestClient.retrieveDataSource(sourceargument.getValue().getId())).thenReturn(sourceargument.getValue());
//						return sourceargument.getValue();
//					}
//				}
//				); 
//		
//		when(repoxRestClient.updateDatasourceFolder(sourceargument.capture())).thenAnswer(
//				new Answer<Source>(){
//					@Override
//					public Source answer(InvocationOnMock invocation)
//							throws Throwable {
//						sourceargument.getValue().setLastIngest("Mock Date");
//						sourceargument.getValue().setStatus("Mock status");
//						sourceargument.getValue().setType("Mock Type");
//						ExportDirPath exportDirPath = new ExportDirPath();
//						exportDirPath.setExportDirPath("Mock Path");
//						sourceargument.getValue().setExportDirPath(exportDirPath);
//						RecordIdPolicy recordIdPolicy = new RecordIdPolicy();
//						recordIdPolicy.setType("mockType");
//						sourceargument.getValue().setRecordIdPolicy(recordIdPolicy);
//						sourceargument.getValue().setSample(BigInteger.valueOf(12121));
//						sourceargument.getValue().setId("mockDSID");
//						sourceargument.getValue().setIsSample(false);
//						when(repoxRestClient.retrieveDataSource(sourceargument.getValue().getId())).thenReturn(sourceargument.getValue());
//						return sourceargument.getValue();
//					}
//				}
//				);
//		
//		
//		when(repoxRestClient.deleteDatasource(stringargument.capture())).thenAnswer(
//				new Answer<Success>(){
//					@Override
//					public Success answer(InvocationOnMock invocation)
//							throws Throwable {
//						Success agg = new Success();
//						agg.setSuccess("true");
//						when(repoxRestClient.retrieveDataSource(stringargument.getValue())).thenReturn(null);
//						return agg;
//					}
//				}
//				);
//		
//		when(repoxRestClient.initiateHarvesting(anyString(), anyBoolean())).thenAnswer(
//				new Answer<Success>(){
//					@Override
//					public Success answer(InvocationOnMock invocation)
//							throws Throwable {
//						Success success = new Success();
//						success.setSuccess("true");
//						return success;
//					}
//				}
//				);
//		
//		when(repoxRestClient.cancelHarvesting(anyString())).thenAnswer(
//				new Answer<Success>(){
//					@Override
//					public Success answer(InvocationOnMock invocation)
//							throws Throwable {
//						Success success = new Success();
//						success.setSuccess("true");
//						return success;
//					}
//				}
//				);
//	
//		
//		when(repoxRestClient.getHarvestingStatus(anyString())).thenAnswer(
//				new Answer<HarvestingStatus>(){
//					@Override
//					public HarvestingStatus answer(InvocationOnMock invocation)
//							throws Throwable {
//						HarvestingStatus status = new HarvestingStatus();
//						Status rstatus = new Status();
//						rstatus.setStatus("mockstatus");
//						status.setStatus(rstatus );
//						return status;
//					}
//				}
//				);
//
//		when(repoxRestClient.getActiveHarvestingSessions()).thenAnswer(
//				new Answer<RunningTasks>(){
//					@Override
//					public RunningTasks answer(InvocationOnMock invocation)
//							throws Throwable {
//						RunningTasks tasks = new RunningTasks();
//						List<DataSource> list = new ArrayList<DataSource>();
//						tasks.setDataSourceList(list);
//						return tasks;
//					}
//				}
//				);
//	}
}
