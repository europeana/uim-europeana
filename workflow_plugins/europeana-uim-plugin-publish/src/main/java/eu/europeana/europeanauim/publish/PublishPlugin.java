package eu.europeana.europeanauim.publish;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.europeana.corelib.edm.utils.construct.FullBeanHandler;

import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import org.apache.commons.lang.StringUtils;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;

import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.util.ClientUtils;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.theeuropeanlibrary.model.common.qualifier.Status;

import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.edm.utils.MongoConstructor;
import eu.europeana.corelib.edm.utils.construct.SolrDocumentHandler;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.tools.lookuptable.EuropeanaId;
import eu.europeana.europeanauim.publish.service.PublishService;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.logging.LoggingEngine;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeana.EuropeanaRedirectId;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.storage.StorageEngineException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;

public class PublishPlugin<I> extends AbstractIngestionPlugin<MetaDataRecord<I>, I> {

    private static PublishService publishService;
    private static IBindingFactory bfact;
    private static LoggingEngine logEngine;
    private static final Logger log = Logger.getLogger(PublishPlugin.class.getName());
    private final static String OVERRIDECHECKS
            = "override.all.checks.force.delete";
    //    private final static String OVERRIDEENRICHMENT = "override.enrichment.save";
    private final static String FORCELASTUPDATE = "override.last.update.check";
    //private static FullBeanHandler handler;
    private final static TKey<PublishPlugin, Long> date = TKey.register(
            PublishPlugin.class, "publish", Long.class);
    private static SolrServer productionCloudSolrServer;
    private static FullBeanHandler handler;
    private static final List<String> params = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(OVERRIDECHECKS);
//            add(OVERRIDEENRIHMENT);
            add(FORCELASTUPDATE);
        }
    };

    public PublishPlugin(String name, String description) {
        super(name, description);

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

    public PublishPlugin(PublishService publishService, String name, String description) {
        super(name, description);
        PublishPlugin.publishService = publishService;
    }

    public TKey<?, ?>[] getInputFields() {
        // TODO Auto-generated method stub
        return null;
    }

    public TKey<?, ?>[] getOptionalFields() {
        // TODO Auto-generated method stub
        return null;
    }

    public TKey<?, ?>[] getOutputFields() {
        // TODO Auto-generated method stub
        return null;
    }

    public boolean process(MetaDataRecord<I> mdr, ExecutionContext<MetaDataRecord<I>, I> context)
            throws IngestionPluginFailedException, CorruptedDatasetException {

        List<Status> status = mdr.getValues(EuropeanaModelRegistry.STATUS);
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


        Date updateDate = null;
        try {
            updateDate = sdf.parse((mdr.getValues(
                    EuropeanaModelRegistry.UIMUPDATEDDATE).size() > 0) ? mdr
                    .getValues(EuropeanaModelRegistry.UIMUPDATEDDATE).get(0)
                    : new Date(0).toString());

            Date ingestionDate = new Date(context.getValue(date));
            if (updateDate.after(ingestionDate)
                    || updateDate.toString().equals(ingestionDate.toString())
                    || check || checkUpdate) {


                if (!(status != null && status.get(0).equals(Status.DELETED))) {
                    String value = null;
                    if (mdr.getValues(EuropeanaModelRegistry.EDMENRICHEDRECORD) != null
                            && mdr.getValues(EuropeanaModelRegistry.EDMENRICHEDRECORD).size() > 0) {
                        value = mdr.getValues(EuropeanaModelRegistry.EDMENRICHEDRECORD).get(0);
                    } else
                        return false;
                    IUnmarshallingContext uctx;
                    try {
                        uctx = bfact.createUnmarshallingContext();
                        RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));

                        FullBeanImpl fBean = (FullBeanImpl) publishService.getMongoIngestion().getFullBean((String) mdr.getId());
                        SolrInputDocument doc =
                                new SolrDocumentHandler(publishService.getSolrServer()).generate(fBean);


                        ModifiableSolrParams params = new ModifiableSolrParams();
                        params.add("q", "europeana_id:" + ClientUtils.escapeQueryChars(fBean.getAbout()));

                        params.add("fl", "is_fulltext,has_thumbnails,has_media,filter_tags,facet_tags,has_landingpage");
                        QueryResponse resp = publishService.getSolrIngestionServer().query(params);
                        if (resp.getResults().size() > 0) {
                            SolrDocument retrievedDoc = resp.getResults().get(0);
                            if (retrievedDoc.containsKey("is_fulltext")) {
                                doc.addField("is_fulltext", retrievedDoc.get("is_fulltext"));
                            }
                            if (retrievedDoc.containsKey("has_thumbnails")) {
                                doc.addField("has_thumbnails", retrievedDoc.get("has_thumbnails"));
                            }
                            if (retrievedDoc.containsKey("has_media")) {
                                doc.addField("has_media", retrievedDoc.get("has_media"));
                            }
                            if (retrievedDoc.containsKey("filter_tags")) {
                                doc.addField("filter_tags", retrievedDoc.get("filter_tags"));
                            }
                            if (retrievedDoc.containsKey("facet_tags")) {
                                doc.addField("facet_tags", retrievedDoc.get("facet_tags"));
                            }
                            if (retrievedDoc.containsKey("has_landingpage")) {
                                doc.addField("has_landingpage", retrievedDoc.get("has_landingpage"));
                            }
                            publishService.getSolrServer().add(doc);
                            FullBeanImpl saved;
                            if (publishService.getMongoProduction().getFullBean(fBean.getAbout()) == null) {
                                new FullBeanHandler(publishService.getMongoProduction()).saveEdmClasses(fBean, true);
                                publishService.getMongoProduction().getDatastore().save(fBean);
                                saved = (FullBeanImpl) publishService.getMongoProduction()
                                        .getFullBean(fBean.getAbout());
                            } else {
                                saved = new FullBeanHandler(publishService.getMongoProduction()).updateFullBean(fBean);
                            }

                        }
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JiBXException e) {
                        e.printStackTrace();
                    } catch (SolrServerException e) {
                        e.printStackTrace();


                    } catch (Exception e) {
                        logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e, e.getMessage());
                        e.printStackTrace();
                        log.log(Level.SEVERE, "Generic Exception occured with error " + e.getMessage() + "\n");
                        return false;
                    }

                    // Check if a redirect exist and push it to production
                    if (mdr.getValues(EuropeanaModelRegistry.EDMRECORDREDIRECT) != null
                            && mdr.getValues(EuropeanaModelRegistry.EDMRECORDREDIRECT).size() > 0) {
                        // Push redirects to production
                        EuropeanaId europeanaId =
                                publishService
                                        .getEuropeanaIdMongoServer()
                                        .retrieveEuropeanaIdFromNew(
                                                (String)mdr.getId())
                                        .get(0);
                        publishService.getEuropeanaIdMongoServerProduction().saveEuropeanaId(europeanaId);
                        // Remove it from the mdr
                        mdr.deleteValues(EuropeanaModelRegistry.EDMRECORDREDIRECT);
                        try {
                            context.getStorageEngine().updateMetaDataRecord(mdr);
                        } catch (StorageEngineException e) {
                            logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e, e.getMessage());
                            log.log(Level.SEVERE, "Storage Engine Exception occured with error " + e.getMessage()
                                    + "\nRetrying");
                        }
                    }
                } else // Delete records from production
                {
                    String value = null;
                    if (mdr.getValues(EuropeanaModelRegistry.EDMENRICHEDRECORD) != null
                            && mdr.getValues(EuropeanaModelRegistry.EDMENRICHEDRECORD).size() > 0) {
                        value = mdr.getValues(EuropeanaModelRegistry.EDMENRICHEDRECORD).get(0);
                    } else {
                        value = mdr.getValues(EuropeanaModelRegistry.EDMRECORD).get(0);
                    }

                    IUnmarshallingContext uctx;
                    try {
                        uctx = bfact.createUnmarshallingContext();
                        RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));

                        handler.removeRecordById(publishService.getSolrServer(), (String)mdr.getId());


                    } catch (JiBXException e) {
                        logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e, e.getMessage());
                        log.log(Level.SEVERE, "JiBXException occured with error " + e.getMessage() + "\n");
                        e.printStackTrace();
                        return false;
                    } catch (Exception e) {
                        logEngine.logFailed(context.getExecution(), Level.SEVERE, this, e, e.getMessage());
                        e.printStackTrace();
                        log.log(Level.SEVERE, "Generic Exception occurred with error " + e.getMessage() + "\n");
                        return false;
                    }
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return true;
    }

    public void initialize(ExecutionContext<MetaDataRecord<I>, I> context)
            throws IngestionPluginFailedException {


        Collection collection = null;
        collection = (Collection) context.getExecution().getDataSet();
        productionCloudSolrServer = publishService.getSolrServer();
        logEngine = context.getLoggingEngine();
        String overrideChecks = context.getProperties().getProperty(
                OVERRIDECHECKS);
        handler = new FullBeanHandler(publishService.getMongoProduction());
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
                || (collection.getValue("forcedelete")!= null || Boolean.parseBoolean(collection.getValue("forcedelete")))||check) {


            handler.clearData(collection.getMnemonic());

            try {
                productionCloudSolrServer.deleteByQuery("europeana_collectionName:"
                        + collection.getName().split("_")[0] + "_*");
            } catch (SolrServerException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }


    }

    public void completed(ExecutionContext<MetaDataRecord<I>, I> context)
            throws IngestionPluginFailedException {
        // TODO Auto-generated method stub

    }

    public void initialize() {
        // TODO Auto-generated method stub

    }

    public void shutdown() {
        // TODO Auto-generated method stub

    }

    public List<String> getParameters() {
        return params;
    }

    public int getPreferredThreadCount() {
        // TODO Auto-generated method stub
        return 24;
    }

    public int getMaximumThreadCount() {
        // TODO Auto-generated method stub
        return 30;
    }

}
