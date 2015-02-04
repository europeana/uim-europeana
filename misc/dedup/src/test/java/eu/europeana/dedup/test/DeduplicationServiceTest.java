package eu.europeana.dedup.test;

import java.net.UnknownHostException;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.mongodb.Mongo;

import eu.europeana.corelib.lookup.impl.EuropeanaIdRegistryMongoServerImpl;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdRegistry;
import eu.europeana.corelib.tools.lookuptable.EuropeanaIdRegistryMongoServer;
import eu.europeana.corelib.tools.lookuptable.LookupState;
import eu.europeana.dedup.osgi.service.DeduplicationService;
import eu.europeana.dedup.osgi.service.DeduplicationServiceImpl;
import eu.europeana.dedup.utils.PropertyReader;
import eu.europeana.dedup.utils.UimConfigurationProperty;

public class DeduplicationServiceTest {

	private DeduplicationService serv;
	private EuropeanaIdRegistryMongoServer server;

	@Before
	public void prepare() {
		PropertyReader red = new PropertyReader();
		red.loadPropertiesFromFile("src/test/resources/uim.properties");
		int port = Integer.parseInt(red
				.getProperty(UimConfigurationProperty.MONGO_HOSTPORT));
		MongoProvider.start(port);
		try {
			server = new EuropeanaIdRegistryMongoServerImpl(
					new Mongo(
							red.getProperty(UimConfigurationProperty.MONGO_HOSTURL),
							port),
					red.getProperty(UimConfigurationProperty.MONGO_DB_EUROPEANAIDREGISTRY));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		serv = new DeduplicationServiceImpl();
	}

	@Test
	public void test() {
		server.createFailedRecord(LookupState.ID_REGISTERED, "test", "test",
				"/test/test", "samplexml");
		List<Map<String, String>> recList = serv.getFailedRecords("test");
		Assert.assertEquals(1, serv.getFailedRecords("test").size());
		Assert.assertEquals(recList.get(0).get("edm"), "samplexml");
		Assert.assertEquals(recList.get(0).get("collectionId"), "test");
		Assert.assertEquals(recList.get(0).get("originalId"), "test");
		Assert.assertEquals(recList.get(0).get("lookupState"),
				LookupState.ID_REGISTERED.toString());
		Assert.assertEquals(recList.get(0).get("europeanaId"), "/test/test");
		Assert.assertEquals(0, serv.getFailedRecords("test2").size());
		serv.createUpdateIdStatus("/test/test", "/test/test2", "test",
				"samplexml", LookupState.UPDATE);
		Assert.assertEquals(2, serv.getFailedRecords("test").size());
		List<Map<String, String>> recs = serv.getFailedRecords("test");
		for (Map<String, String> map : recs) {
			for (Entry<String, String> entry : map.entrySet()) {
				System.out.println(entry.getKey() + " : " + entry.getValue());
			}
		}
		EuropeanaIdRegistry id = new EuropeanaIdRegistry();
		id.setCid("test");
		id.setEid("/test/test");
		id.setOrid("test");
		id.setDeleted(false);
		server.getDatastore().save(id);
		Assert.assertFalse(serv.isdeleted("/test/test"));

		List<String> str = serv.retrieveEuropeanaIDFromOld("test", "test");
		Assert.assertEquals(1, str.size());
		List<String> str2 = serv.retrieveEuropeanaIDFromOld("test5", "test");
		Assert.assertEquals(0, str2.size());
		serv.markdeleted("/test/test", true);
		Assert.assertTrue(serv.isdeleted("/test/test"));
		serv.markdeleted("/test/test", false);
		Assert.assertFalse(serv.isdeleted("/test/test"));
		// TODO: testing deleteEuropeanaId makes no sense in this context as
		// EuropeanaId is for the redirects

		serv.deleteFailedRecord("/test/test", "test");
		List<Map<String, String>> recs2 = serv.getFailedRecords("test");
		Assert.assertEquals(1, recs2.size());
		serv.deleteFailedRecords("test");
		recs2 = serv.getFailedRecords("test");
		Assert.assertEquals(0, recs2.size());
	}

	@After
	public void destroy() {
		MongoProvider.stop();
	}
}
