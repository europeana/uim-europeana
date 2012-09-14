package eu.europeana.uim.gui.cp.server;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import eu.europeana.corelib.definitions.model.EdmLabel;
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
				"about", EdmLabel.PROVIDER_AGGREGATION_ORE_AGGREGATION));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"aggregatedCHO",
				EdmLabel.PROVIDER_AGGREGATION_EDM_AGGREGATED_CHO));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"dcRights", EdmLabel.PROVIDER_AGGREGATION_DC_RIGHTS));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"edmDataProvider",
				EdmLabel.PROVIDER_AGGREGATION_EDM_DATA_PROVIDER));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"edmHasView", EdmLabel.PROVIDER_AGGREGATION_EDM_HASVIEW));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"edmIsShownAt", EdmLabel.PROVIDER_AGGREGATION_EDM_IS_SHOWN_AT));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"edmIsShownBy", EdmLabel.PROVIDER_AGGREGATION_EDM_IS_SHOWN_BY));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"edmObject", EdmLabel.PROVIDER_AGGREGATION_EDM_OBJECT));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"edmProvider", EdmLabel.PROVIDER_AGGREGATION_EDM_PROVIDER));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"edmRights", EdmLabel.PROVIDER_AGGREGATION_EDM_RIGHTS));
		fieldValueList.add(getFieldValues(AggregationImpl.class, aggregation,
				"aggregates", EdmLabel.PROVIDER_AGGREGATION_ORE_AGGREGATES));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertProxy(ProxyImpl proxy) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "about",
				EdmLabel.ORE_PROXY));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dcContributor", EdmLabel.PROXY_DC_CONTRIBUTOR));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcCoverage",
				EdmLabel.PROXY_DC_COVERAGE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcCreator",
				EdmLabel.PROXY_DC_CREATOR));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcDate",
				EdmLabel.PROXY_DC_DATE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcFormat",
				EdmLabel.PROXY_DC_FORMAT));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dcIdentifier", EdmLabel.PROXY_DC_IDENTIFIER));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcLanguage",
				EdmLabel.PROXY_DC_LANGUAGE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dcPublisher", EdmLabel.PROXY_DC_PUBLISHER));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcRelation",
				EdmLabel.PROXY_DC_RELATION));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcRights",
				EdmLabel.PROXY_DC_RIGHTS));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcSource",
				EdmLabel.PROXY_DC_SOURCE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcSubject",
				EdmLabel.PROXY_DC_SUBJECT));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcTitle",
				EdmLabel.PROXY_DC_TITLE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dcType",
				EdmLabel.PROXY_DC_TYPE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsAlternative", EdmLabel.PROXY_DCTERMS_ALTERNATIVE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsConformsTo", EdmLabel.PROXY_DCTERMS_CONFORMS_TO));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsCreated", EdmLabel.PROXY_DCTERMS_CREATED));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsExtent", EdmLabel.PROXY_DCTERMS_EXTENT));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsHasFormat", EdmLabel.PROXY_DCTERMS_HAS_FORMAT));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsHasPart", EdmLabel.PROXY_DCTERMS_HAS_PART));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsHasVersion", EdmLabel.PROXY_DCTERMS_HAS_VERSION));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsIsFormatOf", EdmLabel.PROXY_DCTERMS_IS_FORMAT_OF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsIsPartOf", EdmLabel.PROXY_DCTERMS_IS_PART_OF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsIsReferencedBy",
				EdmLabel.PROXY_DCTERMS_IS_REFERENCED_BY));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsIsReplacedBy", EdmLabel.PROXY_DCTERMS_IS_REPLACED_BY));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsIsRequiredBy", EdmLabel.PROXY_DCTERMS_IS_REQUIRED_BY));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsIsVersionOf", EdmLabel.PROXY_DCTERMS_IS_VERSION_OF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsIssued", EdmLabel.PROXY_DCTERMS_ISSUED));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsMedium", EdmLabel.PROXY_DCTERMS_MEDIUM));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsProvenance", EdmLabel.PROXY_DCTERMS_PROVENANCE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsReferences", EdmLabel.PROXY_DCTERMS_REFERENCES));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsReplaces", EdmLabel.PROXY_DCTERMS_REPLACES));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsRequires", EdmLabel.PROXY_DCTERMS_REQUIRES));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsSpatial", EdmLabel.PROXY_DCTERMS_SPATIAL));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "dctermsTOC",
				EdmLabel.PROXY_DCTERMS_TABLE_OF_CONTENTS));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"dctermsTemporal", EdmLabel.PROXY_DCTERMS_TEMPORAL));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"edmCurrentLocation", EdmLabel.PROXY_EDM_CURRENT_LOCATION));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "edmHasType",
				EdmLabel.PROXY_EDM_HAS_TYPE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"edmIncorporates", EdmLabel.PROXY_EDM_INCORPORATES));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"edmIsNextInSequence", EdmLabel.PROXY_EDM_IS_NEXT_IN_SEQUENCE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"edmIsDerivativeOf", EdmLabel.PROXY_EDM_ISDERIVATIVE_OF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"edmIsRelatedTo", EdmLabel.PROXY_EDM_ISRELATEDTO));
		fieldValueList
				.add(getFieldValues(ProxyImpl.class, proxy,
						"edmIsRepresentationOf",
						EdmLabel.PROXY_EDM_ISREPRESENTATIONOF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"edmIsSimilarTo", EdmLabel.PROXY_EDM_ISSIMILARTO));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"edmIsSuccessorOf", EdmLabel.PROXY_EDM_ISSUCCESSOROF));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"edmRealizes", EdmLabel.PROXY_EDM_REALIZES));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "edmRights",
				EdmLabel.PROXY_EDM_RIGHTS));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"edmWasPresentAt", EdmLabel.PROXY_EDM_WASPRESENTAT));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"oreProxyFor", EdmLabel.PROXY_ORE_PROXY_FOR));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "oreProxyIn",
				EdmLabel.PROXY_ORE_PROXY_IN));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy, "edmType",
				EdmLabel.PROVIDER_EDM_TYPE));
		fieldValueList.add(getFieldValues(ProxyImpl.class, proxy,
				"europeanaProxy", EdmLabel.EDM_ISEUROPEANA_PROXY));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertEuropeanaAggregation(
			EuropeanaAggregationImpl aggregation) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();

		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "about", EdmLabel.EDM_EUROPEANA_AGGREGATION));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "aggregatedCHO",
				EdmLabel.EUROPEANA_AGGREGATION_ORE_AGGREGATEDCHO));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "dcCreator",
				EdmLabel.EUROPEANA_AGGREGATION_DC_CREATOR));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "edmCountry",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_COUNTRY));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "edmHasView",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_HASVIEW));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "edmIsShownBy",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_ISSHOWNBY));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "edmLandingPage",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_LANDINGPAGE));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "edmLanguage",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_LANGUAGE));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "edmRights",
				EdmLabel.EUROPEANA_AGGREGATION_EDM_RIGHTS));
		fieldValueList.add(getFieldValues(EuropeanaAggregationImpl.class,
				aggregation, "aggregates",
				EdmLabel.EUROPEANA_AGGREGATION_ORE_AGGREGATES));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertProvidedCHO(
			ProvidedCHOImpl providedCHO) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();

		fieldValueList.add(getFieldValues(ProvidedCHOImpl.class, providedCHO,
				"about", EdmLabel.EUROPEANA_ID));
		fieldValueList.add(getFieldValues(ProvidedCHOImpl.class, providedCHO,
				"owlSameas", EdmLabel.PROXY_OWL_SAMEAS));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertAgent(AgentImpl agent) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "about",
				EdmLabel.EDM_AGENT));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "dcDate",
				EdmLabel.AG_DC_DATE));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"dcIdentifier", EdmLabel.AG_DC_IDENTIFIER));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "begin",
				EdmLabel.AG_EDM_BEGIN));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "end",
				EdmLabel.AG_EDM_END));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "edmHasMet",
				EdmLabel.AG_EDM_HASMET));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"edmIsRelatedto", EdmLabel.AG_EDM_ISRELATEDTO));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"edmWasPresentAt", EdmLabel.AG_EDM_WASPRESENTAT));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "owlSameAs",
				EdmLabel.AG_OWL_SAMEAS));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "foafName",
				EdmLabel.AG_FOAF_NAME));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"rdaGr2BiographicalInformation",
				EdmLabel.AG_RDAGR2_BIOGRAPHICALINFORMATION));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"rdaGr2DateOfBirth", EdmLabel.AG_RDAGR2_DATEOFBIRTH));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"rdaGr2DateOfDeath", EdmLabel.AG_RDAGR2_DATEOFDEATH));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"rdaGr2DateOfEstablishment",
				EdmLabel.AG_RDAGR2_DATEOFESTABLISHMENT));
		fieldValueList
				.add(getFieldValues(AgentImpl.class, agent,
						"rdaGr2DateOfTermination",
						EdmLabel.AG_RDAGR2_DATEOFTERMINATION));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"rdaGr2Gender", EdmLabel.AG_RDAGR2_GENDER));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent,
				"rdaGr2ProfessionOrOccupation",
				EdmLabel.AG_RDAGR2_PROFESSIONOROCCUPATION));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "prefLabel",
				EdmLabel.AG_SKOS_PREF_LABEL));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "altLabel",
				EdmLabel.AG_SKOS_ALT_LABEL));
		fieldValueList.add(getFieldValues(AgentImpl.class, agent, "note",
				EdmLabel.AG_SKOS_NOTE));
		return fieldValueList;
	}

	public static List<FieldValueDTO> convertPlace(PlaceImpl place) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "about",
				EdmLabel.EDM_PLACE));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place,
				"dctermsHasPart", EdmLabel.PL_DCTERMS_HASPART));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place,
				"dctermsIsPartOf", EdmLabel.PL_DCTERMS_ISPART_OF));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "owlSameAs",
				EdmLabel.PL_OWL_SAMEAS));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "prefLabel",
				EdmLabel.PL_SKOS_PREF_LABEL));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "altLabel",
				EdmLabel.PL_SKOS_ALT_LABEL));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place,
				"hiddenLabel", EdmLabel.PL_SKOS_HIDDENLABEL));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "note",
				EdmLabel.PL_SKOS_NOTE));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "altitude",
				EdmLabel.PL_WGS84_POS_ALT));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "latitude",
				EdmLabel.PL_WGS84_POS_LAT));
		fieldValueList.add(getFieldValues(PlaceImpl.class, place, "longitude",
				EdmLabel.PL_WGS84_POS_LONG));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertTimespan(TimespanImpl timespan) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"about", EdmLabel.EDM_TIMESPAN));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"dctermsHasPart", EdmLabel.TS_DCTERMS_HASPART));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"dctermsIsPartOf", EdmLabel.TS_DCTERMS_ISPART_OF));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"begin", EdmLabel.TS_EDM_BEGIN));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan, "end",
				EdmLabel.TS_EDM_END));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"owlSameAs", EdmLabel.TS_OWL_SAMEAS));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"altLabel", EdmLabel.TS_SKOS_ALT_LABEL));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"prefLabel", EdmLabel.TS_SKOS_PREF_LABEL));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan,
				"hiddenLabel", EdmLabel.TS_SKOS_HIDDENLABEL));
		fieldValueList.add(getFieldValues(TimespanImpl.class, timespan, "note",
				EdmLabel.TS_SKOS_NOTE));

		return fieldValueList;
	}

	public static List<FieldValueDTO> convertConcept(ConceptImpl concept) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept, "about",
				EdmLabel.SKOS_CONCEPT));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"broader", EdmLabel.CC_SKOS_BROADER));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"altLabel", EdmLabel.CC_SKOS_ALT_LABEL));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"broadMatch", EdmLabel.CC_SKOS_BROADMATCH));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"closeMatch", EdmLabel.CC_SKOS_CLOSEMATCH));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"exactMatch", EdmLabel.CC_SKOS_EXACTMATCH));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"hiddenLabel", EdmLabel.CC_SKOS_HIDDEN_LABEL));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"inScheme", EdmLabel.CC_SKOS_INSCHEME));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"narrower", EdmLabel.CC_SKOS_NARROWER));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"narrowMatch", EdmLabel.CC_SKOS_NARROWMATCH));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"notation", EdmLabel.CC_SKOS_NOTATIONS));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept, "note",
				EdmLabel.CC_SKOS_NOTE));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"prefLabel", EdmLabel.CC_SKOS_PREF_LABEL));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"related", EdmLabel.CC_SKOS_RELATED));
		fieldValueList.add(getFieldValues(ConceptImpl.class, concept,
				"relatedMatch", EdmLabel.CC_SKOS_RELATEDMATCH));
		return fieldValueList;
	}

	public static List<FieldValueDTO> convertWebResource(
			WebResourceImpl webResource) {
		List<FieldValueDTO> fieldValueList = new ArrayList<FieldValueDTO>();
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"about", EdmLabel.EDM_WEB_RESOURCE));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"dcDescription", EdmLabel.WR_DC_DESCRIPTION));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"dcFormat", EdmLabel.WR_DC_FORMAT));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"dcRights", EdmLabel.WR_DC_RIGHTS));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"dcSource", EdmLabel.WR_DC_SOURCE));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"dctermsConformsTo", EdmLabel.WR_DCTERMS_CONFORMSTO));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"dctermsCreated", EdmLabel.WR_DCTERMS_CREATED));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"dctermsHasPart", EdmLabel.WR_DCTERMS_HAS_PART));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"dctermsExtent", EdmLabel.WR_DCTERMS_EXTENT));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"dctermsIsFormatOf", EdmLabel.WR_DCTERMS_ISFORMATOF));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"dctermsIssued", EdmLabel.WR_DCTERMS_ISSUED));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"edmIsNextInSequence", EdmLabel.WR_EDM_IS_NEXT_IN_SEQUENCE));
		fieldValueList.add(getFieldValues(WebResourceImpl.class, webResource,
				"edmRights", EdmLabel.WR_EDM_RIGHTS));
		return fieldValueList;
	}

	@SuppressWarnings("unchecked")
	public static FieldValueDTO getFieldValues(Class<?> input, Object obj,
			String fieldName, EdmLabel label) {
		FieldValueDTO fieldValueDTO = new FieldValueDTO();

		try {
			// for(Field field:obj.getClass().getDeclaredFields())
			// {
			// System.out.println("Class: " + obj.getClass().getCanonicalName()
			// + " | Field: "+ field.getName());
			// }
			Field field = obj.getClass().getDeclaredField(fieldName);
			field.setAccessible(true);
			fieldValueDTO.setFieldName(label.toString());
			List<String> values = new ArrayList<String>();
			if (field.getType().isAssignableFrom(Map.class)) {

				Map<String, List<String>> val = new HashMap<String, List<String>>();
				field.get(val);
				if (val != null) {
					for (Entry<String, List<String>> entry : ((HashMap<String, List<String>>) val)
							.entrySet()) {
						for (String str : entry.getValue()) {
							values.add(entry.getKey() + ":" + str);
						}
					}
				}
			}

			if (field.getType().isAssignableFrom(String[].class)) {

				String[] val = new String[] {};
				field.get(val);
				if (val != null) {

					for (String str : val) {
						values.add(str);
					}

				}
			}

			if (field.getType().isAssignableFrom(String.class)) {

				String val = new String();
				field.get(val);
				if (val != null) {

					values.add(val);

				}
			}

			if (field.getType().isAssignableFrom(Float.class)) {

				float val = 0f;
				field.get(val);

				values.add(Float.toString(val));

			}

			if (field.getType().isAssignableFrom(Boolean.class)) {

				boolean val = false;
				field.get(val);

				values.add(Boolean.toString(val));

			}

			fieldValueDTO.setFieldValue(values);

		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return fieldValueDTO;
	}
}
