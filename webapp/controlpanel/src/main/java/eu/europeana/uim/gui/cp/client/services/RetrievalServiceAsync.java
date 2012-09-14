package eu.europeana.uim.gui.cp.client.services;

import eu.europeana.uim.gui.cp.shared.validation.EdmRecordDTO;
import eu.europeana.uim.gui.cp.shared.validation.LinksResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.MetaDataResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.NGramResultDTO;
import eu.europeana.uim.gui.cp.shared.validation.SearchResultDTO;

import com.google.gwt.user.client.rpc.AsyncCallback;

/**
 * Definition of the asynchronous service to retrieve orchestration dependent information from the
 * server.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @since May 23, 2011
 */
public interface RetrievalServiceAsync {
    /**
     * Delivers up to a maxSize number of records starting at the offset for the provided collection
     * from the central repository
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
     * @param async
     */
    void getRecordsForCollection(String collection, int offset, int maxSize, String constraint,
            AsyncCallback<MetaDataResultDTO> async);

    /**
     * raw record how we got it in our uim
     * 
     * @param recordId
     * @param async
     */
    void getRawRecord(String recordId, AsyncCallback<String> async);

    /**
     * xml representation of our metadata record object model
     * 
     * @param recordId
     * @param async
     */
    void getXmlRecord(String recordId, AsyncCallback<String> async);

    /**
     * Delivers up to a maxSize number of records starting at the offset for the provided collection
     * from the search index together with facets and the total number of results.
     * 
     * @param searchQuery
     * @param offset
     * @param maxSize
     * @param facets
     * @param async
     */
    void searchIndex(String searchQuery, int offset, int maxSize, String[] facets,
            AsyncCallback<SearchResultDTO> async);

    /**
     * xml representation of the actual record in the search index
     * 
     * @param recordId
     * @param async
     */
    void getSearchRecord(String recordId, AsyncCallback<EdmRecordDTO> async);

    /**
     * Delivers up to a maxSize number of suggestions starting at the offset together with facets
     * and the total number of results.
     * 
     * @param searchQuery
     * @param offset
     * @param maxSize
     * @param async
     */
    void searchSuggestions(String searchQuery, int offset, int maxSize,
            AsyncCallback<NGramResultDTO> async);

    /**
     * Delivers the links from a record
     * 
     * @param recordId
     * @param callback
     */
    void getLinks(String recordId, AsyncCallback<LinksResultDTO> callback);
}