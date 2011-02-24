/**
 * 
 */
package eu.europeana.uim.sugarcrmclient.ws.test;

import static org.junit.Assert.*;

import javax.annotation.Resource;


import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.test.annotation.*;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import eu.europeana.uim.sugarcrmclient.ws.SugarWsClient;



import com.sugarcrm.sugarcrm.GetServerVersion;

import com.sugarcrm.sugarcrm.GetAvailableModules;
import com.sugarcrm.sugarcrm.GetAvailableModulesResponse;
import com.sugarcrm.sugarcrm.GetEntries;
import com.sugarcrm.sugarcrm.GetEntriesResponse;
import com.sugarcrm.sugarcrm.GetEntry;
import com.sugarcrm.sugarcrm.GetEntryList;
import com.sugarcrm.sugarcrm.GetEntryListResponse;
import com.sugarcrm.sugarcrm.GetEntryListResult;
import com.sugarcrm.sugarcrm.GetEntryResponse;
import com.sugarcrm.sugarcrm.GetModuleFields;
import com.sugarcrm.sugarcrm.GetModuleFieldsResponse;
import com.sugarcrm.sugarcrm.GetUserId;
import com.sugarcrm.sugarcrm.GetUserIdResponse;
import com.sugarcrm.sugarcrm.Login;
import com.sugarcrm.sugarcrm.Logout;
import com.sugarcrm.sugarcrm.LogoutResponse;
import com.sugarcrm.sugarcrm.NameValueList;
import com.sugarcrm.sugarcrm.SelectFields;
import com.sugarcrm.sugarcrm.SetEntry;
import com.sugarcrm.sugarcrm.SetEntryResponse;
import com.sugarcrm.sugarcrm.UserAuth;
import com.sugarcrm.sugarcrm.LoginResponse;
import com.sugarcrm.sugarcrm.IsUserAdmin;
import com.sugarcrm.sugarcrm.IsUserAdminResponse;

import com.sugarcrm.sugarcrm.ObjectFactory;
import eu.europeana.uim.sugarcrmclient.internal.helpers.ClientUtils;
import org.apache.log4j.Logger;


/**
 * 
 * 
 * @author Georgios Markakis
 */

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:test-context.xml"})
public final class SugarCRMWSTest {

	@Resource
	private SugarWsClient sugarWsClient; 
	
	private String sessionID;

	private ObjectFactory factory = new ObjectFactory();	
	
	private static org.apache.log4j.Logger LOGGER = Logger.getLogger(SugarCRMWSTest.class);
	
	/**
	 *  Method invoked before test execution. It performs the initial login in order for allow permission to the 
	 *  subsequent web service calls. It also sets the session id for this test run. 
	 */
	@Before 
	public void setupSession(){
		
		LOGGER.info("****Setting Up Session****");
		
		LoginResponse lresponse =  sugarWsClient.login2(ClientUtils.createStandardLoginObject("test", "test"));
		sessionID = lresponse.getReturn().getId();
	}
	
	
	/**
	 * Method invoked after all tests have been executed. It destroys the current session. 
	 */
	@After
	public void destroySession(){
		
		Logout request = factory.createLogout();
		request.setSession(sessionID);
		LogoutResponse lgresponse =  sugarWsClient.logout(request );
		LOGGER.info("****Destroyed Session****");
		ClientUtils.logMarshalledObject(lgresponse);
	}


	@Test
	
	public void testLogin(){
		
		LoginResponse response =  sugarWsClient.login2(ClientUtils.createStandardLoginObject("test", "test"));

		LOGGER.info(response.getReturn().getId());
		
		ClientUtils.logMarshalledObject(response);
	}
	
	
	@Test
	public void testIsUserAdmin(){	
		IsUserAdmin user = factory.createIsUserAdmin();		
		user.setSession(sessionID);		
		IsUserAdminResponse response = sugarWsClient.is_user_admin(user);		
		ClientUtils.logMarshalledObject(response);
	}
	
	
	@Test
	public void testGetUserID(){	 
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
		GetAvailableModules request = factory.createGetAvailableModules();
		request.setSession(sessionID);
		ClientUtils.logMarshalledObject(request);
		GetAvailableModulesResponse response =  sugarWsClient.get_available_modules(request);
		
		ClientUtils.logMarshalledObject(response);
	}

	@Test
	public void testGetModuleFields(){	 		
		GetModuleFields request = factory.createGetModuleFields();
		request.setSession(sessionID);
		request.setModuleName("Contacts");
		ClientUtils.logMarshalledObject(request);
		GetModuleFieldsResponse response =  sugarWsClient.get_module_fields(request);
		
		ClientUtils.logMarshalledObject(response);
	}
	
	@Test
	public void testGetEntryList(){		
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
	
	
	
	/**
	 * 
	 */
	@Test
	public void testGetEntry(){
		GetEntry request = factory.createGetEntry();
		request.setId("1c3a03dd-753b-7741-92e8-4d6509d442d3");
		request.setModuleName("Contacts");
		request.setSession(sessionID);	
		ClientUtils.logMarshalledObject(request);
		GetEntryResponse response =  sugarWsClient.get_entry(request);
		ClientUtils.logMarshalledObject(response);
				
	}
	
	/**
	 * Test the SetEntry Operation:
	 */
	@Test
	public void testSetEntry(){
		SetEntry request = factory.createSetEntry();
		NameValueList value = factory.createNameValueList();
		
		value.setId("1c3a03dd-753b-7741-92e8-4d6509d442d3");
		
		request.setNameValueList(value);
		request.setModuleName("Contacts");
		request.setSession(sessionID);	
		ClientUtils.logMarshalledObject(request);
		SetEntryResponse response =  sugarWsClient.set_entry(request);
		ClientUtils.logMarshalledObject(response);
				
	}
	
	
	
		
	}

