/**
 * 
 */
package eu.europeana.uim.mintclient.ampq;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;



/**
 *
 * @author Georgios Markakis
 */
public abstract class MintAbstractAMPQClient {

	private final static  Properties props = new Properties();
	private static  String username;
	private static String password;
	private static String host;
	
	
	static {
		
		username = "guest";
		password = "guest";
		host = "panic.image.ntua.gr";
		/*
		try {

			URL url = ClassLoader.getSystemResource("mint.properties");
			props.load(url.openStream());
			username = props.getProperty("username");
			password = props.getProperty("password");
			host = props.getProperty("host");
			
			
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
	}
	
	protected MintAbstractAMPQClient(){

	}

	public static String getUsername() {
		return username;
	}


	public static String getPassword() {
		return password;
	}


	public static String getHost() {
		return host;
	}

}
