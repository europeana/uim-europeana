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

import com.google.gwt.view.client.ProvidesKey;
import eu.europeana.uim.gui.cp.shared.DataSourceDTO;

/**
 * Collection data object used in GWT for visualization.
 * 
 * @author Markus Muhr (markus.muhr@kb.nl)
 * @author Georgios Markakis <gwarkx@hotmail.com>
 * @since Apr 28, 2011
 */
public class SearchRecordDTO extends DataSourceDTO {
    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<SearchRecordDTO> KEY_PROVIDER = new ProvidesKey<SearchRecordDTO>() {
                                                                      @Override
                                                                      public Object getKey(
                                                                              SearchRecordDTO item) {
                                                                          return item == null
                                                                                  ? null
                                                                                  : item.getId();
                                                                      }
                                                                  };

    private String                                   title;
    private String                                   creator;
    private String                                   year;

    private float                                    score;

    /**
     * Creates a new instance of this class.
     */
    public SearchRecordDTO() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param id
     * @param score
     * @param title
     * @param creator
     * @param year
     */
    public SearchRecordDTO(Long id, float score, String title, String creator, String year) {
        super(id);
        this.score = score;
        this.title = title;
        this.creator = creator;
        this.year = year;
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
     * @return title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * @return creator
     */
    public String getCreator() {
        return creator;
    }

    /**
     * @param creator
     */
    public void setCreator(String creator) {
        this.creator = creator;
    }

    /**
     * @return year
     */
    public String getYear() {
        return year;
    }

    /**
     * @param year
     */
    public void setYear(String year) {
        this.year = year;
    }
}