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
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;
import org.xml.sax.SAXException;

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
import eu.europeana.corelib.dereference.impl.Dereferencer;
import eu.europeana.corelib.dereference.impl.VocabularyMongoServer;
import eu.europeana.corelib.solr.utils.SolrConstructor;
import eu.europeana.corelib.tools.rdf.Solr2Rdf;
import eu.europeana.uim.api.AbstractIngestionPlugin;
import eu.europeana.uim.api.CorruptedMetadataRecordException;
import eu.europeana.uim.api.ExecutionContext;
import eu.europeana.uim.api.IngestionPluginFailedException;
import eu.europeana.uim.common.TKey;
import eu.europeana.uim.model.europeana.EuropeanaModelRegistry;
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
			IBindingFactory bfact = BindingDirectory.getFactory(RDF.class);
			IUnmarshallingContext uctx = bfact.createUnmarshallingContext();
			String value = mdr.getValues(EuropeanaModelRegistry.EDMRECORD).get(
					0);
			RDF rdf = (RDF) uctx.unmarshalDocument(new StringReader(value));
			
			Map<String, List<String>> dereferencedValues = new HashMap<String,List<String>>();
			List<Choice> choices =	rdf.getChoiceList();
			for(Choice choice : choices){
				if(choice.ifAgent()){
					dereferencedValues.putAll(dereferenceAgent(choice.getAgent()));
				}
				else if(choice.ifAggregation()){
					dereferencedValues.putAll(dereferenceAggregation(choice.getAggregation()));
				}
				else if(choice.ifConcept()){
					dereferencedValues.putAll(dereferenceConcept(choice.getConcept()));
				}
				else if(choice.ifEuropeanaAggregation()){
					dereferencedValues.putAll(dereferenceEuropeanaAggregation(choice.getEuropeanaAggregation()));
				}
				else if(choice.ifPlace()){
					dereferencedValues.putAll(dereferencePlace(choice.getPlace()));
				}
				else if(choice.ifProvidedCHO()){
					dereferencedValues.putAll(dereferenceProvidedCHO(choice.getProvidedCHO()));
				}
				else if(choice.ifProxy()){
					dereferencedValues.putAll(dereferenceProxy(choice.getProxy()));
				}
				else if(choice.ifTimeSpan()){
					dereferencedValues.putAll(dereferenceTimespan(choice.getTimeSpan()));
				}
				else if(choice.ifWebResource()){
					dereferencedValues.putAll(dereferenceWebResource(choice.getWebResource()));
				}
			}
			
			Solr2Rdf solr2Rdf = new Solr2Rdf();
			solr2Rdf.initialize();
			RDF der = solr2Rdf.constructFromMap(SolrConstructor.constructSolrDocument(rdf), dereferencedValues);
			IMarshallingContext marshallingContext = bfact.createMarshallingContext();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			 marshallingContext.marshalDocument(der, "UTF-8", null, out);
			context.putValue(EuropeanaModelRegistry.EDMDEREFERENCEDRECORD, out.toString());
			
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

	private Map<String, ? extends List<String>> dereferenceWebResource(
			WebResourceType webResource) throws MalformedURLException, IOException {
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		retVal.putAll(derefResourceOrLiteralList(webResource.getConformsToList()));
		retVal.putAll(derefResourceOrLiteralList(webResource.getCreatedList()));
		retVal.putAll(derefResourceOrLiteralList(webResource.getDescriptionList()));
		retVal.putAll(derefResourceOrLiteralList(webResource.getExtentList()));
		retVal.putAll(derefResourceOrLiteralList(webResource.getFormatList()));
		retVal.putAll(derefResourceOrLiteralList(webResource.getHasPartList()));
		retVal.putAll(derefResourceOrLiteralList(webResource.getIsFormatOfList()));
		retVal.putAll(derefResourceOrLiteralList(webResource.getIssuedList()));
		retVal.putAll(derefResourceOrLiteralList(webResource.getRightList()));
		retVal.putAll(derefResourceOrLiteralList(webResource.getSourceList()));
		retVal.putAll(derefResourceOrLiteral(webResource.getIsNextInSequence()));
		retVal.putAll(derefResourceOrLiteral(webResource.getRights()));
		retVal.putAll(derefResourceOrLiteral(webResource.getAbout()));
		return retVal;
	}

	private Map<String, ? extends List<String>> dereferenceTimespan(
			TimeSpanType timeSpan) throws MalformedURLException, IOException {
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		retVal.putAll(derefResourceOrLiteralList(timeSpan.getAltLabelList()));
		retVal.putAll(derefResourceOrLiteralList(timeSpan.getPrefLabelList()));
		retVal.putAll(derefResourceOrLiteralList(timeSpan.getHasPartList()));
		retVal.putAll(derefResourceOrLiteralList(timeSpan.getIsPartOfList()));
		retVal.putAll(derefResourceOrLiteralList(timeSpan.getNoteList()));
		retVal.putAll(derefResourceOrLiteralList(timeSpan.getSameAList()));
		retVal.putAll(derefResourceOrLiteral(timeSpan.getAbout()));
		retVal.putAll(derefResourceOrLiteral(timeSpan.getBegin()));
		retVal.putAll(derefResourceOrLiteral(timeSpan.getEnd()));
		return retVal;
	}

	private Map<String, ? extends List<String>> dereferenceProxy(
			ProxyType proxy) throws MalformedURLException, IOException {
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		retVal.putAll(derefResourceOrLiteralList(proxy.getHasMetList()));
		retVal.putAll(derefResourceOrLiteralList(proxy.getHasTypeList()));
		retVal.putAll(derefResourceOrLiteralList(proxy.getIncorporateList()));
		retVal.putAll(derefResourceOrLiteralList(proxy.getIsDerivativeOfList()));
		retVal.putAll(derefResourceOrLiteralList(proxy.getIsRelatedToList()));
		retVal.putAll(derefResourceOrLiteralList(proxy.getIsSimilarToList()));
		retVal.putAll(derefResourceOrLiteralList(proxy.getIsSuccessorOfList()));
		retVal.putAll(derefResourceOrLiteralList(proxy.getProxyInList()));
		retVal.putAll(derefResourceOrLiteralList(proxy.getRealizeList()));
		retVal.putAll(derefResourceOrLiteralList(proxy.getUserTagList()));
		retVal.putAll(derefResourceOrLiteralList(proxy.getYearList()));
		retVal.putAll(derefResourceOrLiteral(proxy.getAbout()));
		retVal.putAll(derefResourceOrLiteral(proxy.getCurrentLocation()));
		retVal.putAll(derefResourceOrLiteral(proxy.getProxyFor()));
		retVal.putAll(derefResourceOrLiteral(proxy.getType()));
		List<eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice> choices = proxy.getChoiceList();
		for(eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice choice:choices){
			retVal.putAll(derefResourceOrLiteral(choice.getAlternative()));
			retVal.putAll(derefResourceOrLiteral(choice.getConformsTo()));
			retVal.putAll(derefResourceOrLiteral(choice.getContributor()));
			retVal.putAll(derefResourceOrLiteral(choice.getCoverage()));
			retVal.putAll(derefResourceOrLiteral(choice.getCreated()));
			retVal.putAll(derefResourceOrLiteral(choice.getCreator()));
			retVal.putAll(derefResourceOrLiteral(choice.getDate()));
			retVal.putAll(derefResourceOrLiteral(choice.getDescription()));
			retVal.putAll(derefResourceOrLiteral(choice.getExtent()));
			retVal.putAll(derefResourceOrLiteral(choice.getFormat()));
			retVal.putAll(derefResourceOrLiteral(choice.getHasFormat()));
			retVal.putAll(derefResourceOrLiteral(choice.getHasPart()));
			retVal.putAll(derefResourceOrLiteral(choice.getHasVersion()));
			retVal.putAll(derefResourceOrLiteral(choice.getIdentifier()));
			retVal.putAll(derefResourceOrLiteral(choice.getIsFormatOf()));
			retVal.putAll(derefResourceOrLiteral(choice.getIsPartOf()));
			retVal.putAll(derefResourceOrLiteral(choice.getIsReferencedBy()));
			retVal.putAll(derefResourceOrLiteral(choice.getIsReplacedBy()));
			retVal.putAll(derefResourceOrLiteral(choice.getIsRequiredBy()));
			retVal.putAll(derefResourceOrLiteral(choice.getIssued()));
			retVal.putAll(derefResourceOrLiteral(choice.getIsVersionOf()));
			retVal.putAll(derefResourceOrLiteral(choice.getLanguage()));
			retVal.putAll(derefResourceOrLiteral(choice.getMedium()));
			retVal.putAll(derefResourceOrLiteral(choice.getProvenance()));
			retVal.putAll(derefResourceOrLiteral(choice.getPublisher()));
			retVal.putAll(derefResourceOrLiteral(choice.getReferences()));
			retVal.putAll(derefResourceOrLiteral(choice.getRelation()));
			retVal.putAll(derefResourceOrLiteral(choice.getReplaces()));
			retVal.putAll(derefResourceOrLiteral(choice.getRights()));
			retVal.putAll(derefResourceOrLiteral(choice.getSource()));
			retVal.putAll(derefResourceOrLiteral(choice.getSpatial()));
			retVal.putAll(derefResourceOrLiteral(choice.getSubject()));
			retVal.putAll(derefResourceOrLiteral(choice.getTableOfContents()));
			retVal.putAll(derefResourceOrLiteral(choice.getTemporal()));
			retVal.putAll(derefResourceOrLiteral(choice.getTitle()));
			retVal.putAll(derefResourceOrLiteral(choice.getType()));
		}
		return retVal;
	}

	private Map<String, ? extends List<String>> dereferenceProvidedCHO(
			ProvidedCHOType providedCHO) throws MalformedURLException, IOException {
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		retVal.putAll(derefResourceOrLiteral(providedCHO.getAbout()));
		retVal.putAll(derefResourceOrLiteralList(providedCHO.getSameAList()));
		return retVal;
	}

	private Map<String, ? extends List<String>> dereferencePlace(
			PlaceType place) throws MalformedURLException, IOException {
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		retVal.putAll(derefResourceOrLiteral(place.getAbout()));
		retVal.putAll(derefResourceOrLiteral(place.getAlt()));
		retVal.putAll(derefResourceOrLiteral(place.getLat()));
		retVal.putAll(derefResourceOrLiteral(place.getLong()));
		retVal.putAll(derefResourceOrLiteralList(place.getAltLabelList()));
		retVal.putAll(derefResourceOrLiteralList(place.getPrefLabelList()));
		retVal.putAll(derefResourceOrLiteralList(place.getIsPartOfList()));
		retVal.putAll(derefResourceOrLiteralList(place.getNoteList()));
		retVal.putAll(derefResourceOrLiteralList(place.getSameAList()));
		return retVal;
	}

	private Map<String, ? extends List<String>> dereferenceConcept(
			Concept concept) throws MalformedURLException, IOException {
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		retVal.putAll(derefResourceOrLiteral(concept.getAbout()));
		for(eu.europeana.corelib.definitions.jibx.Concept.Choice choice : concept.getChoiceList()){
				
		retVal.putAll(derefResourceOrLiteral(choice.getAltLabel()));
		retVal.putAll(derefResourceOrLiteral(choice.getPrefLabel()));
		retVal.putAll(derefResourceOrLiteral(choice.getBroader()));
		retVal.putAll(derefResourceOrLiteral(choice.getBroadMatch()));
		retVal.putAll(derefResourceOrLiteral(choice.getCloseMatch()));
		retVal.putAll(derefResourceOrLiteral(choice.getExactMatch()));
		retVal.putAll(derefResourceOrLiteral(choice.getNarrower()));
		retVal.putAll(derefResourceOrLiteral(choice.getNarrowMatch()));
		retVal.putAll(derefResourceOrLiteral(choice.getNote()));
		retVal.putAll(derefResourceOrLiteral(choice.getNotation()));
		retVal.putAll(derefResourceOrLiteral(choice.getRelated()));
		retVal.putAll(derefResourceOrLiteral(choice.getRelatedMatch()));
		}
		return retVal;
	}

	private Map<String, ? extends List<String>> dereferenceEuropeanaAggregation(
			EuropeanaAggregationType aggregation) throws MalformedURLException, IOException {
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		retVal.putAll(derefResourceOrLiteral(aggregation.getAbout()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getAggregatedCHO()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getCountry()));
		retVal.putAll(derefResourceOrLiteralList(aggregation.getHasViewList()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getCreator()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getIsShownBy()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getLandingPage()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getLanguage()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getRights()));
		retVal.putAll(derefResourceOrLiteralList(aggregation.getAggregateList()));
		return retVal;
	}

	private Map<String, ? extends List<String>> dereferenceAggregation(
			Aggregation aggregation) throws MalformedURLException, IOException {
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		retVal.putAll(derefResourceOrLiteral(aggregation.getAbout()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getAggregatedCHO()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getDataProvider()));
		retVal.putAll(derefResourceOrLiteralList(aggregation.getHasViewList()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getIsShownAt()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getIsShownBy()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getObject()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getProvider()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getRights()));
		retVal.putAll(derefResourceOrLiteral(aggregation.getUgc()));
		retVal.putAll(derefResourceOrLiteralList(aggregation.getRightList()));
		return retVal;
	}

	private Map<String, ? extends List<String>> dereferenceAgent(
			AgentType agent) throws MalformedURLException, IOException {
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		retVal.putAll(derefResourceOrLiteral(agent.getAbout()));
		retVal.putAll(derefResourceOrLiteralList(agent.getAltLabelList()));
		retVal.putAll(derefResourceOrLiteralList(agent.getDateList()));
		retVal.putAll(derefResourceOrLiteralList(agent.getHasMetList()));
		retVal.putAll(derefResourceOrLiteralList(agent.getIdentifierList()));
		retVal.putAll(derefResourceOrLiteralList(agent.getIsRelatedToList()));
		retVal.putAll(derefResourceOrLiteralList(agent.getNameList()));
		retVal.putAll(derefResourceOrLiteralList(agent.getNoteList()));
		retVal.putAll(derefResourceOrLiteralList(agent.getPrefLabelList()));
		retVal.putAll(derefResourceOrLiteralList(agent.getSameAList()));
		retVal.putAll(derefResourceOrLiteral(agent.getBegin()));
		retVal.putAll(derefResourceOrLiteral(agent.getEnd()));
		retVal.putAll(derefResourceOrLiteral(agent.getBiographicalInformation()));
		retVal.putAll(derefResourceOrLiteral(agent.getDateOfBirth()));
		retVal.putAll(derefResourceOrLiteral(agent.getDateOfDeath()));
		retVal.putAll(derefResourceOrLiteral(agent.getDateOfEstablishment()));
		retVal.putAll(derefResourceOrLiteral(agent.getDateOfTermination()));
		retVal.putAll(derefResourceOrLiteral(agent.getGender()));
		return retVal;
	}
	
	private Map<String,List<String>> derefResourceOrLiteralList(List<?>list) throws MalformedURLException, IOException{
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		if(list!=null){
		for(Object object:list){
			retVal.putAll(derefResourceOrLiteral(object));
			}
		}
		return retVal;
	}
	
	private Map<String, ? extends List<String>> derefResourceOrLiteral(
			Object object) throws MalformedURLException, IOException {
		Map<String,List<String>> retVal = new HashMap<String, List<String>>();
		if (object instanceof ResourceType){
			if(((ResourceType)object).getResource()!=null){
				retVal.putAll(Dereferencer.normalize(((ResourceType)object).getResource()));
			}
		}
		else if(object instanceof ResourceOrLiteralType){
			if(((ResourceOrLiteralType)object).getResource()!=null){
				retVal.putAll(Dereferencer.normalize(((ResourceOrLiteralType)object).getResource()));
			}
			if(((ResourceOrLiteralType)object).getString()!=null){
				retVal.putAll(Dereferencer.normalize(((ResourceOrLiteralType)object).getString()));
			}
		}
		
		else if(object instanceof LiteralType){
			if(((LiteralType)object).getString()!=null){
				retVal.putAll(Dereferencer.normalize(((LiteralType)object).getString()));
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
		return 1;
	}

	public int getMaximumThreadCount() {
		return 1;
	}

	public <I> void initialize(ExecutionContext<I> context)
			throws IngestionPluginFailedException {
		try {
			Dereferencer.setServer(new VocabularyMongoServer(new Mongo("localhost",27017), "vocabulary"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MongoException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

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
}
