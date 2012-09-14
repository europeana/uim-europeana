package eu.europeana.uim.gui.cp.server;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.europeana.corelib.definitions.model.EdmLabel;
import eu.europeana.corelib.definitions.solr.DocType;
import eu.europeana.corelib.solr.entity.AgentImpl;
import eu.europeana.corelib.solr.entity.AggregationImpl;
import eu.europeana.corelib.solr.entity.ConceptImpl;
import eu.europeana.corelib.solr.entity.EuropeanaAggregationImpl;
import eu.europeana.corelib.solr.entity.PlaceImpl;
import eu.europeana.corelib.solr.entity.ProvidedCHOImpl;
import eu.europeana.corelib.solr.entity.ProxyImpl;
import eu.europeana.corelib.solr.entity.TimespanImpl;
import eu.europeana.corelib.solr.entity.WebResourceImpl;
import eu.europeana.uim.gui.cp.shared.validation.FieldValueDTO;

/**
 * Converter class from FullBean to list of values
 * 
 * @author Yorgos.Mamakis@ kb.nl
 * 
 */
public final class MongoConverter {

	public static List<FieldValueDTO> convertAggregation(
			AggregationImpl aggregation) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();

		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getAbout", EdmLabel.PROVIDER_AGGREGATION_ORE_AGGREGATION));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getAggregatedCHO",
				EdmLabel.PROVIDER_AGGREGATION_EDM_AGGREGATED_CHO));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getDcRights", EdmLabel.PROVIDER_AGGREGATION_DC_RIGHTS));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getEdmDataProvider",
				EdmLabel.PROVIDER_AGGREGATION_EDM_DATA_PROVIDER));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getHasView", EdmLabel.PROVIDER_AGGREGATION_EDM_HASVIEW));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getEdmIsShownAt", EdmLabel.PROVIDER_AGGREGATION_EDM_IS_SHOWN_AT));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getEdmIsShownBy", EdmLabel.PROVIDER_AGGREGATION_EDM_IS_SHOWN_BY));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getEdmObject", EdmLabel.PROVIDER_AGGREGATION_EDM_OBJECT));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getEdmProvider", EdmLabel.PROVIDER_AGGREGATION_EDM_PROVIDER));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getEdmRights", EdmLabel.PROVIDER_AGGREGATION_EDM_RIGHTS));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"getAggregates", EdmLabel.PROVIDER_AGGREGATION_ORE_AGGREGATES));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertProxy(ProxyImpl proxy) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getAbout",
				EdmLabel.ORE_PROXY));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDcContributor", EdmLabel.PROXY_DC_CONTRIBUTOR));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcCoverage",
				EdmLabel.PROXY_DC_COVERAGE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcCreator",
				EdmLabel.PROXY_DC_CREATOR));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcDate",
				EdmLabel.PROXY_DC_DATE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcFormat",
				EdmLabel.PROXY_DC_FORMAT));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDcIdentifier", EdmLabel.PROXY_DC_IDENTIFIER));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcLanguage",
				EdmLabel.PROXY_DC_LANGUAGE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDcPublisher", EdmLabel.PROXY_DC_PUBLISHER));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcRelation",
				EdmLabel.PROXY_DC_RELATION));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcRights",
				EdmLabel.PROXY_DC_RIGHTS));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcSource",
				EdmLabel.PROXY_DC_SOURCE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcSubject",
				EdmLabel.PROXY_DC_SUBJECT));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcTitle",
				EdmLabel.PROXY_DC_TITLE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDcType",
				EdmLabel.PROXY_DC_TYPE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsAlternative", EdmLabel.PROXY_DCTERMS_ALTERNATIVE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsConformsTo", EdmLabel.PROXY_DCTERMS_CONFORMS_TO));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsCreated", EdmLabel.PROXY_DCTERMS_CREATED));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsExtent", EdmLabel.PROXY_DCTERMS_EXTENT));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsHasFormat", EdmLabel.PROXY_DCTERMS_HAS_FORMAT));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsHasPart", EdmLabel.PROXY_DCTERMS_HAS_PART));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsHasVersion", EdmLabel.PROXY_DCTERMS_HAS_VERSION));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsIsFormatOf", EdmLabel.PROXY_DCTERMS_IS_FORMAT_OF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsIsPartOf", EdmLabel.PROXY_DCTERMS_IS_PART_OF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsIsReferencedBy",
				EdmLabel.PROXY_DCTERMS_IS_REFERENCED_BY));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsIsReplacedBy", EdmLabel.PROXY_DCTERMS_IS_REPLACED_BY));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsIsRequiredBy", EdmLabel.PROXY_DCTERMS_IS_REQUIRED_BY));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsIsVersionOf", EdmLabel.PROXY_DCTERMS_IS_VERSION_OF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsIssued", EdmLabel.PROXY_DCTERMS_ISSUED));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsMedium", EdmLabel.PROXY_DCTERMS_MEDIUM));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsProvenance", EdmLabel.PROXY_DCTERMS_PROVENANCE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsReferences", EdmLabel.PROXY_DCTERMS_REFERENCES));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsReplaces", EdmLabel.PROXY_DCTERMS_REPLACES));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsRequires", EdmLabel.PROXY_DCTERMS_REQUIRES));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsSpatial", EdmLabel.PROXY_DCTERMS_SPATIAL));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getDctermsTOC",
				EdmLabel.PROXY_DCTERMS_TABLE_OF_CONTENTS));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getDctermsTemporal", EdmLabel.PROXY_DCTERMS_TEMPORAL));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getEdmCurrentLocation", EdmLabel.PROXY_EDM_CURRENT_LOCATION));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getEdmHasType",
				EdmLabel.PROXY_EDM_HAS_TYPE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getEdmIncorporates", EdmLabel.PROXY_EDM_INCORPORATES));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getEdmIsNextInSequence", EdmLabel.PROXY_EDM_IS_NEXT_IN_SEQUENCE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getEdmIsDerivativeOf", EdmLabel.PROXY_EDM_ISDERIVATIVE_OF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getEdmIsRelatedTo", EdmLabel.PROXY_EDM_ISRELATEDTO));
		fieldValueList
				.add(getFieldValues(ProxyImpl.class, proxy,
						"getEdmIsRepresentationOf",
						EdmLabel.PROXY_EDM_ISREPRESENTATIONOF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getEdmIsSimilarTo", EdmLabel.PROXY_EDM_ISSIMILARTO));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getEdmIsSuccessorOf", EdmLabel.PROXY_EDM_ISSUCCESSOROF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getEdmRealizes", EdmLabel.PROXY_EDM_REALIZES));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getEdmRights",
				EdmLabel.PROXY_EDM_RIGHTS));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getEdmWasPresentAt", EdmLabel.PROXY_EDM_WASPRESENTAT));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"getProxyFor", EdmLabel.PROXY_ORE_PROXY_FOR));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getProxyIn",
				EdmLabel.PROXY_ORE_PROXY_IN));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "getEdmType",
				EdmLabel.PROVIDER_EDM_TYPE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"isEuropeanaProxy", EdmLabel.EDM_ISEUROPEANA_PROXY));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertEuropeanaAggregation(
			EuropeanaAggregationImpl aggregation) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();

		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "getAbout", EdmLabel.EDM_EUROPEANA_AGGREGATION));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "getAggregatedCHO",
				EdmLabel.EUROPEANA_AGGREGATION_ORE_AGGREGATEDCHO));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "getDcCreator",
				EdmLabel.EUROPEANA_AGGREGATION_DC_CREATOR));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "getEdmCountry",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_COUNTRY));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "getEdmHasView",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_HASVIEW));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "getEdmIsShownBy",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_ISSHOWNBY));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "getEdmLandingPage",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_LANDINGPAGE));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "getEdmLanguage",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_LANGUAGE));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "getEdmRights",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_RIGHTS));
		/*This does not work*/
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "getAggregates",
				EdmLabel.EUROPEANA_AGGREGATION_ORE_AGGREGATES));
		/*This works*/
//		String[] val = aggregation.getAggregates();
//		FieldValueDTO fieldValueDTO = new FieldValueDTO();
//		fieldValueDTO.setFieldName(EdmLabel.EUROPEANA_AGGREGATION_ORE_AGGREGATES.toString());
//		List<String> values = new ArrayList<String>();
//		if (val != null) {
//			for (String entry :  val)
//					 {
//					values.add(entry);
//			}
//		}
//		fieldValueDTO.setFieldValue(values);
//		fieldValueList.add(fieldValueDTO);
		return fieldValueList;
	}

	public static List<FieldValueDTO> convertProvidedCHO(
			ProvidedCHOImpl providedCHO) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();

		fieldValueList.add(getFieldValues(ProvidedCHOImpl.class, providedCHO,
				"getAbout", EdmLabel.EUROPEANA_ID));
		fieldValueList.add(getFieldValues(ProvidedCHOImpl.class, providedCHO,
				"getOwlSameas", EdmLabel.PROXY_OWL_SAMEAS));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertAgent(AgentImpl agent) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "getAbout",
				EdmLabel.EDM_AGENT));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "getDcDate",
				EdmLabel.AG_DC_DATE));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"getDcIdentifier", EdmLabel.AG_DC_IDENTIFIER));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "getBegin",
				EdmLabel.AG_EDM_BEGIN));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "getEnd",
				EdmLabel.AG_EDM_END));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "getEdmHasMet",
				EdmLabel.AG_EDM_HASMET));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"getEdmIsRelatedto", EdmLabel.AG_EDM_ISRELATEDTO));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"getEdmWasPresentAt", EdmLabel.AG_EDM_WASPRESENTAT));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "getOwlSameAs",
				EdmLabel.AG_OWL_SAMEAS));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "getFoafName",
				EdmLabel.AG_FOAF_NAME));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"getRdaGr2BiographicalInformation",
				EdmLabel.AG_RDAGR2_BIOGRAPHICALINFORMATION));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"getRdaGr2DateOfBirth", EdmLabel.AG_RDAGR2_DATEOFBIRTH));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"getRdaGr2DateOfDeath", EdmLabel.AG_RDAGR2_DATEOFDEATH));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"getRdaGr2DateOfEstablishment",
				EdmLabel.AG_RDAGR2_DATEOFESTABLISHMENT));
		fieldValueList
				.add(getFieldValues(AgentImpl.class, agent,
						"getRdaGr2DateOfTermination",
						EdmLabel.AG_RDAGR2_DATEOFTERMINATION));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"getRdaGr2Gender", EdmLabel.AG_RDAGR2_GENDER));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"getRdaGr2ProfessionOrOccupation",
				EdmLabel.AG_RDAGR2_PROFESSIONOROCCUPATION));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "getPrefLabel",
				EdmLabel.AG_SKOS_PREF_LABEL));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "getAltLabel",
				EdmLabel.AG_SKOS_ALT_LABEL));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "getNote",
				EdmLabel.AG_SKOS_NOTE));
		return fieldValueList;
	}

	public static List<FieldValueDTO> convertPlace(PlaceImpl place) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "getAbout",
				EdmLabel.EDM_PLACE));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place,
				"getDctermsHasPart", EdmLabel.PL_DCTERMS_HASPART));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place,
				"getDctermsIsPartOf", EdmLabel.PL_DCTERMS_ISPART_OF));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "getOwlSameAs",
				EdmLabel.PL_OWL_SAMEAS));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "getPrefLabel",
				EdmLabel.PL_SKOS_PREF_LABEL));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "getAltLabel",
				EdmLabel.PL_SKOS_ALT_LABEL));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place,
				"getHiddenLabel", EdmLabel.PL_SKOS_HIDDENLABEL));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "getNote",
				EdmLabel.PL_SKOS_NOTE));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "getAltitude",
				EdmLabel.PL_WGS84_POS_ALT));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "getLatitude",
				EdmLabel.PL_WGS84_POS_LAT));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "getLongitude",
				EdmLabel.PL_WGS84_POS_LONG));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertTimespan(TimespanImpl timespan) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"getAbout", EdmLabel.EDM_TIMESPAN));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"getDctermsHasPart", EdmLabel.TS_DCTERMS_HASPART));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"getDctermsIsPartOf", EdmLabel.TS_DCTERMS_ISPART_OF));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"getBegin", EdmLabel.TS_EDM_BEGIN));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan, "getEnd",
				EdmLabel.TS_EDM_END));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"getOwlSameAs", EdmLabel.TS_OWL_SAMEAS));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"getAltLabel", EdmLabel.TS_SKOS_ALT_LABEL));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"getPrefLabel", EdmLabel.TS_SKOS_PREF_LABEL));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"getHiddenLabel", EdmLabel.TS_SKOS_HIDDENLABEL));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan, "getNote",
				EdmLabel.TS_SKOS_NOTE));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertConcept(ConceptImpl concept) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept, "getAbout",
				EdmLabel.SKOS_CONCEPT));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getBroader", EdmLabel.CC_SKOS_BROADER));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getAltLabel", EdmLabel.CC_SKOS_ALT_LABEL));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getBroadMatch", EdmLabel.CC_SKOS_BROADMATCH));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getCloseMatch", EdmLabel.CC_SKOS_CLOSEMATCH));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getExactMatch", EdmLabel.CC_SKOS_EXACTMATCH));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getHiddenLabel", EdmLabel.CC_SKOS_HIDDEN_LABEL));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getInScheme", EdmLabel.CC_SKOS_INSCHEME));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getNarrower", EdmLabel.CC_SKOS_NARROWER));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getNarrowMatch", EdmLabel.CC_SKOS_NARROWMATCH));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getNotation", EdmLabel.CC_SKOS_NOTATIONS));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept, "getNote",
				EdmLabel.CC_SKOS_NOTE));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getPrefLabel", EdmLabel.CC_SKOS_PREF_LABEL));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getRelated", EdmLabel.CC_SKOS_RELATED));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"getRelatedMatch", EdmLabel.CC_SKOS_RELATEDMATCH));
		return fieldValueList;
	}

	public static List<FieldValueDTO> convertWebResource(
			WebResourceImpl webResource) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getAbout", EdmLabel.EDM_WEB_RESOURCE));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getDcDescription", EdmLabel.WR_DC_DESCRIPTION));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getDcFormat", EdmLabel.WR_DC_FORMAT));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getWebResourceDcRights", EdmLabel.WR_DC_RIGHTS));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getDcSource", EdmLabel.WR_DC_SOURCE));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getDctermsConformsTo", EdmLabel.WR_DCTERMS_CONFORMSTO));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getDctermsCreated", EdmLabel.WR_DCTERMS_CREATED));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getDctermsHasPart", EdmLabel.WR_DCTERMS_HAS_PART));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getDctermsExtent", EdmLabel.WR_DCTERMS_EXTENT));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getDctermsIsFormatOf", EdmLabel.WR_DCTERMS_ISFORMATOF));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getDctermsIssued", EdmLabel.WR_DCTERMS_ISSUED));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getIsNextInSequence", EdmLabel.WR_EDM_IS_NEXT_IN_SEQUENCE));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"getWebResourceEdmRights", EdmLabel.WR_EDM_RIGHTS));
		return fieldValueList;
	}

	@SuppressWarnings("unchecked")
	public static FieldValueDTO getFieldValues(Class<?> input, Object obj,
			String methodName, EdmLabel label) {
		FieldValueDTO fieldValueDTO = new FieldValueDTO();

		try {
			// for(Field field:obj.getClass().getDeclaredFields())
			// {
			// System.out.println("Class: " + obj.getClass().getCanonicalName()
			// + " | Field: "+ field.getName());
			// }
			
			//Field field = input.getDeclaredField(methodName);
			//field.setAccessible(true);
			fieldValueDTO.setFieldName(label.toString());
			
			Method method = input.getMethod(methodName);
			Object field = method.invoke(obj);
			
			List<String> values = new ArrayList<String>();
			if(field!=null){
			if (field.getClass().isAssignableFrom(HashMap.class)) {

				Map<String, List<String>> val = (HashMap<String, List<String>>) field;
				//field.get(val);
				
				if (val != null) {
					for (Entry<String, List<String>> entry :  val.entrySet()) {
						for (String str : entry.getValue()) {
							values.add(entry.getKey() + ":" + str);
						}
					}
				}
			}

			if (field.getClass().isAssignableFrom(String[].class)) {

				String[] val =  (String[]) field;
				if (val != null) {

					for (String str : val) {
						values.add(str);
					}

				}
			}

			if (field.getClass().isAssignableFrom(String.class)) {

				String val = (String)field;
				if (val != null) {

					values.add(val);

				}
			}

			if (field.getClass().isAssignableFrom(Float.class)) {

				float val = (Float) field;

				values.add(Float.toString(val));

			}

			if (field.getClass().isAssignableFrom(Boolean.class)) {

				boolean val = (Boolean) field;

				values.add(Boolean.toString(val));

			}
			
			if(field.getClass().isAssignableFrom(DocType.class)){
				String val = ((DocType)field).toString();
				values.add(val);
			}
			}

			fieldValueDTO.setFieldValue(values);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fieldValueDTO;
	}
}
