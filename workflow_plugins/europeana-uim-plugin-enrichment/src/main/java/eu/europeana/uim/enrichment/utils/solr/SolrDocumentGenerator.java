package eu.europeana.uim.enrichment.utils.solr;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.common.SolrInputDocument;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.map.JsonMappingException;

import eu.europeana.corelib.definitions.model.EdmLabel;
import eu.europeana.corelib.definitions.solr.beans.FullBean;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.entity.AgentImpl;
import eu.europeana.corelib.solr.entity.AggregationImpl;
import eu.europeana.corelib.solr.entity.ConceptImpl;
import eu.europeana.corelib.solr.entity.PlaceImpl;
import eu.europeana.corelib.solr.entity.ProxyImpl;
import eu.europeana.corelib.solr.entity.TimespanImpl;
import eu.europeana.uim.enrichment.enums.EnrichmentFields;
import eu.europeana.uim.enrichment.utils.EuropeanaProxyUtils;
import eu.europeana.uim.enrichment.utils.RetrievedEntity;

public class SolrDocumentGenerator {

	public void updateProviderProxyInSolr(FullBeanImpl fBean,
			SolrInputDocument basicDocument) {
		int i = 0;
		ProxyImpl proxy = new ProxyImpl();
		int index = 0;
		for (ProxyImpl proxyIn : fBean.getProxies()) {
			if (!proxyIn.isEuropeanaProxy()) {
				proxy = proxyIn;
				index = i;
			}
			i++;
		}

		proxy.setAbout("/proxy/provider" + fBean.getAbout());
		proxy.setProxyFor("/item" + fBean.getAbout());
		proxy.setProxyIn(new String[] { "/aggregation/provider"
				+ fBean.getAbout() });
		fBean.getProxies().set(index, proxy);
		basicDocument.setField(EdmLabel.ORE_PROXY.toString(), proxy.getAbout());
		basicDocument.setField(EdmLabel.PROXY_ORE_PROXY_FOR.toString(),
				proxy.getProxyFor());
		basicDocument.setField(EdmLabel.PROXY_ORE_PROXY_IN.toString(),
				proxy.getProxyIn());

	}

	public void updateEuropeanaProxyInSolr(FullBeanImpl fBean,
			SolrInputDocument basicDocument) {
		ProxyImpl proxy = EuropeanaProxyUtils.getEuropeanaProxy(fBean);
		basicDocument.setField(EdmLabel.ORE_PROXY.toString(), proxy.getAbout());
		basicDocument.setField(EdmLabel.PROXY_ORE_PROXY_FOR.toString(),
				proxy.getProxyFor());
		basicDocument.setField(EdmLabel.PROXY_ORE_PROXY_IN.toString(),
				proxy.getProxyIn());
		basicDocument.setField(EdmLabel.EDM_ISEUROPEANA_PROXY.toString(),
				proxy.isEuropeanaProxy());
		for (EnrichmentFields field : EnrichmentFields.values()) {
			field.appendInDoc(basicDocument, proxy);
		}

	}

	public void updateProviderAggregationInSolr(FullBeanImpl fBean,
			SolrInputDocument basicDocument) {
		AggregationImpl ag = fBean.getAggregations().get(0);
		ag.setAbout("/aggregation/provider" + fBean.getAbout());
		ag.setAggregatedCHO("/item" + fBean.getAbout());
		basicDocument.setField(
				EdmLabel.PROVIDER_AGGREGATION_ORE_AGGREGATION.toString(),
				"/aggregation/provider" + fBean.getAbout());
		basicDocument.setField(
				EdmLabel.PROVIDER_AGGREGATION_EDM_AGGREGATED_CHO.toString(),
				"/item" + fBean.getAbout());
		fBean.getAggregations().set(0, ag);

	}

	public void addEntities(SolrInputDocument basicDocument, FullBean fBean,
			ProxyImpl europeanaProxy, List<RetrievedEntity> enrichedEntities)
			throws JsonParseException, JsonMappingException, IOException {

		for (RetrievedEntity enrichedEntity : enrichedEntities) {
			if (enrichedEntity.getEntity().getClass().getName().equals(
					ConceptImpl.class.getName())) {
				appendConcept(basicDocument, fBean, europeanaProxy,
						enrichedEntity);
			} else if (enrichedEntity.getEntity().getClass().getName().equals(
					AgentImpl.class.getName())) {
				appendAgent(basicDocument, fBean, europeanaProxy,
						enrichedEntity);
			} else if (enrichedEntity.getEntity().getClass().getName().equals(
					PlaceImpl.class.getName())) {
				appendPlace(basicDocument, fBean, europeanaProxy,
						enrichedEntity);
			} else if (enrichedEntity.getEntity().getClass().getName().equals(
					TimespanImpl.class.getName())) {
				appendTimespan(basicDocument, fBean, europeanaProxy,
						enrichedEntity);
			}
		}

	}

	private void appendTimespan(SolrInputDocument basicDocument,
			FullBean fBean, ProxyImpl europeanaProxy,
			RetrievedEntity enrichedEntity) throws JsonParseException,
			JsonMappingException, IOException {
		TimespanImpl ts =(TimespanImpl) enrichedEntity.getEntity();
		List<TimespanImpl> tsList = (List<TimespanImpl>) fBean.getTimespans();
		if (tsList == null) {
			tsList = new ArrayList<TimespanImpl>();
		}
		boolean isContained = false;
		for (TimespanImpl contained : tsList) {
			if (contained.getAbout().equals(ts.getAbout())) {
				isContained = true;
			}
		}
		if (!isContained) {
			tsList.add(ts);
		}
		fBean.setTimespans(tsList);
		if (enrichedEntity.getOriginalField() != null) {

			if (enrichedEntity.getOriginalField().equals(
					"proxy_dcterms_temporal")) {
				Map<String, List<String>> map = europeanaProxy
						.getDctermsTemporal();
				List<String> values;
				if (map == null) {
					map = new HashMap<String, List<String>>();
					values = new ArrayList<String>();

				} else {
					values = map.get("def");
				}
				if (!values.contains(ts.getAbout())) {
					values.add(ts.getAbout());
				}
				map.put("def", values);
				europeanaProxy.setDctermsTemporal(map);
			} else if (enrichedEntity.getOriginalField()
					.equals("proxy_dc_date")) {
				Map<String, List<String>> map = europeanaProxy.getDcDate();
				List<String> values;
				if (map == null) {
					map = new HashMap<String, List<String>>();
					values = new ArrayList<String>();

				} else {
					values = map.get("def");
				}
				if (!values.contains(ts.getAbout())) {
					values.add(ts.getAbout());
				}
				map.put("def", values);
				europeanaProxy.setDcDate(map);
			} else if (enrichedEntity.getOriginalField().equals(
					"proxy_edm_year")) {
				Map<String, List<String>> map = europeanaProxy.getYear();
				List<String> values;
				if (map == null) {
					map = new HashMap<String, List<String>>();
					values = new ArrayList<String>();

				} else {
					values = map.get("eur");
				}
				if (!values.contains(ts.getAbout())) {
					values.add(ts.getAbout());
				}
				map.put("eur", values);
				europeanaProxy.setYear(map);
			}

		}
		new TimespanSolrCreator().create(basicDocument, ts);

	}

	private void appendPlace(SolrInputDocument basicDocument, FullBean fBean,
			ProxyImpl europeanaProxy, RetrievedEntity enrichedEntity)
			throws JsonParseException, JsonMappingException, IOException {
		PlaceImpl place = (PlaceImpl)enrichedEntity.getEntity();

		List<PlaceImpl> placeList = (List<PlaceImpl>) fBean.getPlaces();
		if (placeList == null) {
			placeList = new ArrayList<PlaceImpl>();
		}
		boolean isContained = false;
		for (PlaceImpl contained : placeList) {
			if (contained.getAbout().equals(place.getAbout())) {
				isContained = true;
			}
		}
		if (!isContained) {
			placeList.add(place);
		}
		fBean.setPlaces(placeList);
		if (enrichedEntity.getOriginalField() != null) {

			if (enrichedEntity.getOriginalField().equals(
					"proxy_dcterms_spatial")) {
				Map<String, List<String>> map = europeanaProxy
						.getDctermsSpatial();
				List<String> values;
				if (map == null) {
					map = new HashMap<String, List<String>>();
					values = new ArrayList<String>();

				} else {
					values = map.get("def");
				}
				if (!values.contains(place.getAbout())) {
					values.add(place.getAbout());
				}
				map.put("def", values);
				europeanaProxy.setDctermsSpatial(map);
			} else if (enrichedEntity.getOriginalField().equals(
					"proxy_dc_coverage")) {
				Map<String, List<String>> map = europeanaProxy.getDcCoverage();
				List<String> values;
				if (map == null) {
					map = new HashMap<String, List<String>>();
					values = new ArrayList<String>();

				} else {
					values = map.get("def");
				}
				if (!values.contains(place.getAbout())) {
					values.add(place.getAbout());
				}
				map.put("def", values);
				europeanaProxy.setDcCoverage(map);
			}

		}
		new PlaceSolrCreator().create(basicDocument, place);

	}

	private void appendAgent(SolrInputDocument basicDocument, FullBean fBean,
			ProxyImpl europeanaProxy, RetrievedEntity enrichedEntity)
			throws JsonParseException, JsonMappingException, IOException {
		AgentImpl agent = (AgentImpl) enrichedEntity.getEntity();

		List<AgentImpl> agentList = (List<AgentImpl>) fBean.getAgents();
		if (agentList == null) {
			agentList = new ArrayList<AgentImpl>();
		}
		boolean isContained = false;
		for (AgentImpl contained : agentList) {
			if (contained.getAbout().equals(agent.getAbout())) {
				isContained = true;
			}
		}
		if (!isContained) {
			agentList.add(agent);
		}
		fBean.setAgents(agentList);
		if (enrichedEntity.getOriginalField() != null) {

			if (enrichedEntity.getOriginalField().equals("proxy_dc_creator")) {
				Map<String, List<String>> map = europeanaProxy.getDcCreator();
				List<String> values;
				if (map == null) {
					map = new HashMap<String, List<String>>();
					values = new ArrayList<String>();

				} else {
					values = map.get("def");
				}
				if (!values.contains(agent.getAbout())) {
					values.add(agent.getAbout());
				}
				map.put("def", values);
				europeanaProxy.setDcCreator(map);
			} else if (enrichedEntity.getOriginalField().equals(
					"proxy_dc_contributor")) {
				Map<String, List<String>> map = europeanaProxy
						.getDcContributor();
				List<String> values;
				if (map == null) {
					map = new HashMap<String, List<String>>();
					values = new ArrayList<String>();

				} else {
					values = map.get("def");
				}
				if (!values.contains(agent.getAbout())) {
					values.add(agent.getAbout());
				}
				map.put("def", values);
				europeanaProxy.setDcContributor(map);
			}

		}
		new AgentSolrCreator().create(basicDocument, agent);

	}

	private void appendConcept(SolrInputDocument basicDocument, FullBean fBean,
			ProxyImpl europeanaProxy, RetrievedEntity enrichedEntity)
			throws JsonParseException, JsonMappingException, IOException {
		ConceptImpl concept = (ConceptImpl)enrichedEntity.getEntity();

		List<ConceptImpl> conceptList = (List<ConceptImpl>) fBean.getConcepts();
		if (conceptList == null) {
			conceptList = new ArrayList<ConceptImpl>();
		}
		boolean isContained = false;
		for (ConceptImpl contained : conceptList) {
			if (contained.getAbout().equals(concept.getAbout())) {
				isContained = true;
			}
		}
		if (!isContained) {
			conceptList.add(concept);
		}
		fBean.setConcepts(conceptList);
		if (enrichedEntity.getOriginalField() != null) {

			if (enrichedEntity.getOriginalField().equals("proxy_dc_subject")) {
				Map<String, List<String>> map = europeanaProxy.getDcSubject();
				List<String> values;
				if (map == null) {
					map = new HashMap<String, List<String>>();
					values = new ArrayList<String>();

				} else {
					values = map.get("def");
				}
				if (!values.contains(concept.getAbout())) {
					values.add(concept.getAbout());
				}
				map.put("def", values);
				europeanaProxy.setDcSubject(map);
			} else if (enrichedEntity.getOriginalField()
					.equals("proxy_dc_type")) {
				Map<String, List<String>> map = europeanaProxy.getDcType();
				List<String> values;
				if (map == null) {
					map = new HashMap<String, List<String>>();
					values = new ArrayList<String>();

				} else {
					values = map.get("def");
				}
				if (!values.contains(concept.getAbout())) {
					values.add(concept.getAbout());
				}
				map.put("def", values);
				europeanaProxy.setDcType(map);
			}

		}
		new ConceptSolrCreator().create(basicDocument, concept);
	}

}
