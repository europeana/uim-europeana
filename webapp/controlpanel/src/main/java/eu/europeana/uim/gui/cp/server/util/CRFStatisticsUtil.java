package eu.europeana.uim.gui.cp.server.util;

import java.util.ArrayList;
import java.util.List;

import com.google.code.morphia.Datastore;
import com.google.code.morphia.Morphia;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoException;
import com.mongodb.ServerAddress;

/**
 * 
 * @author Alena Fedasenka
 *
 */
public class CRFStatisticsUtil {
	
	private static String[] addresses = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_HOSTURL).split(",");
	
	private static int port = Integer.parseInt(PropertyReader.getProperty(UimConfigurationProperty.CLIENT_HOSTPORT));

	private static String db = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_DB);

	private static String user = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_USERNAME);
	
	private static String password = PropertyReader.getProperty(UimConfigurationProperty.CLIENT_PASSWORD);
	
	public static Datastore getMongo() {
		try {
			List<ServerAddress> serverAddresses = new ArrayList<ServerAddress>();
			for (String address : addresses) {
				serverAddresses.add(new ServerAddress(address, port));
			}
			Mongo mongo = new MongoClient(serverAddresses);
			Morphia morphia = new Morphia();
			boolean auth = mongo.getDB("admin").authenticate(user, password.toCharArray());
			if (!auth) {
				throw new MongoException("ERROR: Couldn't authenticate the admin-user against admin-db");
			}
			 return morphia.createDatastore(mongo, db);
		} catch (MongoException e) {
			e.printStackTrace();
		} catch (Exception any) {
			any.printStackTrace();
		}
		return null;
	}
}
