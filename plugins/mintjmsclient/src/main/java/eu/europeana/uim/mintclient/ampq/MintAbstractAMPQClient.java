/*
 * Copyright 2007-2012 The Europeana Foundation
 *
 *  Licenced under the EUPL, Version 1.1 (the "Licence") and subsequent versions as approved
 *  by the European Commission;
 *  You may not use this work except in compliance with the Licence.
 * 
 *  You may obtain a copy of the Licence at:
 *  http://joinup.ec.europa.eu/software/page/eupl
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under
 *  the Licence is distributed on an "AS IS" basis, without warranties or conditions of
 *  any kind, either express or implied.
 *  See the Licence for the specific language governing permissions and limitations under
 *  the Licence.
 */
package eu.europeana.uim.mintclient.ampq;
import java.util.Properties;




/**
 * Abstract class for an AMPQ client
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 6 Mar 2012
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

	/**
	 * @return the username for the AMPQ broker
	 */
	public static String getUsername() {
		return username;
	}


	/**
	 * @return the password for the AMPQ broker
	 */
	public static String getPassword() {
		return password;
	}


	/**
	 * @return the hostname where the AMPQ broker resides
	 */
	public static String getHost() {
		return host;
	}

}
