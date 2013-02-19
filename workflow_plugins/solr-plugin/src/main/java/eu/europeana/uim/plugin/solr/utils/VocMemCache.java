package eu.europeana.uim.plugin.solr.utils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.corelib.dereference.impl.ControlledVocabularyImpl;
import eu.europeana.uim.plugin.solr.service.SolrWorkflowService;

public class VocMemCache {

	private static Map<String, List<ControlledVocabularyImpl>> vocMemCache;
	
	public static  Map<String, List<ControlledVocabularyImpl>> getMemCache(SolrWorkflowService solrWorkflowService){
		if(vocMemCache==null){
			OsgiExtractor extractor = solrWorkflowService.getExtractor();

			List<ControlledVocabularyImpl> vocs = extractor
					.getControlledVocabularies(solrWorkflowService.getDatastore());
			vocMemCache = new HashMap<String, List<ControlledVocabularyImpl>>();
			List<ControlledVocabularyImpl> vocsInMap;
			for (ControlledVocabularyImpl voc : vocs) {
				if (vocMemCache.containsKey(voc.getURI())) {
					vocsInMap = vocMemCache.get(voc.getURI());
				} else {
					vocsInMap = new ArrayList<ControlledVocabularyImpl>();
				}
				vocsInMap.add(voc);
				vocMemCache.put(voc.getURI(), vocsInMap);
			}
		}
		return vocMemCache;
	}
}
