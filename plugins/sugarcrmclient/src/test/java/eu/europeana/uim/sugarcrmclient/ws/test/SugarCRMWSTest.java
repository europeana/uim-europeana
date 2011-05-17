/*
 * Copyright 2007 EDL FOUNDATION
 *
 * Licensed under the EUPL, Version 1.1 or - as soon they
 * will be approved by the European Commission - subsequent
 * versions of the EUPL (the "Licence");
 * you may not use this work except in compliance with the
 * Licence.
 * You may obtain a copy of the Licence at:
 *
 * http://ec.europa.eu/idabc/eupl
 *
 * Unless required by applicable law or agreed to in
 * writing, software distributed under the Licence is
 * distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied.
 * See the Licence for the specific language governing
 * permissions and limitations under the Licence.
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
import eu.europeana.uim.sugarcrmclient.ws.exceptions.GenericSugarCRMException;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LoginFailureException;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.LougoutFailureException;
import eu.europeana.uim.sugarcrmclient.ws.exceptions.QueryResultException;

import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModules;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModulesResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntries;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntriesResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFieldsResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetUserId;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetUserIdResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Login;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Logout;
import eu.europeana.uim.sugarcrmclient.jibxbindings.LogoutResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NameValue;
import eu.europeana.uim.sugarcrmclient.jibxbindings.NameValueList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SelectFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntryResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.UserAuth;
import eu.europeana.uim.sugarcrmclient.jibxbindings.LoginResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.IsUserAdmin;
import eu.europeana.uim.sugarcrmclient.jibxbindings.IsUserAdminResponse;


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
		} catch (GenericSugarCRMException e) {
			e.printStackTrace();
		}
		
	}
	
	
	/**
	 * Method invoked after each test has been executed. It destroys the current session. 
	 */
	@After
	public void destroySession(){
		
		Logout request = new Logout();
		request.setSession(sessionID);
		LogoutResponse lgresponse;
		try {
			lgresponse = sugarWsClient.logout(request );
			assertNotNull(lgresponse);
		} catch (LougoutFailureException e) {
			e.printStackTrace();
		} catch (GenericSugarCRMException e) {
			e.printStackTrace();
		}
		
	}


	/**
	 * User Login Test (make sure that the user has been created in advance in in the configured SCRM installation)
	 */
	@Test
	public void testLogin(){
		LoginResponse response;

			try {
				Login login = ClientUtils.createStandardLoginObject("test", "test");
					
				ClientUtils.logMarshalledObject(login);
				
				response = sugarWsClient.login2(login);
				
				assertNotNull(response);
				LOGGER.info(response.getReturn().getId());
				ClientUtils.logMarshalledObject(response);
			} catch (LoginFailureException e) {
				response = null;
			} catch (GenericSugarCRMException e) {
				e.printStackTrace();
			}

	}
	
	
	/**
	 * Is User Admin Test: Checks if the user has admin rights.
	 */
	@Test
	public void testIsUserAdmin(){	
		IsUserAdmin user = new IsUserAdmin();		
		user.setSession(sessionID);
		
		IsUserAdminResponse response;
		try {
			response = sugarWsClient.is_user_admin(user);
			assertNotNull(response);
			ClientUtils.logMarshalledObject(response);
		} catch (GenericSugarCRMException e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Get User ID Test: Retrieves a user id for a session.
	 */
	@Test
	public void testGetUserID(){	 
		GetUserId request = new GetUserId();
		request.setSession(sessionID);
		ClientUtils.logMarshalledObject(request);
		GetUserIdResponse response;
		try {
			response = sugarWsClient.get_user_id(request);
			assertNotNull(response);
			ClientUtils.logMarshalledObject(response);
		} catch (GenericSugarCRMException e) {
			e.printStackTrace();
		}

	}
	
	
	/**
	 * Get Available Modules: Gets the available modules for a specific SugarCRM installation.
	 */
	@Test
	public void testGetAvailableModules(){	 	
		GetAvailableModules request = new GetAvailableModules();
		request.setSession(sessionID);
		ClientUtils.logMarshalledObject(request);
		GetAvailableModulesResponse response;
		try {
			response = sugarWsClient.get_available_modules(request);
			assertNotNull(response);
			ClientUtils.logMarshalledObject(response);
		} catch (QueryResultException e) {
			e.printStackTrace();
		}

	}

	
	
	/**
	 * Get Module Fields Test: Get the fields for a specific modules.
	 */
	@Test
	public void testGetModuleFields(){	 		
		GetModuleFields request = new GetModuleFields();
		request.setSession(sessionID);
		request.setModuleName("Contacts");
		ClientUtils.logMarshalledObject(request);
		GetModuleFieldsResponse response;
		try {
			response = sugarWsClient.get_module_fields(request);
			assertNotNull(response);		
			ClientUtils.logMarshalledObject(response);
		} catch (QueryResultException e) {
			e.printStackTrace();
		}

	}
	
	
	
	
	/**
	 * Get Entry List Test: Retrieves all fields from a specific module that match a 
	 * specific query String. 
	 */
	@Test
	public void testGetEntryList(){		
		GetEntryList request = new GetEntryList();
		
		ArrayList <String> fieldnames = new  ArrayList<String>();
		
		fieldnames.add("id");
		fieldnames.add("first_name");
		fieldnames.add("last_name");
		fieldnames.add("salutation");
		
		SelectFields fields = ClientUtils.generatePopulatedSelectFields(fieldnames);
		
  		request.setModuleName("Contacts");	
		request.setSelectFields(fields);
		request.setSession(sessionID);
		request.setOrderBy("last_name");
		request.setMaxResults(100);
		request.setOffset(10);
		//request.setQuery("(contacts.salutation = 'Mr.' AND contacts.title LIKE 'doctor appointment%')");
		request.setQuery("(contacts.first_name LIKE '%M%')");
		
		ClientUtils.logMarshalledObject(request);
		GetEntryListResponse response;
		try {
			response = sugarWsClient.get_entry_list(request);
			assertNotNull(response);
			ClientUtils.logMarshalledObject(response);
		} catch (QueryResultException e) {
			e.printStackTrace();
		}

		
	}
	
	
	/**
	 * Get Entry Test: Retrieves all fields from a specific module. 
	 */
	@Test
	public void testGetEntries(){	 		
		GetEntries request = new GetEntries();
		ArrayList <String> fieldnames = new  ArrayList<String>();
		fieldnames.add("id");
		fieldnames.add("first_name");
		fieldnames.add("last_name");
		fieldnames.add("salutation");
		SelectFields fields = ClientUtils.generatePopulatedSelectFields(fieldnames);
  		request.setModuleName("Contacts");	
		request.setSelectFields(fields);
		request.setSession(sessionID);
		request.setIds(fields);
		ClientUtils.logMarshalledObject(request);
		GetEntriesResponse response;
		try {
			response = sugarWsClient.get_entries(request);
			ClientUtils.logMarshalledObject(response);
		} catch (QueryResultException e) {
			e.printStackTrace();
		}

	}
	
	
	
	/**
	 * Get Entry Test: Get a test entry for a specific ID.
	 */
	@Test
	public void testGetEntry(){
		GetEntry request = new GetEntry();
		request.setId("1c3a03dd-753b-7741-92e8-4d6509d442d3");
		request.setModuleName("Contacts");
		request.setSession(sessionID);	
		SelectFields selectFields = new SelectFields();
		request.setSelectFields(selectFields );
		ClientUtils.logMarshalledObject(request);
		GetEntryResponse response;
		try {
			response = sugarWsClient.get_entry(request);
			ClientUtils.logMarshalledObject(response);
		} catch (QueryResultException e) {
			e.printStackTrace();
		}
	}
	
	
	
	/**
	 * Set Entry Test: Create an entry with the specified ID and the declared 
	 * name-value pairs and update it if it already exists. 
	 */
	@Test
	public void testSetEntry(){
		SetEntry request = new SetEntry();
		
		
		//NameValue nv1 = new NameValue();
		//nv1.setName("id");
		//nv1.setValue("99f37146-8e19-473d-171c-4d66de7024c0");
		
		NameValue nv0 = new NameValue();
		nv0.setName("first_name");
		nv0.setValue("JohnX");

		NameValue nv2 = new NameValue();
		nv2.setName("last_name");
		nv2.setValue("SmithX");		

		ArrayList <NameValue> nvList = new  ArrayList <NameValue>();
		
		nvList.add(nv0);
		nvList.add(nv2);
		
		NameValueList valueList = ClientUtils.generatePopulatedNameValueList(nvList);
		
		request.setNameValueList(valueList);
		request.setModuleName("Contacts");
		request.setSession(sessionID);	
		ClientUtils.logMarshalledObject(request);
		SetEntryResponse response;
		try {
			response = sugarWsClient.set_entry(request);
			ClientUtils.logMarshalledObject(response);
		} catch (QueryResultException e) {
			e.printStackTrace();
		}

				
	}
	
	
	
		
	}

