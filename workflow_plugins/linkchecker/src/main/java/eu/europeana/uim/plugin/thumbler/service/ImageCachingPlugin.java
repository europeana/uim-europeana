/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package eu.europeana.uim.plugin.thumbler.service;

import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import eu.europeana.harvester.domain.ProcessingJob;
import eu.europeana.harvester.domain.ReferenceOwner;
import eu.europeana.jobcreator.JobCreator;
import eu.europeana.jobcreator.domain.ProcessingJobCreationOptions;
import eu.europeana.jobcreator.domain.ProcessingJobTuple;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import org.apache.commons.lang.StringUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.theeuropeanlibrary.model.common.qualifier.Status;

import eu.europeana.corelib.definitions.jibx.Aggregation;
import eu.europeana.corelib.definitions.jibx.HasView;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.harvester.client.HarvesterClientImpl;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.plugin.thumbler.InstanceCreator;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;

/**
 * @author gmamakis
 */
public class ImageCachingPlugin<I> extends
        AbstractIngestionPlugin<MetaDataRecord<I>, I> {

    private static IBindingFactory bfact;
    private static HarvesterClientImpl client;
    private static InstanceCreator creator;
    private static String colId;
    private static String provId;
    private static String execId;
    private static int priority = 50;


    static {
        try {
            // Should be placed in a static block for performance reasons
            bfact = BindingDirectory.getFactory(RDF.class);

        } catch (JiBXException e) {
            e.printStackTrace();
        }

    }

    private static final List<String> params = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add("collection.priority");

        }
    };

    public ImageCachingPlugin() {
        super("", "");
    }

    public ImageCachingPlugin(String name, String description) {
        super(name, description);
    }

    @Override
    public TKey<?, ?>[] getInputFields() {
        return null;
    }

    @Override
    public TKey<?, ?>[] getOptionalFields() {
        return null;
    }

    @Override
    public TKey<?, ?>[] getOutputFields() {
        return null;
    }

    @Override
    public boolean process(MetaDataRecord<I> mdr, ExecutionContext<MetaDataRecord<I>, I> context) throws
            IngestionPluginFailedException, CorruptedDatasetException {

        String value = null;
        String collection = ((Collection) context.getExecution().getDataSet()).
                getMnemonic();
        String provider = ((Collection) context.getExecution().getDataSet()).getProvider().getMnemonic();
        if (mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD) != null
                && mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD)
                .size() > 0) {
            value = mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD)
                    .get(0);
        } else {
            value = mdr.getValues(EuropeanaModelRegistry.EDMRECORD).get(0);
        }
        try {
            IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
            RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));
            List<Status> status = mdr
                    .getValues(EuropeanaModelRegistry.STATUS);
            String record = StringUtils.substringAfter(rdf.getProvidedCHOList().get(0).getAbout(), "/item/") + "/";
            if (!(status != null && status.size() > 0 && status.get(0).equals(Status.DELETED))) {

                List<String> hasView = new ArrayList<>();
                List<HasView> hasViewList = rdf.getAggregationList().get(0).getHasViewList();

                if (hasViewList != null) {
                    for (HasView hV : hasViewList) {
                        hasView.add(hV.getResource());
                    }
                }


                List<ProcessingJob> jobs = ProcessingJobTuple.processingJobsFromList(JobCreator.createJobs(
                        colId, provId, record, execId,
                        rdf.getAggregationList().get(0).getObject() != null ? rdf.getAggregationList().get(0).getObject().getResource() : null,
                        hasView,
                        rdf.getAggregationList().get(0).getIsShownBy() != null ? rdf.getAggregationList().get(0).getIsShownBy().getResource() : null,
                        rdf.getAggregationList().get(0).getIsShownAt() != null ? rdf.getAggregationList().get(0).getIsShownAt().getResource() : null,
                        priority,
                        new ProcessingJobCreationOptions(true)
                ));
                client.createOrModify(jobs);


            } else {

                client.setActive(record, new Boolean(false));
                client.deactivateJobs(new ReferenceOwner(provId, colId, record));

            }
        } catch (Exception ex) {
            Logger.getLogger(ImageCachingPlugin.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }


        return true;

    }

    @Override
    public void initialize(ExecutionContext<MetaDataRecord<I>, I> context) throws IngestionPluginFailedException {
        client = new HarvesterClientImpl(creator.getDatastore(), creator.getConfig());

        Collection collection = (Collection) context.getExecution().getDataSet();
        String collectionId = collection.getMnemonic();
        colId = collectionId;
        provId = context.getDataSetCollection().getProvider().getMnemonic();
        execId = (String)context.getExecution().getId();
        String prio = context.getProperties().getProperty(
                "collection.priority");
        if(StringUtils.isNotEmpty(prio)&& StringUtils.isNumeric(prio)){
            priority = Integer.parseInt(prio);
        }
        String oldId = creator.getCollectionMongoServer().findOldCollectionId(collectionId);
        if (oldId != null) {
            collectionId = oldId;
        }
        if (Boolean.parseBoolean(collection
                .getValue(ControlledVocabularyProxy.ISNEW.toString()))|| !StringUtils.equals(oldId,collectionId)) {
            ReferenceOwner owner = new ReferenceOwner(provId, collectionId, null);
            client.deactivateJobs(owner);
        }
    }

    @Override
    public void completed(ExecutionContext<MetaDataRecord<I>, I> context) throws IngestionPluginFailedException {

    }

    @Override
    public void initialize() {

    }

    @Override
    public void shutdown() {
    }

    @Override
    public List<String> getParameters() {
        return params;
    }

    @Override
    public int getPreferredThreadCount() {
        return 1;
    }

    @Override
    public int getMaximumThreadCount() {
        return 10;
    }


    public void setCreator(InstanceCreator creator) {
        this.creator = creator;
    }

    private class Link {

        private String url;
        private Boolean isEdmObject;

        public Link(String url, Boolean isEdmObject) {
            this.url = url;
            this.isEdmObject = isEdmObject;
        }

        public String getUrl() {
            return url;
        }

        public Boolean getIsEdmObject() {
            return isEdmObject;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 97 * hash + Objects.hashCode(this.url);
            hash = 97 * hash + Objects.hashCode(this.isEdmObject);
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Link other = (Link) obj;
            if (!Objects.equals(this.url, other.url)) {
                return false;
            }
            if (!Objects.equals(this.isEdmObject, other.isEdmObject)) {
                return false;
            }
            return true;
        }
    }
}
