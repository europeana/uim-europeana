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
package eu.europeana.uim.mintclient.service;


import java.util.Set;
import org.jibx.runtime.IMarshallable;
import eu.europeana.uim.Registry;
import eu.europeana.uim.orchestration.Orchestrator;
import eu.europeana.uim.storage.StorageEngine;
import eu.europeana.uim.storage.StorageEngineException;
import eu.europeana.uim.mintclient.jibxbindings.CreateImportAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateOrganizationAction;
import eu.europeana.uim.mintclient.jibxbindings.CreateUserAction;
import eu.europeana.uim.mintclient.jibxbindings.GetImportsAction;
import eu.europeana.uim.mintclient.jibxbindings.GetTransformationsAction;
import eu.europeana.uim.mintclient.jibxbindings.ImportExistsAction;
import eu.europeana.uim.mintclient.jibxbindings.OrganizationExistsAction;
import eu.europeana.uim.mintclient.jibxbindings.PublicationAction;
import eu.europeana.uim.mintclient.jibxbindings.PublishTransformationAction;
import eu.europeana.uim.mintclient.jibxbindings.UserExistsAction;
import eu.europeana.uim.mintclient.service.exceptions.MintOSGIClientException;
import eu.europeana.uim.mintclient.utils.MintClientUtils;
import eu.europeana.uim.model.europeanaspecific.fieldvalues.ControlledVocabularyProxy;
import eu.europeana.uim.store.Collection;
import eu.europeana.uim.store.Provider;
import eu.europeana.uim.store.UimEntity;
import eu.europeana.uim.sugar.SugarCrmService;
import eu.europeana.uim.workflow.Workflow;

/**
 * This is the core class for handling both synchronous and asyncronous  incoming messages 
 * from Mint to UIM. This class defines the actual interaction between incoming
 * Mint messages and UIM entities & processes
 * 
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since 22 Mar 2012
 */
public class ReponseHandler {

	private Registry registry;
	private Orchestrator<?> orchestrator;
	private StorageEngine<?> storage;
	private SugarCrmService sugservice;
	private final static String ingestionWf = "InitialIngestionWorkflow";
	private static Set<String> providerlock;
	
	
	/**
	 * Default constructor
	 * 
	 * @param registry
	 * @param orchestrator
	 * @param sugservice
	 */
	public ReponseHandler(Registry registry, Orchestrator<?> orchestrator,SugarCrmService sugservice,
			Set<String> providerlock) {
		this.registry = registry;
		this.orchestrator = orchestrator;
		this.storage = registry.getStorageEngine();
		this.sugservice = sugservice;
		ReponseHandler.providerlock = providerlock;
	}

	
	/**
	 * This method is used by the asynchronous listener in order to format the 
	 * input received by the UIMConsumerListener in a format compatible
	 *  to an asynchronous response
	 *  
	 * @param responseStr the raw XML string of the received message
	 * @param corrID the correlation id of the message
	 * @throws MintOSGIClientException
	 * @throws StorageEngineException
	 * @see MintUIMServiceImpl.UIMConsumerListener
	 */
	public void handleResponse(String responseStr, String corrID)
			throws MintOSGIClientException, StorageEngineException {

		String uimID = MintClientUtils.extractIDfromCorrId(corrID);

		IMarshallable response = MintClientUtils.unmarshallobject(responseStr);

		Collection<?> coll = storage.findCollection(uimID);
		if (coll != null) {
			handleResponse(response, coll);
		} else {
			Provider<?> prov = storage.findProvider(uimID);
			if (prov != null) {
				handleResponse(response, prov);
			} else {
				throw new MintOSGIClientException(
						"Erroneous Message sent by Mint: Collection or provider Id declared "
								+ "in the correlationID ("
								+ corrID
								+ ") of the received"
								+ "AMPQ message does not correspond to an existing UIM entity");
			}
		}

	}

	
	
	/**
	 * This is the actual place where the interactions with the entities
	 * and operations of UIM take place. If you need to process new types
	 * of incoming messages just place them here...
	 * 
	 * @param response the JIBX implementation of the response
	 * @param entity the UIM entity (either a provider or collection)
	 * @throws MintOSGIClientException
	 * @throws StorageEngineException
	 */
	public void handleResponse(IMarshallable response, UimEntity entity)
			throws MintOSGIClientException, StorageEngineException {

   	   if(response instanceof CreateOrganizationAction){
			@SuppressWarnings("rawtypes")
			Provider prov = (Provider) entity;
			CreateOrganizationAction action = (CreateOrganizationAction) response;
			prov.putValue(ControlledVocabularyProxy.MINTID, action
					.getCreateOrganizationResponse().getOrganizationId());
			storage.updateProvider(prov);
			providerlock.remove(prov.getMnemonic());
		}
		else if(response instanceof CreateUserAction){
			@SuppressWarnings("rawtypes")
			Provider prov2 = (Provider) entity;
			CreateUserAction action2 = (CreateUserAction) response;
			//TODO:Create user support when needed here
		}
		else if(response instanceof CreateImportAction){
			@SuppressWarnings("rawtypes")
			Collection collection = (Collection) entity;
			CreateImportAction action = (CreateImportAction) response;
			collection.putValue(ControlledVocabularyProxy.MINTID,
					action.getCreateImportResponse().getImportId());
			storage.updateCollection(collection);
		}
		else if(response instanceof GetImportsAction){

			// This method does not affect the status of the UIM Entity
		}
		else if(response instanceof GetTransformationsAction){
			// This method does not affect the status of the UIM Entity
		}
		else if(response instanceof PublicationAction){
			// This method does not affect the status of the UIM Entity
		}
		else if(response instanceof PublishTransformationAction){
			@SuppressWarnings("rawtypes")
			Collection collection = (Collection) entity;
			PublishTransformationAction action = (PublishTransformationAction) response;

			String ziplocation = action.getPublishTransformationResponse().getUrl();

			String transformationID = action.getPublishTransformationResponse().getTransformationId();
			
			collection.putValue(ControlledVocabularyProxy.LATESTMINTTRANSFORMATIONID,transformationID);
			
			collection.putValue(ControlledVocabularyProxy.MINTPUBLICATIONLOCATION,
					ziplocation);

			storage.updateCollection(collection);
			
			Workflow ingestionworkflow =registry.getWorkflow(ingestionWf);
			
			orchestrator.executeWorkflow(ingestionworkflow, collection);
		}
		else if(response instanceof ImportExistsAction){
			// This method does not affect the status of the UIM Entity
		}
		else if(response instanceof UserExistsAction){
			// This method does not affect the status of the UIM Entity
		}
		else if(response instanceof OrganizationExistsAction){
			// This method does not affect the status of the UIM Entity
		}
		else{
			throw new MintOSGIClientException("Received message is not considered to be a valid operation by" +
					"the consumer...");
		}
	

	}
}
