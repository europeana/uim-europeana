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
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;

import org.apache.commons.lang.StringUtils;
import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

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
import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
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
			RDF rdfCopy = (RDF) uctx.unmarshalDocument(new StringReader(value));
			List<Choice> choices = rdf.getChoiceList();
			for (Choice choice : choices) {
				if (choice.ifAgent()) {
					dereferenceAgent(rdfCopy, datastore, choice.getAgent());
				} else if (choice.ifAggregation()) {
					dereferenceAggregation(rdfCopy, datastore,
							choice.getAggregation());
				} else if (choice.ifConcept()) {
					dereferenceConcept(rdfCopy, datastore, choice.getConcept());
				} else if (choice.ifEuropeanaAggregation()) {
					dereferenceEuropeanaAggregation(rdfCopy, datastore,
							choice.getEuropeanaAggregation());
				} else if (choice.ifPlace()) {
					dereferencePlace(rdf, datastore, choice.getPlace());
				} else if (choice.ifProvidedCHO()) {
					dereferenceProvidedCHO(rdfCopy, datastore,
							choice.getProvidedCHO());
				} else if (choice.ifProxy()) {
					dereferenceProxy(rdfCopy, datastore, choice.getProxy());
				} else if (choice.ifTimeSpan()) {
					dereferenceTimespan(rdfCopy, datastore,
							choice.getTimeSpan());
				} else if (choice.ifWebResource()) {
					dereferenceWebResource(rdfCopy, datastore,
							choice.getWebResource());
				}
			}

			IBindingFactory bfact2 = BindingDirectory.getFactory(RDF.class);
			IMarshallingContext marshallingContext = bfact2
					.createMarshallingContext();
			marshallingContext.setIndent(2);
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			RDF rdfFinal = cleanRDF(rdfCopy);
			marshallingContext.marshalDocument(rdfFinal, "UTF-8", null, out);
			String der = out.toString("UTF-8");
			mdr.addValue(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD, der);
			System.out.println(der);
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
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return false;
	}

	private RDF cleanRDF(RDF rdf) {
		RDF rdfFinal = new RDF();
		List<AgentType> agents = new CopyOnWriteArrayList<AgentType>();
		List<TimeSpanType> timespans = new CopyOnWriteArrayList<TimeSpanType>();
		List<PlaceType> places = new CopyOnWriteArrayList<PlaceType>();
		List<Concept> concepts = new CopyOnWriteArrayList<Concept>();
		List<RDF.Choice> choices = new ArrayList<RDF.Choice>();
		for (RDF.Choice rdfChoice : rdf.getChoiceList()) {
			if (rdfChoice.ifAgent()) {
				AgentType newAgent = rdfChoice.getAgent();
				for (AgentType agent : agents) {
					if (StringUtils.equals(agent.getAbout(),
							newAgent.getAbout())) {
						if (agent.getPrefLabelList().size() <= newAgent
								.getPrefLabelList().size()) {
							agents.remove(agent);
						}
					}
				}
				agents.add(newAgent);
			} else if (rdfChoice.ifConcept()) {
				Concept newConcept = rdfChoice.getConcept();
				for (Concept concept : concepts) {
					if (StringUtils.equals(concept.getAbout(),
							newConcept.getAbout())) {
						if (concept.getChoiceList().size() <= newConcept
								.getChoiceList().size()) {
							concepts.remove(concept);
						}
					}
				}
				concepts.add(newConcept);
			} else if (rdfChoice.ifTimeSpan()) {
				TimeSpanType newTs = rdfChoice.getTimeSpan();
				for (TimeSpanType ts : timespans) {
					if (StringUtils.equals(ts.getAbout(), newTs.getAbout())) {
						if (ts.getIsPartOfList().size() <= newTs
								.getIsPartOfList().size()) {
							timespans.remove(ts);
						}
					}
				}
				timespans.add(newTs);
			} else if (rdfChoice.ifPlace()) {
				PlaceType newPlace = rdfChoice.getPlace();
				for (PlaceType place : places) {
					if (StringUtils.equals(place.getAbout(),
							newPlace.getAbout())) {
						if (place.getPrefLabelList().size() <= newPlace
								.getPrefLabelList().size()) {
							places.remove(place);
						}
					}
				}
				places.add(newPlace);
			} else {
				choices.add(rdfChoice);
			}
		}
		for (AgentType agent : agents) {
			Choice choice = new Choice();
			choice.setAgent(agent);
			choices.add(choice);
		}
		for (PlaceType place : places) {
			Choice choice = new Choice();
			choice.setPlace(place);
			choices.add(choice);
		}
		for (TimeSpanType timespan : timespans) {
			Choice choice = new Choice();
			choice.setTimeSpan(timespan);
			choices.add(choice);
		}
		for (Concept concept : concepts) {
			Choice choice = new Choice();
			choice.setConcept(concept);
			choices.add(choice);
		}
		rdfFinal.setChoiceList(choices);
		return rdfFinal;
	}

	private void dereferenceWebResource(RDF rdf, Datastore datastore,
			WebResourceType webResource) throws MalformedURLException,
			IOException, SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		derefResourceOrLiteralList(rdf, datastore,
				webResource.getConformsToList());
		derefResourceOrLiteralList(rdf, datastore, webResource.getCreatedList());
		derefResourceOrLiteralList(rdf, datastore,
				webResource.getDescriptionList());
		derefResourceOrLiteralList(rdf, datastore, webResource.getExtentList());
		derefResourceOrLiteralList(rdf, datastore, webResource.getFormatList());
		derefResourceOrLiteralList(rdf, datastore, webResource.getHasPartList());
		derefResourceOrLiteralList(rdf, datastore,
				webResource.getIsFormatOfList());
		derefResourceOrLiteralList(rdf, datastore, webResource.getIssuedList());
		derefResourceOrLiteralList(rdf, datastore, webResource.getRightList());
		derefResourceOrLiteralList(rdf, datastore, webResource.getSourceList());
		derefResourceOrLiteral(rdf, datastore,
				webResource.getIsNextInSequence());
		derefResourceOrLiteral(rdf, datastore, webResource.getRights());
		derefResourceOrLiteral(rdf, datastore, webResource.getAbout());
	}

	private void dereferenceTimespan(RDF rdf, Datastore datastore,
			TimeSpanType timeSpan) throws MalformedURLException, IOException,
			SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		derefResourceOrLiteralList(rdf, datastore, timeSpan.getAltLabelList());
		derefResourceOrLiteralList(rdf, datastore, timeSpan.getPrefLabelList());
		derefResourceOrLiteralList(rdf, datastore, timeSpan.getHasPartList());
		derefResourceOrLiteralList(rdf, datastore, timeSpan.getIsPartOfList());
		derefResourceOrLiteralList(rdf, datastore, timeSpan.getNoteList());
		derefResourceOrLiteralList(rdf, datastore, timeSpan.getSameAList());
		derefResourceOrLiteral(rdf, datastore, timeSpan.getAbout());
		derefResourceOrLiteral(rdf, datastore, timeSpan.getBegin());
		derefResourceOrLiteral(rdf, datastore, timeSpan.getEnd());
	}

	private void dereferenceProxy(RDF rdf, Datastore datastore, ProxyType proxy)
			throws MalformedURLException, IOException, SecurityException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		derefResourceOrLiteralList(rdf, datastore, proxy.getHasMetList());
		derefResourceOrLiteralList(rdf, datastore, proxy.getHasTypeList());
		derefResourceOrLiteralList(rdf, datastore, proxy.getIncorporateList());
		derefResourceOrLiteralList(rdf, datastore,
				proxy.getIsDerivativeOfList());
		derefResourceOrLiteralList(rdf, datastore, proxy.getIsRelatedToList());
		derefResourceOrLiteralList(rdf, datastore, proxy.getIsSimilarToList());
		derefResourceOrLiteralList(rdf, datastore, proxy.getIsSuccessorOfList());
		derefResourceOrLiteralList(rdf, datastore, proxy.getProxyInList());
		derefResourceOrLiteralList(rdf, datastore, proxy.getRealizeList());
		derefResourceOrLiteralList(rdf, datastore, proxy.getUserTagList());
		derefResourceOrLiteralList(rdf, datastore, proxy.getYearList());
		derefResourceOrLiteral(rdf, datastore, proxy.getAbout());
		derefResourceOrLiteral(rdf, datastore, proxy.getCurrentLocation());
		derefResourceOrLiteral(rdf, datastore, proxy.getProxyFor());
		derefResourceOrLiteral(rdf, datastore, proxy.getType());
		List<eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice> choices = proxy
				.getChoiceList();
		if (choices != null) {
			for (eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice choice : choices) {
				if (choice.ifAlternative())
					derefResourceOrLiteral(rdf, datastore,
							choice.getAlternative());
				if (choice.ifConformsTo())
					derefResourceOrLiteral(rdf, datastore,
							choice.getConformsTo());
				if (choice.ifContributor())
					derefResourceOrLiteral(rdf, datastore,
							choice.getContributor());
				if (choice.ifCoverage())
					derefResourceOrLiteral(rdf, datastore, choice.getCoverage());
				if (choice.ifCreated())
					derefResourceOrLiteral(rdf, datastore, choice.getCreated());
				if (choice.ifCreator())
					derefResourceOrLiteral(rdf, datastore, choice.getCreator());
				if (choice.ifDate())
					derefResourceOrLiteral(rdf, datastore, choice.getDate());
				if (choice.ifDescription())
					derefResourceOrLiteral(rdf, datastore,
							choice.getDescription());
				if (choice.ifExtent())
					derefResourceOrLiteral(rdf, datastore, choice.getExtent());
				if (choice.ifFormat())
					derefResourceOrLiteral(rdf, datastore, choice.getFormat());
				if (choice.ifHasFormat())
					derefResourceOrLiteral(rdf, datastore,
							choice.getHasFormat());
				if (choice.ifHasPart())
					derefResourceOrLiteral(rdf, datastore, choice.getHasPart());
				if (choice.ifHasVersion())
					derefResourceOrLiteral(rdf, datastore,
							choice.getHasVersion());
				if (choice.ifIdentifier())
					derefResourceOrLiteral(rdf, datastore,
							choice.getIdentifier());
				if (choice.ifIsFormatOf())
					derefResourceOrLiteral(rdf, datastore,
							choice.getIsFormatOf());
				if (choice.ifIsPartOf())
					derefResourceOrLiteral(rdf, datastore, choice.getIsPartOf());
				if (choice.ifIsReferencedBy())
					derefResourceOrLiteral(rdf, datastore,
							choice.getIsReferencedBy());
				if (choice.ifIsReplacedBy())
					derefResourceOrLiteral(rdf, datastore,
							choice.getIsReplacedBy());
				if (choice.ifIsRequiredBy())
					derefResourceOrLiteral(rdf, datastore,
							choice.getIsRequiredBy());
				if (choice.ifIssued())
					derefResourceOrLiteral(rdf, datastore, choice.getIssued());
				if (choice.ifIsVersionOf())
					derefResourceOrLiteral(rdf, datastore,
							choice.getIsVersionOf());
				if (choice.ifLanguage())
					derefResourceOrLiteral(rdf, datastore, choice.getLanguage());
				if (choice.ifMedium())
					derefResourceOrLiteral(rdf, datastore, choice.getMedium());
				if (choice.ifProvenance())
					derefResourceOrLiteral(rdf, datastore,
							choice.getProvenance());
				if (choice.ifPublisher())
					derefResourceOrLiteral(rdf, datastore,
							choice.getPublisher());
				if (choice.ifReferences())
					derefResourceOrLiteral(rdf, datastore,
							choice.getReferences());
				if (choice.ifRelation())
					derefResourceOrLiteral(rdf, datastore, choice.getRelation());
				if (choice.ifReplaces())
					derefResourceOrLiteral(rdf, datastore, choice.getReplaces());
				if (choice.ifRights())
					derefResourceOrLiteral(rdf, datastore, choice.getRights());
				if (choice.ifSource())
					derefResourceOrLiteral(rdf, datastore, choice.getSource());
				if (choice.ifSpatial())
					derefResourceOrLiteral(rdf, datastore, choice.getSpatial());
				if (choice.ifSubject())
					derefResourceOrLiteral(rdf, datastore, choice.getSubject());
				if (choice.ifTableOfContents())
					derefResourceOrLiteral(rdf, datastore,
							choice.getTableOfContents());
				if (choice.ifTemporal())
					derefResourceOrLiteral(rdf, datastore, choice.getTemporal());
				if (choice.ifTitle())
					derefResourceOrLiteral(rdf, datastore, choice.getTitle());
				if (choice.ifType())
					derefResourceOrLiteral(rdf, datastore, choice.getType());
			}
		}
	}

	private void dereferenceProvidedCHO(RDF rdf, Datastore datastore,
			ProvidedCHOType providedCHO) throws MalformedURLException,
			IOException, SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		derefResourceOrLiteral(rdf, datastore, providedCHO.getAbout());
		derefResourceOrLiteralList(rdf, datastore, providedCHO.getSameAList());
	}

	private void dereferencePlace(RDF rdf, Datastore datastore, PlaceType place)
			throws MalformedURLException, IOException, SecurityException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		derefResourceOrLiteral(rdf, datastore, place.getAbout());
		derefResourceOrLiteral(rdf, datastore, place.getAlt());
		derefResourceOrLiteral(rdf, datastore, place.getLat());
		derefResourceOrLiteral(rdf, datastore, place.getLong());
		derefResourceOrLiteralList(rdf, datastore, place.getAltLabelList());
		derefResourceOrLiteralList(rdf, datastore, place.getPrefLabelList());
		derefResourceOrLiteralList(rdf, datastore, place.getIsPartOfList());
		derefResourceOrLiteralList(rdf, datastore, place.getNoteList());
		derefResourceOrLiteralList(rdf, datastore, place.getSameAList());
	}

	private void dereferenceConcept(RDF rdf, Datastore datastore,
			Concept concept) throws MalformedURLException, IOException,
			SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		derefResourceOrLiteral(rdf, datastore, concept.getAbout());
		for (eu.europeana.corelib.definitions.jibx.Concept.Choice choice : concept
				.getChoiceList()) {
			if (choice.ifAltLabel())
				derefResourceOrLiteral(rdf, datastore, choice.getAltLabel());
			if (choice.ifPrefLabel())
				derefResourceOrLiteral(rdf, datastore, choice.getPrefLabel());
			if (choice.ifBroader())
				derefResourceOrLiteral(rdf, datastore, choice.getBroader());
			if (choice.ifBroadMatch())
				derefResourceOrLiteral(rdf, datastore, choice.getBroadMatch());
			if (choice.ifCloseMatch())
				derefResourceOrLiteral(rdf, datastore, choice.getCloseMatch());
			if (choice.ifExactMatch())
				derefResourceOrLiteral(rdf, datastore, choice.getExactMatch());
			if (choice.ifNarrower())
				derefResourceOrLiteral(rdf, datastore, choice.getNarrower());
			if (choice.ifNarrowMatch())
				derefResourceOrLiteral(rdf, datastore, choice.getNarrowMatch());
			if (choice.ifNote())
				derefResourceOrLiteral(rdf, datastore, choice.getNote());
			if (choice.ifNotation())
				derefResourceOrLiteral(rdf, datastore, choice.getNotation());
			if (choice.ifRelated())
				derefResourceOrLiteral(rdf, datastore, choice.getRelated());
			if (choice.ifRelatedMatch())
				derefResourceOrLiteral(rdf, datastore, choice.getRelatedMatch());
		}
	}

	private void dereferenceEuropeanaAggregation(RDF rdf, Datastore datastore,
			EuropeanaAggregationType aggregation) throws MalformedURLException,
			IOException, SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		derefResourceOrLiteral(rdf, datastore, aggregation.getAbout());
		derefResourceOrLiteral(rdf, datastore, aggregation.getAggregatedCHO());
		derefResourceOrLiteral(rdf, datastore, aggregation.getCountry());
		derefResourceOrLiteralList(rdf, datastore, aggregation.getHasViewList());
		derefResourceOrLiteral(rdf, datastore, aggregation.getCreator());
		derefResourceOrLiteral(rdf, datastore, aggregation.getIsShownBy());
		derefResourceOrLiteral(rdf, datastore, aggregation.getLandingPage());
		derefResourceOrLiteral(rdf, datastore, aggregation.getLanguage());
		derefResourceOrLiteral(rdf, datastore, aggregation.getRights());
		derefResourceOrLiteralList(rdf, datastore,
				aggregation.getAggregateList());
	}

	private void dereferenceAggregation(RDF rdf, Datastore datastore,
			Aggregation aggregation) throws MalformedURLException, IOException,
			SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		derefResourceOrLiteral(rdf, datastore, aggregation.getAbout());
		derefResourceOrLiteral(rdf, datastore, aggregation.getAggregatedCHO());
		derefResourceOrLiteral(rdf, datastore, aggregation.getDataProvider());
		derefResourceOrLiteralList(rdf, datastore, aggregation.getHasViewList());
		derefResourceOrLiteral(rdf, datastore, aggregation.getIsShownAt());
		derefResourceOrLiteral(rdf, datastore, aggregation.getIsShownBy());
		derefResourceOrLiteral(rdf, datastore, aggregation.getObject());
		derefResourceOrLiteral(rdf, datastore, aggregation.getProvider());
		derefResourceOrLiteral(rdf, datastore, aggregation.getRights());
		derefResourceOrLiteral(rdf, datastore, aggregation.getUgc());
		derefResourceOrLiteralList(rdf, datastore, aggregation.getRightList());
	}

	private void dereferenceAgent(RDF rdf, Datastore datastore, AgentType agent)
			throws MalformedURLException, IOException, SecurityException,
			IllegalArgumentException, InstantiationException,
			IllegalAccessException, NoSuchMethodException,
			InvocationTargetException {
		derefResourceOrLiteral(rdf, datastore, agent.getAbout());
		derefResourceOrLiteralList(rdf, datastore, agent.getAltLabelList());
		derefResourceOrLiteralList(rdf, datastore, agent.getDateList());
		derefResourceOrLiteralList(rdf, datastore, agent.getHasMetList());
		derefResourceOrLiteralList(rdf, datastore, agent.getIdentifierList());
		derefResourceOrLiteralList(rdf, datastore, agent.getIsRelatedToList());
		derefResourceOrLiteralList(rdf, datastore, agent.getNameList());
		derefResourceOrLiteralList(rdf, datastore, agent.getNoteList());
		derefResourceOrLiteralList(rdf, datastore, agent.getPrefLabelList());
		derefResourceOrLiteralList(rdf, datastore, agent.getSameAList());
		derefResourceOrLiteral(rdf, datastore, agent.getBegin());
		derefResourceOrLiteral(rdf, datastore, agent.getEnd());
		derefResourceOrLiteral(rdf, datastore,
				agent.getBiographicalInformation());
		derefResourceOrLiteral(rdf, datastore, agent.getDateOfBirth());
		derefResourceOrLiteral(rdf, datastore, agent.getDateOfDeath());
		derefResourceOrLiteral(rdf, datastore, agent.getDateOfEstablishment());
		derefResourceOrLiteral(rdf, datastore, agent.getDateOfTermination());
		derefResourceOrLiteral(rdf, datastore, agent.getGender());
	}

	private void derefResourceOrLiteralList(RDF rdf, Datastore datastore,
			List<?> list) throws MalformedURLException, IOException,
			SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		if (list != null) {
			for (Object object : list) {
				derefResourceOrLiteral(rdf, datastore, object);
			}
		}
	}

	private void derefResourceOrLiteral(RDF rdf, Datastore datastore,
			Object object) throws MalformedURLException, IOException,
			SecurityException, IllegalArgumentException,
			InstantiationException, IllegalAccessException,
			NoSuchMethodException, InvocationTargetException {
		OsgiExtractor extractor = new OsgiExtractor();
		extractor.setDatastore(datastore);
		if (object instanceof String) {
			if (isURI((String) object)) {
				ControlledVocabularyImpl controlVocabulary = getControlledVocabulary(
						datastore, "URI", (String) object);

				appendInRDF(rdf, extractor.denormalize((String) object,
						controlVocabulary));
			}
		} else if (object instanceof ResourceType) {

			if (((ResourceType) object).getResource() != null) {

				if (isURI(((ResourceType) object).getResource())) {
					ControlledVocabularyImpl controlVocabulary = getControlledVocabulary(
							datastore, "URI",
							((ResourceType) object).getResource());
					appendInRDF(rdf, extractor.denormalize(
							((ResourceType) object).getResource(),
							controlVocabulary));
				}
			}
		} else if (object instanceof ResourceOrLiteralType) {
			if (((ResourceOrLiteralType) object).getResource() != null) {

				if (isURI(((ResourceOrLiteralType) object).getResource())) {
					ControlledVocabularyImpl controlVocabulary = getControlledVocabulary(
							datastore, "URI",
							((ResourceOrLiteralType) object).getResource());
					appendInRDF(rdf, extractor.denormalize(
							((ResourceOrLiteralType) object).getResource(),
							controlVocabulary));
				}
			}
			if (((ResourceOrLiteralType) object).getString() != null) {

				if (isURI(((ResourceOrLiteralType) object).getString())) {
					ControlledVocabularyImpl controlVocabulary = getControlledVocabulary(
							datastore, "URI",
							((ResourceOrLiteralType) object).getString());
					appendInRDF(rdf, extractor.denormalize(
							((ResourceOrLiteralType) object).getString(),
							controlVocabulary));
				}
			}
		} else if (object instanceof LiteralType) {
			if (((LiteralType) object).getString() != null) {

				if (isURI(((LiteralType) object).getString())) {

					ControlledVocabularyImpl controlVocabulary = getControlledVocabulary(
							datastore, "URI",
							((LiteralType) object).getString());
					appendInRDF(rdf, extractor.denormalize(
							((LiteralType) object).getString(),
							controlVocabulary));
				}
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void appendInRDF(RDF rdf, Map<String, List> denormalize) {
		for (Entry<String, List> entry : denormalize.entrySet()) {
			if (StringUtils.equals(entry.getKey(), "concepts")) {
				for (Concept concept : (List<Concept>) entry.getValue()) {
					Choice choice = new Choice();
					choice.setConcept(concept);
					rdf.getChoiceList().add(choice);
				}
			}
			if (StringUtils.equals(entry.getKey(), "agents")) {
				for (AgentType agent : (List<AgentType>) entry.getValue()) {
					Choice choice = new Choice();
					choice.setAgent(agent);
					rdf.getChoiceList().add(choice);
				}
			}
			if (StringUtils.equals(entry.getKey(), "timespans")) {
				for (TimeSpanType timespan : (List<TimeSpanType>) entry
						.getValue()) {
					Choice choice = new Choice();
					choice.setTimeSpan(timespan);
					rdf.getChoiceList().add(choice);
				}
			}
			if (StringUtils.equals(entry.getKey(), "places")) {
				for (PlaceType place : (List<PlaceType>) entry.getValue()) {
					Choice choice = new Choice();
					choice.setPlace(place);
					rdf.getChoiceList().add(choice);
				}
			}
		}

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
