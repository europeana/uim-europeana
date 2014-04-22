package eu.europeana.uim.enrichment.utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.entity.ProxyImpl;

public class EuropeanaProxyUtils {
	
	public static ProxyImpl getProviderProxy(FullBeanImpl fbean) {
		for (ProxyImpl proxy : fbean.getProxies()) {
			if (!proxy.isEuropeanaProxy()) {
				return proxy;
			}
		}
		return null;
	}

	public static ProxyImpl getEuropeanaProxy(FullBeanImpl fbean) {
		for (ProxyImpl proxy : fbean.getProxies()) {
			if (proxy.isEuropeanaProxy()) {
				return proxy;
			}
		}
		ProxyImpl europeanaProxy = new ProxyImpl();
		europeanaProxy.setAbout("/proxy/europeana" + fbean.getAbout());
		europeanaProxy.setProxyIn(new String[] { "/aggregation/europeana"
				+ fbean.getAbout() });
		europeanaProxy.setProxyFor("/item" + fbean.getAbout());
		europeanaProxy.setEuropeanaProxy(true);
		Map<String, List<String>> yearMap = new HashMap<String, List<String>>();
		ProxyImpl providerProxy = getProviderProxy(fbean);
		yearMap.put("eur",
				new EuropeanaDateUtils().createEuropeanaYears(providerProxy));
		europeanaProxy.setYear(yearMap);
		europeanaProxy.setEdmType(providerProxy.getEdmType());
		return europeanaProxy;
	}
}
