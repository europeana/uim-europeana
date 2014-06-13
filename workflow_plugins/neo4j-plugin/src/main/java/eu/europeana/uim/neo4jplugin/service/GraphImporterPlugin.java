/**
 *
 */
package eu.europeana.uim.neo4jplugin.service;

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.entity.ProxyImpl;
import eu.europeana.corelib.solr.utils.Neo4jConstructor;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.neo4jplugin.impl.EDMRepositoryOSGIServiceProvider;
import eu.europeana.uim.neo4jplugin.impl.GraphConstructorSpring;
import eu.europeana.uim.neo4jplugin.impl.ManagedTransaction;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.store.MetaDataRecord;
import java.util.Date;
import java.util.Map;

/**
 * @author geomark
 *
 */
public class GraphImporterPlugin<I> extends
        AbstractIngestionPlugin<MetaDataRecord<I>, I> {

    private final static String OVERRIDECHECKS = "override.all.checks.force.delete";
    private final static String OVERRIDEENRICHMENT = "override.enrichment.save";
    private final static String FORCELASTUPDATE = "override.last.update.check";
    private static int operations;

    /**
     *
     */
    private static GraphConstructorSpring graphconstructor;
    private static IBindingFactory bfact;

    /**
     * The parameters used by this WorkflowStart
     */
    private static final List<String> params = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(OVERRIDECHECKS);
            add(OVERRIDEENRICHMENT);
            add(FORCELASTUPDATE);
        }
    };

    /**
     * @param name
     * @param description
     */
    public GraphImporterPlugin(EDMRepositoryOSGIServiceProvider provider) {

        super("GraphImporterPlugin", "GraphImporterPlugin");
        operations = 0;
        init();
        this.graphconstructor = provider.getGraphconstructor();
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

        String value = null;

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
            Neo4jConstructor neo4jConstructor = new Neo4jConstructor();
            uctx = bfact.createUnmarshallingContext();
            RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));
            if (graphconstructor != null) {
                FullBeanImpl bean = neo4jConstructor.constructFullBean(rdf, graphconstructor);

//                        basicDocument.addField(
//                                EdmLabel.EUROPEANA_COMPLETENESS.toString(),
//                                completeness);
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
//                        basicDocument.addField("timestamp_created",
//                                timestampCreated);
                mdr.deleteValues(EuropeanaModelRegistry.UPDATEDSAVE);
                Date timestampUpdated = new Date();
                bean.setTimestampUpdated(timestampUpdated);
//                        basicDocument.addField("timestamp_update",
//                                timestampUpdated);
                mdr.addValue(EuropeanaModelRegistry.UPDATEDSAVE,
                        timestampUpdated.getTime());
                graphconstructor.parseMorphiaEntity(bean);
                //graphconstructor.extractRDFLinkReferences(value);
                if (operations > 100) {
                    operations=0;
//                    graphconstructor.generateNodes();
//                    graphconstructor.save();
//                    graphconstructor.generateNodeLinks();
//                    graphconstructor.save();
                }
                operations++;
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

        return false;
    }

    public void completed(ExecutionContext<MetaDataRecord<I>, I> execution)
            throws IngestionPluginFailedException {
        graphconstructor.generateNodes();
        graphconstructor.save();
        graphconstructor.generateNodeLinks();
        graphconstructor.save();
        operations=0;
//		graphconstructor.generateNodeLinks((String) execution.getExecution().getId());
//                ManagedTransaction.getInstance(graphconstructor.getGraphDatabase()).stop();
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
