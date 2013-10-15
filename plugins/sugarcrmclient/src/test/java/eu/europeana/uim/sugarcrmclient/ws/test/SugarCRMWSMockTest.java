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
package eu.europeana.uim.sugarcrmclient.ws.test;

import static org.mockito.Mockito.*;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Before;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import eu.europeana.uim.sugar.GenericSugarCrmException;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Array;
import eu.europeana.uim.sugarcrmclient.jibxbindings.EntryList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.ErrorValue;
import eu.europeana.uim.sugarcrmclient.jibxbindings.FieldList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModules;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetAvailableModulesResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntries;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntriesResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryListResult;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetEntryResult;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetModuleFieldsResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetRelationships;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetRelationshipsResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetRelationshipsResult;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetUserId;
import eu.europeana.uim.sugarcrmclient.jibxbindings.GetUserIdResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.IdsMods;
import eu.europeana.uim.sugarcrmclient.jibxbindings.IsUserAdmin;
import eu.europeana.uim.sugarcrmclient.jibxbindings.IsUserAdminResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.Login;
import eu.europeana.uim.sugarcrmclient.jibxbindings.LoginResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.ModuleFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.ModuleList;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SelectFields;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntry;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntryResponse;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetEntryResult;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetNoteAttachment;
import eu.europeana.uim.sugarcrmclient.jibxbindings.SetNoteAttachmentResponse;
import eu.europeana.uim.sugarcrmclient.ws.SugarWsClient;



/**
 * Mock Implementation of SugarCRM Unit Tests
 * 
 * @author Georgios Markakis (gwarkx@hotmail.com)
 * @since Oct 9, 2013
 */
public class SugarCRMWSMockTest extends AbstractSugarCRMTest{
	
	/**
	 * Initialize superclass fields & method call mock behavior here
	 * 
	 * @throws GenericSugarCrmException
	 * @throws ParserConfigurationException
	 */
	@Before
	public void setupClient() throws GenericSugarCrmException, ParserConfigurationException {
		super.sugarWsClient = sugarWsClient = mock(SugarWsClient.class);
		LoginResponse loginrsp = new LoginResponse();
		SetEntryResult returnLoginnResp = new SetEntryResult();
		returnLoginnResp.setId("someid");
		ErrorValue error = new ErrorValue();
		error.setNumber("0");
		error.setName("No Error");
		error.setDescription("No Error");
		returnLoginnResp.setError(error );
		loginrsp.setReturn(returnLoginnResp);

		when(sugarWsClient.login2((Login) anyObject())).thenReturn(loginrsp);
		when(sugarWsClient.getUsername()).thenReturn("mockuser");
		when(sugarWsClient.getPassword()).thenReturn("mockpass");
		when(sugarWsClient.getSessionID()).thenReturn("mocksession");
		
		IsUserAdminResponse isuseradminresp = new IsUserAdminResponse();
		isuseradminresp.setReturn(1);
		when(sugarWsClient.isuseradmin((IsUserAdmin) anyObject())).thenReturn(isuseradminresp);
		
		GetUserIdResponse userid = new GetUserIdResponse();
		when(sugarWsClient.getuserid((GetUserId) anyObject())).thenReturn(userid);
		
		GetAvailableModulesResponse availablemodules = new GetAvailableModulesResponse();
		ModuleList returnm = new ModuleList();
		returnm.setError(error);
		SelectFields modules = new SelectFields();
		Array array = new Array();
		
		List<Element> anylist = new ArrayList<Element>();
		array.setAnyList(anylist );
		
	    DocumentBuilderFactory factory =
	    	      DocumentBuilderFactory.newInstance();
	    DocumentBuilder builder =
	    	        factory.newDocumentBuilder();
	    Document document = builder.newDocument();
	    Element testel = document.createElement("mockelement");
	    anylist.add(testel);
		modules.setArray(array);
		returnm.setModules(modules);
		availablemodules.setReturn(returnm);
		when(sugarWsClient.getavailablemodules((GetAvailableModules) anyObject())).thenReturn(availablemodules);
		
		
		GetModuleFieldsResponse modulefields = new GetModuleFieldsResponse();
		ModuleFields _mfieldsreturn = new ModuleFields();
		_mfieldsreturn.setError(error);
		_mfieldsreturn.setModuleName("Accounts");
		FieldList moduleFields = new FieldList();
		moduleFields.setArray(array);
		_mfieldsreturn.setModuleFields(moduleFields );
		modulefields.setReturn(_mfieldsreturn);
		when(sugarWsClient.getmodulefields((GetModuleFields) anyObject())).thenReturn(modulefields );

		GetEntryListResponse entrylist = new GetEntryListResponse();
		GetEntryListResult entrylistreturn = new GetEntryListResult();
		entrylistreturn.setError(error);
		entrylistreturn.setResultCount(1);
		entrylist.setReturn(entrylistreturn);
		when(sugarWsClient.getentrylist((GetEntryList) anyObject())).thenReturn(entrylist);
		
		GetEntriesResponse entries = new GetEntriesResponse();
		GetEntryResult entriesreturn = new GetEntryResult();
		entriesreturn.setError(error);
		EntryList entryList = new EntryList();
		entryList.setArray(array);
		entriesreturn.setEntryList(entryList );
		entries.setReturn(entriesreturn );
		when(sugarWsClient.getentries((GetEntries) anyObject())).thenReturn(entries);
		
		GetEntryResponse entry = new GetEntryResponse();
		GetEntryResult entryreturn = new GetEntryResult();
		entryreturn.setError(error);
		EntryList entryList2 = new EntryList();
		entryList2.setArray(array);
		entryreturn.setEntryList(entryList2);
		entry.setReturn(entryreturn);
		when(sugarWsClient.getentry((GetEntry) anyObject())).thenReturn(entry );
		
		SetEntryResponse setentryres = new SetEntryResponse();
		SetEntryResult setentryresreturn = new SetEntryResult();
		setentryresreturn.setError(error);
		setentryres.setReturn(setentryresreturn );
		when(sugarWsClient.setentry((SetEntry) anyObject())).thenReturn(setentryres );

		SetNoteAttachmentResponse noteattachment = new SetNoteAttachmentResponse();
		SetEntryResult noteattachmentreturn = new SetEntryResult();
		noteattachmentreturn.setError(error);
		noteattachment.setReturn(noteattachmentreturn );
		when(sugarWsClient.setnoteattachment((SetNoteAttachment) anyObject())).thenReturn(noteattachment );

		GetRelationshipsResponse relationshipsresp = new GetRelationshipsResponse();
		GetRelationshipsResult relationshipsrespreturn = new GetRelationshipsResult();
		relationshipsrespreturn.setError(error);
		IdsMods ids = new IdsMods();
		ids.setArray(array);
		relationshipsrespreturn.setIds(ids);
		relationshipsresp.setReturn(relationshipsrespreturn );
		when(sugarWsClient.getrelationships((GetRelationships) anyObject())).thenReturn(relationshipsresp);
		
	}
}
