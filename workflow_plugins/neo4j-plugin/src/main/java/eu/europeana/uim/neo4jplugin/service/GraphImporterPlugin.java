/**
 *
 */
package eu.europeana.uim.neo4jplugin.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.theeuropeanlibrary.model.common.qualifier.Status;

import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.edm.utils.MongoConstructor;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.neo4jplugin.impl.EDMRepositoryOSGIServiceProvider;
import eu.europeana.uim.neo4jplugin.impl.GraphConstructor;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.management.InvalidAttributeValueException;

/**
 * @author geomark
 *
 */
public class GraphImporterPlugin<I> extends
        AbstractIngestionPlugin<MetaDataRecord<I>, I> {

    private final static String INCLUDE_DELETED = "include.deleted";
    private final static String LIMIT = "commit.limit";

    /**
     *
     */
    private static GraphConstructor graphconstructor;
    private static IBindingFactory bfact;

    private static boolean firstTime;

    /**
     * The parameters used by this WorkflowStart
     */
    private static final List<String> params = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(INCLUDE_DELETED);
            add(LIMIT);
        }
    };

    /**
     * @param name
     * @param description
     */
    public GraphImporterPlugin(EDMRepositoryOSGIServiceProvider provider, boolean isFirstTime) {

        super("GraphImporterPlugin", "GraphImporterPlugin");
        init();
        this.graphconstructor = provider.getGraphconstructor();
        firstTime = isFirstTime;
    }

    /**
     * @param name
     * @param description
     */
    public GraphImporterPlugin() {
        super("GraphImporterPlugin", "GraphImporterPlugin");
        init();
    }

    /**
     * @param name
     * @param description
     */
    public GraphImporterPlugin(String name, String description) {
        super(name, description);
        init();
    }

    public static void init() {

        try {
            // Should be placed in a static block for performance reasons
            bfact = BindingDirectory.getFactory(RDF.class);

        } catch (JiBXException e) {
        }

    }

    /* (non-Javadoc)
     * @see eu.europeana.uim.plugin.ingestion.IngestionPlugin#getInputFields()
     */
    public TKey<?, ?>[] getInputFields() {
        return null;
    }

    /* (non-Javadoc)
     * @see eu.europeana.uim.plugin.ingestion.IngestionPlugin#getOptionalFields()
     */
    public TKey<?, ?>[] getOptionalFields() {
        return null;
    }

    /* (non-Javadoc)
     * @see eu.europeana.uim.plugin.ingestion.IngestionPlugin#getOutputFields()
     */
    public TKey<?, ?>[] getOutputFields() {
        return null;
    }

    /* (non-Javadoc)
     * @see eu.europeana.uim.plugin.ingestion.IngestionPlugin#process(eu.europeana.uim.store.UimDataSet, eu.europeana.uim.orchestration.ExecutionContext)
     */
    public boolean process(MetaDataRecord<I> mdr,
            ExecutionContext<MetaDataRecord<I>, I> context)
            throws IngestionPluginFailedException, CorruptedDatasetException {
        if(firstTime||mdr.getFirstValue(EuropeanaModelRegistry.ISHIERARCHY)!=null) {
            String value = null;
            List<Status> status = mdr
                    .getValues(EuropeanaModelRegistry.STATUS);
            String includedeleted = context.getProperties().getProperty(
                    INCLUDE_DELETED);
            boolean check = Boolean.parseBoolean(includedeleted);
            if (!(status != null && status.size() > 0 && status.get(0).equals(Status.DELETED)) || check) {
                if (mdr.getValues(EuropeanaModelRegistry.EDMENRICHEDRECORD) != null
                        && mdr.getValues(EuropeanaModelRegistry.EDMENRICHEDRECORD)
                        .size() > 0) {
                    value = mdr.getValues(EuropeanaModelRegistry.EDMENRICHEDRECORD)
                            .get(0);
                } else if (mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD) != null
                        && mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD)
                        .size() > 0) {
                    value = mdr.getValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD)
                            .get(0);
                } else {
                    value = mdr.getValues(EuropeanaModelRegistry.EDMRECORD).get(0);
                }

                IUnmarshallingContext uctx;
                try {
                    MongoConstructor neo4jConstructor = new MongoConstructor();
                    uctx = bfact.createUnmarshallingContext();
                    RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));
                    if (graphconstructor != null) {
                        FullBeanImpl bean = neo4jConstructor.constructFullBean(rdf);

                        bean.setEuropeanaCollectionName(new String[]{mdr
                                .getCollection().getName()});
                        if (bean.getEuropeanaAggregation().getEdmLanguage()
                                != null) {
                            bean.setLanguage(new String[]{bean
                                    .getEuropeanaAggregation().getEdmLanguage()
                                    .values().iterator().next().get(0)});
                        }
                        if (bean.getEuropeanaAggregation().getEdmLandingPage()
                                == null) {
                            bean.getEuropeanaAggregation().setEdmLandingPage(
                                    "http://testuri");
                        }

                        bean.setEuropeanaCollectionName(new String[]{mdr
                                .getCollection().getName()});

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
                        bean.setTimestampCreated(timestampCreated);
                        mdr.deleteValues(EuropeanaModelRegistry.UPDATEDSAVE);
                        Date timestampUpdated = new Date();
                        bean.setTimestampUpdated(timestampUpdated);
                        mdr.addValue(EuropeanaModelRegistry.UPDATEDSAVE,
                                timestampUpdated.getTime());
                        graphconstructor.parseMorphiaEntity(bean);
                        mdr.addValue(EuropeanaModelRegistry.ISHIERARCHY,new Boolean(true));
                    } else {
                        throw new IngestionPluginFailedException("Cannot get a reference to the Neo4j endpoint");
                    }

                } catch (JiBXException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (MalformedURLException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                return true;
            } else {
                graphconstructor.populateDeletionCandidates((String) mdr.getId(), ((Collection) context.getExecution().
                        getDataSet()).getMnemonic());
            }
        }
        return false;
    }

    public void completed(ExecutionContext<MetaDataRecord<I>, I> execution)
            throws IngestionPluginFailedException {
        graphconstructor.deleteNodes(((Collection) execution.getExecution().getDataSet()).getMnemonic());
        int limit = 1000;
        if (execution.getProperties().getProperty(
                LIMIT) != null) {
            limit = Integer.parseInt(execution.getProperties().getProperty(
                LIMIT));
        }
        graphconstructor.generateNodes(((Collection) execution.getExecution().getDataSet()).getMnemonic(),limit);
        // graphconstructor.addToIndex(((Collection) execution.getExecution().getDataSet()).getMnemonic());

        try {
            graphconstructor.generateNodeLinks(((Collection) execution.getExecution().getDataSet()).getMnemonic());
        } catch (InvalidAttributeValueException ex) {
            Logger.getLogger(GraphImporterPlugin.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void initialize(ExecutionContext<MetaDataRecord<I>, I> arg0)
            throws IngestionPluginFailedException {

    }

    public int getMaximumThreadCount() {
        return 15;
    }

    public List<String> getParameters() {
        return params;
    }

    public int getPreferredThreadCount() {
        return 12;
    }

    public void initialize() {

    }

    public void shutdown() {
        // TODO Auto-generated method stub

    }

}
