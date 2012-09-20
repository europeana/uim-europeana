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

import java.io.IOException;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Level;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.lang.StringUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.xml.sax.SAXException;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoException;

import eu.europeana.corelib.definitions.jibx.AgentType;
import eu.europeana.corelib.definitions.jibx.Aggregation;
import eu.europeana.corelib.definitions.jibx.Concept;
import eu.europeana.corelib.definitions.jibx.EuropeanaAggregationType;
import eu.europeana.corelib.definitions.jibx.LiteralType;
import eu.europeana.corelib.definitions.jibx.PlaceType;
import eu.europeana.corelib.definitions.jibx.ProvidedCHOType;
import eu.europeana.corelib.definitions.jibx.ProxyType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.jibx.RDF.Choice;
import eu.europeana.corelib.definitions.jibx.ResourceOrLiteralType;
import eu.europeana.corelib.definitions.jibx.ResourceType;
import eu.europeana.corelib.definitions.jibx.TimeSpanType;
import eu.europeana.corelib.definitions.jibx.WebResourceType;
import eu.europeana.corelib.definitions.model.EdmLabel;
import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
import eu.europeana.corelib.solr.utils.SolrConstructor;
import eu.europeana.corelib.tools.rdf.Solr2Rdf;
import eu.europeana.uim.api.AbstractIngestionPlugin;
import eu.europeana.uim.api.CorruptedMetadataRecordException;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
import eu.europeana.uim.plugin.solr.utils.OsgiExtractor;
import eu.europeana.uim.store.MetaDataRecord;

/**
 * This is the main class implementing the UIM functionality for the solr
 * workflow plugin exposed as an OSGI service.
 * 
 * @author Georgios Markakis
 * @author Yorgos.Mamakis@ kb.nl
 * 
 */
public class SolrWorkflowPlugin extends AbstractIngestionPlugin {

	private static int recordNumber;

	/**
	 * The parameters used by this WorkflowStart
	 */
	private static final List<String> params = new ArrayList<String>() {
		private static final long serialVersionUID = 1L;

	};

	public SolrWorkflowPlugin() {
		super("solr_workflow", "Solr Repository Ingestion Plugin");

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see eu.europeana.uim.api.IngestionPlugin#processRecord(eu.europeana.uim.
	 * MetaDataRecord, eu.europeana.uim.api.ExecutionContext)
	 */
	@SuppressWarnings("unchecked")
	public <I> boolean processRecord(MetaDataRecord<I> mdr,
			ExecutionContext<I> context) throws IngestionPluginFailedException,
			CorruptedMetadataRecordException {

		try {

			Datastore datastore = null;
			Morphia morphia;
			try {
				// Dereferencer.setServer(new OsgiVocabularyMongoServer(new
				// Mongo("localhost",27017), "vocabulary"));

				morphia = new Morphia();
				morphia.map(ControlledVocabularyImpl.class);
				datastore = morphia.createDatastore(new Mongo("localhost",
						27017), "vocabulary");
				datastore.ensureIndexes();

			} catch (MongoException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			IBindingFactory bfact = BindingDirectory.getFactory(RDF.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			String value = mdr.getValues(EuropeanaModelRegistry.EDMRECORD).get(
					0);
			RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));

			Map<String, List<String>> dereferencedValues = new HashMap<String, List<String>>();
			List<Choice> choices = rdf.getChoiceList();
			for (Choice choice : choices) {
				if (choice.ifAgent()) {
					appendInMap(dereferencedValues, (Map<String, List<String>>) dereferenceAgent(datastore,
							choice.getAgent()));
				} else if (choice.ifAggregation()) {
					appendInMap(dereferencedValues, (Map<String, List<String>>)dereferenceAggregation(datastore,
							choice.getAggregation()));
				} else if (choice.ifConcept()) {
					appendInMap(dereferencedValues, (Map<String, List<String>>)dereferenceConcept(datastore,
							choice.getConcept()));
				} else if (choice.ifEuropeanaAggregation()) {
					appendInMap(dereferencedValues, (Map<String, List<String>>)dereferenceEuropeanaAggregation(
							datastore, choice.getEuropeanaAggregation()));
				} else if (choice.ifPlace()) {
					appendInMap(dereferencedValues, (Map<String, List<String>>)dereferencePlace(datastore,
							choice.getPlace()));
				} else if (choice.ifProvidedCHO()) {
					appendInMap(dereferencedValues, (Map<String, List<String>>)dereferenceProvidedCHO(datastore,
							choice.getProvidedCHO()));
				} else if (choice.ifProxy()) {
					appendInMap(dereferencedValues, (Map<String, List<String>>)dereferenceProxy(datastore,
							choice.getProxy()));
				} else if (choice.ifTimeSpan()) {
					appendInMap(dereferencedValues, (Map<String, List<String>>)dereferenceTimespan(datastore,
							choice.getTimeSpan()));
				} else if (choice.ifWebResource()) {
					appendInMap(dereferencedValues, (Map<String, List<String>>)dereferenceWebResource(datastore,
							choice.getWebResource()));
				}
			}

			Solr2Rdf solr2Rdf = new Solr2Rdf();
			solr2Rdf.initialize();
			String der = solr2Rdf.constructFromMap(
					new SolrConstructor().constructSolrDocument(rdf),
					dereferencedValues);
			System.out.println(der);
			mdr.addValue(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD, der);
			return true;

		} catch (JiBXException e) {
			context.getLoggingEngine().logFailed(
					Level.SEVERE,
					this,
					e,
					"JiBX unmarshalling has failed with the following error: "
							+ e.getMessage());
			e.printStackTrace();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SAXException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	private Map<String, ? extends List<String>> dereferenceWebResource(
			Datastore datastore, WebResourceType webResource)
			throws MalformedURLException, IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				webResource.getConformsToList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				webResource.getCreatedList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				webResource.getDescriptionList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				webResource.getExtentList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				webResource.getFormatList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				webResource.getHasPartList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				webResource.getIsFormatOfList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				webResource.getIssuedList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				webResource.getRightList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				webResource.getSourceList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				webResource.getIsNextInSequence()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, webResource.getRights()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, webResource.getAbout()));
		return retVal;
	}

	@SuppressWarnings("unchecked")
	private Map<String, ? extends List<String>> dereferenceTimespan(
			Datastore datastore, TimeSpanType timeSpan)
			throws MalformedURLException, IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				timeSpan.getAltLabelList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				timeSpan.getPrefLabelList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				timeSpan.getHasPartList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				timeSpan.getIsPartOfList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				timeSpan.getNoteList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				timeSpan.getSameAList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, timeSpan.getAbout()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, timeSpan.getBegin()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, timeSpan.getEnd()));
		return retVal;
	}

	@SuppressWarnings("unchecked")
	private Map<String, ? extends List<String>> dereferenceProxy(
			Datastore datastore, ProxyType proxy) throws MalformedURLException,
			IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				proxy.getHasMetList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				proxy.getHasTypeList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				proxy.getIncorporateList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				proxy.getIsDerivativeOfList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				proxy.getIsRelatedToList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				proxy.getIsSimilarToList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				proxy.getIsSuccessorOfList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				proxy.getProxyInList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				proxy.getRealizeList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				proxy.getUserTagList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore, proxy.getYearList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, proxy.getAbout()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				proxy.getCurrentLocation()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, proxy.getProxyFor()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, proxy.getType()));
		List<eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice> choices = proxy
				.getChoiceList();
		if (choices != null) {
			for (eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice choice : choices) {
				if (choice.ifAlternative())
					appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
							choice.getAlternative()));
				if(choice.ifConformsTo())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getConformsTo()));
				if(choice.ifContributor())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getContributor()));
				if(choice.ifCoverage())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getCoverage()));
				if(choice.ifCreated())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getCreated()));
				if(choice.ifCreator())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getCreator()));
				if(choice.ifDate())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getDate()));
				if(choice.ifDescription())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getDescription()));
				if(choice.ifExtent())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getExtent()));
				if(choice.ifFormat())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getFormat()));
				if(choice.ifHasFormat())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getHasFormat()));
				if(choice.ifHasPart())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getHasPart()));
				if(choice.ifHasVersion())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getHasVersion()));
				if(choice.ifIdentifier())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getIdentifier()));
				if(choice.ifIsFormatOf())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getIsFormatOf()));
				if(choice.ifIsPartOf())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getIsPartOf()));
				if(choice.ifIsReferencedBy())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getIsReferencedBy()));
				if(choice.ifIsReplacedBy())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getIsReplacedBy()));
				if(choice.ifIsRequiredBy())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getIsRequiredBy()));
				if(choice.ifIssued())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getIssued()));
				if(choice.ifIsVersionOf())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getIsVersionOf()));
				if(choice.ifLanguage())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getLanguage()));
				if(choice.ifMedium())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getMedium()));
				if(choice.ifProvenance())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getProvenance()));
				if(choice.ifPublisher())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getPublisher()));
				if(choice.ifReferences())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getReferences()));
				if(choice.ifRelation())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getRelation()));
				if(choice.ifReplaces())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getReplaces()));
				if(choice.ifRights())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getRights()));
				if(choice.ifSource())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getSource()));
				if(choice.ifSpatial())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getSpatial()));
				if(choice.ifSubject())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getSubject()));
				if(choice.ifTableOfContents())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getTableOfContents()));
				if(choice.ifTemporal())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getTemporal()));
				if(choice.ifTitle())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getTitle()));
				if(choice.ifType())
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
						choice.getType()));
			}
		}
		return retVal;
	}

	@SuppressWarnings("unchecked")
	private Map<String, ? extends List<String>> dereferenceProvidedCHO(
			Datastore datastore, ProvidedCHOType providedCHO)
			throws MalformedURLException, IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, providedCHO.getAbout()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				providedCHO.getSameAList()));
		return retVal;
	}

	@SuppressWarnings("unchecked")
	private Map<String, ? extends List<String>> dereferencePlace(
			Datastore datastore, PlaceType place) throws MalformedURLException,
			IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, place.getAbout()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, place.getAlt()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, place.getLat()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, place.getLong()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				place.getAltLabelList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				place.getPrefLabelList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				place.getIsPartOfList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore, place.getNoteList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				place.getSameAList()));
		return retVal;
	}

	@SuppressWarnings("unchecked")
	private Map<String, ? extends List<String>> dereferenceConcept(
			Datastore datastore, Concept concept) throws MalformedURLException,
			IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, concept.getAbout()));
		for (eu.europeana.corelib.definitions.jibx.Concept.Choice choice : concept
				.getChoiceList()) {
			if(choice.ifAltLabel())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
					choice.getAltLabel()));
			if(choice.ifPrefLabel())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
					choice.getPrefLabel()));
			if(choice.ifBroader())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, choice.getBroader()));
			if(choice.ifBroadMatch())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
					choice.getBroadMatch()));
			if(choice.ifCloseMatch())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
					choice.getCloseMatch()));
			if(choice.ifExactMatch())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
					choice.getExactMatch()));
			if(choice.ifNarrower())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
					choice.getNarrower()));
			if(choice.ifNarrowMatch())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
					choice.getNarrowMatch()));
			if(choice.ifNote())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, choice.getNote()));
			if(choice.ifNotation())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
					choice.getNotation()));
			if(choice.ifRelated())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, choice.getRelated()));
			if(choice.ifRelatedMatch())
			appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
					choice.getRelatedMatch()));
		}
		return retVal;
	}

	@SuppressWarnings("unchecked")
	private Map<String, ? extends List<String>> dereferenceEuropeanaAggregation(
			Datastore datastore, EuropeanaAggregationType aggregation)
			throws MalformedURLException, IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, aggregation.getAbout()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getAggregatedCHO()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getCountry()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				aggregation.getHasViewList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getCreator()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getIsShownBy()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getLandingPage()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getLanguage()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, aggregation.getRights()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				aggregation.getAggregateList()));
		return retVal;
	}

	@SuppressWarnings("unchecked")
	private Map<String, ? extends List<String>> dereferenceAggregation(
			Datastore datastore, Aggregation aggregation)
			throws MalformedURLException, IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, aggregation.getAbout()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getAggregatedCHO()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getDataProvider()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				aggregation.getHasViewList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getIsShownAt()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getIsShownBy()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, aggregation.getObject()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				aggregation.getProvider()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, aggregation.getRights()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, aggregation.getUgc()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				aggregation.getRightList()));
		return retVal;
	}

	@SuppressWarnings("unchecked")
	private Map<String, ? extends List<String>> dereferenceAgent(
			Datastore datastore, AgentType agent) throws MalformedURLException,
			IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, agent.getAbout()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				agent.getAltLabelList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore, agent.getDateList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				agent.getHasMetList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				agent.getIdentifierList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				agent.getIsRelatedToList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore, agent.getNameList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore, agent.getNoteList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				agent.getPrefLabelList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteralList(datastore,
				agent.getSameAList()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, agent.getBegin()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, agent.getEnd()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				agent.getBiographicalInformation()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, agent.getDateOfBirth()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, agent.getDateOfDeath()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				agent.getDateOfEstablishment()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore,
				agent.getDateOfTermination()));
		appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, agent.getGender()));
		return retVal;
	}

	@SuppressWarnings("unchecked")
	private Map<String, List<String>> derefResourceOrLiteralList(
			Datastore datastore, List<?> list) throws MalformedURLException,
			IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		if (list != null) {
			for (Object object : list) {
				appendInMap(retVal,(Map<String, List<String>>) derefResourceOrLiteral(datastore, object));
			}
		}
		return retVal;
	}

	private void appendInMap(Map<String, List<String>> retVal,
			Map<String, List<String>> map) {
		for(Entry<String,List<String>> entry: map.entrySet()){
			if(retVal.containsKey(entry.getKey())){
				List<String> val = retVal.get(entry.getKey());
				List<String> mapVal = entry.getValue();
				for(String str:mapVal){
					if (!val.contains(str)){
						val.add(str);
					}
				}
				retVal.put(entry.getKey(), val);
			}
			else{
				retVal.put(entry.getKey(),entry.getValue());
			}
			
		}
		
	}

	private Map<String, ? extends List<String>> derefResourceOrLiteral(
			Datastore datastore, Object object) throws MalformedURLException,
			IOException {
		Map<String, List<String>> retVal = new HashMap<String, List<String>>();
		List<String> originalValue = new ArrayList<String>();
		OsgiExtractor extractor = new OsgiExtractor();
		extractor.setDatastore(datastore);
		if (object instanceof String) {
			originalValue.add((String) object);
			retVal.put(EdmLabel.ORIGINAL.toString(), originalValue);
			if (isURI((String) object)) {
				ControlledVocabularyImpl controlVocabulary = getControlledVocabulary(
						datastore, "URI", (String) object);

				appendInMap(retVal,(Map<String, List<String>>) extractor.denormalize((String) object,
						controlVocabulary));
			}
		}
		else if (object instanceof ResourceType) {

			if (((ResourceType) object).getResource() != null) {

				originalValue.add(((ResourceType) object).getResource());
				retVal.put(EdmLabel.ORIGINAL.toString(), originalValue);
				if (isURI(((ResourceType) object).getResource())) {
					ControlledVocabularyImpl controlVocabulary = getControlledVocabulary(
							datastore, "URI",
							((ResourceType) object).getResource());
					appendInMap(retVal,(Map<String, List<String>>) extractor.denormalize(
							((ResourceType) object).getResource(),
							controlVocabulary));
				}
			}
		} else if (object instanceof ResourceOrLiteralType) {
			if (((ResourceOrLiteralType) object).getResource() != null) {
				originalValue.add(((ResourceOrLiteralType) object)
						.getResource());
				retVal.put(EdmLabel.ORIGINAL.toString(), originalValue);
				if (isURI(((ResourceOrLiteralType) object).getResource())) {
					ControlledVocabularyImpl controlVocabulary = getControlledVocabulary(
							datastore, "URI",
							((ResourceOrLiteralType) object).getResource());
					appendInMap(retVal,(Map<String, List<String>>) extractor.denormalize(
							((ResourceOrLiteralType) object).getResource(),
							controlVocabulary));
				}
			}
			if (((ResourceOrLiteralType) object).getString() != null) {
				originalValue.add(((ResourceOrLiteralType) object).getString());
				retVal.put(EdmLabel.ORIGINAL.toString(), originalValue);
				if (isURI(((ResourceOrLiteralType) object).getString())) {
					ControlledVocabularyImpl controlVocabulary = getControlledVocabulary(
							datastore, "URI",
							((ResourceOrLiteralType) object).getString());
					appendInMap(retVal,(Map<String, List<String>>) extractor.denormalize(
							((ResourceOrLiteralType) object).getString(),
							controlVocabulary));
				}
			}
		} else if (object instanceof LiteralType) {
			if (((LiteralType) object).getString() != null) {
				originalValue.add(((LiteralType) object).getString());
				retVal.put(EdmLabel.ORIGINAL.toString(), originalValue);
				if (isURI(((LiteralType) object).getString())) {
					ControlledVocabularyImpl controlVocabulary = getControlledVocabulary(
							datastore, "URI",
							((LiteralType) object).getString());
					appendInMap(retVal,(Map<String, List<String>>) extractor.denormalize(
							((LiteralType) object).getString(),
							controlVocabulary));
				}
			}
		}
		return retVal;
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	public void shutdown() {
		// TODO Auto-generated method stub

	}

	public int getPreferredThreadCount() {
		return 3;
	}

	public int getMaximumThreadCount() {
		return 5;
	}

	public <I> void initialize(ExecutionContext<I> context)
			throws IngestionPluginFailedException {

	}

	public <I> void completed(ExecutionContext<I> context)
			throws IngestionPluginFailedException {

	}

	@Override
	public TKey<?, ?>[] getInputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TKey<?, ?>[] getOptionalFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public TKey<?, ?>[] getOutputFields() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getParameters() {
		return params;
	}

	public static int getRecords() {
		return recordNumber;
	}

	private static boolean isURI(String uri) {

		try {
			new URL(uri);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}

	}

	private ControlledVocabularyImpl getControlledVocabulary(
			Datastore datastore, String field, String filter)
			throws UnknownHostException, MongoException {
		String[] splitName = filter.split("/");
		String vocabularyName = splitName[0] + "/" + splitName[1] + "/"
				+ splitName[2] + "/";
		List<ControlledVocabularyImpl> vocabularies = datastore
				.find(ControlledVocabularyImpl.class)
				.filter(field, vocabularyName).asList();
		for (ControlledVocabularyImpl vocabulary : vocabularies) {
			boolean ruleController = true;
			for (String rule : vocabulary.getRules()) {
				ruleController = ruleController
						&& (filter.contains(rule) || StringUtils.equals(rule,
								"*"));
			}
			if (ruleController) {
				return vocabulary;
			}
		}
		return null;
	}
}
