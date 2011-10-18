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
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import eu.europeana.uim.api.ActiveExecution;
import eu.europeana.uim.api.Orchestrator;
import eu.europeana.uim.api.Registry;
import eu.europeana.uim.api.StorageEngine;
import eu.europeana.uim.api.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.sugarcrm.ConnectionStatus;
import eu.europeana.uim.sugarcrm.FileAttachmentException;
import eu.europeana.uim.sugarcrm.LoginFailureException;
import eu.europeana.uim.sugarcrm.PollingListener;
import eu.europeana.uim.sugarcrm.QueryResultException;
import eu.europeana.uim.sugarcrm.SugarCrmQuery;
import eu.europeana.uim.sugarcrm.SugarCrmRecord;
import eu.europeana.uim.sugarcrm.SugarCrmService;
import eu.europeana.uim.sugarcrm.model.DatasetStates;
import eu.europeana.uim.sugarcrm.model.UpdatableField;
import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
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
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntryResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetNoteAttachment;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetNoteAttachmentResponse;
import eu.europeana.uim.sugarcrmclient.plugin.objects.SugarCrmRecordImpl;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.EuropeanaRetrievableField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.data.EuropeanaUpdatableField;
import eu.europeana.uim.sugarcrmclient.plugin.objects.queries.SimpleSugarCrmQuery;
import eu.europeana.uim.sugarcrmclient.ws.SugarWsClientImpl;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.JIXBQueryResultException;
import eu.europeana.uim.workflow.Workflow;

/**
 * This is the implementing class for the OSGI based SugarCrm plugin OSGI service.
 * 
 * @author Georgios Markakis
 */
public class SugarCRMServiceImpl implements SugarCrmService {

    private SugarWsClientImpl                      sugarwsClient;
    private Orchestrator                           orchestrator;
    private Registry                               registry;
    private LinkedHashMap<String, PollingListener> pollingListeners;
    private static final String                    DSMODULENAME = "Opportunities";

    /**
     * Constructor
     */
    public SugarCRMServiceImpl() {
        this.pollingListeners = new LinkedHashMap<String, PollingListener>();
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
        StringBuffer connectionInfo = new StringBuffer();
        Login login = ClientUtils.createStandardLoginObject(username, password);
        String newsessionID = sugarwsClient.login(login);
        sugarwsClient.setSessionID(newsessionID);
        connectionInfo.append(sugarwsClient.login(login));
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

        sugarwsClient.set_entry(request);
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

        GetEntryListResponse response = sugarwsClient.get_entry_list(request);

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

        GetEntryListResponse response = sugarwsClient.get_entry_list(request);

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
    public Workflow initWorkflowFromRecord(String worklfowName, SugarCrmRecord record,
            DatasetStates endstate) throws QueryResultException, StorageEngineException {

        StorageEngine<?> engine = registry.getStorageEngine();
        Workflow w = registry.getWorkflow(worklfowName);

        Collection<?> dataset = inferCollection(engine, record);

        ActiveExecution<?> execution = orchestrator.executeWorkflow(w, dataset);
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
    public List<Workflow> initWorkflowsFromRecords(String worklfowName, DatasetStates currentstate,
            DatasetStates endstate) throws QueryResultException, StorageEngineException {

        ArrayList<Workflow> wfs = new ArrayList<Workflow>();

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

        String collectionName = record.getItemValue(EuropeanaRetrievableField.NAME); 
        String providerName = record.getItemValue(EuropeanaRetrievableField.ORGANIZATION_NAME); 
        String providerAcronymName = record.getItemValue(EuropeanaRetrievableField.ACRONYM); 
        String mnemonicCode = providerInfo.get("identifier"); 
        String countryCode = record.getItemValue(EuropeanaRetrievableField.COUNTRY).toLowerCase(); 
        String harvestUrl = record.getItemValue(EuropeanaRetrievableField.HARVEST_URL); 
        String metadataformat = record.getItemValue(EuropeanaRetrievableField.METADATA_FORMAT); 

        
        
        Provider cuurprovider = engine.findProvider(mnemonicCode);
        

        if (cuurprovider == null) {
            cuurprovider = engine.createProvider();
        }
        cuurprovider.setAggregator(false);
        cuurprovider.setMnemonic(mnemonicCode);
        cuurprovider.setName(providerName);
        cuurprovider.setOaiBaseUrl(harvestUrl);
        cuurprovider.setOaiMetadataPrefix(metadataformat);

        cuurprovider.putValue("providerIdentifier", providerInfo.get("identifier"));
        
        String encodedDescription = null;
        try {
            encodedDescription = URLEncoder.encode(providerInfo.get("description"), "UTF8");
        } catch (UnsupportedEncodingException e) {
            encodedDescription = "None";
            e.printStackTrace();
        }
        cuurprovider.putValue("providerDescription", encodedDescription);
        cuurprovider.putValue("providerName", providerInfo.get("name"));
        cuurprovider.putValue("providerWebsite", providerInfo.get("website"));
        cuurprovider.putValue("sugarCRMID", providerInfo.get("sugarCRMID"));
        // FIXME:Handle Repox Datatypes
        // cuurprovider.putValue("type", providerInfo.get("type"));
        cuurprovider.putValue("providerType", "ARCHIVE");
        cuurprovider.putValue("providerCountry", providerInfo.get("country"));
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

        GetRelationshipsResponse resp = sugarwsClient.get_relationships(request);

        if (resp.getReturn().getIds().getArray() == null) { throw new JIXBQueryResultException(
                "Could not retrieve related provider information from 'Accounts module' "); }

        List<Element> el = resp.getReturn().getIds().getArray().getAnyList();

        NodeList idFieldList = el.get(0).getElementsByTagName("id");

        Node node = idFieldList.item(0);

        if (node == null) { throw new JIXBQueryResultException(
                "Could not retrieve related provider information from 'Accounts module' "); }

        String provideriD = node.getTextContent();

        HashMap<String, String> providerInfo = retrieveproviderinfo(provideriD);

        return providerInfo;

    }

    /**
     * @param providerID
     * @return
     * @throws JIXBQueryResultException
     */
    private HashMap<String, String> retrieveproviderinfo(String providerID)
            throws QueryResultException {

        HashMap<String, String> returnInfo = new HashMap<String, String>();

        GetEntry request = new GetEntry();
        request.setId(providerID);
        request.setModuleName("Accounts");
        request.setSession(sugarwsClient.getSessionID());
        SelectFields selectFields = new SelectFields();
        request.setSelectFields(selectFields);

        GetEntryResponse response = sugarwsClient.get_entry(request);

        Element el = response.getReturn().getEntryList().getArray().getAnyList().get(0);

        String identifier = extractFromElement("name_id_c", el);
        String description = extractFromElement("description", el);
        String name = extractFromElement("name", el);
        String website = extractFromElement("website", el);
        String type = extractFromElement("account_type", el);
        String country = extractFromElement("country_c", el);

        //Insert values in Provider Object
        returnInfo.put("sugarCRMID", providerID);
        returnInfo.put("identifier", identifier);
        returnInfo.put("description", description);
        returnInfo.put("name", name);
        returnInfo.put("website", website);
        returnInfo.put("type", type);
        returnInfo.put("country", country);

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


        String mnemonicCode = null;

        StringBuffer buffername = new StringBuffer();

        String[] fulname = record.getItemValue(EuropeanaRetrievableField.NAME).split("_");

        List<String> list = Arrays.asList(fulname);

        for (int i = 0; i < list.size(); i++) {
            if (i == 0) {
                mnemonicCode = list.get(i);
            } else if (i > 3) {
                buffername.append(list.get(i));
                buffername.append(" ");
            }
        }

        String collectionName = buffername.toString();
        String collectionID = record.getItemValue(EuropeanaRetrievableField.NAME); // "name"
        String countryCode = record.getItemValue(EuropeanaRetrievableField.COUNTRY).toLowerCase(); // "country_c"
        String harvestUrl = record.getItemValue(EuropeanaRetrievableField.HARVEST_URL); // "harvest_url_c"
        String set = record.getItemValue(EuropeanaRetrievableField.SETSPEC);
        String sugarCRMID = record.getItemValue(EuropeanaRetrievableField.ID);
        
        String metadataformat = record.getItemValue(EuropeanaRetrievableField.METADATA_FORMAT); 
        String metadatanamespace = record.getItemValue(EuropeanaRetrievableField.METADATA_NAMESPACE);      
        String metadataschema = record.getItemValue(EuropeanaRetrievableField.METADATA_SCHEMA); 
        
        
        
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
        currcollection.putValue("collectionID", collectionID);
        currcollection.putValue("sugarCRMID", sugarCRMID);
        currcollection.putValue("metadatanamespace", metadatanamespace);
        currcollection.putValue("metadataschema", metadataschema);
        
        
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
        SetNoteAttachmentResponse resp = sugarwsClient.set_note_attachment(request);

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
    protected void alterSugarCRMItemStatus(String id, String status) throws eu.europeana.uim.sugarcrm.QueryResultException {
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

        SetEntryResponse response = sugarwsClient.set_entry(request);

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

    public void setOrchestrator(Orchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    public Orchestrator getOrchestrator() {
        return orchestrator;
    }

    public void setRegistry(Registry registry) {
        this.registry = registry;
    }

    public Registry getRegistry() {
        return registry;
    }

}
