/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.internal.helpers;

import java.io.StringWriter;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import com.sugarcrm.sugarcrm.GetEntries;
import com.sugarcrm.sugarcrm.Login;
import com.sugarcrm.sugarcrm.ObjectFactory;
import com.sugarcrm.sugarcrm.SelectFields;
import com.sugarcrm.sugarcrm.UserAuth;


import org.apache.log4j.Logger;

/**
 * 
 * @author Georgios Markakis
 */
public class ClientUtils {

	private static org.apache.log4j.Logger LOGGER = Logger.getLogger(ClientUtils.class);

	
	
	/**
	 * @param returnObject
	 */
	public static void logMarshalledObject(Object returnObject){
		
		JAXBContext context;
		try {
			context = JAXBContext.newInstance("com.sugarcrm.sugarcrm");
			Marshaller m = context.createMarshaller();
			StringWriter writer = new StringWriter();
			m.marshal(returnObject, writer);
			LOGGER.info(writer.toString());
		} catch (JAXBException e) {
	
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * 
	 * @param value
	 * @return
	 */
	public static String md5(String value){
		
		StringBuffer md5Password = new StringBuffer("0");
		
		MessageDigest mdEnc;
		try {
			mdEnc = MessageDigest.getInstance("MD5");
			mdEnc.update(value.getBytes(), 0, value.length());
			md5Password.append(new BigInteger(1, mdEnc.digest()).toString(16)); 
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} 
		
		return md5Password.toString();
	}
	
	
	
	/**
	 * @param username
	 * @param passwrd
	 * @return
	 */
	public static Login createStandardLoginObject(String username, String passwrd){
		
		ObjectFactory factory = new ObjectFactory();
		Login login = factory.createLogin();
		UserAuth user = factory.createUserAuth();

		user.setUserName(username);
		user.setPassword(md5(passwrd));
		user.setVersion("1.0");
		
		login.setApplicationName("EuropeanaSugarCRMClient");
		login.setUserAuth(user);
		
		return login;
	}
	
	
	
	
	/**
	 * @param fieldnames
	 * @return
	 */
	public static SelectFields generatePopulatedSelectFields(List<String> fieldnames){
		
		ObjectFactory factory = new ObjectFactory();		

		SelectFields selfields = factory.createSelectFields();
		StringBuffer arrayType = new StringBuffer();
		
		arrayType.append("string[");
		arrayType.append(fieldnames.size());
		arrayType.append("]");
				
		selfields.setArrayType(arrayType.toString());


		Element rootElement = null;
		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        Document document = documentBuilder.newDocument();
	        
	        for( String fieldname : fieldnames){
	        	Element element = document.createElement("item");
	    		selfields.getAnies().add(rootElement);
	    		element.appendChild(document.createTextNode(fieldname));
	    		selfields.getAnies().add(element);
	    		
	        }

            
	        
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}
		
		
		return selfields;
		
	}
	
	
}
