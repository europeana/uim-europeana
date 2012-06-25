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
public class MetaDataRecordDTO extends DataSourceDTO {
    /**
     * The key provider that provides the unique ID of a contact.
     */
    public static final ProvidesKey<MetaDataRecordDTO> KEY_PROVIDER = new ProvidesKey<MetaDataRecordDTO>() {
                                                                        @Override
                                                                        public Object getKey(
                                                                                MetaDataRecordDTO item) {
                                                                            return item == null
                                                                                    ? null
                                                                                    : item.getId();
                                                                        }
                                                                    };

    private String                                     title;

    private String                                     creator;
    private String                                     contributor;
    private String                                     publicationYear;

    private String                                     workLanguage;
    private String                                     publicationPlace;

    /**
     * Creates a new instance of this class.
     */
    public MetaDataRecordDTO() {
        super();
    }

    /**
     * Creates a new instance of this class.
     * 
     * @param id
     * @param title
     * @param creator
     * @param contributor
     * @param publicationYear
     * @param workLanguage
     * @param publicationPlace
     */
    public MetaDataRecordDTO(Long id, String title, String creator, String contributor,
                             String publicationYear, String workLanguage, String publicationPlace) {
        super(id);
        this.title = title;
        this.creator = creator;
        this.contributor = contributor;
        this.publicationYear = publicationYear;
        this.workLanguage = workLanguage;
        this.publicationPlace = publicationPlace;
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
     * @return contributor
     */
    public String getContributor() {
        return contributor;
    }

    /**
     * @param contributor
     */
    public void setContributor(String contributor) {
        this.contributor = contributor;
    }

    /**
     * @return publicationYear
     */
    public String getPublicationYear() {
        return publicationYear;
    }

    /**
     * @param publicationYear
     */
    public void setPublicationYear(String publicationYear) {
        this.publicationYear = publicationYear;
    }

    /**
     * @return workLanguage
     */
    public String getWorkLanguage() {
        return workLanguage;
    }

    /**
     * @param workLanguage
     */
    public void setWorkLanguage(String workLanguage) {
        this.workLanguage = workLanguage;
    }

    /**
     * @return publicationPlace
     */
    public String getPublicationPlace() {
        return publicationPlace;
    }

    /**
     * @param publicationPlace
     */
    public void setPublicationPlace(String publicationPlace) {
        this.publicationPlace = publicationPlace;
    }
}