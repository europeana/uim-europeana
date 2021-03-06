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
package eu.europeana.uim.enrichment;

import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.edm.utils.EdmUtils;
import eu.europeana.corelib.edm.utils.MongoConstructor;
import eu.europeana.corelib.edm.utils.SolrConstructor;
import eu.europeana.corelib.edm.utils.construct.FullBeanHandler;
import eu.europeana.corelib.edm.utils.construct.SolrDocumentHandler;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.entity.AgentImpl;
import eu.europeana.corelib.solr.entity.AggregationImpl;
import eu.europeana.corelib.solr.entity.ConceptImpl;
import eu.europeana.corelib.solr.entity.PlaceImpl;
import eu.europeana.corelib.solr.entity.ProxyImpl;
import eu.europeana.corelib.solr.entity.TimespanImpl;
import eu.europeana.enrichment.api.external.EntityWrapper;
import eu.europeana.enrichment.api.external.InputValue;
import eu.europeana.enrichment.rest.client.EnrichmentDriver;
import eu.europeana.harvester.client.HarvesterClient;
import eu.europeana.harvester.client.HarvesterClientImpl;
import eu.europeana.harvester.domain.ProcessingJob;
import eu.europeana.harvester.domain.ReferenceOwner;
import eu.europeana.jobcreator.JobCreator;
import eu.europeana.jobcreator.domain.ProcessingJobCreationOptions;
import eu.europeana.jobcreator.domain.ProcessingJobTuple;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.enrichment.service.EnrichmentService;
import eu.europeana.uim.enrichment.service.InstanceCreator;
import eu.europeana.uim.enrichment.utils.EnrichmentUtils;
import eu.europeana.uim.enrichment.utils.EntityCache;
import eu.europeana.uim.enrichment.utils.EuropeanaProxyUtils;
import eu.europeana.uim.enrichment.utils.OsgiEdmMongoServer;
import eu.europeana.uim.enrichment.utils.PropertyReader;
import eu.europeana.uim.enrichment.utils.RecordCompletenessRanking;
import eu.europeana.uim.enrichment.utils.RetrievedEntity;
import eu.europeana.uim.enrichment.utils.UimConfigurationProperty;
import eu.europeana.uim.enrichment.utils.solr.SolrDocumentGenerator;
import eu.europeana.uim.logging.LoggingEngine;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.EuropeanaRetrievableField;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.storage.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;
import eu.europeana.uim.sugar.LoginFailureException;
import eu.europeana.uim.sugar.QueryResultException;
import eu.europeana.uim.sugar.SugarCrmRecord;
import eu.europeana.uim.sugar.SugarCrmService;
import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.codehaus.jackson.map.ObjectMapper;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.theeuropeanlibrary.model.common.qualifier.Status;


/**
 * Enrichment plugin implementation
 *
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
@SuppressWarnings("rawtypes")
public class EnrichmentPlugin<I> extends
        AbstractIngestionPlugin<MetaDataRecord<I>, I> {

//    private static HttpSolrServer solrServer;
    private static CloudSolrServer cloudSolrServer;
    private static CloudSolrServer productionCloudSolrServer;
    private static SugarCrmService sugarCrmService;
    private static EnrichmentService enrichmentService;
    private static String previewsOnlyInPortal;
    private static EnrichmentDriver enricher = new EnrichmentDriver(
            PropertyReader
            .getProperty(UimConfigurationProperty.ENRICHMENT_PATH));
    private final static String PORTALURL
            = "http://www.europeana.eu/portal/record";
    private final static String SUFFIX = ".html";
    private static IBindingFactory bfact;
    private static OsgiEdmMongoServer mongoServer;
    private static final Logger log = Logger.getLogger(EnrichmentPlugin.class
            .getName());
    private final static String OVERRIDECHECKS
            = "override.all.checks.force.delete";
//    private final static String OVERRIDEENRICHMENT = "override.enrichment.save";
    private final static String FORCELASTUPDATE = "override.last.update.check";
    private static FullBeanHandler handler;
    private final static SolrDocumentGenerator docGen
            = new SolrDocumentGenerator();
    private static Map<String, Map<String, State>> enrichmentQueryCache = Collections.
            synchronizedMap(new HashMap<String, Map<String, State>>());
    private static LoggingEngine logEngine;
    // Caches
    private static EntityCache entityCache = new EntityCache();
    private static InstanceCreator creator;

    public EnrichmentPlugin(String name, String description) {
        super(name, description);
    }

    public EnrichmentPlugin() {
        super("", "");
    }

    static {
        try {
            // Should be placed in a static block for performance reasons
            bfact = BindingDirectory.getFactory(RDF.class);

        } catch (JiBXException e) {
            e.printStackTrace();
            log.log(Level.SEVERE, "Error creating the JibX factory");
        }

    }

    private enum State {

        PENDING, DONE;
    }
    private final static TKey<EnrichmentPlugin, Long> date = TKey.register(
            EnrichmentPlugin.class, "enrichment", Long.class);
    private final static TKey<EnrichmentPlugin, Long> processCalledTKey = TKey
            .register(EnrichmentPlugin.class, "processCalled", Long.class);
    private final static TKey<EnrichmentPlugin, Long> deletedTKey = TKey
            .register(EnrichmentPlugin.class, "deleted", Long.class);
    private final static TKey<EnrichmentPlugin, Long> addedTKey = TKey
            .register(EnrichmentPlugin.class, "added", Long.class);
    /**
     * The parameters used by this WorkflowStart
     */
    private static final List<String> params = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(OVERRIDECHECKS);
//            add(OVERRIDEENRIHMENT);
            add(FORCELASTUPDATE);
        }
    };

    /*
     * (non-Javadoc)
     *
     * @see eu.europeana.uim.plugin.ingestion.IngestionPlugin#getInputFields()
     */
    @Override
    public TKey<?, ?>[] getInputFields() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * eu.europeana.uim.plugin.ingestion.IngestionPlugin#getOptionalFields()
     */
    @Override
    public TKey<?, ?>[] getOptionalFields() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see eu.europeana.uim.plugin.ingestion.IngestionPlugin#getOutputFields()
     */
    @Override
    public TKey<?, ?>[] getOutputFields() {
        return null;
    }

    /*
     * (non-Javadoc)
     *
     * @see eu.europeana.uim.plugin.Plugin#initialize()
     */
    @Override
    public void initialize() {
    }

    /*
     * (non-Javadoc)
     *
     * @see eu.europeana.uim.plugin.Plugin#shutdown()
     */
    @Override
    public void shutdown() {
    }

    /*
     * (non-Javadoc)
     *
     * @see eu.europeana.uim.plugin.Plugin#getParameters()
     */
    @Override
    public List<String> getParameters() {

        return params;
    }

    /*
     * (non-Javadoc)
     *
     * @see eu.europeana.uim.plugin.Plugin#getPreferredThreadCount()
     */
    @Override
    public int getPreferredThreadCount() {
        return 12;
    }

    /*
     * (non-Javadoc)
     *
     * @see eu.europeana.uim.plugin.Plugin#getMaximumThreadCount()
     */
    @Override
    public int getMaximumThreadCount() {
        return 15;
    }

    /*
     * (non-Javadoc)
     *
     * @see eu.europeana.uim.plugin.ExecutionPlugin#initialize(eu.europeana.uim.
     * orchestration.ExecutionContext)
     */
    @Override
    public void initialize(ExecutionContext<MetaDataRecord<I>, I> context)
            throws IngestionPluginFailedException {
        Collection collection = null;
        context.putValue(processCalledTKey, 0l);
        context.putValue(deletedTKey, 0l);
        context.putValue(addedTKey, 0l);
        logEngine = context.getLoggingEngine();
        try {
            cloudSolrServer = enrichmentService.getCloudSolrServer();
            productionCloudSolrServer = enrichmentService.getProductionCloudSolrServer();

            if (mongoServer == null) {

                mongoServer = enrichmentService.getEuropeanaMongoServer();

            }
            handler = new FullBeanHandler(mongoServer);
            collection = (Collection) context.getExecution().getDataSet();
            enrichmentQueryCache.put(collection.getMnemonic(),
                    Collections.synchronizedMap(new HashMap<String, State>()));
            String overrideChecks = context.getProperties().getProperty(
                    OVERRIDECHECKS);
            boolean check = false;
            if (StringUtils.isNotEmpty(overrideChecks)) {
                check = Boolean.parseBoolean(overrideChecks);
            }

            if (collection
                    .getValue(ControlledVocabularyProxy.LASTINGESTION_DATE
                            .toString()) != null) {
                context.putValue(date, Long.parseLong(((Collection) context
                        .getDataSet())
                        .getValue(ControlledVocabularyProxy.LASTINGESTION_DATE
                                .toString())));

            } else {
                context.putValue(date, new Date(0).getTime());
                check = true;
            }
            if (Boolean.parseBoolean(collection
                    .getValue(ControlledVocabularyProxy.ISNEW.toString()))
                    || check) {
                handler.clearData(collection.getMnemonic());

                cloudSolrServer.deleteByQuery("europeana_collectionName:"
                    + collection.getName().split("_")[0] + "_*");
                collection.putValue("forcedelete","true");
                //Wait for two minutes to ensure tht things are properly removed on large datasets
                if(context.getStorageEngine().getTotalByCollection(collection)>500000) {
                    //If its a massive collection sleep for one hour just in case
                    Thread.sleep(1000*60*60);
                }

            }

        } catch (Exception e) {
            if (logEngine != null) {
                logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
            }
            log.log(Level.SEVERE, e.getMessage());
        }
        String sugarCrmId = collection
                .getValue(ControlledVocabularyProxy.SUGARCRMID);
        try {
            try {
                sugarCrmService
                        .updateSession(
                                PropertyReader
                                .getProperty(
                                        UimConfigurationProperty.SUGARCRM_USERNAME),
                                PropertyReader
                                .getProperty(
                                        UimConfigurationProperty.SUGARCRM_PASSWORD));
            } catch (LoginFailureException e) {
                if (logEngine != null) {
                    logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
                }
                log.log(Level.SEVERE,
                        "Error updating Sugar Session id. " + e.getMessage());
            } catch (Exception e) {
                if (logEngine != null) {
                    logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
                }
                log.log(Level.SEVERE,
                        "Generic SugarCRM error. " + e.getMessage());
            }
            SugarCrmRecord sugarCrmRecord = sugarCrmService
                    .retrieveRecord(sugarCrmId);
            previewsOnlyInPortal = sugarCrmRecord
                    .getItemValue(
                            EuropeanaRetrievableField.PREVIEWS_ONLY_IN_PORTAL);

        } catch (QueryResultException e) {
            log.log(Level.SEVERE, "Error retrieving SugarCRM record");
            previewsOnlyInPortal = "false";
        } catch (Exception e) {
            log.log(Level.SEVERE,
                    "Record could not be retrieved. " + e.getMessage());
        }
        if (logEngine != null) {
            logEngine.log(Level.INFO, "Preview Only in portal acquired with value: "
                    + previewsOnlyInPortal);
        }
        log.log(Level.INFO, "Preview Only in portal acquired with value: "
                + previewsOnlyInPortal);
    }

    /*
     * (non-Javadoc)
     *
     * @see eu.europeana.uim.plugin.ExecutionPlugin#completed(eu.europeana.uim.
     * orchestration.ExecutionContext)
     */
    @Override
    public void completed(ExecutionContext<MetaDataRecord<I>, I> context)
            throws IngestionPluginFailedException {
        enrichmentQueryCache.remove(((Collection) context.getExecution().
                getDataSet()).getMnemonic());
        long recordNumber = context.getValue(addedTKey);
        long deleted = context.getValue(deletedTKey);
        long processCount = context.getValue(processCalledTKey);
        log.log(Level.INFO, "Adding " + recordNumber + " documents");
        log.log(Level.INFO, "Process called " + processCount);
        context.getLoggingEngine().log(context.getExecution(), Level.INFO,
                "Adding " + recordNumber + " documents");
        context.getLoggingEngine().log(context.getExecution(), Level.INFO,
                "Process called " + processCount);
//        try {
//            solrServer.commit();
//            cloudSolrServer.commit();
            logEngine.log(context.getExecution(), Level.INFO,
                    "Added " + recordNumber + " documents");
            log.log(Level.INFO, "Added " + recordNumber + " documents");
            logEngine.log(context.getExecution(), Level.INFO,
                    "Deleted are " + deleted);
            log.log(Level.INFO, "Deleted are " + deleted);
//        } catch (SolrServerException e) {
//            logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e,
//                    e.getMessage());
//            log.log(Level.SEVERE, e.getMessage());
//        } catch (IOException e) {
//            logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e,
//                    e.getMessage());
//            log.log(Level.SEVERE, e.getMessage());
//        }
        log.log(Level.INFO, "Committed in Solr Server");

    }

    /*
     * (non-Javadoc)
     *
     * @see
     * eu.europeana.uim.plugin.ingestion.IngestionPlugin#process(eu.europeana
     * .uim.store.UimDataSet, eu.europeana.uim.orchestration.ExecutionContext)
     */
    @Override
    public boolean process(MetaDataRecord<I> mdr,
            ExecutionContext<MetaDataRecord<I>, I> context)
            throws IngestionPluginFailedException, CorruptedDatasetException {
        String value = null;
        String collection = ((Collection) context.getExecution().getDataSet()).
                getMnemonic();
        context.putValue(processCalledTKey,
                context.getValue(processCalledTKey) + 1);
        if (mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD) != null
                && mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD)
                .size() > 0) {
            value = mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD)
                    .get(0);
        } else {
            value = mdr.getValues(EuropeanaModelRegistry.EDMRECORD).get(0);
        }
        String overrideChecks = context.getProperties().getProperty(
                OVERRIDECHECKS);
        String overrideUpdateCheck = context.getProperties().getProperty(
                FORCELASTUPDATE);
        boolean check = false;
        boolean checkUpdate = false;
        if (StringUtils.isNotEmpty(overrideChecks)) {
            check = Boolean.parseBoolean(overrideChecks);
        }
        if (StringUtils.isNotEmpty(overrideUpdateCheck)) {
            checkUpdate = Boolean.parseBoolean(overrideUpdateCheck);
        }
        // Check dates first
        SimpleDateFormat sdf = new SimpleDateFormat(
                "EEE MMM dd HH:mm:ss Z yyyy", Locale.US);
        try {
            Date updateDate = sdf.parse((mdr.getValues(
                    EuropeanaModelRegistry.UIMUPDATEDDATE).size() > 0) ? mdr
                            .getValues(EuropeanaModelRegistry.UIMUPDATEDDATE).get(0)
                            : new Date(0).toString());
            Date ingestionDate = new Date(context.getValue(date));
            if (updateDate.after(ingestionDate)
                    || updateDate.toString().equals(ingestionDate.toString())
                    || check || checkUpdate) {
                IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
                RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));
                List<Status> status = mdr
                        .getValues(EuropeanaModelRegistry.STATUS);
                if (!(status != null && status.size() > 0 && status.get(0).equals(Status.DELETED))) {
                    try {
                        mdr.deleteValues(EuropeanaModelRegistry.EDMENRICHEDRECORD);
                        SolrInputDocument basicDocument = new SolrConstructor()
                                .constructSolrDocument(rdf);

                        FullBeanImpl fBean = new MongoConstructor()
                                .constructFullBean(rdf);
                        List<InputValue> inputValues = new EnrichmentUtils()
                                .createValuesForEnrichment(basicDocument);

                        List<RetrievedEntity> enrichedEntities
                                = new ArrayList();

                        List<RetrievedEntity> enriched = new ArrayList<>();
                        if (inputValues.size() > 0) {
                           enriched = convertToObjects(
                                    enricher.enrich(
                                            inputValues, false));

                            enrichedEntities.addAll(enriched);
                        }

                        ProxyImpl europeanaProxy = EuropeanaProxyUtils
                                .getEuropeanaProxy(fBean);
                        if(enriched.size()>0) {
                            docGen.addEntities(basicDocument, fBean,
                                    europeanaProxy, enrichedEntities);
                        }

                        boolean prOO = StringUtils.contains(previewsOnlyInPortal, "1");
                        fBean.getAggregations()
                                .get(0)
                                .setEdmPreviewNoDistribute(prOO);
                        int completeness = RecordCompletenessRanking
                                .rankRecordCompleteness(basicDocument);
                        fBean.setEuropeanaCompleteness(completeness);

                        fBean.setEuropeanaCollectionName(new String[]{mdr
                            .getCollection().getName()});
                        if (fBean.getEuropeanaAggregation().getEdmLanguage()
                                != null) {
                            fBean.setLanguage(new String[]{fBean
                                .getEuropeanaAggregation().getEdmLanguage()
                                .values().iterator().next().get(0)});
                        }
                        if (fBean.getEuropeanaAggregation().getEdmLandingPage()
                                == null) {
                            fBean.getEuropeanaAggregation().setEdmLandingPage(
                                    PORTALURL + fBean.getAbout() + SUFFIX);
                        }
                        fBean.getEuropeanaAggregation().setAbout(
                                "/aggregation/europeana" + fBean.getAbout());

                        fBean.getEuropeanaAggregation().setAggregatedCHO(
                                "/item" + fBean.getAbout());

                        fBean.setEuropeanaCollectionName(new String[]{mdr
                            .getCollection().getName()});
                        if (europeanaProxy.getYear() != null) {

                            fBean.setYear(europeanaProxy.getYear().get("def").toArray(new String[europeanaProxy.getYear().get("def").size()]));
                        }
                        ProxyImpl providerProxy = EuropeanaProxyUtils
                                .getProviderProxy(fBean);
                        List<String> titles = new ArrayList<String>();
                        if (providerProxy.getDcTitle() != null) {
                            for (Entry<String, List<String>> entry
                                    : providerProxy
                                    .getDcTitle().entrySet()) {
                                if (entry.getValue() != null) {
                                    titles.addAll(entry.getValue());
                                }
                            }
                        }
                        if (titles.size() > 0) {
                            fBean.setTitle(titles.toArray(new String[titles
                                    .size()]));
                        }
                        Date timestampCreated = new Date();
                        if (mdr.getValues(EuropeanaModelRegistry.INITIALSAVE)
                                != null
                                && mdr.getValues(
                                        EuropeanaModelRegistry.INITIALSAVE)
                                .size() > 0) {
                            timestampCreated = new Date(mdr.getValues(
                                    EuropeanaModelRegistry.INITIALSAVE).get(0));
                        } else {
                            mdr.addValue(EuropeanaModelRegistry.INITIALSAVE,
                                    timestampCreated.getTime());
                        }
                        fBean.setTimestampCreated(timestampCreated);

                        mdr.deleteValues(EuropeanaModelRegistry.UPDATEDSAVE);
                        Date timestampUpdated = new Date();
                        fBean.setTimestampUpdated(timestampUpdated);

                        mdr.addValue(EuropeanaModelRegistry.UPDATEDSAVE,
                                timestampUpdated.getTime());

                        List<ProxyImpl> proxies = new ArrayList<ProxyImpl>();
                        proxies.add(providerProxy);
                        proxies.add(europeanaProxy);
                        fBean.setProxies(proxies);

                        docGen.updateProviderAggregationInSolr(fBean,
                                basicDocument);
                        docGen.updateEuropeanaProxyInSolr(fBean, basicDocument);
                        docGen.updateProviderProxyInSolr(fBean, basicDocument);
                        FullBeanImpl saved;
                        if (mongoServer.getFullBean(fBean.getAbout()) == null) {
                            handler.saveEdmClasses(fBean, true);
                            mongoServer.getDatastore().save(fBean);
                            saved = (FullBeanImpl) mongoServer
                                    .getFullBean(fBean.getAbout());
                        } else {
                            saved = handler.updateFullBean(fBean);
                        }
                        if (check|| checkUpdate) {
                            HarvesterClient client = new HarvesterClientImpl(creator.getDatastore(), creator.getConfig());

                            AggregationImpl aggr = fBean.getAggregations().get(0);
                            client.createOrModify(getProcessingJobs(aggr,(String)context.getExecution().getId(),context.getDataSetCollection().getProvider().getMnemonic(),collection,fBean.getAbout()));
                        }
                        mdr.addValue(EuropeanaModelRegistry.EDMENRICHEDRECORD,EdmUtils.toEDM(saved,true));
                        context.getStorageEngine().updateMetaDataRecord(mdr);

                        context.putValue(addedTKey,
                                context.getValue(addedTKey) + 1);
                        context.getStorageEngine().checkpoint();
                        fBean.setState(eu.europeana.publication.common.State.ACCEPTED);

                        SolrInputDocument doc = new SolrDocumentHandler(cloudSolrServer).generate(fBean);
//                        if(!(check||checkUpdate)) {
//                            ModifiableSolrParams params = new ModifiableSolrParams();
//                            params.add("q", "europeana_id:" + ClientUtils.escapeQueryChars(fBean.getAbout()));
//
//
//                            params.add("fl", "is_fulltext,has_thumbnails,has_media,filter_tags,facet_tags,has_landingpage");
//                            QueryResponse resp = cloudSolrServer.query(params);
//                            if(resp.getResults().size()>0){
//                                SolrDocument retrievedDoc = resp.getResults().get(0);
//                                if(retrievedDoc.containsKey("is_fulltext")){
//                                    doc.addField("is_fulltext",retrievedDoc.get("is_fulltext"));
//                                }
//                                if(retrievedDoc.containsKey("has_thumbnails")){
//                                    doc.addField("has_thumbnails",retrievedDoc.get("has_thumbnails"));
//                                }
//                                if(retrievedDoc.containsKey("has_media")){
//                                    doc.addField("has_media",retrievedDoc.get("has_media"));
//                                }
//                                if(retrievedDoc.containsKey("has_landingpage")){
//                                    doc.addField("has_landingpage",retrievedDoc.get("has_landingpage"));
//                                }
//                                if(retrievedDoc.containsKey("filter_tags")){
//                                    doc.addField("filter_tags",retrievedDoc.get("filter_tags"));
//                                }
//                                if(retrievedDoc.containsKey("facet_tags")){
//                                    doc.addField("facet_tags",retrievedDoc.get("facet_tags"));
//                                }
//                            }
//                        }

                        cloudSolrServer.add(doc);
                        return true;
                    } catch (MalformedURLException e) {
                        logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e,
                                e.getMessage());
                        log.log(Level.SEVERE,
                                "Malformed URL Exception occured with error "
                                + e.getMessage() + "\nRetrying");
                    } catch (IOException e) {
                        logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e,
                                e.getMessage());
                        log.log(Level.SEVERE,
                                "IO Exception occured with error "
                                + e.getMessage() + "\nRetrying");
                    } catch (Exception e) {
                        logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e,
                                e.getMessage());
                        e.printStackTrace();
                        log.log(Level.SEVERE,
                                "Generic Exception occured with error "
                                + e.getMessage() + "\nRetrying");
                    }
                    return false;
                } else {
                    boolean res = true;
//                    res = handler.removeRecord(solrServer, rdf);
                    if(!check) {
                        res = handler.removeRecord(cloudSolrServer, rdf);
                    }
                    //HarvesterClientImpl client = new HarvesterClientImpl(creator.getDatastore(), creator.getConfig());
                    try {
                        creator.getClient().setActive(rdf.getProvidedCHOList().get(0).getAbout(),new Boolean(false));
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (TimeoutException e) {
                        e.printStackTrace();
                    }

                    if (res) {
                        context.putValue(deletedTKey,
                                context.getValue(deletedTKey) + 1);
                    }
                    mdr.deleteValues(EuropeanaModelRegistry.UPDATEDSAVE);
                    mdr.addValue(EuropeanaModelRegistry.UPDATEDSAVE,
                            new Date().getTime());
                    try {
                        context.getStorageEngine().updateMetaDataRecord(mdr);
                    } catch (StorageEngineException e) {
                        logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e,
                                e.getMessage());
                        log.log(Level.SEVERE,
                                "Storage Engine Exception occured with error "
                                + e.getMessage() + "\nRetrying");
                    }
                    return res;
                }
            }
        } catch (JiBXException e) {
            logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e,
                    e.getMessage());
            log.log(Level.SEVERE,
                    "JibX Exception occured with error " + e.getMessage()
                    + "\nRetrying");
        } catch (ParseException e) {
            logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e,
                    e.getMessage());
            log.log(Level.SEVERE,
                    "Parse Exception occured with error " + e.getMessage()
                    + "\nRetrying");
        }
        return false;
    }

    private List<ProcessingJob> getProcessingJobs(AggregationImpl aggr, String executionId, String providerId, String collectionId,String recordId) {
        ReferenceOwner owner = new ReferenceOwner(providerId,collectionId,recordId,executionId);

        List<ProcessingJob> jobs = new ArrayList<>();
        try {
            jobs = ProcessingJobTuple.processingJobsFromList(JobCreator.createJobs(collectionId, providerId, recordId, executionId, aggr.getEdmObject(), aggr.getHasView()!=null?Arrays.asList(aggr.getHasView()):null, aggr.getEdmIsShownBy(), aggr.getEdmIsShownAt(), 50, new ProcessingJobCreationOptions(false)));

        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        return jobs;
    }

    public void setSugarCrmService(SugarCrmService sugarCrmService) {
        EnrichmentPlugin.sugarCrmService = sugarCrmService;
    }

    public void setEnrichmentService(EnrichmentService enrichmentService) {
        EnrichmentPlugin.enrichmentService = enrichmentService;
    }

    public void setCreator(InstanceCreator creator) {
        EnrichmentPlugin.creator = creator;
    }

    private List<RetrievedEntity> convertToObjects(
            List<EntityWrapper> enrichments) throws IOException {
        List<RetrievedEntity> entities = new ArrayList<RetrievedEntity>();
        for (EntityWrapper entity : enrichments) {
            if(entity!=null) {
                RetrievedEntity ret = new RetrievedEntity();
                //This can be null
                if (entity.getOriginalField() != null) {
                    ret.setOriginalField(entity.getOriginalField());
                }
                //This should not but just in case
                if (entity.getOriginalValue() != null) {
                    ret.setOriginalLabel(entity.getOriginalValue());
                }
                ret.setUri(entity.getUrl());
                if (entity.getClassName().equals(TimespanImpl.class.getName())) {
                    ret.setEntity(new ObjectMapper().readValue(entity.
                            getContextualEntity(), TimespanImpl.class));
                } else if (entity.getClassName().equals(AgentImpl.class.getName())) {
                    ret.setEntity(new ObjectMapper().readValue(entity.
                            getContextualEntity(), AgentImpl.class));
                } else if (entity.getClassName().equals(ConceptImpl.class.getName())) {
                    ret.setEntity(new ObjectMapper().readValue(entity.
                            getContextualEntity(), ConceptImpl.class));
                } else {
                    ret.setEntity(new ObjectMapper().readValue(entity.
                            getContextualEntity(), PlaceImpl.class));
                }
                entities.add(ret);
            }
        }

        return entities;
    }

}
