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
import com.sugarcrm.sugarcrm.NameValueList;
import com.sugarcrm.sugarcrm.NameValue;

import org.apache.log4j.Logger;

/**
 * This Class provides auxiliary methods that can be used for the creation & instantiation
 * of SugarWSClient instances (see provided Junit tests for usage examples). 
 * 
 * @author Georgios Markakis
 */
public class ClientUtils {

	private static org.apache.log4j.Logger LOGGER = Logger.getLogger(ClientUtils.class);

	
	
	/**
	 * This method marshals the contents of a  JAXB Element and outputs the results to the
	 * Logger.  
	 * @param jaxbObject A JAXB representation of a SugarCRM SOAP Element. 
	 */
	public static void logMarshalledObject(Object jaxbObject){
		
		JAXBContext context;
		try {
			context = JAXBContext.newInstance("com.sugarcrm.sugarcrm");
			Marshaller m = context.createMarshaller();
			StringWriter writer = new StringWriter();
			m.marshal(jaxbObject, writer);
			LOGGER.info("===========================================");
			StringBuffer sb = new StringBuffer("Soap Ouput for Class: ");
			sb.append(jaxbObject.getClass().getSimpleName());
			LOGGER.info(sb.toString());
			LOGGER.info(writer.toString());
			LOGGER.info("===========================================");
		} catch (JAXBException e) {
	
			e.printStackTrace();
		}
		
	}
	
	
	
	/**
	 * Encrypts a given String into a MD5 format.
	 * @param value The string to be encrypted
	 * @return the encrypted String
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
	 * Creates a Login Soap Request given the user credentials.
	 * @param username 
	 * @param passwrd
	 * @return  a Login Soap Request Jaxb representation
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
	 * Creates a SelectFields Soap Object given a List<String> fieldnames object which sets the 
	 * fields to be retrieved.
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
	        	Element element = document.createElement("string");
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
	
	/**
	 * Creates a NameValueList Soap Element given a List<String> namevalues object which sets the 
	 * fields to be retrieved.
	 * @param fieldnames
	 * @return
	 */
	public static NameValueList generatePopulatedNameValueList(List<NameValue> namevalues){

		ObjectFactory factory = new ObjectFactory();
		NameValueList namevalueList = factory.createNameValueList();
		
		StringBuffer arrayType = new StringBuffer();
		
		arrayType.append("name_value[");
		arrayType.append(namevalues.size());
		arrayType.append("]");
				
		namevalueList.setArrayType(arrayType.toString());

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder;
		try {
			documentBuilder = documentBuilderFactory.newDocumentBuilder();
	        Document document = documentBuilder.newDocument();
	        
	        for( NameValue namevalue : namevalues){
	        	Element name_value = document.createElement("name_value");
	        	
	        	Element name = document.createElement("name");
	        	Element value = document.createElement("value");
	        	
	        	name.appendChild(document.createTextNode(namevalue.getName()));
	        	value.appendChild(document.createTextNode(namevalue.getValue()));
	        	name_value.appendChild(name);
	        	name_value.appendChild(value);
	        	
	        	namevalueList.getAnies().add(name_value);
	    		
	        }
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			return null;
		}

		return namevalueList;

	}
		
}
