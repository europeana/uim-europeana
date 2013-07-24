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

package eu.europeana.uim.sugarcrmclient.plugin;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.soap.MessageFactory;
import javax.xml.soap.SOAPException;

import org.jibx.runtime.JiBXException;
import org.springframework.oxm.Marshaller;
import org.springframework.oxm.Unmarshaller;
import org.springframework.oxm.jibx.JibxMarshaller;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.europeana.uim.orchestration.ActiveExecution;
import eu.europeana.uim.orchestration.Orchestrator;
import eu.europeana.uim.Registry;
import eu.europeana.uim.storage.StorageEngine;
import eu.europeana.uim.storage.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.UimDataSet;
import eu.europeana.uim.sugar.ConnectionStatus;
import eu.europeana.uim.sugar.FileAttachmentException;
import eu.europeana.uim.sugar.LoginFailureException;
import eu.europeana.uim.sugar.PollingListener;
import eu.europeana.uim.sugar.QueryResultException;
import eu.europeana.uim.sugar.SugarCrmQuery;
import eu.europeana.uim.sugar.SugarCrmRecord;
import eu.europeana.uim.sugar.SugarCrmService;
import eu.europeana.uim.sugar.model.DatasetStates;
import eu.europeana.uim.sugar.model.UpdatableField;
import eu.europeana.uim.sugarcrmclient.internal.ExtendedSaajSoapMessageFactory;
import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import eu.europeana.uim.sugarcrmclient.internal.helpers.PropertyReader;
import eu.europeana.uim.sugarcrmclient.internal.helpers.UimConfigurationProperty;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetRelationships;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetRelationshipsResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Login;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NameValue;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NameValueList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NoteAttachment;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SelectFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetNoteAttachment;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetNoteAttachmentResponse;
import eu.europeana.uim.sugarcrmclient.plugin.objects.SugarCrmRecordImpl;
import eu.europeana.uim.common.BlockingInitializer;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.EuropeanaRetrievableField;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.EuropeanaUpdatableField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.SimpleSugarCrmQuery;
import eu.europeana.uim.sugarcrmclient.ws.SugarWsClientImpl;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.JIXBLoginFailureException;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.JIXBQueryResultException;
import eu.europeana.uim.workflow.Workflow;
import org.apache.commons.lang.StringUtils;


/**
 * This is the implementing class for the OSGI based SugarCrm plugin OSGI service.
 * 
 * @author Georgios Markakis
 */
public class SugarCRMServiceImpl implements SugarCrmService {

    private SugarWsClientImpl                      sugarwsClient;
    private Registry                               registry;
    private LinkedHashMap<String, PollingListener> pollingListeners;
    private static final String                    DSMODULENAME = "Opportunities";

    /**
     * Constructor
     */
    public SugarCRMServiceImpl() {
        this.pollingListeners = new LinkedHashMap<String, PollingListener>();
        
        
		BlockingInitializer initializer = new BlockingInitializer() {
			@Override
			public void initializeInternal() {
				MessageFactory mf = null;
				try {
				   mf = MessageFactory.newInstance();
				} catch (SOAPException e1) {
					e1.printStackTrace();
				}
				
				ExtendedSaajSoapMessageFactory mfactory = new ExtendedSaajSoapMessageFactory(mf);
				WebServiceTemplate webServiceTemplate = new WebServiceTemplate(mfactory);

				JibxMarshaller marshaller = new JibxMarshaller();

				marshaller.setStandalone(true);
				marshaller.setTargetClass(eu.europeana.uim.sugarcrmclient.jibxbindings.Login.class);
				marshaller.setTargetClass(eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntries.class);
				marshaller.setEncoding("UTF-8");
				marshaller.setTargetPackage("eu.europeana.uim.sugarcrmclient.jibxbindings");

				try {
					marshaller.afterPropertiesSet();
				} catch (JiBXException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				System.out.println(marshaller.supports(eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntries.class));
				System.out.println(marshaller.supports(eu.europeana.uim.sugarcrmclient.jibxbindings.Login.class));
				
				webServiceTemplate.setMarshaller(marshaller);
				webServiceTemplate.setUnmarshaller(marshaller);
				
				sugarwsClient = new SugarWsClientImpl();
				String userName = PropertyReader.getProperty(UimConfigurationProperty.SUGARCRM_USERNAME);
				String password = PropertyReader.getProperty(UimConfigurationProperty.SUGARCRM_PASSWORD);
				String uri = PropertyReader.getProperty(UimConfigurationProperty.SUGARCRM_HOST);
				webServiceTemplate.setDefaultUri(uri);
				sugarwsClient.setUsername(userName);
				sugarwsClient.setPassword(password);
				sugarwsClient.setWebServiceTemplate(webServiceTemplate);

				try {
					sugarwsClient.setSessionID(sugarwsClient.login(ClientUtils.createStandardLoginObject(userName,password)));
				} catch (JIXBLoginFailureException e) {
					sugarwsClient.setSessionID("-1");
					e.printStackTrace();
				} catch (Exception e){
					e.printStackTrace();
				}

				System.out.println("Done");
			}

		};
		initializer.initialize(SugarWsClientImpl.class
				.getClassLoader());
        
    
    
    }
    
    /*
     * (non-Javadoc)
     * 
     * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#showConnectionStatus()
     */
    @Override
    public ConnectionStatus showConnectionStatus() {

        ConnectionStatus cstatus = new ConnectionStatus();

        cstatus.setDefaultURI(sugarwsClient.getDefaultUri());
        cstatus.setSessionID(sugarwsClient.getSessionID());

        return cstatus;
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#updateSession()
     */
    @Override
    public String updateSession(String username, String password) throws LoginFailureException {
    	
    	if(username == null){
    		username = sugarwsClient.getUsername();
    	}
    	
    	if(password ==  null){
    		password = sugarwsClient.getPassword();
    	}
    	
        StringBuffer connectionInfo = new StringBuffer();
        Login login = ClientUtils.createStandardLoginObject(username, password);
        String newsessionID = sugarwsClient.login(login);
        sugarwsClient.setSessionID(newsessionID);
        connectionInfo.append(newsessionID);
        return connectionInfo.toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#changeEntryStatus(java.lang.String,
     * eu.europeana.uim.sugarcrmclient.plugin.objects.data.EuropeanaDatasetStates)
     */
    @Override
    public void changeEntryStatus(String recordId, DatasetStates state) throws QueryResultException {
        alterSugarCRMItemStatus(recordId, state.getSysId());
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#updateRecordData(java.lang.String,
     * java.util.HashMap)
     */
    @Override
    public void updateRecordData(String recordID, HashMap<UpdatableField, String> values)
            throws QueryResultException {

        SetEntry request = new SetEntry();
        ArrayList<NameValue> nvList = new ArrayList<NameValue>();
        Iterator<?> it = values.entrySet().iterator();

        // First add the id name\value pair
        NameValue nvid = new NameValue();
        nvid.setName("id");
        nvid.setValue(recordID);
        nvList.add(nvid);

        while (it.hasNext()) {
            @SuppressWarnings("unchecked")
            Map.Entry<EuropeanaUpdatableField, String> pairs = (Map.Entry<EuropeanaUpdatableField, String>)it.next();
            NameValue nv = new NameValue();
            nv.setName(pairs.getKey().getFieldId());
            nv.setValue(pairs.getValue());

            nvList.add(nv);
        }

        NameValueList valueList = ClientUtils.generatePopulatedNameValueList(nvList);

        request.setNameValueList(valueList);
        request.setModuleName("Opportunities");
        request.setSession(sugarwsClient.getSessionID());

        sugarwsClient.setentry(request);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#updateRecordData(eu.europeana.uim.
     * sugarcrmclient.plugin.objects.SugarCrmRecordImpl)
     */
    @Override
    public void updateRecordData(SugarCrmRecord record) throws QueryResultException {
        HashMap<UpdatableField, String> values = new HashMap<UpdatableField, String>();

        String recordId = record.getItemValue(EuropeanaRetrievableField.ID);

        values.put(EuropeanaUpdatableField.AMOUNT,
                record.getItemValue(EuropeanaUpdatableField.AMOUNT));
        values.put(EuropeanaUpdatableField.INGESTED_IMAGE,
                record.getItemValue(EuropeanaUpdatableField.INGESTED_IMAGE));
        values.put(EuropeanaUpdatableField.INGESTED_SOUND,
                record.getItemValue(EuropeanaUpdatableField.INGESTED_SOUND));
        values.put(EuropeanaUpdatableField.INGESTED_TEXT,
                record.getItemValue(EuropeanaUpdatableField.INGESTED_TEXT));
        values.put(EuropeanaUpdatableField.INGESTED_VIDEO,
                record.getItemValue(EuropeanaUpdatableField.INGESTED_VIDEO));
        values.put(EuropeanaUpdatableField.NEXT_STEP,
                record.getItemValue(EuropeanaUpdatableField.NEXT_STEP));
        values.put(EuropeanaUpdatableField.TOTAL_INGESTED,
                record.getItemValue(EuropeanaUpdatableField.TOTAL_INGESTED));

        updateRecordData(recordId, values);
    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#retrieveRecord(java.lang.String)
     */
    @Override
    public SugarCrmRecord retrieveRecord(String id) throws QueryResultException {
        GetEntryList request = new GetEntryList();

        SelectFields fields = new SelectFields(); // We want to retrieve all fields
        request.setSelectFields(fields);
        request.setModuleName(DSMODULENAME);
        request.setSession(sugarwsClient.getSessionID());
        request.setOrderBy(EuropeanaRetrievableField.DATE_ENTERED.getFieldId());
        request.setMaxResults(1);
        request.setOffset(0);

        request.setQuery("(opportunities.id LIKE '" + id + "')");

        GetEntryListResponse response = sugarwsClient.getentrylist(request);

        ArrayList<Element> list = (ArrayList<Element>)response.getReturn().getEntryList().getArray().getAnyList();

        Element record = list.get(0);

        if (record != null) {

            return SugarCrmRecordImpl.getInstance(record);
        } else {
            return null;
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#retrieveRecords(eu.europeana.uim.
     * sugarcrmclient.plugin.objects.queries.SugarCrmQuery)
     */
    @Override
    public List<SugarCrmRecord> retrieveRecords(SugarCrmQuery query) throws QueryResultException {

        GetEntryList request = new GetEntryList();

        SelectFields fields = new SelectFields(); // We want to retrieve all fields
        request.setSelectFields(fields);
        request.setModuleName(DSMODULENAME);
        request.setSession(sugarwsClient.getSessionID());
        request.setOrderBy(query.getOrderBy().getFieldId());
        request.setMaxResults(query.getMaxResults());
        request.setOffset(query.getOffset());

        request.setQuery(query.toString());

        GetEntryListResponse response = sugarwsClient.getentrylist(request);

        ArrayList<Element> list = null;

        if (response.getReturn().getEntryList().getArray() != null) {
            list = (ArrayList<Element>)response.getReturn().getEntryList().getArray().getAnyList();
        } else {
            list = new ArrayList<Element>();
        }
        ArrayList<SugarCrmRecord> returnList = wrapDomElements2Objects(list);

        return returnList;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#initWorkflowFromRecord(java.lang.String
     * , eu.europeana.uim.sugarcrmclient.plugin.objects.SugarCrmRecordImpl,
     * eu.europeana.uim.sugarcrmclient.plugin.objects.data.EuropeanaDatasetStates)
     */
    @Override
    public  Workflow initWorkflowFromRecord(String worklfowName, SugarCrmRecord record,
            DatasetStates endstate) throws QueryResultException, StorageEngineException {

        StorageEngine<?> engine = registry.getStorageEngine();
        @SuppressWarnings("unchecked")
		Workflow<UimDataSet<String>, String> w = (Workflow<UimDataSet<String>, String>) registry.getWorkflow(worklfowName);

        @SuppressWarnings("unchecked")
		Collection<String> dataset = inferCollection(engine, record);
        
        Orchestrator<String> orch = (Orchestrator<String>) registry.getOrchestrator();
        
        ActiveExecution<UimDataSet<String>, String> execution = orch.executeWorkflow(w, dataset);   //executeWorkflow(w,dataset);
        alterSugarCRMItemStatus(dataset.getMnemonic(), endstate.getSysId());

        execution.waitUntilFinished();

        return w;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#initWorkflowsFromRecords(java.lang
     * .String, eu.europeana.uim.sugarcrmclient.plugin.objects.data.EuropeanaDatasetStates,
     * eu.europeana.uim.sugarcrmclient.plugin.objects.data.EuropeanaDatasetStates)
     */
    @Override
    public List<Workflow<?,?>> initWorkflowsFromRecords(String worklfowName, DatasetStates currentstate,
            DatasetStates endstate) throws QueryResultException, StorageEngineException {

        List<Workflow<?, ?>> wfs = new ArrayList<Workflow<?, ?>>();

        SimpleSugarCrmQuery query = new SimpleSugarCrmQuery(currentstate);
        query.setMaxResults(1000);
        query.setOffset(0);
        query.setOrderBy(EuropeanaRetrievableField.DATE_ENTERED);

        List<SugarCrmRecord> relevantRecords = retrieveRecords(query);

        for (SugarCrmRecord record : relevantRecords) {
            Workflow ws = initWorkflowFromRecord(worklfowName, record, endstate);
            wfs.add(ws);
        }

        return wfs;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#createProviderFromRecord(eu.europeana
     * .uim.sugarcrmclient.plugin.objects.SugarCrmRecordImpl)
     */
    @Override
    public Provider<?> updateProviderFromRecord(SugarCrmRecord record)
            throws StorageEngineException {

        StorageEngine<?> engine = registry.getStorageEngine();

        HashMap<String, String> providerInfo = new HashMap<String, String>();

        try {
            providerInfo = getProviderInfoMap(record.getItemValue(EuropeanaRetrievableField.ID).toString());

        } catch (QueryResultException e) {
            e.printStackTrace();
        }

        String providerName = record.getItemValue(EuropeanaRetrievableField.ORGANIZATION_NAME); 
        String mnemonicCode = providerInfo.get("identifier"); 
        String countryCode = record.getItemValue(EuropeanaRetrievableField.COUNTRY).toLowerCase(); 
        String harvestUrl = record.getItemValue(EuropeanaUpdatableField.HARVEST_URL); 
        String metadataformat = record.getItemValue(EuropeanaUpdatableField.METADATA_FORMAT); 
       
        //Try to see if the provider with the same mnemonic has already been registered 
        Provider cuurprovider = engine.findProvider(mnemonicCode);
        
        if (cuurprovider == null) {
        	
        	List<?> providers = engine.getAllProviders();
        	
        	
        	for(Object provider: providers){
        		
        		Provider cpprovider = (Provider) provider;
        		
        		//If we encounter a provider that has the same name but different menmonic with the previous
        		//with another provider then we assume that the same provider had his identifier updated 
        		if(providerName.equals(cpprovider.getName()) && !mnemonicCode.equals(cpprovider.getMnemonic())){
        			cuurprovider = cpprovider;
        		}
        	}
            
        }
        
        //Do a second check here if the provider has not been detected so far then create a new one
        if (cuurprovider == null) {
        cuurprovider = engine.createProvider();
        }
        
        cuurprovider.setAggregator(false);
        cuurprovider.setMnemonic(mnemonicCode);
        cuurprovider.setName(providerName);
        cuurprovider.setOaiBaseUrl(harvestUrl);
        cuurprovider.setOaiMetadataPrefix(metadataformat);
        
        String encodedDescription = null;
        try {
            encodedDescription = URLEncoder.encode(providerInfo.get("description"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            encodedDescription = "None";
            e.printStackTrace();
        }
        cuurprovider.putValue(ControlledVocabularyProxy.PROVIDERDESCRIPTION, encodedDescription);
        cuurprovider.putValue(ControlledVocabularyProxy.PROVIDERWEBSITE, providerInfo.get("website"));
        cuurprovider.putValue(ControlledVocabularyProxy.SUGARCRMID, providerInfo.get("sugarCRMID"));
        cuurprovider.putValue(ControlledVocabularyProxy.PROVIDERTYPE, providerInfo.get("type"));
        cuurprovider.putValue(ControlledVocabularyProxy.PROVIDERDEASENT, providerInfo.get("providerdeaSent"));
        cuurprovider.putValue(ControlledVocabularyProxy.PROVIDERDEASIGNED, providerInfo.get("providerdeaSigned"));
        
        
        cuurprovider.putValue(ControlledVocabularyProxy.PROVIDERCOUNTRY, countryCode);
        engine.updateProvider(cuurprovider);
        engine.checkpoint();

        return cuurprovider;
    }

    /**
     * @param recordID
     * @return
     * @throws JIXBQueryResultException
     */
    private HashMap<String, String> getProviderInfoMap(String recordID) throws QueryResultException {

        GetRelationships request = new GetRelationships();

        request.setDeleted(0);
        request.setModuleId(recordID);
        request.setModuleName("Opportunities");
        request.setRelatedModule("Accounts");
        request.setRelatedModuleQuery("");
        request.setSession(sugarwsClient.getSessionID());

        GetRelationshipsResponse resp = sugarwsClient.getrelationships(request);

        if (resp.getReturn().getIds().getArray() == null) { throw new JIXBQueryResultException(
                "Could not retrieve related provider information from 'Accounts module' "); }

        List<Element> el = resp.getReturn().getIds().getArray().getAnyList();

        NodeList idFieldList = el.get(0).getElementsByTagName("id");

        Node node = idFieldList.item(0);

        if (node == null) { throw new JIXBQueryResultException(
                "Could not retrieve related provider information from 'Accounts module' "); }

        String provideriD = node.getTextContent();

        HashMap<String, String> providerInfo = (HashMap<String, String>) retrieveproviderinfo(provideriD);

        return providerInfo;

    }

    /**
     * @param providerID
     * @return
     * @throws JIXBQueryResultException
     */
    private Map<String, String> retrieveproviderinfo(String providerID)
            throws QueryResultException {

        HashMap<String, String> returnInfo = new HashMap<String, String>();

        GetEntry request = new GetEntry();
        request.setId(providerID);
        request.setModuleName("Accounts");
        request.setSession(sugarwsClient.getSessionID());
        SelectFields selectFields = new SelectFields();
        request.setSelectFields(selectFields);

        GetEntryResponse response = sugarwsClient.getentry(request);

        Element el = response.getReturn().getEntryList().getArray().getAnyList().get(0);

        String identifier = extractFromElement("name_id_c", el);
        String description = extractFromElement("description", el);
        String name = extractFromElement("name", el);
        String website = extractFromElement("website", el);
        String type = extractFromElement("account_type", el);
        String country = extractFromElement("country_c", el);
        String providerdeaSent = extractFromElement("dea_sent_c", el);
        String providerdeaSigned = extractFromElement("dea_signed_c", el);

        //Insert values in Provider Object
        returnInfo.put("sugarCRMID", providerID);
        returnInfo.put("identifier", identifier);
        returnInfo.put("description", description);
        returnInfo.put("name", name);
        returnInfo.put("website", website);
        returnInfo.put("type", type);
        returnInfo.put("country", country);
        returnInfo.put("providerdeaSent", providerdeaSent);
        returnInfo.put("providerdeaSigned", providerdeaSigned);

        return returnInfo;
    }

    
    
    /**
     * @param value
     * @param el
     * @return
     */
    private String extractFromElement(String value, Element el) {
        NodeList nl = el.getElementsByTagName("item");

        for (int i = 0; i < nl.getLength(); i++) {
            Node nd = nl.item(i);
            String textcontent = nd.getChildNodes().item(0).getTextContent();
            if (value.equals(textcontent)) { return nd.getChildNodes().item(1).getTextContent(); }

        }
        return null;
    }

    
    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#createCollectionFromRecord(eu.europeana
     * .uim.sugarcrmclient.plugin.objects.SugarCrmRecordImpl, eu.europeana.uim.store.Provider)
     */
    @Override
    public Collection<?> updateCollectionFromRecord(SugarCrmRecord record, Provider provider)
            throws StorageEngineException {

        StorageEngine<?> engine = registry.getStorageEngine();


        String mnemonicCode = record.getItemValue(EuropeanaRetrievableField.NAME).split("_")[0];
        String fulname = record.getItemValue(EuropeanaRetrievableField.NAME);
        StringUtils.replaceChars(fulname, "_", " ");
        StringUtils.replace(fulname, mnemonicCode, "");

        String collectionName = fulname;
        String collectionID = record.getItemValue(EuropeanaRetrievableField.NAME); 
        String countryCode = record.getItemValue(EuropeanaRetrievableField.COUNTRY).toLowerCase(); 
        String harvestUrl = record.getItemValue(EuropeanaUpdatableField.HARVEST_URL);
        String set = record.getItemValue(EuropeanaUpdatableField.SETSPEC);
        String sugarCRMID = record.getItemValue(EuropeanaRetrievableField.ID);
        String collectionDescription = record.getItemValue(EuropeanaRetrievableField.DESCRIPTION); 
        String ingestionDate = record.getItemValue(EuropeanaRetrievableField.EXPECTED_INGESTION_DATE);
        String collectionStatus = record.getItemValue(EuropeanaUpdatableField.STATUS); 
        String metadataformat = record.getItemValue(EuropeanaUpdatableField.METADATA_FORMAT); 
        String metadatanamespace = record.getItemValue(EuropeanaUpdatableField.METADATA_NAMESPACE);      
        String metadataschema = record.getItemValue(EuropeanaUpdatableField.METADATA_SCHEMA);  
       
        String Z3950ADDRESS = record.getItemValue(EuropeanaUpdatableField.Z3950ADDRESS);  
        String Z3950PORT = record.getItemValue(EuropeanaUpdatableField.Z3950PORT);  
        String Z3950DATABASE = record.getItemValue(EuropeanaUpdatableField.Z3950DATABASE);  
        String FTP_Z3950_USER = record.getItemValue(EuropeanaUpdatableField.FTP_Z3950_USER);  
        String FTP_Z3950_PASSWORD = record.getItemValue(EuropeanaUpdatableField.FTP_Z3950_PASSWORD);  
        String Z3950RECORD_SYNTAX = record.getItemValue(EuropeanaUpdatableField.Z3950RECORD_SYNTAX);  
        String Z3950CHARSET = record.getItemValue(EuropeanaUpdatableField.Z3950CHARSET);  
        String Z3950METHOD = record.getItemValue(EuropeanaUpdatableField.Z3950METHOD);  
        String Z3950FILEPATH = record.getItemValue(EuropeanaUpdatableField.Z3950FILEPATH);  
        String Z3950MAXIMUMID = record.getItemValue(EuropeanaUpdatableField.Z3950MAXIMUMID);  
        
        String Z3950EARLIEST_TIMESTAMP = record.getItemValue(EuropeanaUpdatableField.Z3950EARLIEST_TIMESTAMP);  
        String FTPPATH = record.getItemValue(EuropeanaUpdatableField.FTPPATH); 
        String FTP_HTTP_ISOFORMAT = record.getItemValue(EuropeanaUpdatableField.FTP_HTTP_ISOFORMAT); 
        String FTPSERVER = record.getItemValue(EuropeanaUpdatableField.FTPSERVER); 
        String RECORD_XPATH = record.getItemValue(EuropeanaUpdatableField.RECORDXPATH);
        
        String HTTPURL = record.getItemValue(EuropeanaUpdatableField.HTTPURL); 
        String FOLDER = record.getItemValue(EuropeanaUpdatableField.FOLDER); 
        String HARVESTING_TYPE = record.getItemValue(EuropeanaRetrievableField.HARVESTING_TYPE); 
        String DATE_ENTERED = record.getItemValue(EuropeanaRetrievableField.DATE_ENTERED);      
        String CREATED_BY_USER = record.getItemValue(EuropeanaRetrievableField.CREATED_BY_USER); 
        String DELETED = record.getItemValue(EuropeanaUpdatableField.DELETED); 
        String ACRONYM = record.getItemValue(EuropeanaRetrievableField.ACRONYM); 
        String ENABLED = record.getItemValue(EuropeanaUpdatableField.ENABLED); 
        String PREVIEWS_IN_PORTAL = record.getItemValue(EuropeanaRetrievableField.PREVIEWS_ONLY_IN_PORTAL);

        
        
        Collection currcollection = engine.findCollection(mnemonicCode);

        if (currcollection == null) {
            currcollection = engine.createCollection(provider);
        }
        currcollection.setLanguage(countryCode);
        currcollection.setMnemonic(mnemonicCode);
        currcollection.setName(collectionName);
        currcollection.setOaiBaseUrl(harvestUrl);
        currcollection.setOaiMetadataPrefix(metadataformat);
        currcollection.setOaiSet(set);
        currcollection.putValue(ControlledVocabularyProxy.METADATA_FORMAT, metadataformat);
        currcollection.putValue(ControlledVocabularyProxy.STATUS, collectionStatus);
        currcollection.putValue(ControlledVocabularyProxy.NAME, collectionID);
        currcollection.putValue(ControlledVocabularyProxy.SUGARCRMID, sugarCRMID);
        currcollection.putValue(ControlledVocabularyProxy.DESCRIPTION, collectionDescription);
        currcollection.putValue(ControlledVocabularyProxy.EXPECTED_INGESTION_DATE, ingestionDate);
        currcollection.putValue(ControlledVocabularyProxy.DATE_ENTERED, DATE_ENTERED);
        currcollection.putValue(ControlledVocabularyProxy.METADATA_NAMESPACE, metadatanamespace);
        currcollection.putValue(ControlledVocabularyProxy.METADATA_SCHEMA, metadataschema);
        currcollection.putValue(ControlledVocabularyProxy.Z3950ADDRESS, Z3950ADDRESS);
        currcollection.putValue(ControlledVocabularyProxy.Z3950PORT, Z3950PORT);
        currcollection.putValue(ControlledVocabularyProxy.Z3950DATABASE, Z3950DATABASE);
        currcollection.putValue(ControlledVocabularyProxy.FTP_Z3950_USER, FTP_Z3950_USER);
        currcollection.putValue(ControlledVocabularyProxy.FTP_Z3950_PASSWORD, FTP_Z3950_PASSWORD);
        currcollection.putValue(ControlledVocabularyProxy.Z3950RECORD_SYNTAX, Z3950RECORD_SYNTAX);
        currcollection.putValue(ControlledVocabularyProxy.Z3950CHARSET, Z3950CHARSET);
        currcollection.putValue(ControlledVocabularyProxy.Z3950METHOD, Z3950METHOD);
        currcollection.putValue(ControlledVocabularyProxy.Z3950FILEPATH, Z3950FILEPATH);
        currcollection.putValue(ControlledVocabularyProxy.Z3950MAXIMUMID, Z3950MAXIMUMID);
        currcollection.putValue(ControlledVocabularyProxy.Z3950EARLIEST_TIMESTAMP, Z3950EARLIEST_TIMESTAMP);
        currcollection.putValue(ControlledVocabularyProxy.FTPPATH, FTPPATH);
        currcollection.putValue(ControlledVocabularyProxy.FTP_HTTP_ISOFORMAT, FTP_HTTP_ISOFORMAT);
        currcollection.putValue(ControlledVocabularyProxy.FTPSERVER, FTPSERVER);
        currcollection.putValue(ControlledVocabularyProxy.RECORDXPATH, RECORD_XPATH);
        currcollection.putValue(ControlledVocabularyProxy.HTTPURL, HTTPURL);
        currcollection.putValue(ControlledVocabularyProxy.FOLDER, FOLDER);
        currcollection.putValue(ControlledVocabularyProxy.HARVESTING_TYPE, HARVESTING_TYPE);      
        currcollection.putValue(ControlledVocabularyProxy.CREATED_BY_USER, CREATED_BY_USER);
        currcollection.putValue(ControlledVocabularyProxy.DELETED, DELETED);
        currcollection.putValue(ControlledVocabularyProxy.ACRONYM, ACRONYM);
        currcollection.putValue(ControlledVocabularyProxy.ENABLED, ENABLED);        
        currcollection.putValue(ControlledVocabularyProxy.PREVIEWS_ONLY_IN_PORTAL, PREVIEWS_IN_PORTAL); 
        
        
        engine.updateCollection(currcollection);
        engine.checkpoint();

        return currcollection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#addNoteAttachmentToRecord(java.lang
     * .String, java.lang.String)
     */
    @Override
    public void addNoteAttachmentToRecord(String recordId, String message)
            throws FileAttachmentException {
        SetNoteAttachment request = new SetNoteAttachment();
        NoteAttachment note = new NoteAttachment();

        note.setId(recordId);
        note.setFile(message);

        Date date = new Date();

        note.setFilename(date.toString() + ".txt");

        request.setNote(note);
        request.setSession(sugarwsClient.getSessionID());
        ClientUtils.logMarshalledObject(request);
        SetNoteAttachmentResponse resp = sugarwsClient.setnoteattachment(request);

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#addPollingListener(eu.europeana.uim
     * .sugarcrmclient.plugin.objects.listeners.PollingListener)
     */
    @Override
    public synchronized void addPollingListener(String id, PollingListener listener) {
        pollingListeners.put(id, listener);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#removePollingListener(eu.europeana
     * .uim.sugarcrmclient.plugin.objects.listeners.PollingListener)
     */
    @Override
    public synchronized void removePollingListener(String id) {
        pollingListeners.remove(id);

    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#getPollingListeners()
     */
    @Override
    public LinkedHashMap<String, PollingListener> getPollingListeners() {

        return pollingListeners;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * eu.europeana.uim.sugarcrmclient.plugin.SugarCRMService#setPollingListeners(java.util.List)
     */
    @Override
    public void setPollingListeners(LinkedHashMap<String, PollingListener> listeners) {
        this.pollingListeners = listeners;

    }

    /*
     * Private methods
     */

    /**
     * @param list
     * @return
     */
    private ArrayList<SugarCrmRecord> wrapDomElements2Objects(ArrayList<Element> list) {

        ArrayList<SugarCrmRecord> returnList = new ArrayList<SugarCrmRecord>();

        for (int i = 0; i < list.size(); i++) {

            SugarCrmRecordImpl record = SugarCrmRecordImpl.getInstance(list.get(i));
            returnList.add(record);
        }

        return returnList;
    }

    /**
     * @param engine
     * @param trigger
     * @return
     * @throws StorageEngineException
     */
    private Collection inferCollection(StorageEngine engine, SugarCrmRecord trigger)
            throws StorageEngineException {

        Provider prov = updateProviderFromRecord(trigger);
        Collection currcollection = updateCollectionFromRecord(trigger, prov);
        return currcollection;
    }

    /**
     * @param id
     * @param status
     * @throws JIXBQueryResultException
     */
    protected void alterSugarCRMItemStatus(String id, String status) throws QueryResultException {
        SetEntry request = new SetEntry();

        NameValue nv0 = new NameValue();
        nv0.setName("sales_stage");
        nv0.setValue(status);

        NameValue nv1 = new NameValue();
        nv1.setName("id");
        nv1.setValue(id);

        ArrayList<NameValue> nvList = new ArrayList<NameValue>();

        nvList.add(nv0);
        nvList.add(nv1);

        NameValueList valueList = ClientUtils.generatePopulatedNameValueList(nvList);

        request.setNameValueList(valueList);
        request.setModuleName(DSMODULENAME);
        request.setSession(sugarwsClient.getSessionID());

        sugarwsClient.setentry(request);

    }

    /*
     * Getters & Setters
     */

    public void setSugarwsClient(SugarWsClientImpl sugarwsClient) {
        this.sugarwsClient = sugarwsClient;
    }

    public SugarWsClientImpl getSugarwsClient() {
        return sugarwsClient;
    }


    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Registry getRegistry() {
        return registry;
    }

}
