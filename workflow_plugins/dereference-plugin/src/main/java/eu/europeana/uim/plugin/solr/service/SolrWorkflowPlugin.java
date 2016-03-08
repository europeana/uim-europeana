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
package eu.europeana.uim.plugin.solr.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.lang.StringUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.theeuropeanlibrary.model.common.qualifier.Status;

import eu.europeana.corelib.definitions.jibx.AgentType;
import eu.europeana.corelib.definitions.jibx.Concept;
import eu.europeana.corelib.definitions.jibx.EuropeanaProxy;
import eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice;
import eu.europeana.corelib.definitions.jibx.LiteralType.Lang;
import eu.europeana.corelib.definitions.jibx.PlaceType;
import eu.europeana.corelib.definitions.jibx.ProxyType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.jibx.ResourceOrLiteralType;
import eu.europeana.corelib.definitions.jibx.Subject;
import eu.europeana.corelib.definitions.jibx.TimeSpanType;
import eu.europeana.corelib.definitions.jibx.WebResourceType;
import eu.europeana.corelib.definitions.jibx.Year;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.logging.LoggingEngine;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.orchestration.ExecutionContext;
import eu.europeana.uim.plugin.ingestion.AbstractIngestionPlugin;
import eu.europeana.uim.plugin.ingestion.CorruptedDatasetException;
import eu.europeana.uim.plugin.ingestion.IngestionPluginFailedException;
import eu.europeana.uim.plugin.solr.helpers.AgentDereferencer;
import eu.europeana.uim.plugin.solr.helpers.ConceptDereferencer;
import eu.europeana.uim.plugin.solr.helpers.Dereferencer;
import eu.europeana.uim.plugin.solr.helpers.PlaceDereferencer;
import eu.europeana.uim.plugin.solr.helpers.ProxyDereferencer;
import eu.europeana.uim.plugin.solr.helpers.ResourceNotRDFException;
import eu.europeana.uim.plugin.solr.helpers.TimespanDereferencer;
import eu.europeana.uim.plugin.solr.helpers.WebResourceDereferencer;
import eu.europeana.uim.plugin.solr.utils.EuropeanaDateUtils;
import eu.europeana.uim.plugin.solr.utils.JibxUtils;
import eu.europeana.uim.plugin.solr.utils.OsgiExtractor;
import eu.europeana.uim.plugin.solr.utils.VocMemCache;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.MetaDataRecord;

/**
 * This is the main class implementing the UIM functionality for the solr workflow plugin exposed as an OSGI service.
 *
 * @author Georgios Markakis
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public class SolrWorkflowPlugin<I> extends
        AbstractIngestionPlugin<MetaDataRecord<I>, I> {

    private final static String OVERRIDECHECKS = "override.all.checks";
    private final static String CLEARCACHE = "clear.vocabulary.cache";
    private final static String LIBRARYOFCONGRESS = "loc.dataset";
    @SuppressWarnings("rawtypes")
    private final static TKey<SolrWorkflowPlugin, Long> date = TKey.register(
            SolrWorkflowPlugin.class, "enrichment", Long.class);
    private static SolrWorkflowService solrWorkflowService;
    private static IBindingFactory bfact;
    private static final Logger log = Logger.getLogger(SolrWorkflowPlugin.class
            .getName());
    private static LoggingEngine logEngine;
    /**
     * The parameters used by this WorkflowStart
     */
    private static final List<String> params = new ArrayList<String>() {
        private static final long serialVersionUID = 1L;

        {
            add(OVERRIDECHECKS);
            add(CLEARCACHE);
            add(LIBRARYOFCONGRESS);
        }
    };

    /**
     *
     */
    public SolrWorkflowPlugin(SolrWorkflowService solrWorkflowService) {
        super("solr_workflow", "Solr Repository Ingestion Plugin");
        SolrWorkflowPlugin.solrWorkflowService = solrWorkflowService;

        try {
            bfact = BindingDirectory.getFactory(RDF.class);
        } catch (JiBXException e) {
            log.log(Level.SEVERE,
                    "Error initializing Jibx Factory:" + e.getMessage());
        }

    }

    /*
     * (non-Javadoc)
     * 
     * @see eu.europeana.uim.api.IngestionPlugin#processRecord(eu.europeana.uim.
     * MetaDataRecord, eu.europeana.uim.api.ExecutionContext)
     */
    @Override
    public boolean process(MetaDataRecord<I> mdr,
            ExecutionContext<MetaDataRecord<I>, I> context)
            throws IngestionPluginFailedException, CorruptedDatasetException {
        mdr.deleteValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD);

        String overrideChecks = context.getProperties().getProperty(
                OVERRIDECHECKS);
        boolean check = false;
        if (StringUtils.isNotEmpty(overrideChecks)) {
            check = Boolean.parseBoolean(overrideChecks);
        }
        if (StringUtils.isNotEmpty(context.getProperties().getProperty(
                CLEARCACHE))) {
            if (Boolean.parseBoolean(context.getProperties().getProperty(
                    CLEARCACHE))) {
                OsgiExtractor.clearCache();
                VocMemCache.clearCache(solrWorkflowService);
            }
        }
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
                    || check) {
                if (mdr.getValues(EuropeanaModelRegistry.STATUS).size() == 0
                        || !mdr.getValues(EuropeanaModelRegistry.STATUS).get(0)
                        .equals(Status.DELETED)) {
                    try {
                    	// already done this above!
                    	// mdr.deleteValues(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD);
                        String value = mdr.getValues(
                                EuropeanaModelRegistry.EDMRECORD).get(0);
                        IUnmarshallingContext uctx = bfact
                                .createUnmarshallingContext();
                        IMarshallingContext marshallingContext = bfact
                                .createMarshallingContext();
                        marshallingContext.setIndent(2);
                        RDF rdf = (RDF) uctx
                                .unmarshalDocument(new StringReader(value));
                        RDF rdfCopy = clone(rdf);
                        if (rdf.getAgentList() != null) {
                            for (AgentType agent : rdf.getAgentList()) {
                                dereferenceAgent(rdfCopy, agent);
                            }
                        }
                        if (rdf.getConceptList() != null) {
                            for (Concept concept : rdf.getConceptList()) {
                                dereferenceConcept(rdfCopy, concept);
                            }
                        }

                        if (rdf.getPlaceList() != null) {
                            for (PlaceType place : rdf.getPlaceList()) {
                                dereferencePlace(rdfCopy, place);
                            }
                        }

                        for (ProxyType proxy : rdf.getProxyList()) {
                            if (proxy.getEuropeanaProxy() == null
                                    || !proxy.getEuropeanaProxy()
                                    .isEuropeanaProxy()) {
                                if (StringUtils.isNotEmpty(context.getProperties().getProperty(
                                        LIBRARYOFCONGRESS))) {
                                    if (Boolean.parseBoolean(context.getProperties().getProperty(
                                            LIBRARYOFCONGRESS))) {
                                        for (Choice choice : proxy.getChoiceList()) {
                                            if (choice.ifSubject()) {
                                                if (StringUtils.startsWith(choice.getSubject().getString(), "sh")
                                                        && StringUtils.isNumeric(StringUtils.substringAfter(choice.
                                                                        getSubject().getString(), "sh"))) {
                                                    Subject sbj = choice.getSubject();
                                                    String subject = "http://data.europeana.eu/concept/loc/" + sbj.
                                                            getString();
                                                    ResourceOrLiteralType.Resource rs
                                                            = new ResourceOrLiteralType.Resource();
                                                    rs.setResource(subject);
                                                    Subject sbjNrm = new Subject();
                                                    sbjNrm.setResource(rs);
                                                    sbjNrm.setLang(new ResourceOrLiteralType.Lang());
                                                    sbjNrm.setString("");
                                                    choice.setSubject(sbjNrm);
                                                }
                                            }
                                        }
                                    }
                                }
                                dereferenceProxy(rdfCopy, proxy);
                            }
                        }

                        if (rdf.getTimeSpanList() != null) {
                            for (TimeSpanType timespan : rdf.getTimeSpanList()) {
                                dereferenceTimespan(rdfCopy, timespan);
                            }
                        }
                        if (rdf.getWebResourceList() != null) {
                            for (WebResourceType webresource : rdf
                                    .getWebResourceList()) {
                                dereferenceWebResource(rdfCopy, webresource);
                            }
                        }
                        ByteArrayOutputStream out = new ByteArrayOutputStream();
                        RDF rdfFinal = cleanRDF(rdfCopy);
                        ProxyType europeanaProxy = new ProxyType();
                        EuropeanaProxy prx = new EuropeanaProxy();
                        prx.setEuropeanaProxy(true);
                        europeanaProxy.setEuropeanaProxy(prx);
                        List<String> years = new ArrayList<String>();
                        for (ProxyType proxy : rdfFinal.getProxyList()) {
                            years.addAll(new EuropeanaDateUtils()
                                    .createEuropeanaYears(proxy));
                            europeanaProxy.setType(proxy.getType());
                        }
                        List<Year> yearList = new ArrayList<Year>();
                        for (String year : years) {
                            Year yearObj = new Year();
                            Lang lang = new Lang();
                            lang.setLang("def");
                            yearObj.setLang(lang);
                            yearObj.setString(year);
                            yearList.add(yearObj);
                        }
                        europeanaProxy.setYearList(yearList);
                        for (ProxyType proxy : rdfFinal.getProxyList()) {
                            if (proxy != null
                                    && proxy.getEuropeanaProxy() != null
                                    && proxy.getEuropeanaProxy()
                                    .isEuropeanaProxy()) {
                                rdfFinal.getProxyList().remove(proxy);
                            }
                        }
                        rdfFinal.getProxyList().add(europeanaProxy);

                        marshallingContext.marshalDocument(rdfFinal, "UTF-8",
                                null, out);
                        String der = out.toString("UTF-8");

                        mdr.addValue(
                                EuropeanaModelRegistry.EDMDEREFERENCEDRECORD,
                                der);
                        return true;

                    } catch (JiBXException e) {
                        e.printStackTrace();
                        context.getLoggingEngine().logFailed(
                                Level.SEVERE,
                                this,
                                e,
                                "JiBX unmarshalling has failed with the following error: "
                                + e.getMessage());

                    } catch (MalformedURLException e) {
                        if (logEngine != null) {
                            logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
                        }
                        log.log(Level.SEVERE,
                                "Error: " + e.getMessage());
                        e.printStackTrace();
                    } catch (IOException e) {
                        if (logEngine != null) {
                            logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
                        }
                        log.log(Level.SEVERE,
                                "Error: " + e.getMessage());
                        e.printStackTrace();
                    } catch (SecurityException e) {
                        if (logEngine != null) {
                            logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
                        }
                        log.log(Level.SEVERE,
                                "Error: " + e.getMessage());
                        e.printStackTrace();
                    } catch (IllegalArgumentException e) {
                        if (logEngine != null) {
                            logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
                        }
                        log.log(Level.SEVERE,
                                "Error: " + e.getMessage());
                        e.printStackTrace();
                    } catch (NoSuchMethodException e) {
                        if (logEngine != null) {
                            logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
                        }
                        log.log(Level.SEVERE,
                                "Error: " + e.getMessage());
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        if (logEngine != null) {
                            logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
                        }
                        log.log(Level.SEVERE,
                                "Error: " + e.getMessage());
                        e.printStackTrace();
                    } catch (InvocationTargetException e) {
                        if (logEngine != null) {
                            logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
                        }
                        log.log(Level.SEVERE,
                                "Error: " + e.getMessage());
                        e.printStackTrace();
                    } catch (InstantiationException e) {
                        if (logEngine != null) {
                            logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
                        }
                        log.log(Level.SEVERE,
                                "Error: " + e.getMessage());

                        e.printStackTrace();
                    }

                }
            }
        } catch (Exception e) {
            if (logEngine != null) {
                logEngine.logFailed(Level.SEVERE, this, e, e.getMessage());
            }
            e.printStackTrace();
        }
        return false;
    }

    private RDF clone(RDF rdf) {
        RDF rdfCopy = new RDF();
        if (rdf.getAgentList() != null) {
            rdfCopy.setAgentList(copyList(rdf.getAgentList()));
        }
        if (rdf.getPlaceList() != null) {
            rdfCopy.setPlaceList(copyList(rdf.getPlaceList()));
        }
        if (rdf.getConceptList() != null) {
            rdfCopy.setConceptList(copyList(rdf.getConceptList()));
        }
        if (rdf.getEuropeanaAggregationList() != null) {
            rdfCopy.setEuropeanaAggregationList(copyList(rdf
                    .getEuropeanaAggregationList()));
        }
        if (rdf.getAggregationList() != null) {
            rdfCopy.setAggregationList(copyList(rdf.getAggregationList()));
        }
        if (rdf.getProvidedCHOList() != null) {
            rdfCopy.setProvidedCHOList(copyList(rdf.getProvidedCHOList()));
        }
        if (rdf.getProxyList() != null) {
            rdfCopy.setProxyList(copyList(rdf.getProxyList()));
        }
        if (rdf.getTimeSpanList() != null) {
            rdfCopy.setTimeSpanList(rdf.getTimeSpanList());
        }
        if (rdf.getWebResourceList() != null) {
            rdfCopy.setWebResourceList(copyList(rdf.getWebResourceList()));
        }
        if(rdf.getOrganizationList()!=null){
        	rdfCopy.setOrganizationList(copyList(rdf.getOrganizationList()));
        }
        if(rdf.getDatasetList()!=null){
        	rdfCopy.setDatasetList(copyList(rdf.getDatasetList()));
        }
        if(rdf.getLicenseList()!=null){
        	rdfCopy.setLicenseList(copyList(rdf.getLicenseList()));
        }
        if(rdf.getServiceList()!=null){
            rdfCopy.setServiceList(copyList(rdf.getServiceList()));
        }
        return rdfCopy;
    }

    private <T> List<T> copyList(List<T> originalList) {
        List<T> copy = new ArrayList<T>(originalList.size());
        for (int i = 0; i < originalList.size(); i++) {

            copy.add(originalList.get(0));
        }
        Collections.copy(copy, originalList);
        return copy;
    }

    private RDF cleanRDF(RDF rdf) {
        RDF rdfFinal = new RDF();
        List<AgentType> agents = new ArrayList<AgentType>();
        List<TimeSpanType> timespans = new ArrayList<TimeSpanType>();
        List<PlaceType> places = new ArrayList<PlaceType>();
        List<Concept> concepts = new ArrayList<Concept>();
        JibxUtils utils = new JibxUtils();

        if (rdf.getAgentList() != null) {

            agents.addAll(rdf.getAgentList());

            for (int i = 0; i < agents.size() - 1; i++) {
                AgentType sAgent = agents.get(i);
                for (int k = i + 1; k < agents.size(); k++) {
                    AgentType fAgent = agents.get(k);
                    if (StringUtils.contains(fAgent.getAbout(),
                            sAgent.getAbout())
                            || StringUtils.contains(fAgent.getAbout(),
                                    sAgent.getAbout())) {

                        agents.set(i, utils.mergeAgentFields(fAgent, sAgent));
                        sAgent = agents.get(i);
                        agents.remove(k);
                        k--;
                    }
                }

            }
            rdfFinal.setAgentList(agents);
        }
        if (rdf.getConceptList() != null) {
            concepts.addAll(rdf.getConceptList());
            for (int i = 0; i < concepts.size() - 1; i++) {
                Concept sConcept = concepts.get(i);
                for (int k = i + 1; k < concepts.size(); k++) {
                    Concept fConcept = concepts.get(k);
                    if (StringUtils.contains(fConcept.getAbout(),
                            sConcept.getAbout())
                            || StringUtils.contains(sConcept.getAbout(),
                                    fConcept.getAbout())) {
                        concepts.set(i,
                                utils.mergeConceptsField(fConcept, sConcept));
                        sConcept = concepts.get(i);
                        concepts.remove(k);
                        k--;
                    }
                }
            }

            rdfFinal.setConceptList(concepts);
        }
        if (rdf.getTimeSpanList() != null) {
            timespans.addAll(rdf.getTimeSpanList());
            for (int i = 0; i < timespans.size() - 1; i++) {
                TimeSpanType sTs = timespans.get(i);
                for (int k = i + 1; k < timespans.size(); k++) {
                    TimeSpanType fTs = timespans.get(k);
                    if (StringUtils.contains(fTs.getAbout(), sTs.getAbout())
                            || StringUtils.contains(sTs.getAbout(),
                                    fTs.getAbout())) {
                        timespans.set(i, utils.mergeTimespanFields(fTs, sTs));
                        sTs = timespans.get(i);
                        timespans.remove(k);
                        k--;
                    }
                }
            }
            rdfFinal.setTimeSpanList(timespans);
        }
        if (rdf.getPlaceList() != null) {
            places.addAll(rdf.getPlaceList());

            for (int i = 0; i < places.size() - 1; i++) {
                PlaceType sPlace = places.get(i);
                for (int k = i + 1; k < places.size(); k++) {
                    PlaceType fPlace = places.get(k);
                    if (StringUtils
                            .equals(fPlace.getAbout(), sPlace.getAbout())
                            || StringUtils.contains(sPlace.getAbout(),
                                    fPlace.getAbout())) {
                        if (fPlace.getAbout() != null
                                && sPlace.getAbout() != null) {
                            places.set(i,
                                    utils.mergePlacesFields(fPlace, sPlace));
                        }
                        sPlace = places.get(i);
                        places.remove(k);
                        k--;

                    }
                }
            }

            rdfFinal.setPlaceList(places);
        }
        rdfFinal.setAggregationList(rdf.getAggregationList());
        rdfFinal.setProxyList(rdf.getProxyList());
        rdfFinal.setProvidedCHOList(rdf.getProvidedCHOList());
        rdfFinal.setEuropeanaAggregationList(rdf.getEuropeanaAggregationList());
        rdfFinal.setWebResourceList(rdf.getWebResourceList());
        rdfFinal.setLicenseList(rdf.getLicenseList());
        rdfFinal.setOrganizationList(rdf.getOrganizationList());
        rdfFinal.setDatasetList(rdf.getDatasetList());
        rdfFinal.setServiceList(rdf.getServiceList());
        return rdfFinal;
    }

    private void dereferenceWebResource(RDF rdf, WebResourceType webResource)
            throws MalformedURLException, IOException, SecurityException,
            IllegalArgumentException, InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Dereferencer der = new WebResourceDereferencer(solrWorkflowService);
        try {
            der.dereference(rdf, webResource);
        } catch (ResourceNotRDFException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void dereferenceTimespan(RDF rdf, TimeSpanType timeSpan)
            throws MalformedURLException, IOException, SecurityException,
            IllegalArgumentException, InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        TimespanDereferencer der = new TimespanDereferencer(solrWorkflowService);
        try {
            der.dereference(rdf, timeSpan);
        } catch (ResourceNotRDFException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void dereferenceProxy(RDF rdf, ProxyType proxy)
            throws MalformedURLException, IOException, SecurityException,
            IllegalArgumentException, InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Dereferencer der = new ProxyDereferencer(solrWorkflowService);
        try {
            der.dereference(rdf, proxy);
        } catch (ResourceNotRDFException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void dereferencePlace(RDF rdf, PlaceType place)
            throws MalformedURLException, IOException, SecurityException,
            IllegalArgumentException, InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Dereferencer der = new PlaceDereferencer(solrWorkflowService);
        try {
            der.dereference(rdf, place);
        } catch (ResourceNotRDFException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void dereferenceConcept(RDF rdf, Concept concept)
            throws MalformedURLException, IOException, SecurityException,
            IllegalArgumentException, InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Dereferencer der = new ConceptDereferencer(solrWorkflowService);
        try {
            der.dereference(rdf, concept);
        } catch (ResourceNotRDFException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void dereferenceAgent(RDF rdf, AgentType agent)
            throws MalformedURLException, IOException, SecurityException,
            IllegalArgumentException, InstantiationException,
            IllegalAccessException, NoSuchMethodException,
            InvocationTargetException {
        Dereferencer der = new AgentDereferencer(solrWorkflowService);
        try {
            der.dereference(rdf, agent);
        } catch (ResourceNotRDFException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
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
     * @see eu.europeana.uim.plugin.ExecutionPlugin#completed(eu.europeana.uim.
     * orchestration.ExecutionContext)
     */
    @Override
    public void completed(ExecutionContext<MetaDataRecord<I>, I> context)
            throws IngestionPluginFailedException {
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
        logEngine = context.getLoggingEngine();
        if (((Collection<I>) context.getDataSet())
                .getValue(ControlledVocabularyProxy.LASTINGESTION_DATE
                        .toString()) != null) {
            context.putValue(date, Long.parseLong(((Collection<I>) context
                    .getDataSet())
                    .getValue(ControlledVocabularyProxy.LASTINGESTION_DATE
                            .toString())));

        } else {
            context.putValue(date, new Date().getTime());
        }
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
     * @see eu.europeana.uim.plugin.Plugin#getParameters()
     */
    @Override
    public List<String> getParameters() {
        return params;
    }

}
