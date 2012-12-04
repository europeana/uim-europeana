package eu.europeana.uim.plugin.solr.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MemCache {

	private static MemCache mCache;
	private static Map<String,Map<String,List>> entityCache;
	private MemCache(){
		
	}
	
	public static MemCache getInstance(){
		if(mCache == null){
			mCache = new MemCache();
			entityCache = new HashMap<String,Map<String,List>>();
			
		}
		return mCache;
	}

	

	public  Map<String, Map<String,List>> getEntityCache() {
		return entityCache;
	}

	public void setEntityCache(Map<String, Map<String,List>> entityCache) {
		MemCache.entityCache = entityCache;
	}
	
	
}
