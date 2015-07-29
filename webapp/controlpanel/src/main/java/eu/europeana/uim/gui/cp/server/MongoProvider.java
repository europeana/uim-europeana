package eu.europeana.uim.gui.cp.server;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import com.mongodb.Mongo;
import com.mongodb.ServerAddress;

import eu.europeana.uim.gui.cp.server.util.PropertyReader;
import eu.europeana.uim.gui.cp.server.util.UimConfigurationProperty;

public class MongoProvider {
  private static Mongo tgtMongo;

  public static Mongo getMongo() {
    if (tgtMongo == null) {
      List<ServerAddress> addresses = new ArrayList<ServerAddress>();
      String[] mongoHost =
          PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTURL).split(",");
      String mongoPort = PropertyReader.getProperty(UimConfigurationProperty.MONGO_HOSTPORT);
      for (String mongoStr : mongoHost) {
        ServerAddress address = null;
        try {
          address = new ServerAddress(mongoStr, Integer.parseInt(mongoPort));
        } catch (UnknownHostException e) {
          e.printStackTrace();
        }
        addresses.add(address);
      }
      tgtMongo = new Mongo(addresses);
    }

    return tgtMongo;
  }
}
