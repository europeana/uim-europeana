package eu.europeana.dedup.osgi.service;

import java.util.List;
import java.util.Map;

import eu.europeana.uim.common.BlockingInitializer;

public abstract class ExtendedBlockingInitializer extends BlockingInitializer {

	String str;
	List<Map<String,String>> list;
	public ExtendedBlockingInitializer(){
		super();
	}
	
	public ExtendedBlockingInitializer(String str, List<Map<String,String>> list){
	super();	
		this.str = str;
		this.list = list;
		
	}

public List<Map<String,String>> getList(){
	return list;
}
}
