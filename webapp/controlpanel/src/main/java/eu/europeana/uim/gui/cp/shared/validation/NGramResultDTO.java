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
import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * A ngram result object holding ngram records, search facets with count and the total number of
 * results.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since May 27, 2011
 */
public class NGramResultDTO implements IsSerializable {
    /**
     * list with search results
     */
    private List<NGramRecordDTO> records;
    /**
     * number of records
     */
    private int                  numberRecords;

    /**
     * Creates a new instance of this class.
     */
    public NGramResultDTO() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param records
     *            list with search results
     * @param numberRecords
     *            number of records
     */
    public NGramResultDTO(List<NGramRecordDTO> records, int numberRecords) {
        this.records = records;
        this.numberRecords = numberRecords;
    }

    /**
     * @return list with search results
     */
    public List<NGramRecordDTO> getRecords() {
        return records;
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
    public void setRecords(List<NGramRecordDTO> records) {
        this.records = records;
    }

    /**
     * @param numberRecords
     *            number of records
     */
    public void setNumberRecords(int numberRecords) {
        this.numberRecords = numberRecords;
    }
}