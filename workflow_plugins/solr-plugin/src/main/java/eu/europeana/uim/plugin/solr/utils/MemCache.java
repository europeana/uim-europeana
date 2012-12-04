package eu.europeana.uim.plugin.solr.utils;

import java.util.HashMap;
import java.util.Map;

import eu.europeana.corelib.dereference.impl.EntityImpl;

public class MemCache {

	private static Map<String,EntityImpl> memcache;
	private static MemCache mCache;
	private MemCache(){
		
	}
	
	public static MemCache getInstance(){
		if(mCache == null){
			mCache = new MemCache();
			memcache = new HashMap<String,EntityImpl>();
			
		}
		return mCache;
	}

	public  Map<String, EntityImpl> getMemcache() {
		return memcache;
	}

	public  void setMemcache(Map<String, EntityImpl> memcache) {
		MemCache.memcache = memcache;
	}
	
	
}
