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

import com.google.gwt.user.client.rpc.IsSerializable;
import com.google.gwt.view.client.ProvidesKey;

/**
 * Collection data object used in GWT for visualization.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @since Apr 28, 2011
 */
public class NGramRecordDTO implements IsSerializable {
    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<NGramRecordDTO> KEY_PROVIDER = new ProvidesKey<NGramRecordDTO>() {
                                                                     @Override
                                                                     public Object getKey(
                                                                             NGramRecordDTO item) {
                                                                         return item == null ? null
                                                                                 : item.getId();
                                                                     }
                                                                 };

    private String                                  id;
    private String                                  display;
    private String                                  type;
    private String                                  language;

    private float                                   score;

    /**
     * Creates a new instance of this class.
     */
    public NGramRecordDTO() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param score
     * @param id
     * @param display
     * @param type
     * @param language
     */
    public NGramRecordDTO(float score, String id, String display, String type, String language) {
        this.score = score;
        this.id = id;
        this.display = display;
        this.type = type;
        this.language = language;
    }

    /**
     * @return score
     */
    public float getScore() {
        return score;
    }

    /**
     * @param score
     */
    public void setScore(float score) {
        this.score = score;
    }

    /**
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * @return display
     */
    public String getDisplay() {
        return display;
    }

    /**
     * @param display
     */
    public void setDisplay(String display) {
        this.display = display;
    }

    /**
     * @return type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * @param language
     */
    public void setLanguage(String language) {
        this.language = language;
    }
}