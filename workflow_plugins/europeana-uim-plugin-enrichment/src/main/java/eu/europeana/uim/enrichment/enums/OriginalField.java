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
package eu.europeana.uim.enrichment.enums;

import java.util.ArrayList;
import java.util.List;

import eu.europeana.corelib.definitions.jibx.Contributor;
import eu.europeana.corelib.definitions.jibx.Coverage;
import eu.europeana.corelib.definitions.jibx.Creator;
import eu.europeana.corelib.definitions.jibx.Date;
import eu.europeana.corelib.definitions.jibx.EuropeanaType.Choice;
import eu.europeana.corelib.definitions.jibx.ProxyType;
import eu.europeana.corelib.definitions.jibx.ResourceOrLiteralType.Resource;
import eu.europeana.corelib.definitions.jibx.Spatial;
import eu.europeana.corelib.definitions.jibx.Subject;
import eu.europeana.corelib.definitions.jibx.Temporal;
import eu.europeana.corelib.definitions.jibx.Type;
import eu.europeana.corelib.definitions.jibx.Year;

/**
 * Enumeration to set the original fields from which enrichments were created.
 * This should correspond to the field rule pairs of Annocultor
 * 
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
public enum OriginalField {

	PROXY_DC_DATE("proxy_dc_date") {
		@Override
		public ProxyType appendField(ProxyType proxy, String uri) {
			Date date = new Date();			
			Resource dateres = new Resource();
			dateres.setResource(uri);
			date.setResource(dateres);
			Choice choice = new Choice();
			choice.setDate(date);
			if(proxy.getChoiceList()!=null){
				proxy.getChoiceList().add(choice);
			} else {
				List<Choice> choices = new ArrayList<Choice>();
				choices.add(choice);
				proxy.setChoiceList(choices);
			}
			return proxy;
		}
	},
	PROXY_DC_COVERAGE("proxy_dc_coverage") {
		@Override
		public ProxyType appendField(ProxyType proxy, String uri) {
			Coverage coverage = new Coverage();
			Resource coverageres = new Resource();
			coverageres.setResource(uri);
			coverage.setResource(coverageres);
			Choice choice = new Choice();
			choice.setCoverage(coverage);
			if(proxy.getChoiceList()!=null){
				proxy.getChoiceList().add(choice);
			} else {
				List<Choice> choices = new ArrayList<Choice>();
				choices.add(choice);
				proxy.setChoiceList(choices);
			}
			return proxy;
		}
	},
	PROXY_DCTERMS_TEMPORAL("proxy_dcterms_temporal") {
		@Override
		public ProxyType appendField(ProxyType proxy, String uri) {
			Temporal obj = new Temporal();
			Resource temporalres = new Resource();
			temporalres.setResource(uri);
			obj.setResource(temporalres);
			Choice choice = new Choice();
			choice.setTemporal(obj);
			if(proxy.getChoiceList()!=null){
				proxy.getChoiceList().add(choice);
			} else {
				List<Choice> choices = new ArrayList<Choice>();
				choices.add(choice);
				proxy.setChoiceList(choices);
			}
			return proxy;
		}
	},	PROXY_EDM_YEAR("proxy_edm_year") {
		@Override
		public ProxyType appendField(ProxyType proxy, String uri) {
			Year obj = new Year();
			obj.setString(uri);
			proxy.getYearList().add(obj);
			return proxy;
		}
	},
	PROXY_DCTERMS_SPATIAL("proxy_dcterms_spatial") {
		@Override
		public ProxyType appendField(ProxyType proxy, String uri) {
			Spatial obj = new Spatial();
			Resource spatialres = new Resource();
			spatialres.setResource(uri);
			obj.setResource(spatialres);
			Choice choice = new Choice();
			choice.setSpatial(obj);
			if(proxy.getChoiceList()!=null){
				proxy.getChoiceList().add(choice);
			} else {
				List<Choice> choices = new ArrayList<Choice>();
				choices.add(choice);
				proxy.setChoiceList(choices);
			}
			return proxy;
		}
	},
	PROXY_DC_TYPE("proxy_dc_type") {
		@Override
		public ProxyType appendField(ProxyType proxy, String uri) {
			Type obj = new Type();
			Resource typeres = new Resource();
			typeres.setResource(uri);
			obj.setResource(typeres);
			Choice choice = new Choice();
			choice.setType(obj);
			if(proxy.getChoiceList()!=null){
				proxy.getChoiceList().add(choice);
			} else {
				List<Choice> choices = new ArrayList<Choice>();
				choices.add(choice);
				proxy.setChoiceList(choices);
			}
			return proxy;			
		}
	},
	PROXY_DC_SUBJECT("proxy_dc_subject") {
		@Override
		public ProxyType appendField(ProxyType proxy, String uri) {
			Subject obj = new Subject();
			Resource subjectres = new Resource();
			subjectres.setResource(uri);
			obj.setResource(subjectres);
			Choice choice = new Choice();
			choice.setSubject(obj);
			if(proxy.getChoiceList()!=null){
				proxy.getChoiceList().add(choice);
			} else {
				List<Choice> choices = new ArrayList<Choice>();
				choices.add(choice);
				proxy.setChoiceList(choices);
			}
			return proxy;
			
		}
	},
	PROXY_DC_CREATOR("proxy_dc_creator") {
		@Override
		public ProxyType appendField(ProxyType proxy, String uri) {
			Creator obj = new Creator();
			Resource creatortres = new Resource();
			creatortres.setResource(uri);
			obj.setResource(creatortres);
			Choice choice = new Choice();
			choice.setCreator(obj);
			if(proxy.getChoiceList()!=null){
				proxy.getChoiceList().add(choice);
			} else {
				List<Choice> choices = new ArrayList<Choice>();
				choices.add(choice);
				proxy.setChoiceList(choices);
			}
			return proxy;
			
		}
	},
	PROXY_DC_CONTRIBUTOR("proxy_dc_contributor") {
		@Override
		public ProxyType appendField(ProxyType proxy, String uri) {
			Contributor obj = new Contributor();
			Resource contribtres = new Resource();
			contribtres.setResource(uri);
			
			obj.setResource(contribtres);
			Choice choice = new Choice();
			choice.setContributor(obj);
			if(proxy.getChoiceList()!=null){
				proxy.getChoiceList().add(choice);
			} else {
				List<Choice> choices = new ArrayList<Choice>();
				choices.add(choice);
				proxy.setChoiceList(choices);
			}
			return proxy;
		}
	}
	;
	
	String originalField;

	private OriginalField(String originalField){
		this.originalField = originalField;
	}
	
	public static OriginalField getOriginalField(String originalField){
		for(OriginalField orField: OriginalField.values()){
			if(orField.originalField.equalsIgnoreCase(originalField)){
				return orField;
			}
		}
		throw new IllegalArgumentException(originalField +" not found");
	}
	/**
	 * Append the appropriate URI of the contextual entity to the field that generated it
	 * @param proxy The proxy to append the object to
	 * @param uri The URI to append
	 * @return The modified proxy
	 */
	public abstract ProxyType appendField(ProxyType proxy, String uri);
}
