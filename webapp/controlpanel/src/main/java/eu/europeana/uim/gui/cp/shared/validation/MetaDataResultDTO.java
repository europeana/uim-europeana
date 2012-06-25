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
 * A result object holding metadata records and the total number of results.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since May 27, 2011
 */
public class MetaDataResultDTO implements IsSerializable {
    /**
     * list with metadata records
     */
    private List<MetaDataRecordDTO> records;
    /**
     * number of records
     */
    private int                     numberRecords;

    /**
     * Creates a new instance of this class.
     */
    public MetaDataResultDTO() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param records
     *            list with metadata records
     * @param numberRecords
     *            number of records
     */
    public MetaDataResultDTO(List<MetaDataRecordDTO> records, int numberRecords) {
        this.records = records;
        this.numberRecords = numberRecords;
    }

    /**
     * @return list with metadata record
     */
    public List<MetaDataRecordDTO> getRecords() {
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
     *            list with metadata records
     */
    public void setRecords(List<MetaDataRecordDTO> records) {
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