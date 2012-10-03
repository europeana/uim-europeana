package eu.europeana.enrichment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

import eu.europeana.corelib.definitions.solr.beans.FullBean;
import eu.europeana.corelib.definitions.solr.entity.Proxy;
import eu.europeana.corelib.solr.bean.impl.FullBeanImpl;
import eu.europeana.corelib.solr.entity.ProxyImpl;
import eu.europeana.uim.enrichment.utils.EuropeanaDateUtils;

public class EuropeanaDateUtilsTest {

	@Test
	public void testUtils(){
		FullBean fb = new FullBeanImpl();
		List<Proxy> proxies = new ArrayList<Proxy>();
		Proxy proxy = new ProxyImpl();
		Map<String,List<String>> dcDate = new HashMap<String,List<String>>();
		List<String> dcDateList = new ArrayList<String>();
		String dcDateString1 = "-150 / -70"; //should give -150 and -70
		String dcDateString2 = "75/140 AD"; //should give 75 and 140
		String dcDateString3 = "circa 1500 BC"; //should give -1500
		dcDateList.add(dcDateString1);
		dcDateList.add(dcDateString2);
		dcDateList.add(dcDateString3);
		dcDate.put("def", dcDateList);
		proxy.setDcDate(dcDate);
		proxies.add(proxy);
		fb.setProxies(proxies);
		List<String> dates = EuropeanaDateUtils.createEuropeanaYears(fb);
		Assert.assertTrue(dates.size()==5);
		Assert.assertTrue(dates.contains("-150"));
		Assert.assertTrue(dates.contains("-70"));
		Assert.assertTrue(dates.contains("75"));
		Assert.assertTrue(dates.contains("140"));
		Assert.assertTrue(dates.contains("-1500"));
		
	}
}
