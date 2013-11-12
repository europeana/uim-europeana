package eu.europeana.uim.gui.cp.client.services;

import eu.europeana.uim.gui.cp.shared.validation.EdmRecordDTO;
import eu.europeana.uim.gui.cp.shared.validation.LinksResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.MetaDataResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.NGramResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.SearchResultDTO;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * Definition of the service to retrieve orchestration dependent information from the server.
 * 
 * @author Manuel Bernhardt <bernhardt.manuel@gmail.com>
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @since May 23, 2011
 */
@RemoteServiceRelativePath("retrieval")
public interface RetrievalService extends RemoteService {
    /**
     * Delivers up to a maxSize number of records starting at the offset for the provided collection
     * from the central repository.
     * 
     * @param collection
     *            for which data set the records should be queried
     * @param offset
     *            offset from start of IDs
     * @param maxSize
     *            maximum number of records in result
     * @param constraint
     *            arbitrary constraint on returned records (right now only a valid record ID is
     *            supported)
     * @return list of simple metadata records to be shown in repository browsing view
     */
    MetaDataResultDTO getRecordsForCollection(String collection, int offset, int maxSize,
            String constraint);

    /**
     * @param recordId
     * @return raw record how we got it in our uim
     */
    String getRawRecord(String recordId);

    /**
     * @param recordId
     * @return xml representation of our metadata record object model
     */
    String getXmlRecord(String recordId);

    /**
     * @param recordId
     * @return xml representation of the dereferenced metadata record object model
     */
    String getDereferencedRecord(String recordId);
    
    /**
     * @param recordId
     * @return xml representation of the dereferenced metadata record object model
     */
    String getEnrichedRecord(String recordId);
    
    /**
     * Delivers up to a maxSize number of records starting at the offset for the provided collection
     * from the search index together with facets and the total number of results.
     * 
     * @param searchQuery
     * @param offset
     * @param maxSize
     * @param facets
     * @return search result object with e.g. list of simple metadata records to be shown in search
     *         view
     */
    SearchResultDTO searchIndex(String searchQuery, int offset, int maxSize, String[] facets);

    /**
     * @param recordId
     * @return The stored SOLR and Mongo values for the record
     */
    EdmRecordDTO getSearchRecord(String recordId);
   
    /**
     * Delivers up to a maxSize number of suggestions starting at the offset together with facets
     * and the total number of results.
     * 
     * @param searchQuery
     * @param offset
     * @param maxSize
     * @return ngram results
     */
    NGramResultDTO searchSuggestions(String searchQuery, int offset, int maxSize);
    
    /**
     * Delivers the links from a record
     * 
     * @param recordId
     * @return the links for the record
     */
    LinksResultDTO getLinks(String recordId);
}