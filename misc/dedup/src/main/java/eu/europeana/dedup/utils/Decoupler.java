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
package eu.europeana.dedup.utils;

import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Vector;
import java.util.List;

import org.jibx.runtime.BindingDirectory;
import org.jibx.runtime.IBindingFactory;
import org.jibx.runtime.IMarshallingContext;
import org.jibx.runtime.IUnmarshallingContext;
import org.jibx.runtime.JiBXException;

import eu.europeana.corelib.definitions.jibx.AgentType;
import eu.europeana.corelib.definitions.jibx.Aggregation;
import eu.europeana.corelib.definitions.jibx.Concept;
import eu.europeana.corelib.definitions.jibx.EuropeanaAggregationType;
import eu.europeana.corelib.definitions.jibx.HasMet;
import eu.europeana.corelib.definitions.jibx.HasType;
import eu.europeana.corelib.definitions.jibx.HasView;
import eu.europeana.corelib.definitions.jibx.PlaceType;
import eu.europeana.corelib.definitions.jibx.ProvidedCHOType;
import eu.europeana.corelib.definitions.jibx.ProxyIn;
import eu.europeana.corelib.definitions.jibx.ProxyType;
import eu.europeana.corelib.definitions.jibx.RDF;
import eu.europeana.corelib.definitions.jibx.RDF.Choice;
import eu.europeana.corelib.definitions.jibx.TimeSpanType;
import eu.europeana.corelib.definitions.jibx.WebResourceType;
import eu.europeana.corelib.definitions.jibx.Year;
import eu.europeana.dedup.osgi.service.exceptions.DeduplicationException;

/**
 * Helper Class that checks if a received EDM record contains 
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 27 Sep 2012
 */
public class Decoupler {

	private static Decoupler INSTANCE;
	
	
	/**
	 * Private constructor, instantiate via getInstance() method
	 */
	private Decoupler() {
	}

	
	/**
	 * Stat
	 * @return
	 */
	public static Decoupler getInstance(){
		
		if(INSTANCE == null){
			INSTANCE = new Decoupler();
		}
		
		return INSTANCE;
	}
	
	
	/**
	 * @param edmXML
	 * @return
	 * @throws DeduplicationException
	 */
	public List<RDF> decouple(String edmXML) throws DeduplicationException {

		if(edmXML == null){
			throw new DeduplicationException("Parameter null passed as an argument in Decoupler.decouple(RDF edmXML) method");
		}		
		
		IBindingFactory context;

			try {
				
				context = BindingDirectory.getFactory(RDF.class);
				
				IUnmarshallingContext uctx = context.createUnmarshallingContext();
				RDF edmOBJ = (RDF) uctx.unmarshalDocument(new StringReader(edmXML));
			
			
			InfoStub stub = new InfoStub(edmOBJ);
			stub.init();
				
			if(stub.proxyList.size() == 1){
				ArrayList<RDF> list = new ArrayList<RDF>();
				list.add(edmOBJ);
				return list;
			}
			else{
				return process(stub);
			}
			
			} catch (JiBXException e) {
				
				throw new DeduplicationException(e);
			}

	}
		
	
	
	
	/**
	 * @param stub
	 * @return
	 */
	private List<RDF> process(InfoStub stub){
		Vector<RDF> agentList = new Vector<RDF>();
		
		
		for (ProxyType proxy : stub.proxyList) {

			RDF cleandoc = new RDF();
			Choice proxyChoice = new Choice(); 
			proxyChoice.setProxy(proxy);
			cleandoc.getChoiceList().add(proxyChoice);
			populatePrCHOs(proxy,stub,cleandoc);
			List<Aggregation> aggregations = appendAggregations(proxy,stub,cleandoc);
			appendWebResources(aggregations,stub,cleandoc);
			appendContextualEntities(proxy,stub, cleandoc);
			
			agentList.add(cleandoc);

		}
		
		return agentList;
		
	}
	
		
	/**
	 * @param proxy
	 * @param stub
	 * @return
	 */
	private List<Aggregation> appendAggregations(ProxyType proxy,InfoStub stub,RDF cleandoc){
		// Get the Aggregator References
		List<Aggregation> foundaggregationlist = new ArrayList<Aggregation>();
				
			Vector<Aggregation> aglist = stub.aggregationList;
			
			for(Aggregation agg : aglist){
				if (agg.getAggregatedCHO().getResource().equals(proxy.getAbout()) ){
					foundaggregationlist.add(agg);
					Choice aggregationChoice = new Choice();
					aggregationChoice.setAggregation(agg);
					cleandoc.getChoiceList().add(aggregationChoice);
					
					if(stub.orphanEntities.contains(agg)){
						stub.orphanEntities.remove(agg);
					}
					else{
						stub.orphanEntities.add(agg);
					}
				}
			}
			
		return foundaggregationlist;
	}
	
	
	/**
	 * @param aggregations
	 * @param stub
	 */
	private void appendWebResources(List<Aggregation> aggregations,InfoStub stub,RDF cleandoc){
		HashSet<String> refstring = new HashSet<String>();
		
		for(Aggregation agg : aggregations ){
			
			//First try to isolate any possible resource references to WebResources
			if(agg.getObject() != null && agg.getObject().getResource() != null){
				String resource = agg.getObject().getResource();
				refstring.add(resource);
			}
			if(agg.getIsShownBy() != null && agg.getIsShownBy().getResource() != null){
				String resource = agg.getIsShownBy().getResource();
				if(refstring.contains(resource))
				refstring.add(resource);
			}
			if(agg.getHasViewList() !=null && !agg.getHasViewList().isEmpty()){
				 List<HasView> viewlist = agg.getHasViewList();
				
				 for(HasView view : viewlist){
						if(refstring.contains(view.getResource()))
							refstring.add(view.getResource());
				 }
			}	
		}
		
		
		Vector<WebResourceType> wrlist = stub.webresourceList;
		
		for(WebResourceType wtype : wrlist){
			if(refstring.contains(wtype.getAbout())){
				Choice webresourceChoice = new Choice();
				webresourceChoice.setWebResource(wtype);
				cleandoc.getChoiceList().add(webresourceChoice);
			}
		}

	}
	
	
	
	/**
	 * @param proxy
	 * @param stub
	 * @param cleandoc
	 */
	private void appendContextualEntities(ProxyType proxy,InfoStub stub,RDF cleandoc){
		HashSet<String> refset = new HashSet<String>();
		
		List<eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice> dclist = proxy.getChoiceList();
		
		for(eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice choiceitem : dclist){
			if(choiceitem.ifAlternative()){
				refset.add(returnResourceFromClass(choiceitem.getAlternative()));
			}
			if(choiceitem.ifConformsTo()){
				refset.add(returnResourceFromClass(choiceitem.getConformsTo()));
			}
			if(choiceitem.ifContributor()){
				refset.add(returnResourceFromClass(choiceitem.getContributor()));
			}
			if(choiceitem.ifCoverage()){
				refset.add(returnResourceFromClass(choiceitem.getCoverage()));
			}
			if(choiceitem.ifCreated()){
				refset.add(returnResourceFromClass(choiceitem.getCreated()));
			}
			if(choiceitem.ifCreator()){
				refset.add(returnResourceFromClass(choiceitem.getCreator()));
			}
			if(choiceitem.ifDate()){
				refset.add(returnResourceFromClass(choiceitem.getDate()));
			}
			if(choiceitem.ifDescription()){
				refset.add(returnResourceFromClass(choiceitem.getDescription()));
			}
			if(choiceitem.ifExtent()){
				refset.add(returnResourceFromClass(choiceitem.getExtent()));
			}
			if(choiceitem.ifFormat()){
				refset.add(returnResourceFromClass(choiceitem.getFormat()));
			}
			if(choiceitem.ifHasFormat()){
				refset.add(returnResourceFromClass(choiceitem.getHasFormat()));
			}
			if(choiceitem.ifHasPart()){
				refset.add(returnResourceFromClass(choiceitem.getHasPart()));
			}
			if(choiceitem.ifHasVersion()){
				refset.add(returnResourceFromClass(choiceitem.getHasVersion()));
			}
			if(choiceitem.ifIdentifier()){
				refset.add(returnResourceFromClass(choiceitem.getIdentifier()));
			}
			if(choiceitem.ifIsFormatOf()){
				refset.add(returnResourceFromClass(choiceitem.getIsFormatOf()));
			}
			if(choiceitem.ifIsPartOf()){
				refset.add(returnResourceFromClass(choiceitem.getIsPartOf()));
			}
			if(choiceitem.ifIsReferencedBy()){
				refset.add(returnResourceFromClass(choiceitem.getIsReferencedBy()));
			}
			if(choiceitem.ifIsReplacedBy()){
				refset.add(returnResourceFromClass(choiceitem.getIsReplacedBy()));
			}
			if(choiceitem.ifIssued()){
				refset.add(returnResourceFromClass(choiceitem.getIssued()));
			}
			if(choiceitem.ifIsVersionOf()){
				refset.add(returnResourceFromClass(choiceitem.getIsVersionOf()));
			}
			if(choiceitem.ifLanguage()){
				refset.add(returnResourceFromClass(choiceitem.getLanguage()));
			}
			if(choiceitem.ifMedium()){
				refset.add(returnResourceFromClass(choiceitem.getMedium()));
			}
			if(choiceitem.ifProvenance()){
				refset.add(returnResourceFromClass(choiceitem.getProvenance()));
			}
			if(choiceitem.ifPublisher()){
				refset.add(returnResourceFromClass(choiceitem.getPublisher()));
			}
			if(choiceitem.ifReferences()){
				refset.add(returnResourceFromClass(choiceitem.getReferences()));
			}
			if(choiceitem.ifRelation()){
				refset.add(returnResourceFromClass(choiceitem.getRelation()));
			}
			if(choiceitem.ifReplaces()){
				refset.add(returnResourceFromClass(choiceitem.getReplaces()));
			}
			if(choiceitem.ifRequires()){
				refset.add(returnResourceFromClass(choiceitem.getRequires()));
			}
			if(choiceitem.ifRights()){
				refset.add(returnResourceFromClass(choiceitem.getRights()));
			}
			if(choiceitem.ifSpatial()){
				refset.add(returnResourceFromClass(choiceitem.getSpatial()));
			}
			if(choiceitem.ifSubject()){
				refset.add(returnResourceFromClass(choiceitem.getSubject()));
			}
			if(choiceitem.ifSource()){
				refset.add(returnResourceFromClass(choiceitem.getSource()));
			}
			if(choiceitem.ifTableOfContents()){
				refset.add(returnResourceFromClass(choiceitem.getTableOfContents()));
			}
			if(choiceitem.ifTemporal()){
				refset.add(returnResourceFromClass(choiceitem.getTemporal()));
			}
			if(choiceitem.ifTitle()){
				refset.add(returnResourceFromClass(choiceitem.getTitle()));
			}
			if(choiceitem.ifType()){
				refset.add(returnResourceFromClass(choiceitem.getType()));
			}
		}
		
		 
		refset.add(returnResourceFromClass(proxy.getCurrentLocation()));

		refset.addAll(returnResourceFromList(proxy.getHasTypeList()));
		
		refset.addAll(returnResourceFromList(proxy.getIncorporateList()));
		
		refset.addAll(returnResourceFromList(proxy.getIsDerivativeOfList()));

		refset.add(returnResourceFromClass(proxy.getIsNextInSequence()));
		 
		refset.addAll(returnResourceFromList(proxy.getIsRelatedToList()));
		
		refset.addAll(returnResourceFromList(proxy.getIsRelatedToList()));
		
		refset.add(returnResourceFromClass(proxy.getIsRepresentationOf()));
		
		refset.addAll(returnResourceFromList(proxy.getIsSimilarToList()));

		refset.addAll(returnResourceFromList(proxy.getIsSuccessorOfList()));
		
		refset.addAll(returnResourceFromList(proxy.getRealizeList()));	
		
		
		populateContextualEntities(refset,stub,cleandoc);

	}
	
	
	/**
	 * @param proxy
	 * @param stub
	 * @param cleandoc
	 */
	private void populatePrCHOs(ProxyType proxy,InfoStub stub,RDF cleandoc){
		
		Vector<ProvidedCHOType> cholist = stub.prchoList;
		
		String id = proxy.getAbout();
		for(ProvidedCHOType cho : cholist){
			if(id.equals(cho.getAbout())){
				Choice choice = new Choice();
				choice.setProvidedCHO(cho);
				cleandoc.getChoiceList().add(choice);
			}
		}
		
	}
	
	
	
	/**
	 * @param refset
	 * @param stub
	 * @param cleandoc
	 */
	private void populateContextualEntities(Set<String> refset,InfoStub stub,RDF cleandoc){
		
		Vector<AgentType> agentlist = stub.agentList;
		Vector<PlaceType> placelist = stub.placeList;
		Vector<TimeSpanType> timelist = stub.timeList;
		Vector<Concept> conceptlist = stub.conceptList;
		
		for(AgentType agtype : agentlist){
			if(refset.contains(agtype.getAbout())){
				Choice choice = new Choice();
				choice.setAgent(agtype);
				cleandoc.getChoiceList().add(choice);
			}
		}
		
		for(PlaceType type : placelist){
			if(refset.contains(type.getAbout())){
				Choice choice = new Choice();
				choice.setPlace(type);
				cleandoc.getChoiceList().add(choice);
			}
		}
		
		for(TimeSpanType type : timelist){
			if(refset.contains(type.getAbout())){
				Choice choice = new Choice();
				choice.setTimeSpan(type);
				cleandoc.getChoiceList().add(choice);
			}
		}
		
		for(Concept type : conceptlist){
			if(refset.contains(type.getAbout())){
				Choice choice = new Choice();
				choice.setConcept(type);
				cleandoc.getChoiceList().add(choice);
			}
		}
	}
	
	
	/**
	 * @param list
	 * @return
	 */
	private <T> List<String> returnResourceFromList(List<T> list){
		
		if (list == null) return new ArrayList<String>();
		
		ArrayList<String> returnList = new ArrayList<String>();
		
		for(T object: list){
			String resource = returnResourceFromClass(object);
			if(resource != null){
				returnList.add(resource);
			}
		}
		
		return returnList;
	}
	
	
	/**
	 * @param object
	 * @return
	 */
	private <T> String returnResourceFromClass(T object){
		
		if(object == null) return null;
		
		Method[] methods = object.getClass().getMethods();
		
		for(int i = 0; i <methods.length; i++){
			
			if(methods[i].getName().equals("getResource")){
				
				try {
					String resource = (String) methods[i].invoke(object);
					
					return resource;
				} catch (IllegalArgumentException e) {

				} catch (IllegalAccessException e) {

				} catch (InvocationTargetException e) {

				}
			}
			
		}

		return null;
		
	}

	
	
	/**
	 *
	 * @author Georgios Markakis <gwarkx@hotmail.com>
	 * @since 1 Oct 2012
	 */
	private class InfoStub {

		
		HashSet<Object> orphanEntities = new HashSet<Object>();
		RDF edmXML;
		Vector<ProvidedCHOType> prchoList = new Vector<ProvidedCHOType>();
		Vector<ProxyType> proxyList = new Vector<ProxyType>();
		Vector<Aggregation> aggregationList = new Vector<Aggregation>();
		Vector<EuropeanaAggregationType> euaggregationList = new Vector<EuropeanaAggregationType>();
		Vector<AgentType> agentList = new Vector<AgentType>();
		Vector<Concept> conceptList = new Vector<Concept>();
		Vector<PlaceType> placeList = new Vector<PlaceType>();
		Vector<TimeSpanType> timeList = new Vector<TimeSpanType>();
		Vector<WebResourceType> webresourceList = new Vector<WebResourceType>();

		public InfoStub(RDF edmXML) {
			this.edmXML = edmXML;
		}

		public void init() {
			List<Choice> chlist = edmXML.getChoiceList();

			for (Choice element : chlist) {

				if (element.ifProxy()) {
					ProxyType proxy = element.getProxy();
					proxyList.add(proxy);
				}
				if (element.ifAgent()) {
					AgentType agent = element.getAgent();
					agentList.add(agent);
				}
				if (element.ifAggregation()) {
					Aggregation aggregation = element.getAggregation();
					aggregationList.add(aggregation);
				}
				if (element.ifConcept()) {
					Concept concept = element.getConcept();
					conceptList.add(concept);
				}
				if (element.ifEuropeanaAggregation()) {
					EuropeanaAggregationType euaggregation = element
							.getEuropeanaAggregation();
					euaggregationList.add(euaggregation);
				}
				if (element.ifPlace()) {
					PlaceType place = element.getPlace();
					placeList.add(place);
				}
				if (element.ifProvidedCHO()) {
					ProvidedCHOType prcho = element.getProvidedCHO();
					prchoList.add(prcho);
				}
				if (element.ifTimeSpan()) {
					TimeSpanType time = element.getTimeSpan();
					timeList.add(time);
				}
				if (element.ifWebResource()) {
					WebResourceType webresource = element.getWebResource();
					webresourceList.add(webresource);
				}

			}
			
			


		}

	}
}
