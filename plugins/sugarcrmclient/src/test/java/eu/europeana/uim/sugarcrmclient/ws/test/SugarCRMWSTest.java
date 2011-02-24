/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.ws.test;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import java.io.StringWriter;
import java.math.*;
import java.security.*;
import java.util.ArrayList;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import eu.europeana.uim.sugarcrmclient.ws.SugarWsClient;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.annotation.XmlType;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.sugarcrm.sugarcrm.GetServerVersion;

import com.sugarcrm.sugarcrm.GetAvailableModules;
import com.sugarcrm.sugarcrm.GetAvailableModulesResponse;
import com.sugarcrm.sugarcrm.GetEntries;
import com.sugarcrm.sugarcrm.GetEntriesResponse;
import com.sugarcrm.sugarcrm.GetEntryList;
import com.sugarcrm.sugarcrm.GetEntryListResponse;
import com.sugarcrm.sugarcrm.GetEntryListResult;
import com.sugarcrm.sugarcrm.GetModuleFields;
import com.sugarcrm.sugarcrm.GetModuleFieldsResponse;
import com.sugarcrm.sugarcrm.GetUserId;
import com.sugarcrm.sugarcrm.GetUserIdResponse;
import com.sugarcrm.sugarcrm.Login;
import com.sugarcrm.sugarcrm.SelectFields;
import com.sugarcrm.sugarcrm.UserAuth;
import com.sugarcrm.sugarcrm.LoginResponse;
import com.sugarcrm.sugarcrm.IsUserAdmin;
import com.sugarcrm.sugarcrm.IsUserAdminResponse;

import com.sugarcrm.sugarcrm.ObjectFactory;
import org.w3c.dom.Element;
import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
/**
 * 
 * 
 * @author geomark
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-context.xml"})
public final class SugarCRMWSTest {

	@Resource
	private SugarWsClient sugarWsClient; 
	
	private String sessionID;

	
	
	@Before 
	public void setupSession(){
		LoginResponse lresponse =  sugarWsClient.login2(ClientUtils.createStandardLoginObject("test", "test"));
		sessionID = lresponse.getReturn().getId();
	}
	


	@Test
	public void testLogin(){
		
		LoginResponse response =  sugarWsClient.login2(ClientUtils.createStandardLoginObject("test", "test"));
		System.out.println(response.getReturn().getId());
		ClientUtils.logMarshalledObject(response);
	}
	
	
	@Test
	public void testIsUserAdmin(){
		
		ObjectFactory factory = new ObjectFactory();		
		IsUserAdmin user = factory.createIsUserAdmin();		
		user.setSession(sessionID);		
		IsUserAdminResponse response = sugarWsClient.is_user_admin(user);		
		ClientUtils.logMarshalledObject(response);
	}
	
	
	@Test
	public void testGetUserID(){	 
		ObjectFactory factory = new ObjectFactory();		
		GetUserId request = factory.createGetUserId();
		request.setSession(sessionID);
		ClientUtils.logMarshalledObject(request);
		GetUserIdResponse response =  sugarWsClient.get_user_id(request);
		ClientUtils.logMarshalledObject(response);
	}
	
	//@Test
	public void simpleTest(){
		
		String result = sugarWsClient.test();
		System.out.println(result);
	}
	
	
	@Test
	public void testGetAvailableModules(){	 
		ObjectFactory factory = new ObjectFactory();		
		GetAvailableModules request = factory.createGetAvailableModules();
		request.setSession(sessionID);
		ClientUtils.logMarshalledObject(request);
		GetAvailableModulesResponse response =  sugarWsClient.get_available_modules(request);
		
		ClientUtils.logMarshalledObject(response);
	}

	@Test
	public void testGetModuleFields(){	 
		ObjectFactory factory = new ObjectFactory();		
		GetModuleFields request = factory.createGetModuleFields();
		request.setSession(sessionID);
		request.setModuleName("Contacts");
		ClientUtils.logMarshalledObject(request);
		GetModuleFieldsResponse response =  sugarWsClient.get_module_fields(request);
		
		ClientUtils.logMarshalledObject(response);
	}
	
	@Test
	public void testGetEntryList(){
		
		ObjectFactory factory = new ObjectFactory();		
		GetEntryList request = factory.createGetEntryList();
		
		ArrayList <String> fieldnames = new  ArrayList<String>();
		
		fieldnames.add("id");
		fieldnames.add("first_name");
		fieldnames.add("last_name");
		
		fieldnames.add("salutation");
		
		SelectFields fields = ClientUtils.generatePopulatedSelectFields(fieldnames);
		
  		request.setModuleName("Contacts");	
		//request.setSelectFields(fields);
		request.setSession(sessionID);
		request.setOrderBy("last_name");
		request.setMaxResults(100);
		//request.setOffset(10);
		//request.setQuery("(contacts.salutation = 'Mr.' AND contacts.title LIKE 'doctor appointment%')");
		request.setQuery("(contacts.first_name LIKE '%M%')");
		
		ClientUtils.logMarshalledObject(request);
		GetEntryListResponse response =  sugarWsClient.get_entry_list(request);
		ClientUtils.logMarshalledObject(response);
		
	}
	
	
	@Test
	public void testGetEntries(){	 
		
		ObjectFactory factory = new ObjectFactory();		
		GetEntries request = factory.createGetEntries();

		ArrayList <String> fieldnames = new  ArrayList<String>();
		
		fieldnames.add("id");
		fieldnames.add("first_name");
		fieldnames.add("last_name");
		
		fieldnames.add("salutation");
		
		SelectFields fields = ClientUtils.generatePopulatedSelectFields(fieldnames);

  		request.setModuleName("Contacts");	
		request.setSelectFields(fields);
		request.setSession(sessionID);		
		

		ClientUtils.logMarshalledObject(request);
		GetEntriesResponse response =  sugarWsClient.get_entries(request);
		ClientUtils.logMarshalledObject(response);
	}
	
		
	}

