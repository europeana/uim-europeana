package eu.europeana.uim.enrichment.enums;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.common.SolrInputDocument;

import eu.europeana.corelib.solr.entity.ProxyImpl;
import eu.europeana.enrichment.api.external.EntityClass;

public enum EnrichmentFields {
	DC_DATE("proxy_dc_date") {
		@Override
		public List<EntityClass> getVocabularies() {
			List<EntityClass> vocs = new ArrayList<EntityClass>();
			vocs.add(EntityClass.TIMESPAN);
			return vocs;
		}

		@Override
		public void appendInDoc(SolrInputDocument doc,
				ProxyImpl europeanaProxy) {
			java.util.Collection<Object> col = doc.getFieldValues(value);
			if (col != null && europeanaProxy.getDcDate() != null) {
				col.addAll(europeanaProxy.getDcDate().get("def"));
				doc.setField(value, col);
			}

		}
	},
	DC_COVERAGE("proxy_dc_coverage") {
		@Override
		public List<EntityClass> getVocabularies() {
			List<EntityClass> vocs = new ArrayList<EntityClass>();
			vocs.add(EntityClass.PLACE);
			return vocs;
		}

		@Override
		public void appendInDoc(SolrInputDocument doc,
				ProxyImpl europeanaProxy) {
			java.util.Collection<Object> col = doc.getFieldValues(value);
			if (col != null && europeanaProxy.getDcCoverage() != null) {
				col.addAll(europeanaProxy.getDcCoverage().get("def"));
				doc.setField(value, col);
			}

		}
	},
	DC_TERMS_TEMPORAL("proxy_dcterms_temporal") {
		@Override
		public List<EntityClass> getVocabularies() {
			List<EntityClass> vocs = new ArrayList<EntityClass>();
			vocs.add(EntityClass.TIMESPAN);
			return vocs;
		}

		@Override
		public void appendInDoc(SolrInputDocument doc,
				ProxyImpl europeanaProxy) {
			java.util.Collection<Object> col = doc.getFieldValues(value);
			if (col != null && europeanaProxy.getDctermsTemporal() != null) {
				col.addAll(europeanaProxy.getDctermsTemporal().get("def"));
				doc.setField(value, col);
			}

		}
	},
	EDM_YEAR("proxy_edm_year") {
		@Override
		public List<EntityClass> getVocabularies() {
			List<EntityClass> vocs = new ArrayList<EntityClass>();
			vocs.add(EntityClass.TIMESPAN);
			return vocs;
		}

		@Override
		public void appendInDoc(SolrInputDocument doc,
				ProxyImpl europeanaProxy) {
			java.util.Collection<Object> col = doc.getFieldValues(value);
			if (col != null && europeanaProxy.getYear() != null) {
				col.addAll(europeanaProxy.getYear().get("def"));
				doc.setField(value, col);
			}

		}
	},
	DCTERMS_SPATIAL("proxy_dcterms_spatial") {
		@Override
		public List<EntityClass> getVocabularies() {
			List<EntityClass> vocs = new ArrayList<EntityClass>();
			vocs.add(EntityClass.PLACE);
			return vocs;
		}

		@Override
		public void appendInDoc(SolrInputDocument doc,
				ProxyImpl europeanaProxy) {
			java.util.Collection<Object> col = doc.getFieldValues(value);
			if (col != null && europeanaProxy.getDctermsSpatial() != null) {
				col.addAll(europeanaProxy.getDctermsSpatial().get("def"));
				doc.setField(value, col);
			}

		}
	},
	DC_TYPE("proxy_dc_type") {
		@Override
		public List<EntityClass> getVocabularies() {
			List<EntityClass> vocs = new ArrayList<EntityClass>();
			vocs.add(EntityClass.CONCEPT);
			return vocs;
		}

		@Override
		public void appendInDoc(SolrInputDocument doc,
				ProxyImpl europeanaProxy) {
			java.util.Collection<Object> col = doc.getFieldValues(value);
			if (col != null && europeanaProxy.getDcType() != null) {
				col.addAll(europeanaProxy.getDcType().get("def"));
				doc.setField(value, col);
			}

		}
	},
	DC_SUBJECT("proxy_dc_subject") {
		@Override
		public List<EntityClass> getVocabularies() {
			List<EntityClass> vocs = new ArrayList<EntityClass>();
			vocs.add(EntityClass.CONCEPT);
			return vocs;
		}

		@Override
		public void appendInDoc(SolrInputDocument doc,
				ProxyImpl europeanaProxy) {
			java.util.Collection<Object> col = doc.getFieldValues(value);
			if (col != null && europeanaProxy.getDcSubject() != null) {
				col.addAll(europeanaProxy.getDcSubject().get("def"));
				doc.setField(value, col);
			}

		}
	},
	DC_CREATOR("proxy_dc_creator") {
		@Override
		public List<EntityClass> getVocabularies() {
			List<EntityClass> vocs = new ArrayList<EntityClass>();
			vocs.add(EntityClass.AGENT);
			return vocs;
		}

		@Override
		public void appendInDoc(SolrInputDocument doc,
				ProxyImpl europeanaProxy) {
			java.util.Collection<Object> col = doc.getFieldValues(value);
			if (col != null && europeanaProxy.getDcCreator() != null) {
				col.addAll(europeanaProxy.getDcCreator().get("def"));
				doc.setField(value, col);
			}

		}
	},
	DC_CONTRIBUTOR("proxy_dc_contributor") {
		@Override
		public List<EntityClass> getVocabularies() {
			List<EntityClass> vocs = new ArrayList<EntityClass>();
			vocs.add(EntityClass.AGENT);
			return vocs;
		}

		@Override
		public void appendInDoc(SolrInputDocument doc,
				ProxyImpl europeanaProxy) {
			java.util.Collection<Object> col = doc.getFieldValues(value);
			if (col != null && europeanaProxy.getDcContributor() != null) {
				col.addAll(europeanaProxy.getDcContributor().get("def"));
				doc.setField(value, col);
			}

		}
	};

	String value;
	List<EntityClass> vocabularies;

	private EnrichmentFields(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}

	public abstract List<EntityClass> getVocabularies();

	public abstract void appendInDoc(SolrInputDocument doc,
			ProxyImpl europeanaProxy);
}
