package eu.europeana.dedup.test;

import de.flapdoodle.embed.mongo.MongodExecutable;
import de.flapdoodle.embed.mongo.MongodStarter;
import de.flapdoodle.embed.mongo.config.IMongodConfig;
import de.flapdoodle.embed.mongo.config.MongodConfigBuilder;
import de.flapdoodle.embed.mongo.config.Net;
import de.flapdoodle.embed.mongo.distribution.Version;
import de.flapdoodle.embed.process.runtime.Network;

public class MongoProvider {
	private static MongodExecutable mongodExecutable;
	public static void start(int port){
		try {
		IMongodConfig conf = new MongodConfigBuilder().version(Version.Main.PRODUCTION)
		        .net(new Net(port, Network.localhostIsIPv6()))
		        .build();

		MongodStarter runtime = MongodStarter.getDefaultInstance();

		mongodExecutable = runtime.prepare(conf);
		mongodExecutable.start();
		}
		catch (Exception e){
			e.printStackTrace();
		}
		
	}
	
	public static void stop(){
		mongodExecutable.stop();
	}
}
