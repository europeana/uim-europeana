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
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


import eu.europeana.uim.sugarcrmclient.ws.SugarWsClient;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LoginFailureException;


import com.sugarcrm.sugarcrm.GetAvailableModules;
import com.sugarcrm.sugarcrm.GetAvailableModulesResponse;
import com.sugarcrm.sugarcrm.GetEntries;
import com.sugarcrm.sugarcrm.GetEntriesResponse;
import com.sugarcrm.sugarcrm.GetEntry;
import com.sugarcrm.sugarcrm.GetEntryList;
import com.sugarcrm.sugarcrm.GetEntryListResponse;
import com.sugarcrm.sugarcrm.GetEntryResponse;
import com.sugarcrm.sugarcrm.GetModuleFields;
import com.sugarcrm.sugarcrm.GetModuleFieldsResponse;
import com.sugarcrm.sugarcrm.GetUserId;
import com.sugarcrm.sugarcrm.GetUserIdResponse;
import com.sugarcrm.sugarcrm.Login;
import com.sugarcrm.sugarcrm.Logout;
import com.sugarcrm.sugarcrm.LogoutResponse;
import com.sugarcrm.sugarcrm.NameValue;
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
 * Class implementing Unit Tests for SugarWsClient
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
	 *  Method invoked before each test execution. It performs the initial login in order for allow permission to the 
	 *  subsequent web service calls. It also sets the session id for this test run. 
	 */
	@Before
	public void setupSession(){
		
		LoginResponse lresponse;
		try {
			lresponse = sugarWsClient.login2(ClientUtils.createStandardLoginObject("test", "test"));
			assertNotNull(lresponse);
			sessionID = lresponse.getReturn().getId();
			
		} catch (LoginFailureException e) {
			sessionID = "-1";
			LOGGER.info(e.getMessage());
			//e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Method invoked after each test has been executed. It destroys the current session. 
	 */
	@After
	public void destroySession(){
		
		Logout request = factory.createLogout();
		request.setSession(sessionID);
		LogoutResponse lgresponse =  sugarWsClient.logout(request );
		assertNotNull(lgresponse);
	}


	/**
	 * User Login Test (make sure that the user has been created in advance in in the configured SCRM installation)
	 */
	@Test
	public void testLogin(){
		LoginResponse response;

			try {
				response = sugarWsClient.login2(ClientUtils.createStandardLoginObject("test", "test"));
			} catch (LoginFailureException e) {
				response = null;
			}
			assertNotNull(response);
			LOGGER.info(response.getReturn().getId());
			ClientUtils.logMarshalledObject(response);
	}
	
	
	/**
	 * Is User Admin Test: Checks if the user has admin rights.
	 */
	@Test
	public void testIsUserAdmin(){	
		IsUserAdmin user = factory.createIsUserAdmin();		
		user.setSession(sessionID);		
		IsUserAdminResponse response = sugarWsClient.is_user_admin(user);
		assertNotNull(response);
		ClientUtils.logMarshalledObject(response);
	}
	
	
	/**
	 * Get User ID Test: Retrieves a user id for a session.
	 */
	@Test
	public void testGetUserID(){	 
		GetUserId request = factory.createGetUserId();
		request.setSession(sessionID);
		ClientUtils.logMarshalledObject(request);
		GetUserIdResponse response =  sugarWsClient.get_user_id(request);
		assertNotNull(response);
		ClientUtils.logMarshalledObject(response);
	}
	
	
	/**
	 * Get Available Modules: Gets the available modules for a specific SugarCRM installation.
	 */
	@Test
	public void testGetAvailableModules(){	 	
		GetAvailableModules request = factory.createGetAvailableModules();
		request.setSession(sessionID);
		ClientUtils.logMarshalledObject(request);
		GetAvailableModulesResponse response =  sugarWsClient.get_available_modules(request);
		assertNotNull(response);
		ClientUtils.logMarshalledObject(response);
	}

	
	
	/**
	 * Get Module Fields Test: Get the fields for a specific modules.
	 */
	@Test
	public void testGetModuleFields(){	 		
		GetModuleFields request = factory.createGetModuleFields();
		request.setSession(sessionID);
		request.setModuleName("Contacts");
		ClientUtils.logMarshalledObject(request);
		GetModuleFieldsResponse response =  sugarWsClient.get_module_fields(request);
		assertNotNull(response);		
		ClientUtils.logMarshalledObject(response);
	}
	
	
	
	
	/**
	 * Get Entry List Test: Retrieves all fields from a specific module that match a 
	 * specific query String. 
	 */
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
		assertNotNull(response);
		ClientUtils.logMarshalledObject(response);
		
	}
	
	
	/**
	 * Get Entry Test: Retrieves all fields from a specific module. 
	 */
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
	 * Get Entry Test: Get a test entry for a specific ID.
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
	 * Set Entry Test: Create an entry with the specified ID and the declared 
	 * name-value pairs and update it if it already exists. 
	 */
	@Test
	public void testSetEntry(){
		SetEntry request = factory.createSetEntry();
		
		
		NameValue nv1 = factory.createNameValue();
		nv1.setName("id");
		nv1.setValue("99f37146-8e19-473d-171c-4d66de7024c0");
		
		NameValue nv0 = factory.createNameValue();
		nv0.setName("first_name");
		nv0.setValue("JohnX");

		NameValue nv2 = factory.createNameValue();
		nv2.setName("last_name");
		nv2.setValue("SmithX");		

		ArrayList <NameValue> nvList = new  ArrayList <NameValue>();
		
		nvList.add(nv1);
		nvList.add(nv2);
		
		
		NameValueList valueList = ClientUtils.generatePopulatedNameValueList(nvList);

		valueList.setId("99f37146-8e19-473d-171c-4d66de7024c0");
		
		request.setNameValueList(valueList);
		request.setModuleName("Contacts");
		request.setSession(sessionID);	
		ClientUtils.logMarshalledObject(request);
		SetEntryResponse response =  sugarWsClient.set_entry(request);
		ClientUtils.logMarshalledObject(response);
				
	}
	
	
	
		
	}

