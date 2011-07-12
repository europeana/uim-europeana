/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
 */
package eu.europeana.uim.repoxclient.rest;

import org.joda.time.DateTime;
import org.springframework.web.client.RestTemplate;


import eu.europeana.uim.repoxclient.jibxbindings.Aggregator;
import eu.europeana.uim.repoxclient.jibxbindings.Aggregators;
import eu.europeana.uim.repoxclient.jibxbindings.DataSources;
import eu.europeana.uim.repoxclient.jibxbindings.Log;
import eu.europeana.uim.repoxclient.jibxbindings.Provider;
import eu.europeana.uim.repoxclient.jibxbindings.DataProviders;
import eu.europeana.uim.repoxclient.jibxbindings.RecordResult;
import eu.europeana.uim.repoxclient.jibxbindings.Response;
import eu.europeana.uim.repoxclient.jibxbindings.RunningTasks;
import eu.europeana.uim.repoxclient.jibxbindings.ScheduleTasks;
import eu.europeana.uim.repoxclient.jibxbindings.Source;
import eu.europeana.uim.repoxclient.jibxbindings.Success;


import eu.europeana.uim.repoxclient.plugin.RepoxRestClient;
import eu.europeana.uim.repoxclient.rest.exceptions.AggregatorOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.DataSourceOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.HarvestingOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.ProviderOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RecordOperationException;
import eu.europeana.uim.repoxclient.rest.exceptions.RepoxException;



/**
 * Class implementing REST functionality for accessing the REPOX repository.
 * 
 * @author Georgios Markakis
 */
public class RepoxRestClientImpl  implements RepoxRestClient {

	private RestTemplate restTemplate;

	private String defaultURI;

	
	
	/*
	 * Aggregator related operations
	 */
	

	/**
	 * 
	 */
	@Override
	public Aggregator createAggregator(Aggregator aggregator)
			throws AggregatorOperationException {
		
		///rest/aggregators/create?name=Judaica&nameCode=093&homepage=http://repox.ist.utl.pt
		
		StringBuffer name = new StringBuffer();
		StringBuffer nameCode = new StringBuffer();
		StringBuffer homepage = new StringBuffer();

		name.append("name=");
		name.append(aggregator.getName().getName());
		nameCode.append("nameCode=");
		nameCode.append(aggregator.getNameCode().getNameCode());
		homepage.append("homepage=");
		homepage.append(aggregator.getUrl().getUrl());
		
		Response resp = invokRestTemplate("/aggregators/create",Response.class,
				name.toString(),nameCode.toString(),homepage.toString());
		
		if (resp.getAggregator() == null) {
			if (resp.getError() != null) {
				throw new AggregatorOperationException(resp.getError());
			} else {
				throw new AggregatorOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getAggregator();
		}
	}


	@Override
	public Success deleteAggregator(String aggregatorId)
			throws AggregatorOperationException {

		StringBuffer id = new StringBuffer();
		id.append("id=");
		id.append(aggregatorId);
		
		Response resp = invokRestTemplate("/aggregators/delete",Response.class,id.toString());
		
		if (resp.getSuccess() == null) {
			if (resp.getError() != null) {
				throw new AggregatorOperationException(resp.getError());
			} else {
				throw new AggregatorOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getSuccess();
		}
		
	}


	@Override
	public Aggregator updateAggregator(Aggregator aggregator)
			throws AggregatorOperationException {
		StringBuffer id = new StringBuffer();
		StringBuffer name = new StringBuffer();
		StringBuffer nameCode = new StringBuffer();
		StringBuffer homepage = new StringBuffer();

		id.append("id=");
		id.append(aggregator.getId());
		name.append("name=");
		name.append(aggregator.getName().getName());
		nameCode.append("nameCode=");
		nameCode.append(aggregator.getNameCode().getNameCode());
		homepage.append("homepage=");
		homepage.append(aggregator.getUrl().getUrl());
		
		Response resp = invokRestTemplate("/aggregators/update",Response.class,
				id.toString(),name.toString(),nameCode.toString(),homepage.toString());
		
		if (resp.getAggregator() == null) {
			if (resp.getError() != null) {
				throw new AggregatorOperationException(resp.getError());
			} else {
				throw new AggregatorOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getAggregator();
		}
		
	}


	@Override
	public Aggregators retrieveAggregators() throws AggregatorOperationException {
		Response resp = invokRestTemplate("/aggregators/list",Response.class);
		
		if (resp.getAggregators() == null) {
			if (resp.getError() != null) {
				throw new AggregatorOperationException(resp.getError());
			} else {
				throw new AggregatorOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getAggregators();
		}
	}

	
	
	
	/*
	 * Provider related operations
	 */
	
	
	@Override
	public Provider createProvider(Provider prov,Aggregator agr) throws ProviderOperationException {
		//http://bd2.inesc-id.pt:8080/repox2/rest/dataProviders/create?aggregatorId=AGGREGATOR_ID&
		//	name=NAME&description=DESCRIPTION&country=2_LETTERS_COUNTRY&nameCode=NAME_CODE&url=URL&dataSetType=DATA_SET_TYPE
		
		StringBuffer aggregatorId = new StringBuffer();
		StringBuffer name = new StringBuffer();
		StringBuffer description = new StringBuffer();	
		StringBuffer country = new StringBuffer();		
		StringBuffer nameCode = new StringBuffer();
		StringBuffer homepage = new StringBuffer();
		StringBuffer datasetType = new StringBuffer();		
		
		
		aggregatorId.append("aggregatorId=");
		aggregatorId.append(agr.getId());
		name.append("name=");
		name.append(prov.getName().getName());
		description.append("description=");
		description.append(prov.getDescription().getDescription());
		country.append("country=");
		country.append(prov.getCountry().getCountry());
		nameCode.append("nameCode=");
		nameCode.append(prov.getNameCode().getNameCode());
		homepage.append("url=");
		homepage.append(prov.getUrl().getUrl());
		datasetType.append("dataSetType=");
		datasetType.append(prov.getType().getType());
		
		Response resp = invokRestTemplate("/dataProviders/create",Response.class,
				aggregatorId.toString(),name.toString(),description.toString(),
				country.toString(),nameCode.toString(),homepage.toString(),datasetType.toString());
		
		
		if (resp.getProvider() == null) {
			if (resp.getError() != null) {
				throw new ProviderOperationException(resp.getError());
			} else {
				throw new ProviderOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getProvider();
		}
	}


	
	@Override
	public Success deleteProvider(Provider prov) throws ProviderOperationException {
		StringBuffer providerId = new StringBuffer();
		providerId.append("id=");
		providerId.append(prov.getId());
		
		Response resp = invokRestTemplate("/dataProviders/delete",Response.class,
				providerId.toString());

		if (resp.getSuccess() == null) {
			if (resp.getError() != null) {
				throw new ProviderOperationException(resp.getError());
			} else {
				throw new ProviderOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getSuccess();
		}
	}


	

	@Override
	public Provider updateProvider(Provider prov) throws ProviderOperationException {

		StringBuffer provId = new StringBuffer();
		StringBuffer name = new StringBuffer();
		StringBuffer description = new StringBuffer();	
		StringBuffer country = new StringBuffer();		
		StringBuffer nameCode = new StringBuffer();
		StringBuffer homepage = new StringBuffer();
		StringBuffer datasetType = new StringBuffer();		
		
		
		provId.append("id=");
		provId.append(prov.getId());
		name.append("name=");
		name.append(prov.getName().getName());
		description.append("description=");
		description.append(prov.getDescription().getDescription());
		country.append("country=");
		country.append(prov.getCountry().getCountry());
		nameCode.append("nameCode=");
		nameCode.append(prov.getNameCode().getNameCode());
		homepage.append("url=");
		homepage.append(prov.getUrl().getUrl());
		datasetType.append("dataSetType=");
		datasetType.append(prov.getType().getType());
		
		Response resp = invokRestTemplate("/dataProviders/update",Response.class,
				provId.toString(),name.toString(),description.toString(),
				country.toString(),nameCode.toString(),homepage.toString(),datasetType.toString());
		
		
		if (resp.getProvider() == null) {
			if (resp.getError() != null) {
				throw new ProviderOperationException(resp.getError());
			} else {
				throw new ProviderOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getProvider();
		}
			
	}


	@Override
	public DataProviders retrieveProviders() throws ProviderOperationException {
		Response resp = invokRestTemplate("/dataProviders/list",Response.class);
		return resp.getDataProviders();
	}
	
	
	
	/*
	 * Datasources related operations
	 */
	
	
	/**
	 * 
	 * @return DataSources the available datasources
	 * @throws RepoxException
	 */
	public DataSources retrieveDataSources() throws DataSourceOperationException {

		Response resp = invokRestTemplate("/dataSources/list",Response.class);

		if (resp.getDataSources() == null) {
			if (resp.getError() != null) {
				throw new DataSourceOperationException(resp.getError());
			} else {
				throw new DataSourceOperationException("Unidentified Repox Error");
			}
		} else {

			return resp.getDataSources();
		}
	}
	
	
	// Public methods exposed by the service
	
	public Source createDatasource(Source ds,Provider prov)
			throws DataSourceOperationException {

		///rest/dataSources/createOai?dataProviderId=DPRestr0&id=bdaSet&description=Biblioteca Digital Do Alentejo&
		//nameCode=00123&name=Alentejo&exportPath=D:/Projectos/repoxdata_new&schema=http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd&
		//namespace=http://www.europeana.eu/schemas/ese/&metadataFormat=ese&oaiURL=http://bd1.inesc-id.pt:8080/repoxel/OAIHandler&oaiSet=bda
		
		
		///rest/dataSources/createZ3950Timestamp?dataProviderId=DPRestr0&id=z3950TimeTest&description=test Z39.50 with time stamp&nameCode=00130&
		//name=Z3950-TimeStamp&exportPath=D:/Projectos/repoxdata_new&schema=info:lc/xmlns/marcxchange-v1.xsd&
		//namespace=info:lc/xmlns/marcxchange-v1&address=193.6.201.205&port=1616&database=B1&user=&password=&
		//recordSyntax=usmarc&charset=UTF-8&earliestTimestamp=20110301&recordIdPolicy=IdGenerated&idXpath=&namespacePrefix=&namespaceUri=
		
		///rest/dataSources/createZ3950IdSequence?dataProviderId=DPRestr0&id=z3950IdSeqTest&description=test%20Z39.50%20with%20id%20sequence&nameCode=00129&
		//name=Z3950-IdSeq&exportPath=D:/Projectos/repoxdata_new&schema=info:lc/xmlns/marcxchange-v1.xsd&namespace=info:lc/xmlns/marcxchange-v1&
		//address=aleph.lbfl.li&port=9909&database=LLB_IDS&user=&password=&recordSyntax=usmarc&charset=UTF-8&maximumId=6000&
		//recordIdPolicy=IdGenerated&idXpath=&namespacePrefix=&namespaceUri=
		
		///rest/dataSources/createFtp?dataProviderId=DPRestr0&id=ftpTest&description=test FTP data source&nameCode=00124&name=FTP&
		//exportPath=D:/Projectos/repoxdata_new&schema=http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd&
		//namespace=http://www.europeana.eu/schemas/ese/&metadataFormat=ese&isoFormat=&charset=&recordIdPolicy=IdGenerated&idXpath=&namespacePrefix=&
		//namespaceUri=&recordXPath=record&server=bd1.inesc-id.pt&user=ftp&password=pmath2010.&ftpPath=/Lizbeth
		
		///rest/dataSources/createHttp?dataProviderId=DPRestr0&id=httpTest&description=test HTTP data source&nameCode=00124&name=HTTP&
		//exportPath=D:/Projectos/repoxdata_new&schema=http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd&
		//namespace=http://www.europeana.eu/schemas/ese/&metadataFormat=ese&isoFormat=&charset=&recordIdPolicy=IdGenerated&idXpath=&namespacePrefix=&
		//namespaceUri=&recordXPath=record&url=http://digmap2.ist.utl.pt:8080/index_digital/contente/09428_Ag_DE_ELocal.zip
		
		///rest/dataSources/createFolder?dataProviderId=DPRestr0&id=folderTest&description=test%20Folder%20data%20source&nameCode=00124&
		//name=Folder&exportPath=D:/Projectos/repoxdata_new&schema=info:lc/xmlns/marcxchange-v1.xsd&namespace=info:lc/xmlns/marcxchange-v1&metadataFormat=ISO2709&
		//isoFormat=pt.utl.ist.marc.iso2709.IteratorIso2709&charset=UTF-8&recordIdPolicy=IdExtracted&idXpath=/mx:record/mx:controlfield[@tag=%22001%22]&
		//namespacePrefix=mx&namespaceUri=info:lc/xmlns/marcxchange-v1&recordXPath=&folder=C:\folder
		
		return ds;
	}

	
	
	public Success deleteDatasource(Source ds)
			throws DataSourceOperationException {
		///rest/dataSources/delete?id=ftpTest
		
				return null;
	}

	
	public Source updateDatasource(Source ds)
			throws DataSourceOperationException {
		///rest/dataSources/updateOai?id=bdaSet&description=222Biblioteca Digital Do Alentejo&nameCode=333300123&name=4444Alentejo&
		//exportPath=D:/Projectos/repoxdata_new2&schema=http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd&namespace=http://www.europeana.eu/schemas/ese/&metadataFormat=ese&
		//oaiURL=http://bd1.inesc-id.pt:8080/repoxel/OAIHandler&oaiSet=bda
		
		///rest/dataSources/updateZ3950Timestamp?id=z3950TimeTest&description=new test Z39.50 with time stamp&nameCode=99900130&name=Z3950-TimeStampWorking&
		//exportPath=D:/Projectos/repoxdata_new&schema=info:lc/xmlns/marcxchange-v1.xsd&namespace=info:lc/xmlns/marcxchange-v1&address=193.6.201.205&
		//port=1616&database=B1&user=&password=&recordSyntax=usmarc&charset=UTF-8&earliestTimestamp=20110301&recordIdPolicy=IdGenerated&idXpath=&namespacePrefix=&namespaceUri=
		
		///rest/dataSources/updateZ3950IdList?id=z3950IdFile&description=new test Z39.50 with id list&nameCode=001245555&name=Z3950-IdFilenew&exportPath=D:/Projectos/repoxdata_new1&
		//schema=info:lc/xmlns/marcxchange-v1.xsd&namespace=info:lc/xmlns/marcxchange-v1&address=aleph.lbfl.li&port=9909&database=LLB_IDS&user=&password=&
		//recordSyntax=usmarc&charset=UTF-8&filePath=C:\folderZ3950\newFile.txt&recordIdPolicy=IdGenerated&idXpath=&namespacePrefix=&namespaceUri=
		
		///rest/dataSources/updateZ3950IdSequence?id=z3950IdSeqTest&description=newtest Z39.50 with id sequence&nameCode=222200129&name=NEWZ3950-IdSeq&
		//exportPath=D:/Projectos/repoxdata_new21&schema=info:lc/xmlns/marcxchange-v1.xsd&namespace=info:lc/xmlns/marcxchange-v1&address=aleph.lbfl.li&port=9909&
		//database=LLB_IDS&user=&password=&recordSyntax=usmarc&charset=UTF-8&maximumId=300&recordIdPolicy=IdGenerated&idXpath=&namespacePrefix=&namespaceUri=
		
		///rest/dataSources/updateFtp?id=ftpTest&description=newtest FTP data source&nameCode=555555500124&name=FTP&exportPath=D:/Projectos/repoxdata_new21212121&
		//schema=http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd&namespace=http://www.europeana.eu/schemas/ese/&metadataFormat=ese&isoFormat=&charset=&
		//recordIdPolicy=IdGenerated&idXpath=&namespacePrefix=&namespaceUri=&recordXPath=record&server=bd1.inesc-id.pt&user=ftp&password=pmath2010.&ftpPath=/Lizbeth
		
		///rest/dataSources/updateHttp?id=httpTest&description=NEWWWWtest HTTP data source&nameCode=999900124&name=HTTP111&exportPath=D:/Projectos/repoxdata_new&
		//schema=http://www.europeana.eu/schemas/ese/ESE-V3.3.xsd&namespace=http://www.europeana.eu/schemas/ese/&metadataFormat=ese&isoFormat=&charset=&recordIdPolicy=IdGenerated&
		//idXpath=&namespacePrefix=&namespaceUri=&recordXPath=record&url=http://digmap2.ist.utl.pt:8080/index_digital/contente/09428_Ag_DE_ELocal.zip
		
		///rest/dataSources/updateFolder?id=folderTest&description=test%20Folder%20data%20source3333333&nameCode=4444444444400124&name=Folder&exportPath=D:/Projectos/repoxdata_new&
		//schema=info:lc/xmlns/marcxchange-v1.xsd&namespace=info:lc/xmlns/marcxchange-v1&metadataFormat=ISO2709&isoFormat=pt.utl.ist.marc.iso2709.IteratorIso2709&charset=UTF-8&
		//recordIdPolicy=IdExtracted&idXpath=/mx:record/mx:controlfield[@tag=%22001%22]&namespacePrefix=mx&namespaceUri=info:lc/xmlns/marcxchange-v1&recordXPath=&folder=C:\folderNew
		
		return ds;
	}

	
	
	/*
	 * Record related operations
	 */
	
	
	
	public RecordResult retrieveRecord(String recordString)
			throws RecordOperationException{

		return null;
	}

	
	@Override
	public Success saveRecord(String recordID, Source ds, String recordXML)
			throws RecordOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Success markRecordAsDeleted(String recordID)
			throws RecordOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Success eraseRecord(String recordID) throws RecordOperationException {
		// TODO Auto-generated method stub
		return null;
	}
	
	
	
	
	/*
	 * Harvest Control/Monitoring operations
	 */
	
	@Override
	public Success initiateHarvesting(Source ds)
			throws HarvestingOperationException{

		///rest/dataSources/startIngest?id=bmfinancas
		
		return null;
	}

	@Override
	public Success initiateHarvesting(Source ds,DateTime ingestionDate) 
	       throws HarvestingOperationException{

		///rest/dataSources/scheduleIngest?id=bmfinancas&firstRunDate=06-07-2011&firstRunHour=17:43&frequency=Daily&xmonths=&fullIngest=true
		return null;
	}

	
	
	@Override
	public Success cancelHarvesting(Source ds)
			throws HarvestingOperationException {
		
		///rest/dataSources/stopIngest?id=bmfinancas

		return null;
	}


	@Override
	public Success getHarvestingStatus(Source ds)
			throws HarvestingOperationException {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public RunningTasks getActiveHarvestingSessions()
			throws HarvestingOperationException {
		////rest/dataSources/harvesting
		return null;
	}


	@Override
	public ScheduleTasks getScheduledHarvestingSessions()
			throws HarvestingOperationException {

		///rest/dataSources/scheduleList?id=bmfinancas

		return null;
	}


	@Override
	public Log getHarvestLog(Source ds)
			throws HarvestingOperationException {
		///rest/dataSources/log?id=httpTest
		
		return null;
	}
	


	// Private Methods

	/**
	 * Auxiliary method for invoking a REST operation
	 * 
	 * @param <S>
	 *            the return type
	 * @param wsOperation
	 * @return
	 */
	private <S> S invokRestTemplate(String restOperation,Class<S> responseClass) {

		StringBuffer operation = new StringBuffer();
		operation.append(defaultURI);
		operation.append(restOperation);
		S restResponse = restTemplate.getForObject(operation.toString(), responseClass);

		return restResponse;
	}

	/**
	 * Auxiliary method for invoking a REST operation with parameters
	 * 
	 * @param <S>
	 *            the return type
	 * @param wsOperation
	 * @return
	 */
	private <S> S invokRestTemplate(String restOperation,Class<S> responseClass,String... params) {

		StringBuffer operation = new StringBuffer();
		operation.append(defaultURI);
		operation.append(restOperation);
		operation.append("?");
		
		for (int i=0; i< params.length; i++){
			if (i != 0){
				operation.append("&");
			}
			operation.append(params[i]);
		}
		
		
		S restResponse = restTemplate.getForObject(operation.toString(), responseClass);

		return restResponse;
	}
	// Getters & Setters

	public void setRestTemplate(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public RestTemplate getRestTemplate() {
		return restTemplate;
	}

	public void setDefaultURI(String defaultURI) {
		this.defaultURI = defaultURI;
	}

	public String getDefaultURI() {
		return defaultURI;
	}












}
