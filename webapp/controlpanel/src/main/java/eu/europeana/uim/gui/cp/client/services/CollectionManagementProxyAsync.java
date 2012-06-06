package eu.europeana.uim.gui.cp.client.services;

import java.util.List;

import com.google.gwt.user.client.rpc.AsyncCallback;

import eu.europeana.uim.gui.cp.shared.CollectionMappingDTO;

public interface CollectionManagementProxyAsync {

	public void retrieveCollections(
			AsyncCallback<List<CollectionMappingDTO>> collections);

	public void saveOneCollection(
			CollectionMappingDTO collection, AsyncCallback<Boolean> result);

	public void saveCollections(
			List<CollectionMappingDTO> collections, AsyncCallback<Boolean> result);

	public void retrieveCsvCollections(
			 String location,
			String delimiter, AsyncCallback<List<CollectionMappingDTO>> collections);
}
