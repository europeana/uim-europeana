package eu.europeana.uim.gui.cp.client.services;


import java.util.List;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

import eu.europeana.uim.gui.cp.shared.ControlledVocabularyDTO;
import eu.europeana.uim.gui.cp.shared.EdmFieldDTO;
import eu.europeana.uim.gui.cp.shared.MappingDTO;
/**
 * Interface for the operations of importing, mapping and saving a ControlledVocabulary representation into EDM
 * @author Yorgos.Mamakis@ kb.nl
 *
 */
@RemoteServiceRelativePath("importvocabulary")
public interface ImportVocabularyProxy extends RemoteService{
	/**
	 * Import a controlled vocabulary
	 * @param vocabulary - The controlled vocabulary to import
	 * @return The imported controlled vocabulary
	 * @throws FileNotFoundException 
	 */
	public ControlledVocabularyDTO importVocabulary(ControlledVocabularyDTO vocabulary);
	
	/**
	 * Method to map an original controlled vocabulary field to an EDM field
	 * @param originalField The original field to map
	 * @param mappedField The EDM field to map
	 * @return A MappingDTO object with the original and EDM field DTO representations
	 */
	public MappingDTO mapField(String originalField, String mappedField);
	
	/**
	 * Method saving the mapping. It returns true on success.
	 * The method does not use a controlled vocabulary, as it expects the vocabulary to be already initialized
	 * @return true on success, false otherwise
	 */
	public boolean saveMapping(int iterations);
	
	/**
	 * Method to retrieve the controlled vocabularies already stored
	 * @return A list with the controlled vocabularies in the form of DTOs
	 */
	public List<ControlledVocabularyDTO> retrieveVocabularies();
	
	/**
	 * Method retrieving all the available EDM fields
	 * @return A list with the available EDM fields accessible through their DTO
	 */
	public List<EdmFieldDTO> retrieveEdmFields();
	
	/**
	 * Remove a controlled vocabulary
	 * @param vocabularyName The name of the Controlled vocabulary to remove
	 * @return
	 */
	public boolean removeVocabulary(String vocabularyName);
	
	public boolean setVocabulary(String vocabularyName, String vocabularyUri);
}
