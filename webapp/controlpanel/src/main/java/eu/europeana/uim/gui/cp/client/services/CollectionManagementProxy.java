package eu.europeana.uim.gui.cp.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import eu.europeana.uim.gui.cp.shared.CollectionMappingDTO;

@RemoteServiceRelativePath("collectionmanagement")
public interface CollectionManagementProxy extends RemoteService{

	public List<CollectionMappingDTO> retrieveCollections();
	public Boolean saveOneCollection(CollectionMappingDTO collection);
	public Boolean saveCollections(List<CollectionMappingDTO> collections);
	public List<CollectionMappingDTO> retrieveCsvCollections(String location, String delimiter);
	
}
