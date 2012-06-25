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
package eu.europeana.uim.gui.cp.shared.validation;

import java.util.List;
import java.util.Map;
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A search result object holding search records, search facets with count and the total number of
 * results.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since May 27, 2011
 */
public class SearchResultDTO implements IsSerializable {
    /**
     * list with search results
     */
    private List<SearchRecordDTO>            records;
    /**
     * facets uplooks
     */
    private Map<String, List<FacetValueDTO>> facets;
    /**
     * number of records
     */
    private int                              numberRecords;

    /**
     * Creates a new instance of this class.
     */
    public SearchResultDTO() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param records
     *            list with search results
     * @param facets
     *            facets uplooks
     * @param numberRecords
     *            number of records
     */
    public SearchResultDTO(List<SearchRecordDTO> records, Map<String, List<FacetValueDTO>> facets,
                           int numberRecords) {
        this.records = records;
        this.facets = facets;
        this.numberRecords = numberRecords;
    }

    /**
     * @return list with search results
     */
    public List<SearchRecordDTO> getRecords() {
        return records;
    }

    /**
     * @return facets uplooks
     */
    public Map<String, List<FacetValueDTO>> getFacets() {
        return facets;
    }

    /**
     * @return number of records
     */
    public int getNumberRecords() {
        return numberRecords;
    }

    /**
     * @param records
     *            list with search results
     */
    public void setRecords(List<SearchRecordDTO> records) {
        this.records = records;
    }

    /**
     * @param facets
     *            facets uplooks
     */
    public void setFacets(Map<String, List<FacetValueDTO>> facets) {
        this.facets = facets;
    }

    /**
     * @param numberRecords
     *            number of records
     */
    public void setNumberRecords(int numberRecords) {
        this.numberRecords = numberRecords;
    }
}